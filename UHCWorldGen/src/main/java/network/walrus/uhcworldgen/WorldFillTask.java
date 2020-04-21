package network.walrus.uhcworldgen;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import network.walrus.utils.bukkit.block.CoordXZ;
import network.walrus.utils.bukkit.world.WorldFileData;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Server;
import org.bukkit.World;

/**
 * A modified version of the world fill task from the WorldBorder plugin by Brettflan.
 *
 * <p>https://github.com/Brettflan/WorldBorder/blob/Pre-MC-1.9/src/main/java/com/wimbli/WorldBorder/WorldFillTask.java
 *
 * @author Brett Flannigan
 */
public class WorldFillTask implements Runnable {
  /** Data this task if for * */
  private final ConfiguredWorldData data;
  /** Server the task if running on * */
  private final Server server;
  /** World the task is for * */
  private final World world;
  /** Data about chunk load state * */
  private final WorldFileData worldData;
  /** Callback to run when the task is started */
  private final Runnable onStart;
  /** Completion callback * */
  private final Runnable onComplete;
  /** Java runtime used to check memory * */
  private final Runtime rt = Runtime.getRuntime();
  /** Task ready status * */
  private boolean readyToGo = false;
  /** Task is paused/finished? * */
  private boolean paused = false;
  /** Task is paused due to low memory? * */
  private boolean pausedForMemory = false;
  /** Bukkit task ID * */
  private int taskID = -1;
  /** If the task just continued from a paused state * */
  private boolean continueNotice = false;
  /** Current chunk X * */
  private int x = 0;
  /** Current chunk Z * */
  private int z = 0;
  /** If the z leg is negative * */
  private boolean isZLeg = false;
  /** If the current leg is negative * */
  private boolean isNeg = false;
  /** Current leg length * */
  private int length = -1;
  /** Location in the current leg * */
  private int current = 0;
  /** If the current x,z pair is inside the border * */
  private boolean insideBorder = true;
  /** Chunks which need to remain loaded * */
  private List<CoordXZ> storedChunks = new LinkedList<>();
  /** Chunks loaded before the task began * */
  private Set<CoordXZ> originalChunks = new HashSet<>();
  /** Last chunk which was processed * */
  private CoordXZ lastChunk = new CoordXZ(0, 0);
  /** Time of last generation message * */
  private long lastReport = System.currentTimeMillis();
  /** Target number of chunks to load * */
  private int reportTarget = 0;
  /** Total processed chunks * */
  private int reportTotal = 0;
  /** Chunks processed since last report * */
  private int reportNum = 0;

  /**
   * @param theServer that the task is running on
   * @param world that the task is for
   * @param data used to determine what the task should do
   * @param onStart Callback to run when the task is started
   * @param onComplete completion callback
   */
  WorldFillTask(
      Server theServer,
      World world,
      ConfiguredWorldData data,
      Runnable onStart,
      Runnable onComplete) {
    this.data = data;
    this.server = theServer;
    this.world = world;
    this.worldData = WorldFileData.create(world);
    this.onStart = onStart;
    this.onComplete = onComplete;

    // load up a new WorldFileData for the world in question, used to scan region files for which
    // chunks are already fully generated and such
    if (worldData == null) {
      this.stop();
      return;
    }

    int chunkWidthX = (int) Math.ceil((double) ((data.size + 16) * 2) / 16);
    int chunkWidthZ = (int) Math.ceil((double) ((data.size + 16) * 2) / 16);
    int biggerWidth =
        (chunkWidthX > chunkWidthZ)
            ? chunkWidthX
            : chunkWidthZ; // We need to calculate the reportTarget with the bigger width, since the
    // spiral will only stop if it has a size of biggerWidth x biggerWidth
    this.reportTarget = (biggerWidth * biggerWidth) + biggerWidth + 1;

    // keep track of the chunks which are already loaded when the task starts, to not unload them
    Chunk[] originals = world.getLoadedChunks();
    for (Chunk original : originals) {
      originalChunks.add(new CoordXZ(original.getX(), original.getZ()));
    }

    this.readyToGo = true;
  }

  /** Start the task */
  void start() {
    onStart.run();
    setTaskID(server.getScheduler().scheduleSyncRepeatingTask(WorldGenPlugin.instance, this, 1, 1));
  }

  private void setTaskID(int ID) {
    if (ID == -1) this.stop();
    this.taskID = ID;
  }

  @Override
  public void run() {
    if (continueNotice) { // notify user that task has continued automatically
      continueNotice = false;
      sendMessage("World map generation task automatically continuing.");
    }

    if (pausedForMemory) { // if available memory gets too low, we automatically pause, so handle
      // that
      if (getAvailableMemory() < 650) return;

      pausedForMemory = false;
      readyToGo = true;
      sendMessage("Available memory is sufficient, automatically continuing.");
    }

    if (server == null || !readyToGo || paused) return;

    // this is set so it only does one iteration at a time, no matter how frequently the timer fires
    readyToGo = false;

    long now = System.currentTimeMillis();

    // every 5 seconds or so, give basic progress report to let user know how it's going
    if (now > lastReport + 5000) reportProgress();

    // if we've made it at least partly outside the border, skip past any such chunks
    while (!data.insideBorder(CoordXZ.chunkToBlock(x) + 8, CoordXZ.chunkToBlock(z) + 8)) {
      if (!moveToNext()) return;
    }
    insideBorder = true;

    // skip past any chunks which are confirmed as fully generated using our super-special
    // isChunkFullyGenerated routine
    int rLoop = 0;
    while (worldData.isChunkFullyGenerated(x, z)) {
      rLoop++;
      insideBorder = true;
      if (!moveToNext()) return;
      if (rLoop
          > 255) { // only skim through max 256 chunks (~8 region files) at a time here, to allow
        // process to take a break if needed
        readyToGo = true;
        return;
      }
    }

    // load the target chunk and generate it if necessary
    world.loadChunk(x, z, true);
    worldData.chunkExistsNow(x, z);

    // There need to be enough nearby chunks loaded to make the server populate a chunk with trees,
    // snow, etc.
    // So, we keep the last few chunks loaded, and need to also temporarily load an extra inside
    // chunk (neighbor closest to center of map)
    int popX = !isZLeg ? x : (x + (isNeg ? -1 : 1));
    int popZ = isZLeg ? z : (z + (!isNeg ? -1 : 1));
    world.loadChunk(popX, popZ, false);

    // make sure the previous chunk in our spiral is loaded as well (might have already existed and
    // been skipped over)
    if (!storedChunks.contains(lastChunk) && !originalChunks.contains(lastChunk)) {
      world.loadChunk(lastChunk.x, lastChunk.z, false);
      storedChunks.add(new CoordXZ(lastChunk.x, lastChunk.z));
    }

    // Store the coordinates of these latest 2 chunks we just loaded, so we can unload them after a
    // bit...
    storedChunks.add(new CoordXZ(popX, popZ));
    storedChunks.add(new CoordXZ(x, z));

    // If enough stored chunks are buffered in, go ahead and unload the oldest to free up memory
    while (storedChunks.size() > 8) {
      CoordXZ coord = storedChunks.remove(0);
      if (!originalChunks.contains(coord)) world.unloadChunkRequest(coord.x, coord.z);
    }

    // move on to next chunk
    if (!moveToNext()) return;

    // ready for the next iteration to run
    readyToGo = true;
  }

  // step through chunks in spiral pattern from center; returns false if we're done, otherwise
  // returns true
  private boolean moveToNext() {
    if (paused || pausedForMemory) return false;

    reportNum++;

    // make sure of the direction we're moving (X or Z? negative or positive?)
    if (current < length) current++;
    else { // one leg/side of the spiral down...
      current = 0;
      isZLeg ^= true;
      if (isZLeg) { // every second leg (between X and Z legs, negative or positive), length
        // increases
        isNeg ^= true;
        length++;
      }
    }

    // keep track of the last chunk we were at
    lastChunk.x = x;
    lastChunk.z = z;

    // move one chunk further in the appropriate direction
    if (isZLeg) z += (isNeg) ? -1 : 1;
    else x += (isNeg) ? -1 : 1;

    // if we've been around one full loop (4 legs)...
    if (isZLeg
        && isNeg
        && current == 0) { // see if we've been outside the border for the whole loop
      if (!insideBorder) { // and finish if so
        finish();
        return false;
      } // otherwise, reset the "inside border" flag
      else insideBorder = false;
    }
    return true;

    /* reference diagram used, should move in this pattern:
     *  8 [>][>][>][>][>] etc.
     * [^][6][>][>][>][>][>][6]
     * [^][^][4][>][>][>][4][v]
     * [^][^][^][2][>][2][v][v]
     * [^][^][^][^][0][v][v][v]
     * [^][^][^][1][1][v][v][v]
     * [^][^][3][<][<][3][v][v]
     * [^][5][<][<][<][<][5][v]
     * [7][<][<][<][<][<][<][7]
     */
  }

  private void finish() {
    this.paused = true;
    reportProgress();
    server.unloadWorld(world, true);
    try {
      Thread.sleep(3000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    this.stop();
    onComplete.run();
    sendMessage("task successfully completed for world \"" + world.getName() + "\"!");
  }

  // we're done, whether finished or cancelled
  private void stop() {
    if (server == null) return;

    readyToGo = false;
    if (taskID != -1) server.getScheduler().cancelTask(taskID);

    // go ahead and unload any chunks we still have loaded
    while (!storedChunks.isEmpty()) {
      CoordXZ coord = storedChunks.remove(0);
      if (!originalChunks.contains(coord)) world.unloadChunk(coord.x, coord.z);
    }
  }

  // let the user know how things are coming along
  private void reportProgress() {
    lastReport = System.currentTimeMillis();
    double perc = getPercentageCompleted();
    if (perc > 100) perc = 100;
    sendMessage(
        reportNum
            + " more chunks processed ("
            + (reportTotal + reportNum)
            + " total, ~"
            + perc
            + "%"
            + ")");
    reportTotal += reportNum;
    reportNum = 0;
  }

  // send a message to the server console/log and possibly to an in-game player
  private void sendMessage(String text) {
    // Due to chunk generation eating up memory and Java being too slow about GC, we need to track
    // memory availability
    int availMem = getAvailableMemory();

    Bukkit.getLogger()
        .info("[Fill " + world.getName() + "] " + text + " (free mem: " + availMem + " MB)");

    if (availMem < 1000) { // running low on memory, auto-pause
      pausedForMemory = true;
      text = "Available memory is very low, task is pausing.";
      Bukkit.getLogger().info("[Fill " + world.getName() + "] " + text);

      // prod Java with a request to go ahead and do GC to clean unloaded chunks from memory; this
      // seems to work wonders almost immediately
      // yes, explicit calls to System.gc() are normally bad, but in this case it otherwise can take
      // a long long long time for Java to recover memory
      System.gc();
    }
  }

  /**
   * Get the percentage completed for the fill task.
   *
   * @return Percentage
   */
  private double getPercentageCompleted() {
    return ((double) (reportTotal + reportNum) / (double) reportTarget) * 100;
  }

  private int getAvailableMemory() {
    return (int)
        ((rt.maxMemory() - rt.totalMemory() + rt.freeMemory())
            / 1048576); // 1024*1024 = 1048576 (bytes in 1 MB)
  }
}

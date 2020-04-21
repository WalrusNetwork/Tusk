package network.walrus.games.core.util;

import co.aikar.timings.Timing;
import co.aikar.timings.Timings;
import network.walrus.games.core.GamesPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

/**
 * Helpful wrapper for {@link BukkitRunnable}.
 *
 * @author Austin Mayes
 */
public class GameTask extends BukkitRunnable {

  private final Runnable runnable;
  private final String id;
  private final Timing tickTimer;
  private BukkitTask task;

  /**
   * Constructor with no task.
   *
   * @param id of the task used for timings
   */
  public GameTask(String id) {
    this.runnable = null;
    this.id = id;
    this.tickTimer = Timings.of(GamesPlugin.instance, this.id + " tick");
  }

  /**
   * Constructor.
   *
   * @param runnable to execute
   * @param id of the task used for timings
   */
  public GameTask(String id, Runnable runnable) {
    this.id = id;
    this.runnable = runnable;
    this.tickTimer = Timings.of(GamesPlugin.instance, this.id + " tick");
  }

  /**
   * Create a new task using the supplied runnable.
   *
   * @param id of the task used for timings
   * @param runnable to execute with the task
   * @return task that will execute the runnable
   */
  public static GameTask of(String id, Runnable runnable) {
    return new GameTask(id, runnable);
  }

  @Override
  public void run() {
    if (this.runnable != null) {
      try (Timing timing = tickTimer.startClosable()) {
        this.runnable.run();
      }
    } else {
      throw new RuntimeException("AtlasTask not implemented!");
    }
  }

  /**
   * Run the enclosed runnable right now on the main server thread.
   *
   * @return the executed task
   */
  public GameTask now() {
    this.createBukkitRunnable().runTask(GamesPlugin.instance);
    return this;
  }

  /**
   * Run the enclosed runnable right now on a secondary thread.
   *
   * @return the executed task
   */
  public GameTask nowAsync() {
    this.task = this.createBukkitRunnable().runTaskAsynchronously(GamesPlugin.instance);
    return this;
  }

  /**
   * Run the enclosed runnable after the specified delay on the main server thread.
   *
   * @param ticksDelay delay (in ticks) before the enclosed runnable is executed
   * @return the task, which will be executed at a later time
   */
  public GameTask later(int ticksDelay) {
    this.task = this.createBukkitRunnable().runTaskLater(GamesPlugin.instance, ticksDelay);
    return this;
  }

  /**
   * Run the enclosed runnable after the specified delay on a secondary thread.
   *
   * @param ticksDelay delay (in ticks) before the enclosed runnable is executed
   * @return the task, which will be executed at a later time
   */
  public GameTask laterAsync(int ticksDelay) {
    this.task =
        this.createBukkitRunnable().runTaskLaterAsynchronously(GamesPlugin.instance, ticksDelay);
    return this;
  }

  /**
   * Execute the enclosed runnable on the main server thread at a fixed rate after an initial delay.
   *
   * @param ticksDelay delay (in ticks) before the enclosed runnable is initially executed
   * @param ticksInterval delay (in ticks) between each execution after the initial execution
   * @return the task, which will be repeated at a later time
   */
  public GameTask repeat(int ticksDelay, int ticksInterval) {
    this.task =
        this.createBukkitRunnable().runTaskTimer(GamesPlugin.instance, ticksDelay, ticksInterval);
    return this;
  }

  /**
   * Execute the enclosed runnable on a secondary thread at a fixed rate after an initial delay.
   *
   * @param ticksDelay delay (in ticks) before the enclosed runnable is initially executed
   * @param ticksInterval delay (in ticks) between each execution after the initial execution
   * @return the task, which will be repeated at a later time
   */
  public GameTask repeatAsync(int ticksDelay, int ticksInterval) {
    this.task =
        this.createBukkitRunnable()
            .runTaskTimerAsynchronously(GamesPlugin.instance, ticksDelay, ticksInterval);
    return this;
  }

  /** Method called when the task is ended */
  protected void onEnd() {}

  /**
   * If the task is running, cancel it and clear the task ID. This method must be called for the
   * instance to be re-used.
   *
   * @return if a running task was canceled
   */
  public boolean reset() {
    if (this.task == null) {
      return false;
    }
    try (Timing timing = Timings.ofStart(GamesPlugin.instance, this.id + " reset")) {
      this.task.cancel();
      onEnd();
    }
    this.task = null;
    return true;
  }

  private BukkitRunnable createBukkitRunnable() {
    if (this.task != null) {
      throw new IllegalStateException(
          "Cannot restart GameTask (existing: " + this.task.getTaskId() + ")");
    }

    return this;
  }
}

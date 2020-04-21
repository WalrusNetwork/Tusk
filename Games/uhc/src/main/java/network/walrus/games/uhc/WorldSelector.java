package network.walrus.games.uhc;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;

/**
 * Helper class to filter through generated worlds and pick one that should be used for a game.
 *
 * @author Austin Mayes
 */
class WorldSelector {
  private final File worldsRoot;
  private final Multimap<String, World> allWorlds = HashMultimap.create();
  private final Multimap<String, World> unusedWorlds = HashMultimap.create();

  /** @param worldsRoot location of generator section directories */
  WorldSelector(File worldsRoot) {
    Validate.isTrue(worldsRoot.exists(), "Worlds root does not exist!");
    this.worldsRoot = worldsRoot;
    gatherWorlds();
  }

  /**
   * Pick the most usable world using the supplied paramaters
   *
   * @param prefix of the desired world type
   * @param nether if a nether world is required
   * @param end if an end world is required
   */
  void selectWorld(String prefix, boolean nether, boolean end) {
    Bukkit.getLogger().info("Selecting world...");
    Collection<World> worlds = new ArrayList<>(unusedWorlds.get(prefix));
    worlds.removeIf(w -> (nether && !w.nether.isPresent()) || (end && !w.end.isPresent()));

    if (worlds.isEmpty()) {
      Bukkit.getLogger()
          .warning("No fresh worlds found for " + prefix + "! Falling back to used worlds.");
      worlds = new ArrayList<>(allWorlds.get(prefix));
      worlds.removeIf(w -> (nether && !w.nether.isPresent()) || (end && !w.end.isPresent()));
    }

    if (worlds.isEmpty()) throw new IllegalStateException("No worlds found for prefix: " + prefix);

    Optional<World> found = Optional.empty();
    long padding = (int) (worlds.size() * Math.random());
    for (World world : worlds) {
      if (padding > 0) {
        padding--;
        continue;
      }
      found = Optional.of(world);
      break;
    }
    World chosen = found.get();
    Bukkit.getLogger().info("Chose " + chosen.overWorld.getName());
    chosen.copy();
    chosen.markUsed();
  }

  private void gatherWorlds() {
    for (File generationRun : Objects.requireNonNull(worldsRoot.listFiles(File::isDirectory))) {
      if (!generationRun.getName().contains("generated")) continue;
      File[] worlds = generationRun.listFiles(File::isDirectory);
      if (worlds == null) continue;
      for (File world : worlds) {
        if (!world.isDirectory()) continue;
        if (world.getName().contains("nether") || world.getName().contains("end")) continue;
        if (new File(world, "generating.lock").exists()) continue;
        String prefix = world.getName().split("_")[0];
        int num = Integer.valueOf(world.getName().split("_")[1]);
        Optional<File> nether = Optional.empty();
        for (File f : worlds) {
          if (f.getName().equalsIgnoreCase(prefix + "_nether_" + num)) {
            nether = Optional.of(f);
            break;
          }
        }
        Optional<File> end = Optional.empty();
        for (File f : worlds) {
          if (f.getName().equalsIgnoreCase(prefix + "_end_" + num)) {
            end = Optional.of(f);
            break;
          }
        }
        allWorlds.put(prefix, new World(world, nether, end));
      }
    }
    for (Entry<String, World> e : allWorlds.entries()) {
      if (!e.getValue().used) unusedWorlds.put(e.getKey(), e.getValue());
    }
    Bukkit.getLogger().info(allWorlds.entries().size() + " worlds found");
    Bukkit.getLogger().info(unusedWorlds.entries().size() + " worlds are fresh");
  }

  /** Simple data class to represent a generated world */
  private static class World {
    private final File overWorld;
    private final Optional<File> nether;
    private final Optional<File> end;
    private final boolean used;

    World(File overWorld, Optional<File> nether, Optional<File> end) {
      this.overWorld = overWorld;
      this.nether = nether;
      this.end = end;
      this.used = new File(overWorld, "used.cache").exists();
    }

    /** Copy all needed files to the server directory */
    void copy() {
      try {
        FileUtils.copyDirectory(
            overWorld,
            Paths.get(Bukkit.getWorldContainer().getAbsolutePath(), "uhc", "over-world").toFile());
        if (nether.isPresent()) {
          File dest = new File(Bukkit.getWorldContainer(), "world_nether");
          FileUtils.deleteDirectory(dest);
          FileUtils.copyDirectory(nether.get(), dest);
        }
        if (end.isPresent()) {
          File dest = new File(Bukkit.getWorldContainer(), "world_the_end");
          FileUtils.deleteDirectory(dest);
          FileUtils.copyDirectory(end.get(), dest);
        }
      } catch (IOException e) {
        Bukkit.getLogger()
            .log(Level.SEVERE, "Failed to copy files for " + overWorld.getName() + "!", e);
      }
    }

    /**
     * Mark the world as used si that other servers will potentially ignore it when picking worlds
     */
    void markUsed() {
      if (this.used) return;

      try {
        new File(overWorld, "used.cache").createNewFile();
      } catch (IOException e) {
        Bukkit.getLogger()
            .log(Level.SEVERE, "Failed to mark " + overWorld.getName() + " as used!", e);
      }
    }
  }
}

package network.walrus.uhcworldgen;

import com.google.common.collect.Lists;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import network.walrus.utils.bukkit.world.GeneratorSettingsBuilder;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerSuspendEvent;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Task used to determine if worlds have suitable seeds for UHC games.
 *
 * @author SmellyPenguin
 * @author Austin Mayes
 */
public class GoodSeedTask extends BukkitRunnable implements Listener {

  /** Builder for custom generation settings for over-worlds */
  private final GeneratorSettingsBuilder generatorSettingsBuilder;
  /** Container for all generated worlds */
  private final File worldContainer;
  /** Parsed world data */
  private final List<ConfiguredWorldData> data = Lists.newArrayList();
  /**
   * Task used to dynamically come out of server suspensions when new world generators are
   * available.
   */
  private final Runnable serverResumer = this::resumeOnWork;
  /** Currently queued world generation tasks */
  private final List<WorldFillTask> tasks = Lists.newArrayList();
  /** Worlds to save */
  /** Number of concurrent generations to run at the same time */
  private final int MAX_CONCURRENT_GENS = 9;
  /** Number of generating worlds */
  private AtomicInteger generating = new AtomicInteger(0);
  /** If pre-gen is in progress */
  private boolean preGen = false;

  /**
   * @param generatorSettingsBuilder for custom generation settings for over-worlds
   * @param worldContainer for all generated worlds
   * @param data used to configure what to generate
   */
  GoodSeedTask(
      GeneratorSettingsBuilder generatorSettingsBuilder,
      File worldContainer,
      List<ConfiguredWorldData> data) {
    this.generatorSettingsBuilder = generatorSettingsBuilder;
    this.worldContainer = worldContainer;
    this.data.addAll(data);
    Bukkit.unloadWorld("world", false);
  }

  private static BiomeThreshold determineBiomeThreshold(Biome biome, int i, int j) {
    boolean inCenter = i <= 100 && i >= -100 && j <= 100 && j >= -100;
    if (biome == Biome.DESERT
        || biome == Biome.DESERT_HILLS
        || biome == Biome.DESERT_MOUNTAINS
        || biome == Biome.PLAINS
        || biome == Biome.SUNFLOWER_PLAINS
        || biome == Biome.SWAMPLAND
        || biome == Biome.SWAMPLAND_MOUNTAINS
        || biome == Biome.SMALL_MOUNTAINS
        || biome == Biome.SAVANNA
        || biome == Biome.SAVANNA_MOUNTAINS
        || biome == Biome.SAVANNA_PLATEAU
        || biome == Biome.SAVANNA_PLATEAU_MOUNTAINS
        || biome == Biome.RIVER
        || biome == Biome.FROZEN_RIVER
        || biome == Biome.ICE_PLAINS) {
      return BiomeThreshold.ALLOWED;
    } else if (inCenter
        && (biome == Biome.FOREST
            || biome == Biome.FOREST_HILLS
            || biome == Biome.BIRCH_FOREST
            || biome == Biome.BIRCH_FOREST_HILLS
            || biome == Biome.BIRCH_FOREST_HILLS_MOUNTAINS
            || biome == Biome.BIRCH_FOREST_MOUNTAINS
            || biome == Biome.TAIGA
            || biome == Biome.TAIGA_HILLS
            || biome == Biome.TAIGA_MOUNTAINS
            || biome == Biome.ICE_PLAINS_SPIKES
            || biome == Biome.MEGA_SPRUCE_TAIGA
            || biome == Biome.MEGA_SPRUCE_TAIGA_HILLS
            || biome == Biome.MEGA_TAIGA
            || biome == Biome.MEGA_TAIGA_HILLS
            || biome == Biome.FLOWER_FOREST
            || biome == Biome.COLD_BEACH
            || biome == Biome.COLD_TAIGA
            || biome == Biome.COLD_TAIGA_HILLS
            || biome == Biome.COLD_TAIGA_MOUNTAINS)) {
      return BiomeThreshold.LIMITED;
    } else if (inCenter
        && (biome == Biome.ROOFED_FOREST
            || biome == Biome.ROOFED_FOREST_MOUNTAINS
            || biome == Biome.MESA
            || biome == Biome.MESA_PLATEAU
            || biome == Biome.MESA_BRYCE
            || biome == Biome.MESA_PLATEAU_FOREST
            || biome == Biome.MESA_PLATEAU_FOREST_MOUNTAINS
            || biome == Biome.MESA_PLATEAU_MOUNTAINS
            || biome == Biome.EXTREME_HILLS
            || biome == Biome.EXTREME_HILLS_MOUNTAINS
            || biome == Biome.EXTREME_HILLS_PLUS
            || biome == Biome.EXTREME_HILLS_PLUS_MOUNTAINS
            || biome == Biome.FROZEN_OCEAN
            || biome == Biome.ICE_MOUNTAINS)) {
      return BiomeThreshold.DISALLOWED;
    }

    // We are only picky at 0,0 otherwise stuff is allowed
    return inCenter ? BiomeThreshold.DISALLOWED : BiomeThreshold.ALLOWED;
  }

  private void resumeOnWork() {
    if (!Bukkit.getServer().isSuspended()) {
      return;
    }

    Bukkit.getLogger().info("Checking to un-suspend");

    boolean anyActive = false;
    for (ConfiguredWorldData datum : data) {
      if (datum.shouldBeActive()) {
        anyActive = true;
        break;
      }
    }
    if (anyActive) {
      Bukkit.getServer().setSuspended(false);
      return;
    }

    ((CraftServer) Bukkit.getServer()).getServer().addMainThreadTask(serverResumer);
  }

  private int totalTodo() {
    int sum = 0;
    for (ConfiguredWorldData c : data) {
      int toDo = c.toDo;
      sum += toDo;
    }
    return sum;
  }

  /** Handle suspensions */
  @EventHandler
  public void onSuspend(ServerSuspendEvent event) {
    if (generating.get() > 0) Bukkit.getServer().setSuspended(false);
    else ((CraftServer) Bukkit.getServer()).getServer().addMainThreadTask(serverResumer);
  }

  private void updateCounts() {
    for (ConfiguredWorldData configuredWorldData : new ArrayList<>(data)) {
      int count = 0;
      for (int i = 1; i <= configuredWorldData.count; i++) {
        File world = new File(worldContainer, configuredWorldData.prefix + "_" + i);
        File nether = new File(worldContainer, configuredWorldData.prefix + "_nether_" + i);
        File end = new File(worldContainer, configuredWorldData.prefix + "_end_" + i);

        if (world.exists()
                && (!configuredWorldData.nether || nether.exists())
                && (!configuredWorldData.end)
            || end.exists()) {
          count++;
        } else {
          world.delete();
          nether.delete();
          end.delete();
        }
      }

      configuredWorldData.toDo = configuredWorldData.count - count;
      if (configuredWorldData.toDo <= 0) {
        this.data.remove(configuredWorldData);
        Bukkit.getLogger()
            .info(
                "all worlds for "
                    + configuredWorldData.prefix
                    + " have been generated... Removing");
      } else
        Bukkit.getLogger()
            .info(configuredWorldData.prefix + " has " + configuredWorldData.toDo + " remaining");
    }
  }

  @Override
  public void run() {
    // Pre generating or above generation threshold
    if (generating.get() > 0 || preGen) return;

    // Nothing to do and not generating
    if (totalTodo() < 1 && generating.get() < 1) {
      Bukkit.getLogger().info("All done!");
      Bukkit.shutdown();
      return;
    }

    updateCounts();

    if (data.isEmpty()) {
      Bukkit.getLogger().info("All done!");
      Bukkit.shutdown();
      return;
    }

    List<ConfiguredWorldData> activeData = new ArrayList<>();
    for (ConfiguredWorldData datum : data) {
      if (datum.shouldBeActive()) {
        activeData.add(datum);
      }
    }

    if (activeData.isEmpty()) {
      Bukkit.getLogger().info("No active world generators, pausing");
      return;
    }

    activeData.sort(
        (d1, d2) -> {
          if (d1.priority == d2.priority) return Integer.compare(d1.toDo, d2.toDo);
          else return Integer.compare(d1.priority, d2.priority);
        });

    ConfiguredWorldData data = activeData.get(0);
    int count = (data.count - data.toDo) + 1;
    generateNewWorld(data, count);
  }

  private void generateNewWorld(ConfiguredWorldData data, int suffix) {
    preGen = true;
    String newWorldName =
        Paths.get(worldContainer.getName(), data.prefix + "_" + suffix).toString();
    String newNetherName =
        Paths.get(worldContainer.getName(), data.prefix + "_nether_" + suffix).toString();
    String newEndName =
        Paths.get(worldContainer.getName(), data.prefix + "_end_" + +suffix).toString();

    WorldCreator worldCreator = new WorldCreator(newWorldName);
    worldCreator.generatorSettings(generatorSettingsBuilder.build());
    worldCreator.setCustomGenSettings(new UHCGenerationSettings());
    World world = WorldGenPlugin.instance.getServer().createWorld(worldCreator);

    int waterCount = 0;

    boolean worldInvalid = false;
    int limitedCount = 0;
    for (int i = -data.size; i <= data.size; ++i) {
      boolean sliceInvalid = false;
      for (int j = -data.size; j <= data.size; j++) {
        Biome biome = world.getBiome(i, j);

        BiomeThreshold threshold = determineBiomeThreshold(biome, i, j);
        if (threshold == BiomeThreshold.DISALLOWED) {
          sliceInvalid = true;
          Bukkit.getLogger().info("Found disallowed biome");
          break;
        } else if (threshold == BiomeThreshold.LIMITED) {
          if (++limitedCount >= 16000) {
            sliceInvalid = true;
            Bukkit.getLogger().info("Hit biome threshold");
            break;
          }
        }

        boolean isCenter = i >= -100 && i <= 100 && j >= -100 && j <= 100;
        if (isCenter) {
          Block block = world.getHighestBlockAt(i, j).getLocation().add(0, -1, 0).getBlock();
          if (block.getType() == Material.STATIONARY_WATER
              || block.getType() == Material.WATER
              || block.getType() == Material.LAVA
              || block.getType() == Material.STATIONARY_LAVA) {
            ++waterCount;
          }
        }

        if (waterCount >= 1000) {
          sliceInvalid = true;
          Bukkit.getLogger().info("Too much water around center");
          break;
        }
      }

      if (sliceInvalid) {
        worldInvalid = true;
        break;
      }
    }

    if (worldInvalid) {
      Bukkit.getLogger().info("Failed to find a good seed (" + world.getSeed() + ").");
      Bukkit.getServer().unloadWorld(world, false);
      try {
        FileUtils.deleteDirectory(new File(newWorldName));
      } catch (IOException e) {
        e.printStackTrace();
      }
      generateNewWorld(data, suffix);
      preGen = false;
      return;
    } else {
      Bukkit.getLogger().info("Found a good seed (" + world.getSeed() + ").");
    }

    generating.incrementAndGet();
    // Create Lock
    data.toDo--;
    tasks.add(makeTask(world, data));
    if (data.nether) {
      WorldCreator netherCreator = new WorldCreator(newNetherName).environment(Environment.NETHER);
      World nether = WorldGenPlugin.instance.getServer().createWorld(netherCreator);
      tasks.add(makeTask(nether, data.cloneWithSizeReduction(.14)));
      generating.incrementAndGet();
    }
    if (data.end) {
      WorldCreator endCreator = new WorldCreator(newEndName).environment(Environment.THE_END);
      World end = WorldGenPlugin.instance.getServer().createWorld(endCreator);
      tasks.add(makeTask(end, data.cloneWithSize(Math.max(data.size * 2 / 10, 1000))));
      generating.incrementAndGet();
    }
    preGen = false;

    try {
      Thread.sleep(3000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    if (data.toDo > 0 && generating.get() <= MAX_CONCURRENT_GENS)
      generateNewWorld(data, suffix + 1);
    else {
      for (WorldFillTask task : tasks) {
        task.start();
      }
      tasks.clear();
    }
  }

  private WorldFillTask makeTask(World world, ConfiguredWorldData data) {
    makeLockFile(world.getName());
    world.setAutoSave(true);
    return new WorldFillTask(
        Bukkit.getServer(),
        world,
        data,
        () -> {},
        () -> {
          deleteLock(world.getName());
          this.generating.decrementAndGet();
        });
  }

  private void makeLockFile(String where) {
    File lock =
        Paths.get(Bukkit.getWorldContainer().getAbsolutePath(), where, "generating.lock").toFile();
    try {
      lock.createNewFile();
    } catch (IOException e) {
      e.printStackTrace();
      Bukkit.shutdown();
    }
  }

  private void deleteLock(String where) {
    File lock =
        Paths.get(Bukkit.getWorldContainer().getAbsolutePath(), where, "generating.lock").toFile();
    try {
      lock.delete();
    } catch (SecurityException e) {
      e.printStackTrace();
      Bukkit.shutdown();
    }
  }

  public enum BiomeThreshold {
    DISALLOWED,
    LIMITED,
    ALLOWED
  }
}

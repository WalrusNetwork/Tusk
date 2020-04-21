package network.walrus.uhcworldgen;

import com.google.common.collect.Lists;
import java.io.File;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.List;
import net.minecraft.server.v1_8_R3.BiomeBase;
import network.walrus.utils.bukkit.NMSUtils;
import network.walrus.utils.bukkit.WalrusBukkitPlugin;
import network.walrus.utils.bukkit.world.GeneratorSettingsBuilder;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;

/**
 * Main class for the plugin which handles all duels.
 *
 * @author Austin Mayes
 */
public class WorldGenPlugin extends WalrusBukkitPlugin {

  static WorldGenPlugin instance;
  private final GeneratorSettingsBuilder generatorSettingsBuilder = new GeneratorSettingsBuilder();

  private static void deleteBrokenWorlds(File holder, ConfiguredWorldData data) {
    if (data.count == 0) return;
    for (int i = 1; i <= data.count; i++) {
      File world = new File(holder, data.prefix + "_" + i);
      File nether = new File(holder, data.prefix + "_nether_" + i);
      File end = new File(holder, data.prefix + "_end_" + i);

      if (!wasCompleted(data, world, end, nether)) {
        try {
          Bukkit.getLogger()
              .info(world.getCanonicalPath() + " was not fully generated! Deleting...");
          FileUtils.deleteDirectory(world);
          FileUtils.deleteDirectory(nether);
          FileUtils.deleteDirectory(end);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  private static boolean wasCompleted(ConfiguredWorldData data, File wold, File nether, File end) {
    if (!wold.exists()) return false;
    if (data.nether && !nether.exists()) return false;
    if (data.end && !end.exists()) return false;
    File worldLock = new File(wold, "generating.lock");
    File netherLock = new File(nether, "generating.lock");
    File endLock = new File(end, "generating.lock");
    if (worldLock.exists()) return false;
    if (data.nether && netherLock.exists()) return false;
    if (data.end && endLock.exists()) return false;
    return true;
  }

  @Override
  public void load() {
    instance = this;
  }

  @Override
  public void enable() {
    ;
    if (getConfig().getBoolean("replace-biomes", true)) replaceBiomes();

    generatorSettingsBuilder
        .dungeonChance(10)
        .graniteCount(0)
        .dioriteCount(0)
        .andesiteCount(0)
        .riverSize(5);

    List<ConfiguredWorldData> data = parseData();

    File worldHolder = new File(Bukkit.getWorldContainer(), "generated-worlds");
    if (worldHolder.exists()) {
      File copyWorld = worldHolder;
      for (int num = 1; copyWorld.exists(); num++) {
        copyWorld =
            new File(Bukkit.getWorldContainer(), "generated-worlds" + (num == 1 ? "" : "-" + num));
        for (ConfiguredWorldData d : data) {
          deleteBrokenWorlds(copyWorld, d);
        }
      }
      try {
        FileUtils.moveDirectory(worldHolder, copyWorld);
      } catch (IOException e) {
        e.printStackTrace();
        Bukkit.shutdown();
      }
    }
    GoodSeedTask task = new GoodSeedTask(generatorSettingsBuilder, worldHolder, data);
    getServer().getScheduler().scheduleSyncRepeatingTask(this, task, 0, 20);
    getServer().getPluginManager().registerEvents(task, this);
  }

  private void replaceBiomes() {
    try {
      NMSUtils.replaceBiome(BiomeBase.OCEAN, BiomeBase.PLAINS);
      NMSUtils.replaceBiome(BiomeBase.FROZEN_OCEAN, BiomeBase.ICE_PLAINS);
      NMSUtils.replaceBiome(BiomeBase.DEEP_OCEAN, BiomeBase.FOREST);
      NMSUtils.replaceBiome(BiomeBase.BEACH, BiomeBase.PLAINS);
      NMSUtils.replaceBiome(BiomeBase.JUNGLE, BiomeBase.TAIGA);
      NMSUtils.replaceBiome(BiomeBase.JUNGLE_HILLS, BiomeBase.TAIGA_HILLS);
      NMSUtils.replaceBiome(BiomeBase.JUNGLE_EDGE, BiomeBase.TAIGA);
      NMSUtils.replaceBiome(BiomeBase.STONE_BEACH, BiomeBase.PLAINS);
      NMSUtils.replaceBiome(BiomeBase.COLD_BEACH, BiomeBase.ICE_PLAINS);
      NMSUtils.replaceBiome(BiomeBase.MESA_PLATEAU, BiomeBase.TAIGA);
      NMSUtils.replaceBiome(BiomeBase.EXTREME_HILLS, BiomeBase.FOREST_HILLS);
      NMSUtils.replaceBiome(BiomeBase.EXTREME_HILLS_PLUS, BiomeBase.TAIGA_HILLS);
      NMSUtils.replaceBiome(BiomeBase.DESERT_HILLS, BiomeBase.DESERT);

    } catch (NoSuchFieldException | IllegalAccessException e) {
      e.printStackTrace();
    }
  }

  private List<ConfiguredWorldData> parseData() {
    List<ConfiguredWorldData> data = Lists.newArrayList();
    for (LinkedHashMap<String, Object> generator :
        (List<LinkedHashMap<String, Object>>) getConfig().getList("generators")) {
      String prefix = generator.get("prefix").toString();
      int count = (int) generator.get("count");
      int size = (int) generator.get("size");
      boolean nether = (boolean) generator.get("nether");
      boolean end = (boolean) generator.get("end");
      LocalTime start = LocalTime.of((int) generator.get("start"), 0);
      LocalTime stop = LocalTime.of((int) generator.get("stop"), 0);
      int priority = (int) generator.get("priority");
      List<DayOfWeek> days = Lists.newArrayList();
      for (String day : (List<String>) generator.get("days")) {
        days.add(DayOfWeek.valueOf(day.toUpperCase()));
      }
      data.add(
          new ConfiguredWorldData(
              prefix,
              count,
              size,
              nether,
              end,
              start,
              stop,
              priority,
              days.toArray(new DayOfWeek[] {})));
    }
    return data;
  }

  @Override
  public void disable() {}
}

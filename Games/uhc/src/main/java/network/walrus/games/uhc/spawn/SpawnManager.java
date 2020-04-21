package network.walrus.games.uhc.spawn;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import network.walrus.games.core.facets.group.Competitor;
import network.walrus.games.core.util.GameTask;
import network.walrus.games.uhc.UHCManager;
import network.walrus.games.uhc.facets.combatlog.CombatLogTracker;
import network.walrus.utils.bukkit.PlayerUtils;
import network.walrus.utils.bukkit.block.CoordXZ;
import network.walrus.utils.bukkit.region.PositionUnavailableException;
import network.walrus.utils.parsing.lobby.facets.spawns.LobbySpawnManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

/**
 * Class which manages spawns for UHC.
 *
 * @author Austin Mayes
 */
public class SpawnManager implements Listener {

  private static final Random RANDOM = new Random();
  private final Map<String, Location> spawns = Maps.newHashMap();
  private CoordXZ lower;
  private CoordXZ upper;
  private World world;
  private double safeSquared;

  /**
   * Load a set number of chunks in each direction around a set of center points.
   *
   * @param locations to be used as center
   */
  public static void loadSpawn(
      Collection<Location> locations,
      World world,
      int spawnChunksRadius,
      Runnable completionCallback) {
    List<CoordXZ> coords = Lists.newArrayList();
    for (Location center : locations) {
      for (int x = (int) (center.getX() / 16) - spawnChunksRadius;
          x < (int) (center.getX() / 16) + spawnChunksRadius;
          x++) {
        for (int z = (int) (center.getZ() / 16) - spawnChunksRadius;
            z < (int) (center.getZ() / 16) + spawnChunksRadius;
            z++) {
          if (!center.getWorld().isChunkLoaded(x, z)) {
            coords.add(new CoordXZ(x, z));
          }
        }
      }
    }
    GameTask.of(
            "Spawn load",
            () -> {
              List<List<CoordXZ>> partitions = Lists.partition(coords, 400);
              final int total = partitions.size() * 400;
              AtomicInteger loaded = new AtomicInteger();
              AtomicBoolean ready = new AtomicBoolean(false);
              for (List<CoordXZ> coord : partitions) {
                GameTask.of(
                        "Spawn load chunk",
                        () -> {
                          Bukkit.getLogger()
                              .info(
                                  "================= LOADING "
                                      + coord.size()
                                      + " chunks. =================");
                          ready.set(false);
                          for (CoordXZ chunk : coord) {
                            world.loadChunk(chunk.x, chunk.z);
                            loaded.incrementAndGet();
                          }
                          for (CoordXZ chunk : coord) {
                            world.unloadChunk(chunk.x, chunk.z);
                          }
                          ready.set(true);
                        })
                    .now();
                try {
                  Thread.sleep(1000);
                } catch (InterruptedException e) {
                  e.printStackTrace();
                }
                Bukkit.getLogger()
                    .info(
                        "================= "
                            + (total - loaded.get())
                            + " remaining =================");
                while (!ready.get()) {
                  try {
                    Thread.sleep(100);
                  } catch (InterruptedException e) {
                    e.printStackTrace();
                  }
                }
              }
              GameTask.of("Spawn callback", completionCallback).now();
            })
        .nowAsync();
  }

  /**
   * Pre-determine locations where competitors should be teleported to during the scatter.
   *
   * @param comps to create spawns for
   * @param center of the scatter area
   */
  public void populateSpawns(List<Competitor> comps, Location center, Runnable loadCallback) {
    int initialBorder = UHCManager.instance.getConfig().initialBorder.get();
    int lowerX = center.getBlockX() - (initialBorder / 2) - 1;
    int upperX = center.getBlockX() + (initialBorder / 2);
    int lowerZ = center.getBlockZ() - (initialBorder / 2) - 1;
    int upperZ = center.getBlockZ() + (initialBorder / 2);
    lower = new CoordXZ(lowerX, lowerZ);
    upper = new CoordXZ(upperX, upperZ);
    this.world = center.getWorld();
    Set<Location> selected = Sets.newHashSet();
    // Find largest radius of n circles which could fit in border and make them fit approximately
    // within border
    safeSquared = ((initialBorder * initialBorder) / (double) comps.size()) * (9.0 / 16.0);
    for (Competitor comp : comps) {
      Location spawn =
          selectSafeLocation(lower, upper, center.getWorld(), safeSquared, selected, 0);
      selected.add(spawn);
      spawns.put(comp.id(), spawn);
    }
    loadSpawn(spawns.values(), center.getWorld(), 2, loadCallback);
  }

  private Location selectSafeLocation(
      CoordXZ lowerBound,
      CoordXZ upperBound,
      World world,
      double minDistanceSquared,
      Collection<Location> others,
      int attempts) {
    int x = RANDOM.nextInt((upperBound.x - lowerBound.x) + 1) + lowerBound.x;
    int z = RANDOM.nextInt((upperBound.z - lowerBound.z) + 1) + lowerBound.z;
    int y = world.getHighestBlockYAt(x, z);
    Location selected = new Location(world, x, y, z);
    if ((isUnsafe(selected) || isTooClose(selected, others, minDistanceSquared)) && attempts < 5) {
      return selectSafeLocation(
          lowerBound, upperBound, world, minDistanceSquared, others, ++attempts);
    } else return selected;
  }

  private boolean isTooClose(
      Location location, Collection<Location> others, double minDistanceSquared) {
    for (Location other : others) {
      if (location.distanceSquared(other) < minDistanceSquared) return true;
    }
    return false;
  }

  private boolean isUnsafe(Location location) {
    for (int x = -2; x < 2; x++) {
      if (isObstructed(location.clone().add(x, 0, 0))) {
        return true;
      }
    }
    for (int y = -2; y < 2; y++) {
      if (isObstructed(location.clone().add(0, y, 0))) {
        return true;
      }
    }
    for (int z = -2; z < 2; z++) {
      if (isObstructed(location.clone().add(0, 0, z))) {
        return true;
      }
    }
    return false;
  }

  private boolean isObstructed(Location location) {
    Material type = location.getBlock().getType();
    switch (type) {
      case LAVA:
      case STATIONARY_LAVA:
      case WATER:
      case STATIONARY_WATER:
      case LEAVES:
      case LEAVES_2:
      case CACTUS:
        return true;
    }
    return false;
  }

  /**
   * Teleport all players in a competitor to the pre-determined location.
   *
   * @param competitor to spawn
   */
  public void spawn(Competitor competitor) {
    Location location = spawns.get(competitor.id());
    if (location == null) {
      throw new IllegalStateException("Competitor has no spawn!");
    }

    for (Player player : competitor.getPlayers()) {
      player.teleport(location);
    }

    UHCManager.instance
        .getUHC()
        .getFacetRequired(CombatLogTracker.class)
        .spawn(competitor, location);
  }

  /**
   * Teleport all players in a competitor to a spawn point, calculating one if it doens't exist.
   *
   * @param comp to spawn
   */
  public void forceSpawn(Competitor comp) {
    Location spawn = spawns.get(comp.id());
    if (spawn == null) {
      spawn = selectSafeLocation(lower, upper, world, safeSquared / 2, spawns.values(), 0);
      spawns.put(comp.id(), spawn);
    }

    for (Player player : comp.getPlayers()) {
      if (!player.getWorld().equals(world)) {
        player.teleport(spawn);
      }
    }

    UHCManager.instance.getUHC().getFacetRequired(CombatLogTracker.class).spawn(comp, spawn);
  }

  /**
   * Spawn a player at the lobby spawn point.
   *
   * @param player to spawn
   */
  public void spawn(Player player) {
    PlayerUtils.reset(player);
    try {
      Vector spawn =
          UHCManager.instance
              .getLobbyLoader()
              .getLobby()
              .getFacetRequired(LobbySpawnManager.class)
              .getSpawn()
              .getRandomPosition(RANDOM);
      player.teleport(
          spawn.toLocation(
              UHCManager.instance.getLobbyLoader().getLobby().getContainer().mainWorld()));
    } catch (PositionUnavailableException e) {
      e.printStackTrace();
    }
  }
}

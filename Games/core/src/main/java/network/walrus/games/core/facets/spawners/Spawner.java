package network.walrus.games.core.facets.spawners;

import com.google.common.collect.Sets;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import network.walrus.games.core.GamesPlugin;
import network.walrus.games.core.facets.filters.context.FilterContext;
import network.walrus.games.core.facets.filters.variable.PlayerVariable;
import network.walrus.utils.bukkit.region.PositionUnavailableException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 * A spawner itself, controls when items spawn and where they spawn
 *
 * @author Matthew Arnold
 */
class Spawner {

  private static final Random RANDOM = new Random();

  private final SpawnerFacet facet;
  private final SpawnerOptions spawnerOptions;
  private final Set<UUID> playersInSpawner;
  private long lastSpawnedTick = 0;

  /**
   * @param facet facet containing this spawner
   * @param spawnerOptions The list of options about the spawner, detailing how it works
   */
  Spawner(SpawnerFacet facet, SpawnerOptions spawnerOptions) {
    this.facet = facet;
    this.spawnerOptions = spawnerOptions;
    this.playersInSpawner = Sets.newHashSet();
  }

  /** Tries to spawn in the items in the spawne */
  void trySpawn(World world) {
    long ticks = GamesPlugin.instance.timer().getTicks();
    if (!hasTimeElapsed(ticks) || !canSpawn()) {
      // Can't spawn, not enough time has passed or no player in region/filter is invalid
      return;
    }

    for (int i = 0; i < spawnerOptions.amount; i++) {
      try {
        Vector vector = spawnerOptions.spawnRegion.getRandomPosition(RANDOM);
        Location dropLocation = new Location(world, vector.getX(), vector.getY(), vector.getZ());
        spawnerOptions.entry.spawn(
            dropLocation, spawnerOptions.velocity.orElse(new Vector(0, 0, 0)));
      } catch (PositionUnavailableException e) {
        GamesPlugin.instance
            .mapLogger()
            .warning("Spawner is using an invalid spawner region " + e.source());
        facet.removeSpawner(this);
        return;
      }
    }

    this.lastSpawnedTick = ticks;
  }

  /**
   * Checks to see if a location is inside the spawner's player region
   *
   * @param location the locaiton to check
   * @return whether or not that location lies within the spawner's player region
   */
  public boolean contains(Location location) {
    return spawnerOptions.playerRegion.contains(location);
  }

  /**
   * Adds a player to the spawner, means that player is now regarded as being in the spawner's
   * region (and therefore can trigger the spawner)
   *
   * @param player the player to add
   */
  public void addPlayer(Player player) {
    playersInSpawner.add(player.getUniqueId());
  }

  /**
   * Removes a player from a spawner, meaning that player can now longer trigger the spawner to
   * spawn
   *
   * @param player the player to remove
   */
  public void removePlayer(Player player) {
    playersInSpawner.remove(player.getUniqueId());
  }

  private boolean canSpawn() {
    for (UUID uuid : playersInSpawner) {
      Player player = Bukkit.getPlayer(uuid);
      if (player != null && isLegalPlayer(player)) {
        return true;
      }
    }

    return false;
  }

  private boolean isLegalPlayer(Player player) {
    FilterContext context = FilterContext.of(new PlayerVariable(player));
    return spawnerOptions.spawnFilter.map(f -> f.test(context).passes()).orElse(true);
  }

  private boolean hasTimeElapsed(long ticks) {
    /* 50 is there because 50 * 20 == 1000, where 20 is the number of ticks in a second
    and 1000 is the number of milli-seconds in a second */
    return (ticks - lastSpawnedTick) * 50 > spawnerOptions.delay.toMillis();
  }
}

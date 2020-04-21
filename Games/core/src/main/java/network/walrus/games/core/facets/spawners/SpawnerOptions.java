package network.walrus.games.core.facets.spawners;

import java.time.Duration;
import java.util.Optional;
import network.walrus.games.core.facets.filters.Filter;
import network.walrus.games.core.facets.spawners.spawneritems.SpawnerEntry;
import network.walrus.utils.bukkit.region.BoundedRegion;
import network.walrus.utils.bukkit.region.Region;
import org.bukkit.util.Vector;

/**
 * User-defined options for {@link Spawner}s.
 *
 * @author Matthew Arnold
 */
class SpawnerOptions {

  final Duration delay;
  final int amount;
  final BoundedRegion spawnRegion;
  final Region playerRegion;
  final SpawnerEntry entry;
  final Optional<Filter> spawnFilter;
  final Optional<Vector> velocity;

  /**
   * @param delay the spawning delay
   * @param amount the amount of items to spawn each entry
   * @param spawnRegion the region items can be spawned in
   * @param playerRegion the region players have to be in for items to spawn
   * @param entry the root spawning entry
   * @param spawnFilter the filter that is tested to see if items can spawn
   * @param velocity velocity being applied to the spawned entity, if applicable
   */
  SpawnerOptions(
      Duration delay,
      int amount,
      BoundedRegion spawnRegion,
      Region playerRegion,
      SpawnerEntry entry,
      Optional<Filter> spawnFilter,
      Optional<Vector> velocity) {
    this.delay = delay;
    this.amount = amount;
    this.spawnRegion = spawnRegion;
    this.playerRegion = playerRegion;
    this.entry = entry;
    this.spawnFilter = spawnFilter;
    this.velocity = velocity;
  }
}

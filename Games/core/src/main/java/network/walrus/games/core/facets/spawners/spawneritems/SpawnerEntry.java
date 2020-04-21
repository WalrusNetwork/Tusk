package network.walrus.games.core.facets.spawners.spawneritems;

import org.bukkit.Location;
import org.bukkit.util.Vector;

/**
 * Represents an item in the spawner (NB: This doesn't actually have to be an item)
 *
 * @author Matthew Arnold
 */
public interface SpawnerEntry {

  /**
   * Spawns items/contents at a specified location
   *
   * @param location the location to spawn things at
   * @param velocity velocity spawned entity should have
   */
  void spawn(Location location, Vector velocity);
}

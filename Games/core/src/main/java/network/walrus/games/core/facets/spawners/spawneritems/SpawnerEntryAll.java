package network.walrus.games.core.facets.spawners.spawneritems;

import java.util.Set;
import org.bukkit.Location;
import org.bukkit.util.Vector;

/**
 * Spawns all child items
 *
 * @author Matthew Arnold
 */
public class SpawnerEntryAll implements SpawnerEntry {

  private final Set<SpawnerEntry> spawnerEntries;

  /** @param spawnerEntries A set of all items to spawn each time this is called */
  public SpawnerEntryAll(Set<SpawnerEntry> spawnerEntries) {
    this.spawnerEntries = spawnerEntries;
  }

  @Override
  public void spawn(Location location, Vector velocity) {
    for (SpawnerEntry spawnerEntry : spawnerEntries) {
      spawnerEntry.spawn(location, velocity);
    }
  }
}

package network.walrus.games.core.facets.spawners.spawneritems;

import java.util.List;
import java.util.Random;
import org.bukkit.Location;
import org.bukkit.util.Vector;

/**
 * Spawns one of the child items
 *
 * @author Matthew Arnold
 */
public class SpawnerEntryAny implements SpawnerEntry {

  private static final Random RANDOM = new Random();
  private final List<SpawnerEntry> spawnerEntries;

  /** @param spawnerEntries A list of possible entries to spawn */
  public SpawnerEntryAny(List<SpawnerEntry> spawnerEntries) {
    this.spawnerEntries = spawnerEntries;
  }

  @Override
  public void spawn(Location location, Vector velocity) {
    int index = RANDOM.nextInt(spawnerEntries.size());
    spawnerEntries.get(index).spawn(location, velocity);
  }
}

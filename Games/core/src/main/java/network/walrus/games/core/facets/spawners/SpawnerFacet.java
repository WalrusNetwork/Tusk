package network.walrus.games.core.facets.spawners;

import com.google.api.client.util.Sets;
import java.util.Set;
import network.walrus.games.core.util.GameTask;
import network.walrus.utils.parsing.facet.Facet;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import org.bukkit.World;
import org.bukkit.event.Listener;

/**
 * Facet which allows users to create spawners... without using spawners
 *
 * <p>Using regions items can be randomly be spawned in with a given cooldown, creating a spawner
 * like system within a facet
 *
 * @author Matthew Arnold
 */
public class SpawnerFacet extends Facet implements Listener {

  private final FacetHolder holder;
  private final Set<Spawner> spawners;
  private final GameTask task;
  private World world;

  /**
   * @param holder facet holder
   * @param spawners The set of active spawners that have been created
   */
  SpawnerFacet(FacetHolder holder, Set<Spawner> spawners) {
    this.holder = holder;
    this.spawners = spawners;
    this.task =
        GameTask.of(
            "spawners",
            () -> {
              if (world == null) {
                world = holder.getContainer().mainWorld();
              }

              for (Spawner spawner : spawners) {
                spawner.trySpawn(world);
              }
            });
  }

  /**
   * Create a new spawner facet with no spawners.
   *
   * @param holder facet holder
   */
  SpawnerFacet(FacetHolder holder) {
    this(holder, Sets.newHashSet());
  }

  /** @return The spawners in this facet */
  public Set<Spawner> spawners() {
    return spawners;
  }

  /**
   * Adds a spawner to the facet.
   *
   * @param spawner to add
   */
  public void addSpawner(Spawner spawner) {
    spawners.add(spawner);
  }

  /**
   * Removes a spawner from the facet.
   *
   * @param spawner to remove
   * @return whether or not the spawner had been added to the facet
   */
  public boolean removeSpawner(Spawner spawner) {
    return spawners.remove(spawner);
  }

  @Override
  public void enable() {
    task.repeat(1, 4);
  }

  @Override
  public void disable() {
    task.reset();
  }
}

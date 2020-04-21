package network.walrus.games.core.facets.spawners.spawneritems;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.util.Vector;

/**
 * Spawns an entity from a spawner
 *
 * @author Matthew Arnold
 */
public class SpawnerEntity implements SpawnerEntry {

  private final EntityType entityType;

  /**
   * Spawns an entity at a specific location
   *
   * @param entityType the type of entity to spawn
   */
  public SpawnerEntity(EntityType entityType) {
    this.entityType = entityType;
  }

  @Override
  public void spawn(Location location, Vector velocity) {
    Entity spawned = location.getWorld().spawnEntity(location, entityType);

    if (spawned instanceof Fireball) {
      Fireball fireball = (Fireball) spawned;
      fireball.setDirection(velocity);
    } else {
      spawned.setVelocity(velocity);
    }
  }
}

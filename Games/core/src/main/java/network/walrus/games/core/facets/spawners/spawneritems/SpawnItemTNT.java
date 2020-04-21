package network.walrus.games.core.facets.spawners.spawneritems;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.util.Vector;

/**
 * Spawns an active tnt, primed
 *
 * @author Matthew Arnold
 */
public class SpawnItemTNT implements SpawnerEntry {

  private final float power;
  private final int fuse;

  /**
   * @param power the power of the tnt, effects the size of explosion
   * @param fuse the fuse of the tnt, effects the fuse time
   */
  public SpawnItemTNT(float power, int fuse) {
    this.power = power;
    this.fuse = fuse;
  }

  @Override
  public void spawn(Location location, Vector velocity) {
    TNTPrimed primed = (TNTPrimed) location.getWorld().spawnEntity(location, EntityType.PRIMED_TNT);
    primed.setFuseTicks(fuse);
    primed.setYield(power);
    primed.setVelocity(velocity);
  }
}

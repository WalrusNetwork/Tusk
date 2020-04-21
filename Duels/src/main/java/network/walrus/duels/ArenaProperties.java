package network.walrus.duels;

import network.walrus.utils.bukkit.region.BoundedRegion;
import network.walrus.utils.bukkit.region.modifiers.BoundedTranslateRegion;
import org.bukkit.util.Vector;

/**
 * Base properties for a {@link Duel}, agnostic of the type of duel that it is.
 *
 * @author Austin Mayes
 */
public class ArenaProperties {

  private final ArenaType type;
  private final BoundedRegion arena;
  private final BoundedRegion specSpawn;
  private final BoundedRegion spawn1;
  private final BoundedRegion spawn2;

  /**
   * @param type of duel these properties are for
   * @param arena region that makes up the entire arena
   * @param specSpawn region where spectators should spawn
   * @param spawn1 region where team 1 should spawn
   * @param spawn2 region where team 2 should spawn
   */
  public ArenaProperties(
      ArenaType type,
      BoundedRegion arena,
      BoundedRegion specSpawn,
      BoundedRegion spawn1,
      BoundedRegion spawn2) {
    this.type = type;
    this.arena = arena;
    this.specSpawn = specSpawn;
    this.spawn1 = spawn1;
    this.spawn2 = spawn2;
  }

  public ArenaType getType() {
    return type;
  }

  public BoundedRegion getArena() {
    return arena;
  }

  public BoundedRegion getSpecSpawn() {
    return specSpawn;
  }

  public BoundedRegion getSpawn1() {
    return spawn1;
  }

  public BoundedRegion getSpawn2() {
    return spawn2;
  }

  /**
   * Create a copy of these properties and apply the given offset to all regions.
   *
   * @param offset to move regions by
   * @return properties with the applied offset
   */
  public ArenaProperties clone(Vector offset) {
    BoundedRegion arena = new BoundedTranslateRegion(this.arena, offset);
    BoundedRegion specSpawn = new BoundedTranslateRegion(this.specSpawn, offset);
    BoundedRegion spawn1 = new BoundedTranslateRegion(this.spawn1, offset);
    BoundedRegion spawn2 = new BoundedTranslateRegion(this.spawn2, offset);
    return new ArenaProperties(type, arena, specSpawn, spawn1, spawn2);
  }
}

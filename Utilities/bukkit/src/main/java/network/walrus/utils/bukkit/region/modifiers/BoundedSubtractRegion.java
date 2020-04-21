package network.walrus.utils.bukkit.region.modifiers;

import java.util.Iterator;
import network.walrus.utils.bukkit.region.BoundedRegion;
import network.walrus.utils.bukkit.region.shapes.CuboidRegion;
import org.bukkit.util.Vector;

/**
 * A {@link SubtractRegion} that only works on {@link BoundedRegion}s.
 *
 * @author Avicus Network
 */
public class BoundedSubtractRegion extends SubtractRegion implements BoundedRegion {

  private BoundedRegion subtracted;

  /**
   * Constructor.
   *
   * @param base region to subtract others from
   * @param subtract to remove from the base
   */
  public BoundedSubtractRegion(BoundedRegion base, BoundedRegion subtract) {
    super(base, subtract);
    boolean cx =
        subtract.min().getX() < base.min().getX() && subtract.max().getX() > base.max().getX();
    boolean cy =
        subtract.min().getY() < base.min().getY() && subtract.max().getY() > base.max().getY();
    boolean cz =
        subtract.min().getZ() < base.min().getZ() && subtract.max().getZ() > base.max().getZ();
    subtracted =
        new CuboidRegion(
            new Vector(
                cy && cz ? Math.max(base.min().getX(), subtract.min().getX()) : base.min().getX(),
                cz && cx ? Math.max(base.min().getY(), subtract.min().getY()) : base.min().getY(),
                cx && cy ? Math.max(base.min().getZ(), subtract.min().getZ()) : base.min().getZ()),
            new Vector(
                cy && cz ? Math.min(base.max().getX(), subtract.max().getX()) : base.max().getX(),
                cz && cx ? Math.min(base.max().getY(), subtract.max().getY()) : base.max().getY(),
                cx && cy ? Math.min(base.max().getZ(), subtract.max().getZ()) : base.max().getZ()));
  }

  /** @return an iterator for all of the vectors in this region */
  @Override
  public Iterator<Vector> iterator() {
    return this.subtracted.iterator();
  }

  @Override
  public Vector min() {
    return this.subtracted.min();
  }

  @Override
  public Vector max() {
    return this.subtracted.max();
  }

  @Override
  public String toString() {
    return "BoundedSubtractRegion{" + "subtracted=" + subtracted + "} " + super.toString();
  }
}

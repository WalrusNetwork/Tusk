package network.walrus.utils.bukkit.region.modifiers;

import java.util.Iterator;
import network.walrus.utils.bukkit.region.BoundedRegion;
import network.walrus.utils.bukkit.region.BoundedRegionIterator;
import org.bukkit.util.Vector;

/**
 * A mirror region which is used to reflect only {@link BoundedRegion}s.
 *
 * @author Rafi Baum
 */
public class BoundedMirrorRegion extends MirrorRegion implements BoundedRegion {

  private final BoundedRegion base;
  private final Vector min;
  private final Vector max;

  /**
   * Constructor.
   *
   * @param base region to be reflected
   * @param origin to reflect vectors on
   * @param normal of the base reflection point
   */
  public BoundedMirrorRegion(BoundedRegion base, Vector origin, Vector normal) {
    super(base, origin, normal);
    this.base = base;

    Vector reflectedMin = reflect(base.min());
    Vector reflectedMax = reflect(base.max());

    this.min = Vector.getMinimum(reflectedMin, reflectedMax);
    this.max = Vector.getMaximum(reflectedMin, reflectedMax);
  }

  @Override
  public Vector min() {
    return min;
  }

  @Override
  public Vector max() {
    return max;
  }

  @Override
  public Iterator<Vector> iterator() {
    return new BoundedRegionIterator(this);
  }

  @Override
  public String toString() {
    return "BoundedMirrorRegion{"
        + "base="
        + base
        + ", min="
        + min
        + ", max="
        + max
        + "} "
        + super.toString();
  }
}

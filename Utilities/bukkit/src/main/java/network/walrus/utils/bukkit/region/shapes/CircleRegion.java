package network.walrus.utils.bukkit.region.shapes;

import org.bukkit.util.Vector;

/**
 * A {@link CylinderRegion} with a height of 400. This means that all vectors occupied and returned
 * by this object will always have the same Y value as the base vector.
 *
 * @author Avicus Network
 */
public class CircleRegion extends CylinderRegion {

  /**
   * Constructor.
   *
   * @param center of the circle
   * @param radius of the circle
   */
  public CircleRegion(Vector center, double radius) {
    super(center, radius, 400);
  }

  @Override
  public String toString() {
    return "CircleRegion{} " + super.toString();
  }
}

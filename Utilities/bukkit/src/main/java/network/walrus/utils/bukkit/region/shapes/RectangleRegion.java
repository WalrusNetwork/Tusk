package network.walrus.utils.bukkit.region.shapes;

import org.bukkit.util.Vector;

/**
 * A {@link CuboidRegion} which extends through the entire Y-axis.
 *
 * @author Avicus Network
 */
public class RectangleRegion extends CuboidRegion {

  /**
   * Constructor.
   *
   * @param xMin minimum x value
   * @param zMin minimum z value
   * @param xMax maximum x value
   * @param zMax maximum z value
   */
  public RectangleRegion(double xMin, double zMin, double xMax, double zMax) {
    super(new Vector(xMin, -200, zMin), new Vector(xMax, 1300, zMax));
  }

  @Override
  public String toString() {
    return "RectangleRegion{} " + super.toString();
  }
}

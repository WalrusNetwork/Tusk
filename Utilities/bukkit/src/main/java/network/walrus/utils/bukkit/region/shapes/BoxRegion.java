package network.walrus.utils.bukkit.region.shapes;

import org.bukkit.util.Vector;

/**
 * Helper region to allow for the creation of {@link CuboidRegion}s using only a center and the
 * supplied X,Y, and Z.
 *
 * @author Avicus Network
 */
public class BoxRegion extends CuboidRegion {

  /**
   * Constructor.
   *
   * @param center of the box
   * @param x amount to move outwards from the center on the X-axis
   * @param y amount to move outwards from the center on the Y-axis
   * @param z amount to move outwards from the center on the Z-axis
   */
  public BoxRegion(Vector center, double x, double y, double z) {
    super(center.clone().subtract(new Vector(x, y, z)), center.clone().add(new Vector(x, y, z)));
  }

  @Override
  public String toString() {
    return "BoxRegion{} " + super.toString();
  }
}

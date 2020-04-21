package network.walrus.utils.bukkit.region.modifiers;

import java.util.Random;
import network.walrus.utils.bukkit.region.BoundedRegion;
import network.walrus.utils.bukkit.region.PositionUnavailableException;
import network.walrus.utils.bukkit.region.Region;
import org.bukkit.util.Vector;

/**
 * Region which is mirrored across one or multiple axes using a normal and origin.
 *
 * <p>Reflection plane equation is: v · normal = offset
 *
 * @author Avicus Network
 */
public class MirrorRegion implements Region {

  private final Region base;
  private final Vector normal;
  private final double offset;

  /**
   * Constructor.
   *
   * @param base region to be reflected
   * @param origin to reflect vectors on
   * @param normal of the base reflection point
   */
  public MirrorRegion(Region base, Vector origin, Vector normal) {
    this.base = base;
    this.normal = normal.clone().normalize();
    this.offset = this.normal.dot(origin);
  }

  @Override
  public boolean contains(Vector vector) {
    Vector test = reflect(vector);
    return this.base.contains(test);
  }

  @Override
  public Vector getRandomPosition(Random random) throws PositionUnavailableException {
    if (this.base instanceof BoundedRegion) {
      return reflect(this.base.getRandomPosition(random));
    } else {
      throw new PositionUnavailableException(this, true);
    }
  }

  Vector reflect(Vector vector) {
    final Vector reflection = normal.clone().multiply(2 * (vector.dot(normal) - offset));
    return vector.clone().subtract(reflection);
  }

  @Override
  public String toString() {
    return "MirrorRegion{" + "base=" + base + ", normal=" + normal + ", offset=" + offset + '}';
  }
}

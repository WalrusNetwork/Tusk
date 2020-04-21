package network.walrus.utils.bukkit.region.shapes;

import java.util.Iterator;
import java.util.Random;
import network.walrus.utils.bukkit.region.BoundedRegion;
import network.walrus.utils.bukkit.region.BoundedRegionIterator;
import org.bukkit.util.Vector;

/**
 * A cylindrical collection of vectors defined by a specified base, spread out by a specified
 * radius, and spread upward by a specified height.
 *
 * <p>Height math uses {@code 0} to denote "flat" cylinders which actually take up a single vector
 * space on the Y (the Y of the base vector) thus making cylinders with a height of {@code 1}
 * actually existing on 2 numbers on the Y-axis, and so on.
 *
 * @author Avicus Network
 */
public class CylinderRegion implements BoundedRegion {

  private final Vector base;
  private final double radius;
  private final int height;
  private final double radiusSquared; // for a more efficient contains calculation

  /**
   * Constructor.
   *
   * @param base of the cylinder
   * @param radius of the cylinder
   * @param height of the cylinder
   */
  public CylinderRegion(Vector base, double radius, int height) {
    this.base = base;
    this.radius = radius;
    this.height = height;
    this.radiusSquared = Math.pow(radius, 2);
  }

  @Override
  public boolean contains(Vector position) {
    if (position.getY() < this.base.getY() || position.getY() > this.base.getY() + this.height) {
      return false;
    }

    double distanceSquared =
        Math.pow(position.getX() - this.base.getX(), 2)
            + Math.pow(position.getZ() - this.base.getZ(), 2);
    return distanceSquared <= this.radiusSquared;
  }

  @Override
  public Vector getRandomPosition(Random random) {
    Vector randy =
        new Vector(
            getRandom(random, -radius, radius),
            getRandom(random, 0, this.height),
            getRandom(random, -radius, radius));
    return this.base.clone().add(randy);
  }

  private double getRandom(Random random, double min, double max) {
    return min + (max - min) * random.nextDouble();
  }

  @Override
  public Vector min() {
    Vector size = new Vector(this.radius, 0, this.radius);
    return this.base.clone().subtract(size);
  }

  @Override
  public Vector max() {
    Vector size = new Vector(this.radius, this.height, this.radius);
    return this.base.clone().add(size);
  }

  @Override
  public Iterator<Vector> iterator() {
    return new BoundedRegionIterator(this);
  }

  /** @return base of the cylinder */
  public Vector base() {
    return base;
  }

  /** @return radius of the cylinder */
  public double radius() {
    return radius;
  }

  /** @return height of the cylinder */
  public int height() {
    return height;
  }

  @Override
  public String toString() {
    return "CylinderRegion{"
        + "base="
        + base
        + ", radius="
        + radius
        + ", height="
        + height
        + ", radiusSquared="
        + radiusSquared
        + '}';
  }
}

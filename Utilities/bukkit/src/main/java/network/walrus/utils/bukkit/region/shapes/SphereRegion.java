package network.walrus.utils.bukkit.region.shapes;

import java.util.Iterator;
import java.util.Random;
import network.walrus.utils.bukkit.region.BoundedRegion;
import network.walrus.utils.bukkit.region.BoundedRegionIterator;
import org.bukkit.util.Vector;

/**
 * A sphere or "ball" of vectors originating from an origin and spreading out by a radius.
 *
 * @author Avicus Network
 */
public class SphereRegion implements BoundedRegion {

  private final Vector origin;
  private final double radius;
  private final double radiusSquared; // for a more efficient contains calculation

  /**
   * Constructor.
   *
   * @param origin of the sphere
   * @param radius of the sphere
   */
  public SphereRegion(Vector origin, double radius) {
    this.origin = origin;
    this.radius = radius;
    this.radiusSquared = (int) Math.pow(radius, 2);
  }

  @Override
  public boolean contains(Vector position) {
    return this.origin.distanceSquared(position) <= this.radiusSquared;
  }

  @Override
  public Vector getRandomPosition(Random random) {
    Vector randy =
        new Vector(
            getRandom(random, -radius, radius),
            getRandom(random, -radius, radius),
            getRandom(random, -radius, radius));
    return this.origin.clone().add(randy);
  }

  private double getRandom(Random random, double min, double max) {
    return min + (max - min) * random.nextDouble();
  }

  @Override
  public Vector min() {
    Vector size = new Vector(this.radius, this.radius, this.radius);
    return this.origin.clone().subtract(size);
  }

  @Override
  public Vector max() {
    Vector size = new Vector(this.radius, this.radius, this.radius);
    return this.origin.clone().add(size);
  }

  @Override
  public Vector getCenter() {
    return this.origin.clone();
  }

  @Override
  public Iterator<Vector> iterator() {
    return new BoundedRegionIterator(this);
  }

  @Override
  public String toString() {
    return "SphereRegion{"
        + "origin="
        + origin
        + ", radius="
        + radius
        + ", radiusSquared="
        + radiusSquared
        + '}';
  }
}

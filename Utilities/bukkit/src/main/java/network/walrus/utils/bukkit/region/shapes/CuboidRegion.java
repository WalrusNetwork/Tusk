package network.walrus.utils.bukkit.region.shapes;

import java.util.Iterator;
import java.util.Random;
import network.walrus.utils.bukkit.region.BoundedRegion;
import network.walrus.utils.bukkit.region.BoundedRegionIterator;
import network.walrus.utils.bukkit.region.Region;
import org.bukkit.util.Vector;

/**
 * A cube of vectors defined by a minimum and maximum value.
 *
 * @author Avicus Network
 */
public class CuboidRegion implements Region, BoundedRegion {

  private final Vector min;
  private final Vector max;

  /**
   * Constructor.
   *
   * @param min vector of the cube
   * @param max vector of the cube
   */
  public CuboidRegion(Vector min, Vector max) {
    this.min = Vector.getMinimum(min, max);
    this.max = Vector.getMaximum(min, max);
  }

  @Override
  public boolean contains(Vector vector) {
    boolean aboveMin =
        this.min.getX() <= vector.getX()
            && this.min.getY() <= vector.getY()
            && this.min.getZ() <= vector.getZ();
    boolean underMax =
        this.max.getX() > vector.getX()
            && this.max.getY() > vector.getY()
            && this.max.getZ() > vector.getZ();
    return aboveMin && underMax;
  }

  @Override
  public Vector getRandomPosition(Random random) {
    double x = random.nextDouble() * (this.max.getX() - this.min.getX()) + this.min.getX();
    double y = random.nextDouble() * (this.max.getY() - this.min.getY()) + this.min.getY();
    double z = random.nextDouble() * (this.max.getZ() - this.min.getZ()) + this.min.getZ();
    return new Vector(x, y, z);
  }

  @Override
  public Vector min() {
    return this.min.clone();
  }

  @Override
  public Vector max() {
    return this.max.clone();
  }

  @Override
  public Iterator<Vector> iterator() {
    return new BoundedRegionIterator(this);
  }

  @Override
  public String toString() {
    return "CuboidRegion{" + "min=" + min + ", max=" + max + '}';
  }
}

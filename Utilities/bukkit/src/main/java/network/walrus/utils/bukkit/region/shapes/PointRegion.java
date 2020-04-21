package network.walrus.utils.bukkit.region.shapes;

import java.util.Collections;
import java.util.Iterator;
import java.util.Random;
import network.walrus.utils.bukkit.region.BoundedRegion;
import network.walrus.utils.bukkit.region.Region;
import org.bukkit.util.Vector;

/**
 * A single vector.
 *
 * @author Avicus Network
 */
public class PointRegion implements Region, BoundedRegion {

  private final Vector vector;

  /**
   * Constructor
   *
   * @param vector marking the point of the region
   */
  public PointRegion(Vector vector) {
    this.vector = vector;
  }

  @Override
  public boolean contains(Vector vector) {
    return this.vector.equals(vector);
  }

  @Override
  public Vector getRandomPosition(Random random) {
    return this.vector;
  }

  @Override
  public Vector min() {
    return this.vector.clone();
  }

  @Override
  public Vector max() {
    return this.vector.clone();
  }

  @Override
  public Iterator<Vector> iterator() {
    return Collections.singletonList(this.vector).iterator();
  }

  @Override
  public String toString() {
    return "PointRegion{" + "vector=" + vector + '}';
  }
}

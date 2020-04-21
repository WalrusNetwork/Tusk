package network.walrus.utils.bukkit.region.shapes;

import java.util.Collections;
import java.util.Iterator;
import java.util.Random;
import network.walrus.utils.bukkit.region.BoundedRegion;
import network.walrus.utils.bukkit.region.Region;
import org.bukkit.util.Vector;

/**
 * A region which allows for fuzzy equality checks of single vectors. Any vector that has the same
 * base values as the one supplied will return {@code true} for containment checks.
 *
 * @author Avicus Network
 */
public class BlockRegion implements Region, BoundedRegion {

  private final Vector vector;

  /**
   * Constructor.
   *
   * @param vector of the block that this region is for
   */
  public BlockRegion(Vector vector) {
    this.vector = vector.setX(vector.getBlockX()).setY(vector.getBlockY()).setZ(vector.getBlockZ());
  }

  @Override
  public boolean contains(Vector vector) {
    if (vector.getBlockX() == this.vector.getBlockX()) {
      if (vector.getBlockY() == this.vector.getBlockY()) {
        return vector.getBlockZ() == this.vector.getBlockZ();
      }
    }
    return false;
  }

  @Override
  public Vector getRandomPosition(Random random) {
    return this.vector
        .clone()
        .add(new Vector(random.nextDouble(), random.nextDouble(), random.nextDouble()));
  }

  // TODO: Consider proper min/max
  @Override
  public Vector min() {
    return this.vector.clone();
  }

  @Override
  public Vector max() {
    return this.vector.clone();
  }

  @Override
  public Vector getCenter() {
    return this.vector.clone();
  }

  @Override
  public Iterator<Vector> iterator() {
    return Collections.singletonList(this.vector).iterator();
  }

  @Override
  public String toString() {
    return "BlockRegion{" + "vector=" + vector + '}';
  }
}

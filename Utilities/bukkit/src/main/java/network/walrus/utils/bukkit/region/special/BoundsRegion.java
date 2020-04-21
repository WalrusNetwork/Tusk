package network.walrus.utils.bukkit.region.special;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import network.walrus.utils.bukkit.region.BoundedRegion;
import network.walrus.utils.bukkit.region.Region;
import org.bukkit.util.Vector;

/**
 * Region which contains all vectors around the region specified. Similar to the WorldEdit //walls
 * command.
 *
 * @author Avicus Network
 */
public class BoundsRegion implements Region, BoundedRegion {

  private final List<Vector> vectors;
  private final Vector min;
  private final Vector max;

  /**
   * Constructor.
   *
   * @param base region to get bounds around
   * @param xSide if the bounds should be applied to the X axis
   * @param ySide if the bounds should be applied to the Y axis
   * @param zSide if the bounds should be applied to the Z axis
   */
  public BoundsRegion(BoundedRegion base, boolean xSide, boolean ySide, boolean zSide) {
    this.min = base.min();
    this.max = base.max();
    this.vectors = new ArrayList<>();
    for (Vector vector : base) {
      if ((vector.getX() == min.getX() || vector.getX() == max.getX()) && xSide) {
        vectors.add(vector);
      }
      if ((vector.getY() == min.getY() || vector.getY() == max.getY()) && ySide) {
        vectors.add(vector);
      }
      if ((vector.getZ() == min.getZ() || vector.getZ() == max.getZ()) && zSide) {
        vectors.add(vector);
      }
    }
  }

  @Override
  public boolean contains(Vector vector) {
    return this.vectors.contains(vector);
  }

  @Override
  public Vector getRandomPosition(Random random) {
    return this.vectors.get(new Random().nextInt(this.vectors.size() - 1));
  }

  @Override
  public Iterator<Vector> iterator() {
    return this.vectors.iterator();
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
  public String toString() {
    return "BoundsRegion{" + "min=" + min + ", max=" + max + '}';
  }
}

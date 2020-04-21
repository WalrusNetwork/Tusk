package network.walrus.utils.bukkit.region.modifiers;

import com.google.common.collect.Iterators;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import network.walrus.utils.bukkit.region.BoundedRegion;
import network.walrus.utils.bukkit.region.PositionUnavailableException;
import org.bukkit.util.Vector;

/**
 * A {@link JoinRegion} that contains only {@link BoundedRegion}s.
 *
 * @author Avicus Network
 */
public class BoundedJoinRegion extends JoinRegion<BoundedRegion> implements BoundedRegion {

  private final Vector min;
  private final Vector max;

  /**
   * Constructor.
   *
   * @param children that will be combined to form this region
   */
  @SuppressWarnings("unchecked")
  public BoundedJoinRegion(List<? extends BoundedRegion> children) {
    super((List<BoundedRegion>) children);

    Vector min = new Vector(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
    Vector max = new Vector(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);

    for (BoundedRegion region : children) {
      Vector regionMin = region.min();
      Vector regionMax = region.max();

      if (regionMin.getX() < min.getX()
          && regionMin.getY() < min.getY()
          && regionMin.getZ() < min.getZ()) {
        min = regionMin;
      }
      if (regionMax.getX() > max.getX()
          && regionMax.getY() > max.getY()
          && regionMax.getZ() > max.getZ()) {
        max = regionMax;
      }
    }

    this.min = min;
    this.max = max;
  }

  @Override
  public Vector getRandomPosition(Random random) throws PositionUnavailableException {
    int num = random.nextInt(children().size());
    return children().get(num).getRandomPosition(random);
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
    Iterator<Vector> iterator = children().get(0).iterator();
    for (BoundedRegion region : children()) {
      if (!region.equals(children().get(0))) {
        iterator = Iterators.concat(iterator, region.iterator());
      }
    }
    return iterator;
  }
}

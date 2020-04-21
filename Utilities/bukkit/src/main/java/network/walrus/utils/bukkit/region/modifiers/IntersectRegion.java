package network.walrus.utils.bukkit.region.modifiers;

import java.util.Random;
import network.walrus.utils.bukkit.region.PositionUnavailableException;
import network.walrus.utils.bukkit.region.Region;
import org.bukkit.util.Vector;

/**
 * Region which represents all of the vectors contained in the intersection of all {@link
 * JoinRegion#children()} of the {@link JoinRegion}.
 *
 * @author Avicus Network
 */
public class IntersectRegion implements Region {

  private final JoinRegion<Region> child;

  /**
   * Constructor.
   *
   * @param child to get intersections from
   */
  public IntersectRegion(JoinRegion child) {
    this.child = child;
  }

  @Override
  public boolean contains(Vector vector) {
    for (Region region : this.child.children()) {
      if (!region.contains(vector)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public Vector getRandomPosition(Random random) throws PositionUnavailableException {
    throw new PositionUnavailableException(this, true);
  }

  @Override
  public String toString() {
    return "IntersectRegion{" + "child=" + child + '}';
  }
}

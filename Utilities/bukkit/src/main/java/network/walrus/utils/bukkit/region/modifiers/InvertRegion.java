package network.walrus.utils.bukkit.region.modifiers;

import java.util.List;
import java.util.Random;
import network.walrus.utils.bukkit.region.PositionUnavailableException;
import network.walrus.utils.bukkit.region.Region;
import org.bukkit.util.Vector;

/**
 * A region containing all vectors outside of the specified {@link Region}.
 *
 * @author Avicus Network
 */
public class InvertRegion implements Region {

  private final Region child;

  /**
   * Constructor.
   *
   * @param child region to be inverted
   */
  public InvertRegion(Region child) {
    this.child = child;
  }

  /**
   * Constructor.
   *
   * @param children to generate the join region from
   */
  public InvertRegion(List<Region> children) {
    this.child = new JoinRegion<>(children);
  }

  @Override
  public boolean contains(Vector vector) {
    // invert contains()
    return !this.child.contains(vector);
  }

  @Override
  public Vector getRandomPosition(Random random) throws PositionUnavailableException {
    throw new PositionUnavailableException(this, true); // No good way to return a random position.
  }

  @Override
  public String toString() {
    return "InvertRegion{" + "child=" + child + '}';
  }
}

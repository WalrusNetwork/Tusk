package network.walrus.utils.bukkit.region.modifiers;

import com.google.common.base.Preconditions;
import java.util.List;
import java.util.Random;
import network.walrus.utils.bukkit.region.PositionUnavailableException;
import network.walrus.utils.bukkit.region.Region;
import org.bukkit.util.Vector;

/**
 * A collection of {@link Region}s "joined" together to make one query-able object.
 *
 * @param <T> type of regions contained in the join
 * @author Avicus Network
 */
public class JoinRegion<T extends Region> implements Region {

  private final List<T> children;

  /**
   * Constructor.
   *
   * @param children to be contained in the join
   */
  public JoinRegion(List<T> children) {
    Preconditions.checkArgument(children.size() > 0, "Joined region lacks children.");
    this.children = children;
  }

  @Override
  public boolean contains(Vector vector) {
    for (Region region : this.children) {
      if (region.contains(vector)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public Vector getRandomPosition(Random random) throws PositionUnavailableException {
    return this.children().get(random.nextInt(children().size())).getRandomPosition(random);
  }

  /** @return all children represented by this join */
  public List<T> children() {
    return children;
  }

  @Override
  public String toString() {
    return "JoinRegion{" + "children=" + children + '}';
  }
}

package network.walrus.utils.bukkit.region.modifiers;

import java.util.Random;
import network.walrus.utils.bukkit.region.BoundedRegion;
import network.walrus.utils.bukkit.region.PositionUnavailableException;
import network.walrus.utils.bukkit.region.Region;
import org.bukkit.util.Vector;

/**
 * Region which supplies to specified translation to the child region.
 *
 * @author Avicus Network
 */
public class TranslateRegion implements Region {

  private final Region child;
  private final Vector offset;

  /**
   * Constructor.
   *
   * @param child to apply the offset to
   * @param offset to shift vectors in the child by
   */
  public TranslateRegion(Region child, Vector offset) {
    this.child = child;
    this.offset = offset;
  }

  /** @return the offset that this region applies on the base */
  public Vector offset() {
    return this.offset.clone();
  }

  @Override
  public boolean contains(Vector vector) {
    Vector test = vector.clone().subtract(this.offset);
    return this.child.contains(test);
  }

  @Override
  public Vector getRandomPosition(Random random) throws PositionUnavailableException {
    if (this.child instanceof BoundedRegion) {
      return this.child.getRandomPosition(random).clone().add(offset);
    } else {
      throw new PositionUnavailableException(this, true);
    }
  }

  @Override
  public String toString() {
    return "TranslateRegion{" + "child=" + child + ", offset=" + offset + '}';
  }
}

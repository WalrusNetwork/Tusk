package network.walrus.utils.bukkit.region.modifiers;

import java.util.Iterator;
import java.util.Random;
import network.walrus.utils.bukkit.region.BoundedRegion;
import network.walrus.utils.bukkit.region.BoundedRegionIterator;
import network.walrus.utils.bukkit.region.PositionUnavailableException;
import org.bukkit.util.Vector;

/**
 * A {@link TranslateRegion} which only applies offsets to a {@link BoundedRegion}.
 *
 * @author Avicus Network
 */
public class BoundedTranslateRegion extends TranslateRegion implements BoundedRegion {

  private final BoundedRegion child;

  /**
   * Constructor.
   *
   * @param child to render vectors in
   * @param offset to move all vectors by
   */
  public BoundedTranslateRegion(BoundedRegion child, Vector offset) {
    super(child, offset);
    this.child = child;
  }

  @Override
  public Vector getRandomPosition(Random random) throws PositionUnavailableException {
    return this.child.getRandomPosition(random).add(offset());
  }

  @Override
  public Vector min() {
    return this.child.max().add(offset());
  }

  @Override
  public Vector max() {
    return this.child.max().add(offset());
  }

  @Override
  public Iterator<Vector> iterator() {
    return new BoundedRegionIterator(this);
  }

  @Override
  public String toString() {
    return "BoundedTranslateRegion{" + "child=" + child + "} " + super.toString();
  }
}

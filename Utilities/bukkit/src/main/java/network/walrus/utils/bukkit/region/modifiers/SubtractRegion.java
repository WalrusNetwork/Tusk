package network.walrus.utils.bukkit.region.modifiers;

import com.google.common.collect.Sets;
import java.util.Random;
import java.util.Set;
import network.walrus.utils.bukkit.region.PositionUnavailableException;
import network.walrus.utils.bukkit.region.Region;
import org.bukkit.util.Vector;

/**
 * Region used to subtract vectors inside one region from another.
 *
 * @author Avicus Network
 */
public class SubtractRegion implements Region {

  private static final int MAX_RANDOM_TRIES = 30;
  protected final Region base;
  protected final Region subtract;
  private final Set<Vector> notInsideCache =
      Sets.newHashSet(); // Used for random position generation.

  /**
   * Constructor.
   *
   * @param base to have vectors subtracted from
   * @param subtract to remove from the base
   */
  public SubtractRegion(Region base, Region subtract) {
    this.base = base;
    this.subtract = subtract;
  }

  @Override
  public boolean contains(Vector vector) {
    return !subtract.contains(vector) && base.contains(vector);
  }

  @Override
  public Vector getRandomPosition(Random random) throws PositionUnavailableException {
    Vector found = getRandomPositionInternal(random);
    int tries = 0;
    while (tries < MAX_RANDOM_TRIES
        && (notInsideCache.contains(found) || subtract.contains(found))) {
      notInsideCache.add(found);
      found = getRandomPositionInternal(random);
      tries++;
    }
    if (subtract.contains(found)) {
      throw new PositionUnavailableException(this, false);
    }
    return found;
  }

  private Vector getRandomPositionInternal(Random random) throws PositionUnavailableException {
    return base.getRandomPosition(random);
  }

  @Override
  public String toString() {
    return "SubtractRegion{" + "base=" + base + ", subtract=" + subtract + '}';
  }
}

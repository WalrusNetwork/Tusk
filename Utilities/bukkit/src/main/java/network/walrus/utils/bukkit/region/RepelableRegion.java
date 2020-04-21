package network.walrus.utils.bukkit.region;

import org.bukkit.util.Vector;

/**
 * A region which allows callers to determine an appropriate {@link Vector} to repel or "push" an
 * object out of it's bounds.
 *
 * @author Avicus Network
 */
public interface RepelableRegion extends Region {

  /**
   * Determine an appropriate {@link Vector} to repel or "push" an object out of bounds.
   *
   * @param from location to get a location from
   * @return the vector to repel players with
   */
  Vector getRepelVector(Vector from);
}

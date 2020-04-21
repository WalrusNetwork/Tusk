package network.walrus.ubiquitous.bukkit.border;

import org.bukkit.util.Vector;

/**
 * Represents a border that is bounded by two vectors: min, max
 *
 * @author David Rodriguez
 */
public interface BoundedBorder {

  /** @return The {@link Vector} containing the max bound of the border */
  Vector getMax();

  /** @return The {@link Vector} containing the min bound of the border */
  Vector getMin();
}

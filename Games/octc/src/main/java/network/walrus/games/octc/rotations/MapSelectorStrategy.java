package network.walrus.games.octc.rotations;

/**
 * Interface for defining a map selection strategy.
 *
 * @author Rafi Baum
 */
public interface MapSelectorStrategy {

  /**
   * Begin the process for selecting the next map based on the strategy being used. Calling this
   * method does not guarantee that a map will be selected within the tick, or even at all, but
   * simply starts the process of doing so according to the instantiated strategy.
   */
  void selectMap();

  /** Reload internal state. Usually invoked when new maps are loaded. */
  default void reload() {}
}

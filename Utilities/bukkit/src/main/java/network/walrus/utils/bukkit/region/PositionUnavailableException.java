package network.walrus.utils.bukkit.region;

import java.util.Random;
import org.bukkit.World;

/**
 * Exception which is thrown when {@link Region#getRandomPosition(Random)} or {@link
 * Region#getChunks(World)} fails.
 *
 * <p>The {@link #constant} boolean denotes whether this exception will *always* occur for the
 * calling method or can, at some point in time, actually return a random position.
 *
 * @author Avicus Network
 */
public class PositionUnavailableException extends Exception {

  private final Region source;
  private final boolean constant;

  /**
   * Constructor
   *
   * @param source region which generated the error
   * @param constant if the exception will always be thrown by the calling method
   */
  public PositionUnavailableException(Region source, boolean constant) {
    this.source = source;
    this.constant = constant;
  }

  /** @return region the error is for */
  public Region source() {
    return source;
  }

  /** @return if the error will always happen for the region type */
  public boolean constant() {
    return constant;
  }
}

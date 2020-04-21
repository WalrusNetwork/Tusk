package network.walrus.utils.bukkit.points;

import org.bukkit.util.Vector;

/**
 * Provides viewing angles based on current position.
 *
 * @author Avicus Network
 */
public interface AngleProvider {

  /**
   * Provide an angle (yaw or pitch) that a player should be facing if they are at the supplied
   * location.
   *
   * @param positionFrom to use as a point of origin
   * @return angle based on location
   */
  float getAngle(Vector positionFrom);
}

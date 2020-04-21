package network.walrus.utils.bukkit.points;

import org.bukkit.util.Vector;

/**
 * Simple provider which will always return a value pointing to a position on the X axis.
 *
 * @author Avicus Network
 */
public class TargetYawProvider implements AngleProvider {

  private final Vector target;

  /**
   * Constructor.
   *
   * @param target which the player should be rotated toward
   */
  public TargetYawProvider(Vector target) {
    this.target = target;
  }

  @Override
  public float getAngle(Vector from) {
    double dx = this.target.getX() - from.getX();
    double dz = this.target.getZ() - from.getZ();
    return (float) Math.toDegrees(Math.atan2(-dx, dz));
  }
}

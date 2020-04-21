package network.walrus.utils.bukkit.points;

import org.bukkit.util.Vector;

/**
 * Simple provider which will always return a value pointing to a position on the Y axis.
 *
 * @author Avicus Network
 */
public class TargetPitchProvider implements AngleProvider {

  private final Vector target;

  /**
   * Constructor.
   *
   * @param target which the player should be pitched toward
   */
  public TargetPitchProvider(Vector target) {
    this.target = target;
  }

  @Override
  public float getAngle(Vector from) {
    double dx = this.target.getX() - from.getX();
    double dz = this.target.getZ() - from.getZ();
    double distance = Math.sqrt(dx * dx + dz * dz);
    double dy = this.target.getY() - (from.getY() + 1.62);
    return (float) Math.toDegrees(Math.atan2(-dy, distance));
  }
}

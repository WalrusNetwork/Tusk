package network.walrus.utils.bukkit.points;

import org.bukkit.util.Vector;

/**
 * Simple provider which will always return the same value.
 *
 * @author Avicus Network
 */
public class StaticAngleProvider implements AngleProvider {

  private final float angle;

  /**
   * Constructor.
   *
   * @param angle to always return
   */
  public StaticAngleProvider(float angle) {
    this.angle = angle;
  }

  @Override
  public float getAngle(Vector positionFrom) {
    return this.angle;
  }
}

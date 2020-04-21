package network.walrus.games.uhc.facets.border;

import java.time.Duration;
import org.bukkit.util.Vector;

/**
 * Object containing data about a world border.
 *
 * @author ShinyDialga
 */
public class WorldBorder {

  public final double radius;
  public final Duration duration;
  final Vector center;
  boolean applied;

  /**
   * @param radius of the border
   * @param duration it takes for the border to change
   */
  WorldBorder(double radius, final Duration duration) {
    this.center = new Vector(0, 0, 0);
    this.radius = radius;
    this.duration = duration;
    this.applied = false;
  }
}

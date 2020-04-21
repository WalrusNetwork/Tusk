package network.walrus.games.core.facets.objectives;

import java.util.Optional;
import network.walrus.games.core.facets.group.Competitor;
import network.walrus.utils.core.math.NumberAction;
import org.bukkit.entity.Player;

/**
 * An objective type which is based on a simple points structure.
 *
 * @author Austin Mayes
 */
public interface IntegerObjective extends Objective {

  /**
   * Determine the number of points a competitor has.
   *
   * @param competitor to get points for
   * @return the number of points the competitor has
   */
  int getPoints(Competitor competitor);

  /**
   * Modify the number of points a competitor has.
   *
   * <p>The actor field is not required, but is used for UI and player-specific statics tracking.
   *
   * @param competitor to modify points for
   * @param amount to change points by
   * @param action used to act on the current points the competitor has
   * @param actor who caused the points to change
   */
  void modify(Competitor competitor, int amount, NumberAction action, Optional<Player> actor);
}

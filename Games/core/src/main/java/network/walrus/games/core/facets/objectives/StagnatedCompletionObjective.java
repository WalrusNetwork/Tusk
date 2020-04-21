package network.walrus.games.core.facets.objectives;

import java.util.Optional;
import network.walrus.games.core.facets.group.Competitor;

/**
 * An objective which can be completed by multiple {@link Competitor}s and will track which
 * competitor has completed the most of the objective.
 *
 * <p>This is extremely useful in complex win calculations because when an objective is {@link
 * Objective#isIncremental()} and can be completed by multiple {@link Competitor}s, it can be
 * difficult to keep track of who has done the most in a uniform way.
 *
 * @author Austin Mayes
 */
public interface StagnatedCompletionObjective extends GlobalObjective {

  /**
   * @return the competitor who has completed the most of this objective, or {@link
   *     Optional#empty()} if no one has started to complete it. If there is a tie for the most
   *     completion, {@link Optional#empty()} will also be returned.
   */
  Optional<Competitor> getHighestCompleter();
}

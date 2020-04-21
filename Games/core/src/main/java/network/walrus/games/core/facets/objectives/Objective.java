package network.walrus.games.core.facets.objectives;

import java.util.Optional;
import network.walrus.games.core.facets.group.Competitor;
import network.walrus.games.core.round.GameRound;
import network.walrus.utils.bukkit.translation.LocalizedConfigurationProperty;

/**
 * A defined goal in the {@link GameRound} that one or more {@link Competitor}s must complete in
 * order to gain points or to fully win the game.
 *
 * <p>All tasks that competitors can do to win the game should extend this class, unless the game
 * type has completely different win logic and statistics tracking.
 *
 * @author Austin Mayes
 */
public interface Objective {

  /**
   * Called after world generation.
   *
   * <p>Use this to determine initial completion or to spawn any needed entities.
   */
  void initialize();

  /** @return The name of the objective */
  LocalizedConfigurationProperty getName();

  /** @return If this objective can be completed by a specified competitor. */
  boolean canComplete(Competitor competitor);

  /** @see #canComplete(Competitor) */
  default boolean canComplete(Optional<Competitor> competitor) {
    return competitor.filter(this::canComplete).isPresent();
  }

  /** @return If the objective is complete. */
  boolean isCompleted(Competitor competitor);

  /** Get the completion of this objective for a specific competitor. */
  double getCompletion(Competitor competitor);

  /**
   * Determines if this objective is incremental.
   *
   * <p>Incremental objectives go through "states" before they are finally completed. If an
   * objective is incremental, for example a two block monument, then breaking one block will not
   * mark completion as true, but will register completion in the {@link #getCompletion(Competitor)}
   * method logic. Incremental objectives also typically show a percentage on the sidebar.
   */
  boolean isIncremental();
}

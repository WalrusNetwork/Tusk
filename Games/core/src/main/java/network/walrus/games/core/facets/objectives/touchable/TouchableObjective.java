package network.walrus.games.core.facets.objectives.touchable;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;
import network.walrus.games.core.facets.group.Competitor;
import network.walrus.games.core.facets.group.GroupsManager;
import network.walrus.games.core.facets.objectives.Objective;
import network.walrus.games.core.facets.objectives.locatable.LocatableObjective;
import network.walrus.games.core.round.GameRound;
import network.walrus.utils.bukkit.distance.DistanceCalculationMetric;
import network.walrus.utils.core.color.NetworkColorConstants.Games;
import network.walrus.utils.core.text.LocalizableFormat;
import network.walrus.utils.core.translation.TextStyle;
import org.bukkit.entity.Player;

/**
 * An objective which tracks which {@link Player}s have interacted with it at some point in the
 * round.
 *
 * @author Austin Mayes
 */
public abstract class TouchableObjective extends LocatableObjective implements Objective {

  private final GameRound round;
  private final Set<Player> recentTouchers;
  private final Set<Competitor> touchers;
  private final TouchableDistanceMetrics metrics;

  /**
   * @param round that the objective is in
   * @param metrics used to calculate distance
   */
  public TouchableObjective(GameRound round, TouchableDistanceMetrics metrics) {
    super(metrics, round);
    this.round = round;
    this.metrics = metrics;
    this.recentTouchers = new HashSet<>();
    this.touchers = new HashSet<>();
  }

  /**
   * @param player to get touch status for
   * @return if the player has touched the objective in a time period that the implementation
   *     defines as recent
   */
  public boolean hasTouchedRecently(Player player) {
    return this.recentTouchers.contains(player);
  }

  /**
   * Set whether the specified player has recently touched this objective.
   *
   * @param player to update recent touch status for
   * @param touched if they have recently touched the objective
   */
  public void setTouchedRecently(Player player, boolean touched) {
    if (touched) {
      Competitor competitor =
          this.round.getFacetRequired(GroupsManager.class).getCompetitorOf(player).orElse(null);
      if (competitor == null) {
        return;
      }
      this.setTouched(competitor, true);
      if (this.isTouchRelevant(player)) {
        this.recentTouchers.add(player);
      }
    } else {
      this.recentTouchers.remove(player);
    }
  }

  /** @return if the objective has been touched by anyone at any point in time */
  public boolean isTouched() {
    return !this.touchers.isEmpty();
  }

  /**
   * Determine if the supplied competitor has any members which have touched this objective during
   * the current round
   *
   * @param competitor to get touch status for
   * @return if the competitor has touched this objective
   */
  public boolean hasTouched(Competitor competitor) {
    return this.touchers.contains(competitor);
  }

  /**
   * Set whether the specified competitor has touched this objective.
   *
   * @param competitor to update touch status for
   * @param touched if they have touched the objective
   */
  public void setTouched(Competitor competitor, boolean touched) {
    if (touched) {
      this.touchers.add(competitor);
    } else {
      this.touchers.remove(competitor);
    }
  }

  /**
   * Determine if the specific player can see touch status for this objective.
   *
   * @param player to check
   * @return if the player can see touch status
   */
  public boolean canSeeTouched(Player player) {
    Optional<Competitor> competitor =
        this.round.getFacetRequired(GroupsManager.class).getCompetitorOf(player);
    return canSeeTouched(competitor);
  }

  /**
   * Determine if the specific competitor can see touch status for this objective.
   *
   * @param competitor to check
   * @return if the player can see touch status
   */
  public boolean canSeeTouched(Optional<Competitor> competitor) {
    if (competitor.isPresent()) {
      for (Competitor toucher : this.touchers) {
        if (toucher.equals(competitor.get())) {
          return true;
        }
      }
      return false;
    }
    // No Competitor = spectator
    return true;
  }

  @Override
  public TextStyle distanceStyle(Competitor ref, Player viewer) {
    if (isCompleted()) {
      return super.distanceStyle(ref, viewer);
    }

    return hasTouched(ref) ? Games.OCN.Objectives.TOUCHED : super.distanceStyle(ref, viewer);
  }

  /**
   * Determine if a touch alert and update should be pushed for a specific player.
   *
   * @param player to determine touch relevance for
   * @return if the player's touch of the objective is relevant
   */
  public boolean isTouchRelevant(Player player) {
    Optional<Competitor> competitor =
        this.round.getFacetRequired(GroupsManager.class).getCompetitorOf(player);
    if (competitor.isPresent()) {
      return !this.isCompleted(competitor.get()) && !this.hasTouchedRecently(player);
    }
    return false;
  }

  /** @return the message format that should be used when displaying a new touch */
  public abstract LocalizableFormat getTouchMessage();

  @Override
  public boolean shouldShowDistance(@Nullable Competitor ref, Player viewer) {
    if (isTouched()) {
      return getMetrics().getPostTouchMetric() != null;
    }

    return super.shouldShowDistance(ref, viewer);
  }

  @Nullable
  @Override
  public DistanceCalculationMetric getDistanceCalculationMetric(Competitor ref) {
    if (hasTouched(ref)) {
      return this.metrics.getPostTouchMetric();
    } else if (isCompleted(ref)) {
      return this.metrics.getPostCompleteMetric();
    } else {
      return this.metrics.getPreCompleteMetric();
    }
  }

  @Override
  public TouchableDistanceMetrics getMetrics() {
    return metrics;
  }
}

package network.walrus.games.core.facets.objectives.locatable;

import java.util.Collections;
import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nullable;
import network.walrus.games.core.facets.group.Competitor;
import network.walrus.games.core.facets.group.GroupsManager;
import network.walrus.games.core.facets.objectives.GlobalObjective;
import network.walrus.games.core.facets.objectives.Objective;
import network.walrus.utils.bukkit.distance.DistanceCalculationMetric;
import network.walrus.utils.bukkit.distance.LocatableObject;
import network.walrus.utils.core.color.NetworkColorConstants.Games;
import network.walrus.utils.core.translation.TextStyle;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import org.bukkit.entity.Player;

/**
 * An objective that exists at a singular physical point in the {@link org.bukkit.World} at a given
 * point a time. This is used as a base for all objectives that are a part of the proximity system,
 * and can be used as a fixed point when calculating distance using {@link
 * DistanceCalculationMetric}s.
 *
 * @author Austin Mayes
 */
public abstract class LocatableObjective extends LocatableObject<Competitor, Player>
    implements GlobalObjective, Objective {

  private final FacetHolder holder;
  private final DistanceMetrics metrics;
  private GroupsManager manager;

  /**
   * @param metrics being used to calculate distance for win calculation and UI
   * @param holder that the objective is inside of
   */
  public LocatableObjective(DistanceMetrics metrics, FacetHolder holder) {
    super(Collections.singleton(metrics.getPreCompleteMetric()));
    this.metrics = metrics;
    this.holder = holder;
  }

  @Override
  public void initialize() {
    this.manager = holder.getFacetRequired(GroupsManager.class);
  }

  @Override
  protected boolean canUpdateDistance(Player base) {
    return canComplete(manager.getCompetitorOf(base));
  }

  @Override
  public Function<Player, Competitor> conversionFunc() {
    return (p) -> manager.getCompetitorOf(p).orElse(null);
  }

  @Override
  public boolean canViewAlways(Player base) {
    return manager.getGroup(base).isSpectator();
  }

  @Override
  public TextStyle distanceStyle(Competitor ref, Player viewer) {
    return isCompleted(ref) ? Games.OCN.Objectives.COMPLETED : super.distanceStyle(ref, viewer);
  }

  @Override
  public boolean isDistanceRelevant(Competitor ref) {
    return !ref.getGroup().isObserving() && canComplete(ref);
  }

  @Nullable
  @Override
  public DistanceCalculationMetric getDistanceCalculationMetric(Competitor ref) {
    if (isCompleted(ref)) {
      return this.metrics.getPostCompleteMetric();
    } else {
      return this.metrics.getPreCompleteMetric();
    }
  }

  @Override
  public boolean shouldShowDistance(@Nullable Competitor ref, Player viewer) {
    if ((ref == null && isCompleted()) || isCompleted(ref)) {
      return getMetrics().getPostCompleteMetric() != null;
    }

    return this.canComplete(manager.getCompetitorOf(viewer)) || manager.isSpectator(viewer);
  }

  /**
   * Get the {@link DistanceCalculationMetric.Type metric type} that should be used to calculate
   * distance to the given player, or null if player is not currently competing.
   *
   * @param player to get metric type
   * @return the metric type that should be used for the player, or null if the player is not
   *     competing
   */
  @Nullable
  public DistanceCalculationMetric.Type getDistanceCalculationMetricType(Player player) {
    Optional<Competitor> comp = this.manager.getCompetitorOf(player);
    if (comp.isPresent()) {
      return super.getDistanceCalculationMetricType(comp.get());
    } else {
      return null;
    }
  }

  public boolean canComplete(Player player) {
    return canComplete(manager.getCompetitorOf(player));
  }

  public DistanceMetrics getMetrics() {
    return metrics;
  }

  public FacetHolder getHolder() {
    return holder;
  }
}

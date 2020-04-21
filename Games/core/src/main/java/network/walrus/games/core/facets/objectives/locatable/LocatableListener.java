package network.walrus.games.core.facets.objectives.locatable;

import java.util.List;
import network.walrus.games.core.facets.objectives.Objective;
import network.walrus.games.core.util.EventUtil;
import network.walrus.ubiquitous.bukkit.tracker.event.player.PlayerCoarseMoveEvent;
import network.walrus.ubiquitous.bukkit.tracker.event.player.PlayerDeathByPlayerEvent;
import network.walrus.utils.bukkit.distance.DistanceCalculationMetric.Type;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 * Listener responsbiel for keeping of track how far {@link org.bukkit.entity.Player}s are from
 * {@link LocatableObjective}s.
 *
 * @author Austin Mayes
 */
@SuppressWarnings("JavaDoc")
public class LocatableListener implements Listener {

  private final List<? extends LocatableObjective> objectives;

  /** @param objectives this listener is responsible for tracking distance for */
  public LocatableListener(List<? extends LocatableObjective> objectives) {
    this.objectives = objectives;
  }

  /**
   * Reset all distances for a specific {@link Objective}.
   *
   * @param objective to reset
   */
  public static void reset(Objective objective) {
    if (objective instanceof LocatableObjective) {
      if (((LocatableObjective) objective).resetDistance()) {
        EventUtil.call(new LocatableUpdateDistanceEvent(objective));
      }
    }
  }

  @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
  public void onMove(PlayerCoarseMoveEvent event) {
    for (LocatableObjective o : this.objectives) {
      if (o.getDistanceCalculationMetricType(event.getPlayer()) == Type.PLAYER
          && o.updateDistance(event.getPlayer(), event.getTo())) {
        EventUtil.call(new LocatableUpdateDistanceEvent(o));
      }
    }
  }

  @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
  public void onKill(PlayerDeathByPlayerEvent event) {
    for (LocatableObjective o : this.objectives) {
      if (o.getDistanceCalculationMetricType(event.getCause()) == Type.KILL
          && o.updateDistance(event.getCause(), event.getLocation())) {
        EventUtil.call(new LocatableUpdateDistanceEvent(o));
      }
    }
  }
}

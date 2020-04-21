package network.walrus.games.octc.score.event;

import java.util.Optional;
import network.walrus.games.core.events.objective.ObjectiveStateChangeEvent;
import network.walrus.games.core.facets.group.Competitor;
import network.walrus.games.core.facets.objectives.IntegerObjective;
import network.walrus.games.octc.score.ScoreObjective;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

/**
 * Event fired when a player earns or loses points towards an {@link IntegerObjective}.
 *
 * @author Austin Mayes
 */
public class PointChangeEvent extends ObjectiveStateChangeEvent {

  private static final HandlerList handlers = new HandlerList();
  private final Optional<Player> player;
  private final Competitor competitor;
  private final int amount;

  /**
   * @param objective objective that changed
   * @param player that is the cause of point change
   * @param amount point difference
   */
  public PointChangeEvent(
      ScoreObjective objective, Optional<Player> player, Competitor competitor, int amount) {
    super(objective);
    this.player = player;
    this.amount = amount;
    this.competitor = competitor;
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }

  @Override
  public HandlerList getHandlers() {
    return handlers;
  }

  public Optional<Player> getPlayer() {
    return player;
  }

  public int getAmount() {
    return amount;
  }

  public Competitor getCompetitor() {
    return competitor;
  }
}

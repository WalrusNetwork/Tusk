package network.walrus.games.octc.ctf.flags.events;

import javax.annotation.Nullable;
import network.walrus.games.core.events.objective.ObjectiveStateChangeEvent;
import network.walrus.games.core.facets.objectives.Objective;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

/**
 * Event fired when a flag is dropped at a place besides a {@link
 * network.walrus.games.octc.ctf.flags.Net}.
 *
 * @author Austin Mayes
 */
public class FlagDropEvent extends ObjectiveStateChangeEvent {

  private static final HandlerList handlers = new HandlerList();

  @Nullable private final Player dropper;

  /**
   * @param objective that was dropped
   * @param dropper who dropped the flag
   */
  public FlagDropEvent(Objective objective, Player dropper) {
    super(objective);
    this.dropper = dropper;
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }

  @Override
  public HandlerList getHandlers() {
    return handlers;
  }

  @Nullable
  public Player getDropper() {
    return dropper;
  }
}

package network.walrus.games.core.events.round;

import network.walrus.games.core.round.GameRound;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import org.bukkit.WorldCreator;
import org.bukkit.event.HandlerList;

/**
 * An event that is fired when a {@link GameRound} is loaded.
 *
 * @author Austin Mayes
 */
public class RoundLoadEvent extends RoundEvent {

  /** Event handlers. */
  private static final HandlerList handlers = new HandlerList();
  /** World creator of the round world. */
  private WorldCreator creator;

  /**
   * @param holder round that is being loaded
   * @param creator world creator of the round world
   */
  public RoundLoadEvent(FacetHolder holder, WorldCreator creator) {
    super(holder);
    this.creator = creator;
  }

  /**
   * Get the handlers of the event.
   *
   * @return the handlers of the event
   */
  public static HandlerList getHandlerList() {
    return handlers;
  }

  /**
   * Get the handlers of the event.
   *
   * @return the handlers of the event
   */
  @Override
  public HandlerList getHandlers() {
    return handlers;
  }

  public WorldCreator getCreator() {
    return creator;
  }

  public void setCreator(WorldCreator creator) {
    this.creator = creator;
  }
}

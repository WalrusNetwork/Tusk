package network.walrus.games.core.events.round;

import java.util.Optional;
import network.walrus.games.core.round.GameRound;
import network.walrus.games.core.round.states.RoundState;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import org.bukkit.event.HandlerList;

/**
 * An event that is fired when a {@link GameRound} changes states.
 *
 * @author Austin Mayes
 */
public class RoundStateChangeEvent extends RoundEvent {

  /** Event handlers. */
  private static final HandlerList handlers = new HandlerList();

  private final Optional<RoundState> from;
  private final Optional<RoundState> to;

  /**
   * Constructor.
   *
   * @param holder round that was opened
   * @param from previous state
   * @param to current state
   */
  public RoundStateChangeEvent(
      FacetHolder holder, Optional<RoundState> from, Optional<RoundState> to) {
    super(holder);
    this.from = from;
    this.to = to;
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

  public Optional<RoundState> getFrom() {
    return from;
  }

  public Optional<RoundState> getTo() {
    return to;
  }

  /** @return True to reflect a change from a playing state. */
  public boolean isFromPlaying() {
    return this.from.isPresent() && this.from.get().playing();
  }

  /** @return True to reflect a change to playing state. */
  public boolean isToPlaying() {
    return this.to.isPresent() && this.to.get().playing();
  }

  /** @return True to reflect a change from a non-playing to playing state. */
  public boolean isChangeToNotPlaying() {
    return isFromPlaying() && !isToPlaying();
  }

  /** @return True to reflect a change from a playing to non-playing state. */
  public boolean isChangeToPlaying() {
    return !isFromPlaying() && isToPlaying();
  }
}

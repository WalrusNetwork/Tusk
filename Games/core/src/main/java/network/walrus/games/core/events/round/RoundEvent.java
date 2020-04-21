package network.walrus.games.core.events.round;

import network.walrus.utils.parsing.facet.holder.FacetHolder;
import org.bukkit.event.Event;

/**
 * An event that is the superclass to all events that happen inside of rounds.
 *
 * @author Austin Mayes
 */
public abstract class RoundEvent extends Event {

  /** Holder that the event occurred in. */
  final FacetHolder holder;

  /**
   * Constructor.
   *
   * @param holder round that the event occurred in
   */
  protected RoundEvent(FacetHolder holder) {
    this.holder = holder;
  }

  public FacetHolder getHolder() {
    return holder;
  }
}

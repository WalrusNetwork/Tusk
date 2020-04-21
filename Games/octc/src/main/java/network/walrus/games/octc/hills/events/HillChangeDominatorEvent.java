package network.walrus.games.octc.hills.events;

import java.util.Optional;
import network.walrus.games.core.facets.group.Competitor;
import network.walrus.games.octc.hills.HillObjective;
import org.bukkit.event.HandlerList;

/**
 * A hill event that is called whenever the dominator of a particular hill changes
 *
 * @author Matthew Arnold
 */
public class HillChangeDominatorEvent extends HillEvent {

  private static final HandlerList handlers = new HandlerList();

  private final Optional<Competitor> dominator;
  private final Optional<Competitor> oldDominator;

  /**
   * Creates a new hill change dominator event
   *
   * @param objective the objective that the dominator has changed on
   * @param dominator the current dominator of the hill
   * @param oldDominator the old dominator of the hill, who it used to be before this cycle of
   *     domination
   */
  public HillChangeDominatorEvent(
      HillObjective objective, Optional<Competitor> dominator, Optional<Competitor> oldDominator) {
    super(objective);
    this.oldDominator = oldDominator;
    this.dominator = dominator;
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }

  /** @return the current dominator of the hill */
  public Optional<Competitor> dominator() {
    return dominator;
  }

  /** @return the old dominator of the hill */
  public Optional<Competitor> oldDominator() {
    return oldDominator;
  }

  @Override
  public HandlerList getHandlers() {
    return handlers;
  }
}

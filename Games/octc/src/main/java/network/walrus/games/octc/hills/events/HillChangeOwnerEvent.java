package network.walrus.games.octc.hills.events;

import java.util.Optional;
import network.walrus.games.core.facets.group.Competitor;
import network.walrus.games.octc.hills.HillObjective;
import org.bukkit.event.HandlerList;

/**
 * A hill event that is called whenever the owner of a particular hill changes
 *
 * @author Matthew Arnold
 */
public class HillChangeOwnerEvent extends HillEvent {

  private static final HandlerList handlers = new HandlerList();

  private final Optional<Competitor> owner;
  private final Optional<Competitor> oldOwner;

  /**
   * Creates a new hill change owner event
   *
   * @param objective the objective that the owner has changed on
   * @param owner the new owner of the objective
   * @param oldOwner the old owner of the objective, before the current cycle of domination
   */
  public HillChangeOwnerEvent(
      HillObjective objective, Optional<Competitor> owner, Optional<Competitor> oldOwner) {
    super(objective);
    this.oldOwner = oldOwner;
    this.owner = owner;
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }

  /** @return the new owner of the hill */
  public Optional<Competitor> owner() {
    return owner;
  }

  /** @return the old owner of the hill */
  public Optional<Competitor> oldOwner() {
    return oldOwner;
  }

  @Override
  public HandlerList getHandlers() {
    return handlers;
  }
}

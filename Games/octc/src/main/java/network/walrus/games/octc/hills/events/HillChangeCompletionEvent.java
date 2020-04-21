package network.walrus.games.octc.hills.events;

import java.util.Optional;
import network.walrus.games.core.facets.group.Competitor;
import network.walrus.games.octc.hills.HillObjective;
import org.bukkit.event.HandlerList;

/**
 * A hill event that is called whenever the capture time of a hill changes
 *
 * @author Matthew Arnold
 */
public class HillChangeCompletionEvent extends HillEvent {

  private static final HandlerList handlers = new HandlerList();

  private final int completionPercentage;
  private final int oldCompletionPercentage;

  private final Optional<Competitor> dominator;

  /**
   * Creates a new hill change time event
   *
   * @param objective the hill objective that the capture time has changed on
   * @param dominator the current dominator of the hill
   * @param completionPercentage the current completion percentage of the hill
   * @param oldCompletionPercentage the old completion percentage of the hill, what the current
   *     completion percentage has changed from since the last cycle of domination
   */
  public HillChangeCompletionEvent(
      HillObjective objective,
      Optional<Competitor> dominator,
      int completionPercentage,
      int oldCompletionPercentage) {
    super(objective);
    this.completionPercentage = completionPercentage;
    this.oldCompletionPercentage = oldCompletionPercentage;
    this.dominator = dominator;
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }

  /** @return the completion percentage of the hill */
  public int completionPercentage() {
    return completionPercentage;
  }

  /** @return the old completion percentage of the hill */
  public int oldCompletionPercentage() {
    return oldCompletionPercentage;
  }

  /** @return the dominator of the hill */
  public Optional<Competitor> getDominator() {
    return dominator;
  }

  @Override
  public HandlerList getHandlers() {
    return handlers;
  }
}

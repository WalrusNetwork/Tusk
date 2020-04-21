package network.walrus.games.core.events.group;

import network.walrus.games.core.facets.group.Group;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * An event that is fired when a {@link Group}'s max player count is changed.
 *
 * @author Avicus Network
 */
public class GroupMaxPlayerCountChangeEvent extends Event {

  private static final HandlerList handlers = new HandlerList();
  private final Group group;

  /**
   * Constructor.
   *
   * @param group that has been modified
   */
  public GroupMaxPlayerCountChangeEvent(Group group) {
    this.group = group;
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }

  @Override
  public HandlerList getHandlers() {
    return handlers;
  }

  public Group getGroup() {
    return group;
  }
}

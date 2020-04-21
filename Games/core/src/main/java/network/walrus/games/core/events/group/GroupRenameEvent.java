package network.walrus.games.core.events.group;

import network.walrus.games.core.facets.group.Group;
import network.walrus.utils.bukkit.translation.LocalizedConfigurationProperty;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * An event that is fired when a {@link Group} is being renamed.
 *
 * @author Avicus Network
 */
public class GroupRenameEvent extends Event implements Cancellable {

  /** Event handlers. */
  private static final HandlerList handlers = new HandlerList();
  /** Group that is being renamed. */
  private final Group group;
  /** The new name of the group. */
  private LocalizedConfigurationProperty name;
  /** If the event was canceled. */
  private boolean cancelled;

  /**
   * Constructor.
   *
   * @param group group that is being renamed
   * @param name new name of the group
   */
  public GroupRenameEvent(Group group, LocalizedConfigurationProperty name) {
    this.group = group;
    this.name = name;
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

  public Group getGroup() {
    return group;
  }

  public LocalizedConfigurationProperty getName() {
    return name;
  }

  public void setName(LocalizedConfigurationProperty name) {
    this.name = name;
  }

  @Override
  public boolean isCancelled() {
    return cancelled;
  }

  @Override
  public void setCancelled(boolean cancelled) {
    this.cancelled = cancelled;
  }
}

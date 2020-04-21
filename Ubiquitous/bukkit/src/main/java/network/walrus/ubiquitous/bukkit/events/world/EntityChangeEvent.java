package network.walrus.ubiquitous.bukkit.events.world;

import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;

/**
 * An event that is fired when an {@link Entity} changes.
 *
 * @param <T> type of the event that caused this event to fire.
 * @author Avicus Network
 */
public class EntityChangeEvent<T extends Event> extends EntityEvent implements Cancellable {

  /** Event handlers. */
  private static final HandlerList handlers = new HandlerList();
  /** Entity that caused the event. */
  private final Entity whoChanged;
  /** Event that caused this one to fire. */
  private final T cause;
  /** Type of change that has occurred. */
  private Action action;
  /** If the event was canceled. */
  private boolean cancelled;

  /**
   * Constructor.
   *
   * @param whoChanged entity that caused the event
   * @param entity entity that changed
   * @param cause event that caused this one to fire
   * @param action type of change that has occurred
   */
  public EntityChangeEvent(Entity whoChanged, Entity entity, T cause, Action action) {
    super(entity);
    this.whoChanged = whoChanged;
    this.cause = cause;
    this.action = action;
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

  public Entity getWhoChanged() {
    return whoChanged;
  }

  public T getCause() {
    return cause;
  }

  public Action getAction() {
    return action;
  }

  @Override
  public boolean isCancelled() {
    return cancelled;
  }

  @Override
  public void setCancelled(boolean cancelled) {
    this.cancelled = cancelled;
  }

  /** Base action describing the type of change. */
  public enum Action {
    BREAK,
    CHANGE,
    PLACE
  }
}

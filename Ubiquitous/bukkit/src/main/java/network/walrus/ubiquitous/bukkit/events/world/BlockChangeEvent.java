package network.walrus.ubiquitous.bukkit.events.world;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockEvent;

/**
 * An event that is fired when any block in the world is changed.
 *
 * @param <T> type of the event that caused this event to fire.
 * @author Avicus Network
 */
public class BlockChangeEvent<T extends Event> extends BlockEvent implements Cancellable {

  /** Event handlers. */
  private static final HandlerList handlers = new HandlerList();
  /** The event that caused this event. */
  private final T cause;
  /** State the block is before the change. */
  private final BlockState oldState;
  /** State the block is after the change. */
  private final BlockState newState;
  /** If the event is canceled. */
  private boolean cancelled;

  /**
   * Constructor.
   *
   * @param block block that is being changed
   * @param cause event that caused this event
   * @param oldState state the block is before the change
   * @param newState state the block is after the change
   */
  public BlockChangeEvent(Block block, T cause, BlockState oldState, BlockState newState) {
    super(block);
    this.cause = cause;
    this.oldState = oldState;
    this.newState = newState;
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
   * If the block is changed to air.
   *
   * @return if the block is changed to air
   */
  public boolean isToAir() {
    return this.newState.getType() == Material.AIR;
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

  public T getCause() {
    return cause;
  }

  public BlockState getOldState() {
    return oldState;
  }

  public BlockState getNewState() {
    return newState;
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

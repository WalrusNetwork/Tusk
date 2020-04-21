package network.walrus.ubiquitous.bukkit.events.world;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * An event that is fired when any block in the world is changed by a player.
 *
 * @param <T> type of the event that caused this event to fire.
 * @author Avicus Network
 */
public class BlockChangeByPlayerEvent<T extends Event> extends BlockChangeEvent {

  /** Event handlers. */
  private static final HandlerList handlers = new HandlerList();

  private final Player player;

  /**
   * Constructor.
   *
   * @param block which is being changed
   * @param cause of the change
   * @param oldState state of the block before the change
   * @param newState state of the block after the change
   * @param player who changed the block
   */
  public BlockChangeByPlayerEvent(
      Block block, T cause, BlockState oldState, BlockState newState, Player player) {
    super(block, cause, oldState, newState);
    this.player = player;
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

  public Player getPlayer() {
    return player;
  }
}

package network.walrus.ubiquitous.bukkit.tracker.event.player;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * Event fired every time a {@link Player} moves from one block to another.
 *
 * @author Overcast Network
 */
public class PlayerCoarseMoveEvent extends PlayerEvent implements Cancellable {

  private static final HandlerList handlers = new HandlerList();
  private Location from;
  private Location to;
  private boolean cancelled;

  /**
   * Constructor.
   *
   * @param player who is moving
   * @param from location the player is moving from
   * @param to location the player is moving to
   */
  public PlayerCoarseMoveEvent(final Player player, Location from, Location to) {
    super(player);
    this.from = from;
    this.to = to;
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }

  public Location getFrom() {
    return this.from;
  }

  public void setFrom(Location from) {
    this.from = from;
  }

  public Location getTo() {
    return this.to;
  }

  public void setTo(Location to) {
    this.to = to;
  }

  @Override
  public boolean isCancelled() {
    return cancelled;
  }

  @Override
  public void setCancelled(boolean cancelled) {
    this.cancelled = cancelled;
  }

  @Override
  public HandlerList getHandlers() {
    return handlers;
  }
}

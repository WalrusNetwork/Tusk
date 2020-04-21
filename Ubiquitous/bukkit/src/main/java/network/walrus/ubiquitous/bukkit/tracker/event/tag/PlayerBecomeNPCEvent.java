package network.walrus.ubiquitous.bukkit.tracker.event.tag;

import network.walrus.ubiquitous.bukkit.tracker.tag.CombatLoggerState;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * Event called when a player transforms into a tracker NPC.
 *
 * @author Austin Mayes
 */
public class PlayerBecomeNPCEvent extends PlayerEvent {

  private static final HandlerList handlers = new HandlerList();
  private final CombatLoggerState state;

  /**
   * @param who is changing
   * @param state that is changing
   */
  public PlayerBecomeNPCEvent(Player who, CombatLoggerState state) {
    super(who);
    this.state = state;
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }

  public CombatLoggerState getState() {
    return state;
  }

  @Override
  public HandlerList getHandlers() {
    return handlers;
  }
}

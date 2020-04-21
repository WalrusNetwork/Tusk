package network.walrus.games.octc.global.spawns;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * Event called before the {@link RespawnTask} is executed.
 *
 * @author David Rodriguez
 */
public class PlayerStartRespawnEvent extends PlayerEvent {

  private static final HandlerList handlers = new HandlerList();
  private boolean canceled;

  /** @param who is starting to spawn */
  public PlayerStartRespawnEvent(Player who) {
    super(who);
    this.canceled = false;
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }

  public boolean isCanceled() {
    return canceled;
  }

  public void setCanceled(boolean canceled) {
    this.canceled = canceled;
  }

  @Override
  public HandlerList getHandlers() {
    return handlers;
  }
}

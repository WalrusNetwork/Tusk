package network.walrus.ubiquitous.bukkit.compat;

import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.entity.Player;

/**
 * Compatibility wrapper for action bars.
 *
 * @author Rafi Baum
 */
public class CompatActionBar extends CompatHandler {

  public CompatActionBar(CompatManager compat) {
    super(compat);
  }

  /**
   * Sends the player an action bar if they support it
   *
   * @param player to send action bar to
   * @param component to show to player
   */
  public void sendActionBar(Player player, BaseComponent component) {
    requireSupport(player);
    player.sendActionBar(component);
  }
}

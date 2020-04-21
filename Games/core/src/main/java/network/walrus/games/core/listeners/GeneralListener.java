package network.walrus.games.core.listeners;

import network.walrus.games.core.GamesPlugin;
import network.walrus.utils.core.color.NetworkColorConstants.Punishments;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerInteractEntityEvent;

/**
 * Listener which holds some miscellaneous handlers needed to make various systems work. This is
 * used when certain handlers don't logically fit anywhere else.
 *
 * @author Austin Mayes
 */
public class GeneralListener implements Listener {

  private final GamesPlugin plugin;

  /** @param plugin that owns this listener */
  public GeneralListener(GamesPlugin plugin) {
    this.plugin = plugin;
  }

  /** Block players from joining too soon. */
  @EventHandler
  public void onLogin(AsyncPlayerPreLoginEvent event) {
    if (!plugin.loaded.get() && !Bukkit.getOfflinePlayer(event.getUniqueId()).isOp()) {
      event.setKickMessage(Punishments.REASON + "This server is not ready, join back in a bit!");
      event.setLoginResult(Result.KICK_OTHER);
    }
  }

  /** Enable multiple people using villagers */
  @EventHandler
  public void multiTrade(PlayerInteractEntityEvent event) {
    if (event.getRightClicked().getType() == EntityType.VILLAGER) {
      event.setCancelled(true);
      event.getPlayer().openMerchantCopy((Villager) event.getRightClicked());
    }
  }
}

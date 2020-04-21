package network.walrus.games.core.listeners;

import network.walrus.games.core.events.competitor.PlayerChangeCompetitorEvent;
import network.walrus.games.core.facets.kits.type.DoubleJumpKit;
import network.walrus.ubiquitous.bukkit.UbiquitousBukkitPlugin;
import network.walrus.ubiquitous.bukkit.doublejump.DoubleJumpManager;
import network.walrus.ubiquitous.bukkit.tracker.event.player.PlayerDeathEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Listener that helps the {@link DoubleJumpKit} work by removing the double jump kit from the
 * player
 *
 * @author David Rodriguez
 */
public class DoubleJumpListener implements Listener {

  /** Remove double jump kit from player */
  @EventHandler
  public void onCompetitorChange(PlayerChangeCompetitorEvent event) {
    Player player = event.getPlayer();
    DoubleJumpManager doubleJumpManager =
        UbiquitousBukkitPlugin.getInstance().getDoubleJumpManager();

    if (!doubleJumpManager.hasKit(player)) return;
    doubleJumpManager.disableDoubleJump(player);
  }

  /** Remove double jump kit from player when they die */
  @EventHandler
  public void onPlayerDeath(PlayerDeathEvent event) {
    Player player = event.getPlayer();
    DoubleJumpManager doubleJumpManager =
        UbiquitousBukkitPlugin.getInstance().getDoubleJumpManager();

    if (!doubleJumpManager.hasKit(player)) return;
    doubleJumpManager.disableDoubleJump(player);
  }
}

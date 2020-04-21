package network.walrus.games.uhc.facets.enforcers;

import network.walrus.games.uhc.UHCManager;
import network.walrus.games.uhc.UHCMessages;
import network.walrus.games.uhc.config.UHCConfig;
import network.walrus.utils.bukkit.sound.NetworkSoundConstants.Games.UHC;
import network.walrus.utils.core.color.NetworkColorConstants.Games.UHC.Portals;
import org.bukkit.World.Environment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;

/**
 * Listener which enforces options from the {@link UHCConfig} when players enter portals.
 *
 * @author Austin Mayes
 */
public class PortalEnforcer implements Listener {

  /** Block portals when not enabled. */
  @EventHandler
  public void onPortal(PlayerPortalEvent event) {
    if (UHCManager.instance.getUHC() == null
        || !UHCManager.instance.getUHC().getState().playing()) {
      event.setCancelled(true);
    }

    if (event.getTo() == null || event.getTo().getWorld() == null) return;

    Environment type = event.getTo().getWorld().getEnvironment();
    if (type == Environment.NETHER && !UHCManager.instance.getConfig().nether.get()) {
      event.setCancelled(true);
      event.getPlayer().sendMessage(UHCMessages.NETHER_DISABLED.with(Portals.NETHER_DISABLED));
      UHC.Portals.NETHER_DISABLED.play(event.getPlayer());
    }
    if (type == Environment.THE_END && !UHCManager.instance.getConfig().end.get()) {
      event.setCancelled(true);
      event.getPlayer().sendMessage(UHCMessages.END_DISABLED.with(Portals.END_DISABLED));
      UHC.Portals.END_DISABLED.play(event.getPlayer());
    }
  }
}

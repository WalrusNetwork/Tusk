package network.walrus.games.uhc.facets.deathlightning;

import network.walrus.games.uhc.UHCManager;
import network.walrus.ubiquitous.bukkit.tracker.event.player.PlayerDeathEvent;
import network.walrus.ubiquitous.bukkit.tracker.event.tag.TaggedPlayerDeathEvent;
import network.walrus.utils.parsing.facet.Facet;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Facet which strikes harmless lightning where a player dies.
 *
 * @author Rafi Baum
 */
public class DeathLightningFacet extends Facet implements Listener {

  private final FacetHolder holder;

  public DeathLightningFacet(FacetHolder holder) {
    this.holder = holder;
  }

  @EventHandler
  public void onPlayerDeath(PlayerDeathEvent event) {
    if (UHCManager.instance.getConfig().lightningOnDeath.get()) {
      Location loc = event.getPlayer().getLocation();
      loc.getWorld().strikeLightningEffect(loc);
    }
  }

  @EventHandler
  public void onTaggedPlayerDeath(TaggedPlayerDeathEvent event) {
    if (UHCManager.instance.getConfig().lightningOnDeath.get()) {
      Location loc = event.getPlayer().getLocation();
      loc.getWorld().strikeLightningEffect(loc);
    }
  }
}

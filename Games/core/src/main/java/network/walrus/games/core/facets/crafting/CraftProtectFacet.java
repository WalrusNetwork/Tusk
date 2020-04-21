package network.walrus.games.core.facets.crafting;

import network.walrus.utils.parsing.facet.Facet;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Disassociates crafting bench windows from their respective blocks to prevent other players from
 * breaking crafting benches to steal resources.
 *
 * @author Rafi Baum
 */
public class CraftProtectFacet extends Facet implements Listener {

  private final FacetHolder holder;

  public CraftProtectFacet(FacetHolder holder) {
    this.holder = holder;
  }

  @EventHandler
  public void onBenchClick(PlayerInteractEvent event) {
    if (event.getAction() == Action.RIGHT_CLICK_BLOCK
        && event.getClickedBlock().getType() == Material.WORKBENCH
        && !event.getPlayer().isSneaking()) {
      event.setCancelled(true);
      event.getPlayer().openWorkbench(null, true);
    }
  }
}

package network.walrus.games.core.facets.hunger;

import network.walrus.utils.parsing.facet.Facet;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

/**
 * Facet to disable hunger.
 *
 * @author Rafi Baum
 */
public class HungerFacet extends Facet implements Listener {

  private final FacetHolder holder;

  public HungerFacet(FacetHolder holder) {
    this.holder = holder;
  }

  @EventHandler
  public void onHunger(FoodLevelChangeEvent event) {
    event.setCancelled(true);
  }
}

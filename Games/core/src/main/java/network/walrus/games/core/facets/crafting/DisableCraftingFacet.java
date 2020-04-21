package network.walrus.games.core.facets.crafting;

import com.google.common.collect.Sets;
import java.util.Set;
import network.walrus.utils.bukkit.inventory.MaterialMatcher;
import network.walrus.utils.parsing.facet.Facet;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Facet which removes specified crafting recipes on enable and re-adds them back when it disables.
 *
 * @author Rafi Baum
 */
public class DisableCraftingFacet extends Facet implements Listener {

  private FacetHolder holder;
  private Set<MaterialMatcher> removing;

  /**
   * @param holder The {@link FacetHolder} where this facet is operating
   * @param removing A list of {@link Material}s for which to remove recipes if any are the result
   */
  public DisableCraftingFacet(FacetHolder holder, MaterialMatcher removing) {
    this.holder = holder;
    this.removing = Sets.newHashSet();
    this.removing.add(removing);
  }

  /**
   * Add a material matcher to the list of items to disable.
   *
   * @param matcher to add to the list of matchers
   */
  public void disableItem(MaterialMatcher matcher) {
    this.removing.add(matcher);
  }

  @EventHandler
  public void onCraft(PrepareItemCraftEvent event) {
    for (MaterialMatcher matcher : removing) {
      if (matcher.matches(event.getRecipe().getResult().getData())) {
        event.getInventory().setResult(new ItemStack(Material.AIR));
        break;
      }
    }
  }
}

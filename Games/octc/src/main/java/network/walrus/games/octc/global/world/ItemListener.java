package network.walrus.games.octc.global.world;

import network.walrus.utils.parsing.facet.holder.FacetHolder;
import network.walrus.utils.parsing.facet.parse.configurator.listener.FacetListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickedEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Listener which ensures tools and armor don't take damage unless they already have damage.
 *
 * @author Rafi Baum
 */
public class ItemListener extends FacetListener<WorldFacet> {

  public ItemListener(FacetHolder holder, WorldFacet facet) {
    super(holder, facet);
  }

  @EventHandler(ignoreCancelled = true)
  public void onPickupItem(PlayerPickupItemEvent event) {
    makeUnbreakable(event.getItem().getItemStack());
  }

  @EventHandler(ignoreCancelled = true)
  public void onItemMove(InventoryClickedEvent event) {
    if (event.getCurrentItem() == null) {
      return;
    }

    makeUnbreakable(event.getCurrentItem());
  }

  /* For catching items damaged by none of the above cases, causes UX inconsistencies. */
  @EventHandler(ignoreCancelled = true)
  public void onItemDamage(PlayerItemDamageEvent event) {
    if (event.getItem().getDurability() != 0) {
      return;
    }

    event.setCancelled(true);
    makeUnbreakable(event.getItem());
  }

  private void makeUnbreakable(ItemStack item) {
    if (item.getType().getMaxDurability() != 0 && item.getDurability() == 0) {
      ItemMeta meta = item.getItemMeta();
      if (!meta.spigot().isUnbreakable()) {
        meta.spigot().setUnbreakable(true);
        item.setItemMeta(meta);
      }
    }
  }
}

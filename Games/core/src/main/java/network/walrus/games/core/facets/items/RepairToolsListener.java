package network.walrus.games.core.facets.items;

import network.walrus.games.core.facets.filters.Filter;
import network.walrus.games.core.facets.filters.context.FilterContext;
import network.walrus.games.core.facets.filters.variable.ItemVariable;
import network.walrus.games.core.facets.filters.variable.LocationVariable;
import network.walrus.games.core.facets.filters.variable.MaterialVariable;
import network.walrus.games.core.facets.filters.variable.PlayerVariable;
import network.walrus.games.core.facets.group.GroupsManager;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Listener which solely has the responsibility for repairing items based on a {@link Filter}.
 *
 * @author Austin Mayes
 */
public class RepairToolsListener implements Listener {

  private final FacetHolder holder;
  private final Filter repairDrops;

  /**
   * @param holder which this listener is operating inside of
   * @param repairDrops filter used to decide which items to repair
   */
  RepairToolsListener(FacetHolder holder, Filter repairDrops) {
    this.holder = holder;
    this.repairDrops = repairDrops;
  }

  /** Merge and repair items when they are picked up. */
  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onPlayerPickupItem(PlayerPickupItemEvent event) {
    if (holder.getFacetRequired(GroupsManager.class).isObservingOrDead(event.getPlayer())) {
      return;
    }

    ItemStack drop = event.getItem().getItemStack();

    FilterContext context = new FilterContext();
    context.add(new PlayerVariable(event.getPlayer()));
    context.add(new ItemVariable(drop));
    context.add(new MaterialVariable(drop.getData()));
    context.add(new LocationVariable(event.getItem().getLocation()));

    if (this.repairDrops.test(context).fails()) {
      return;
    }

    ItemStack repair = null;

    for (ItemStack item : event.getPlayer().getInventory().getContents()) {
      if (isRepairable(drop, item)) {
        repair = item;

        // stop at this item if it is damaged
        if (repair.getDurability() > 0) {
          break;
        }
      }
    }

    if (repair != null) {
      // repair
      int remaining = drop.getType().getMaxDurability() - drop.getDurability() + 1;
      repair.setDurability((short) Math.max(repair.getDurability() - remaining, 0));

      event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ITEM_PICKUP, 0.5F, 1.0F);

      // remove and cancel pickup
      event.getItem().remove();
      event.setCancelled(true);
    }
  }

  private boolean isRepairable(ItemStack drop, ItemStack item) {
    return item != null
        && drop.getType() == item.getType()
        && drop.getEnchantments().equals(item.getEnchantments());
  }
}

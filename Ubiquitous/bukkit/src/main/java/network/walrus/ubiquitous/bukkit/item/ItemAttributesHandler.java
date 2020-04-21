package network.walrus.ubiquitous.bukkit.item;

import network.walrus.utils.bukkit.item.ItemAttributesUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

/**
 * Listener which implements a number of custom attributes on items.
 *
 * @author Austin Mayes
 */
public class ItemAttributesHandler implements Listener {

  /** Prevent moving locked items. */
  @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
  public void onInventoryClick(final InventoryClickEvent event) {
    if (event instanceof InventoryCreativeEvent) {
      return;
    }

    // Break out of the switch if the action will move a locked item, otherwise return
    switch (event.getAction()) {
      case HOTBAR_SWAP:
      case HOTBAR_MOVE_AND_READD:
        // These actions can move up to two stacks. Check the hotbar stack,
        // and then fall through to check the stack under the cursor.
        if (ItemAttributesUtils.isLocked(event.getInventory().getItem(event.getHotbarButton()))) {
          break;
        }
      case PICKUP_ALL:
      case PICKUP_HALF:
      case PICKUP_SOME:
      case PICKUP_ONE:
      case SWAP_WITH_CURSOR:
      case MOVE_TO_OTHER_INVENTORY:
      case DROP_ONE_SLOT:
      case DROP_ALL_SLOT:
      case COLLECT_TO_CURSOR:
        if (ItemAttributesUtils.isLocked(event.getCurrentItem())) {
          break;
        }
      default:
        return;
    }

    event.setCancelled(true);
  }

  /** Prevent dropping of locked and non-sharable items. */
  @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
  public void onDropItem(PlayerDropItemEvent event) {
    if (ItemAttributesUtils.isNotSharable(event.getItemDrop().getItemStack())) {
      event.setCancelled(true);
    }
  }

  /** Prevent dropping of locked and non-sharable items. */
  @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
  public void onDeath(PlayerDeathEvent event) {
    event.getDrops().removeIf((item) -> !ItemAttributesUtils.shouldDeathDrop(item));
  }
}

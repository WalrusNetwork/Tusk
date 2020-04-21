package network.walrus.ubiquitous.bukkit.inventory;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

/**
 * A listener that manages the events related to the inventory api
 *
 * @author Matthew Arnold
 */
public class InventoryListener implements Listener {

  private final InventoryManager inventoryManager;

  /**
   * Creates a new inventory listener, for a specific {@link InventoryManager}
   *
   * @param inventoryManager the inventory manager the listener is for
   */
  public InventoryListener(InventoryManager inventoryManager) {
    this.inventoryManager = inventoryManager;
  }

  /**
   * Looks to see if a callback is executed when a player clicks on something inside of an {@link
   * Inventory}
   *
   * @param event the click event
   */
  @EventHandler
  public void onClick(InventoryClickEvent event) {
    // player hasn't clicked an inventory
    if (event.getInventory() == null) {
      return;
    }

    // player clicks outside
    if (event.getSlotType() == InventoryType.SlotType.OUTSIDE) {
      return;
    }

    // there's no item there
    if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) {
      return;
    }

    Inventory inventory = event.getInventory();

    if (inventoryManager.hasInventory(inventory)) {
      int x = event.getSlot() % 9;
      int y = event.getSlot() / 9;
      event.setCancelled(true);
      inventoryManager.getInventory(inventory).clickItem(x, y, event.getActor());
    }
  }

  /**
   * Prevents players from moving items into {@link WalrusInventory}s
   *
   * @param event the move item event
   */
  @EventHandler
  public void onMove(InventoryMoveItemEvent event) {
    if (inventoryManager.hasInventory(event.getDestination())
        || inventoryManager.hasInventory(event.getSource())) {
      event.setCancelled(true);
    }
  }

  /**
   * Removes a {@link Inventory} from the manager
   *
   * @param event the close inventory event
   */
  @EventHandler
  public void onClose(InventoryCloseEvent event) {
    if (event.getInventory() == null) {
      return;
    }
    inventoryManager.remove(event.getInventory());
  }
}

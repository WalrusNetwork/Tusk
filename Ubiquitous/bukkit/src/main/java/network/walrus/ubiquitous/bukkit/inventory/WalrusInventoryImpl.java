package network.walrus.ubiquitous.bukkit.inventory;

import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import network.walrus.ubiquitous.bukkit.inventory.items.ItemHolder;
import network.walrus.utils.core.translation.Localizable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

/**
 * The simple base implementation of {@link WalrusInventory} that represents a simple inventory
 *
 * @author Matthew Arnold
 */
class WalrusInventoryImpl implements WalrusInventory {

  private final InventoryManager inventoryManager;
  private final Map<UUID, WalrusInventory> history;

  private final List<ItemHolder> items;
  private final int rows;
  private final Localizable title;

  /**
   * Creates a new {@link WalrusInventoryImpl}
   *
   * @param manager the inventory manager for this inventory
   * @param items the items to put in this inventory
   * @param rows the number of rows this inventory will have
   * @param title the title of this inventory
   */
  WalrusInventoryImpl(
      InventoryManager manager, List<ItemHolder> items, int rows, Localizable title) {
    this.inventoryManager = manager;
    this.items = items;
    this.rows = rows;
    this.title = title;
    this.history = Maps.newHashMap();
  }

  @Override
  public void open(Player player) {
    open(player, true);
  }

  @Override
  public void open(Player player, boolean override) {
    if (override) {
      history.remove(player.getUniqueId());
    }

    Inventory inventory = Bukkit.createInventory(player, rows * 9, title.toLegacyText(player));
    for (ItemHolder x : items) {
      x.putInInventory(player, inventory, this);
    }
    inventoryManager.addInventory(inventory, this);
    player.openInventory(inventory);
  }

  @Override
  public void open(Player player, WalrusInventory previous) {
    history.put(player.getUniqueId(), previous);
    open(player, false);
  }

  @Override
  public void clickItem(int x, int y, Player player) {
    for (ItemHolder item : items) {
      if (item.x == x && item.y == y) {
        item.item.onClick(this, player);
      }
    }
  }

  @Override
  public boolean hasPrevious(Player player) {
    return history.containsKey(player.getUniqueId());
  }

  @Override
  public void popPrevious(Player player) {
    if (!history.containsKey(player.getUniqueId())) {
      // throw an exception, there's no history to pop!
      throw new IllegalStateException(
          "Tried to pop inventory history with no history for " + player);
    }

    history.get(player.getUniqueId()).open(player, false);
  }

  @Override
  public void update() {
    for (ItemHolder item : items) {
      item.item.updateAll();
    }
  }

  @Override
  public void update(Player player) {
    for (ItemHolder item : items) {
      item.item.update(player);
    }
  }
}

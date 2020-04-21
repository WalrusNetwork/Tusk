package network.walrus.ubiquitous.bukkit.inventory;

import static network.walrus.utils.core.color.NetworkColorConstants.Inventory.Paginated.NEXT_BUTTON_LORE;
import static network.walrus.utils.core.color.NetworkColorConstants.Inventory.Paginated.NEXT_BUTTON_TITLE;
import static network.walrus.utils.core.color.NetworkColorConstants.Inventory.Paginated.PREVIOUS_BUTTON_TITLE;

import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import network.walrus.ubiquitous.bukkit.UbiquitousMessages;
import network.walrus.ubiquitous.bukkit.inventory.items.InventoryItem;
import network.walrus.ubiquitous.bukkit.inventory.items.InventoryItemBuilder;
import network.walrus.ubiquitous.bukkit.inventory.items.ItemBuilder;
import network.walrus.utils.core.translation.Localizable;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * A paginated inventory is a {@link WalrusInventory} that has several pages and can go
 * forward/backwards
 *
 * @author Matthew Arnold
 */
class PaginatedInventory implements WalrusInventory {

  private final Localizable title;
  private final InventoryManager manager;
  private final Function<Player, List<InventoryItem>> func;
  private final Map<UUID, WalrusInventory> history;

  /**
   * Creates a new {@link PaginatedInventory}
   *
   * @param manager the manager for this inventory
   * @param title the title of this inventory
   * @param func the function that is used to fill this inventory with items, the function takes a
   *     {@link Player} as an argument and returns a {@link List<InventoryItem>} that is used to
   *     populate the inventory with items
   */
  PaginatedInventory(
      InventoryManager manager, Localizable title, Function<Player, List<InventoryItem>> func) {
    this.title = title;
    this.manager = manager;
    this.func = func;
    this.history = Maps.newHashMap();
  }

  private InventoryItem nextButton(List<InventoryItem> items, int currentPage) {
    return InventoryItemBuilder.createItem(
            (inv, pl) ->
                ItemBuilder.of(Material.ARROW)
                    .setName(
                        UbiquitousMessages.INVENTORY_NEXT_TITLE
                            .with(NEXT_BUTTON_TITLE)
                            .toLegacyText(pl))
                    .setLore(UbiquitousMessages.INVENTORY_NEXT_LORE, NEXT_BUTTON_LORE, pl)
                    .stack())
        .onClick((inv, pl) -> openPage(pl, currentPage + 1, items))
        .setDelay(1)
        .shouldCache(true)
        .build();
  }

  private InventoryItem backButton(List<InventoryItem> items, int currentPage) {
    return InventoryItemBuilder.createItem(
            (inv, pl) ->
                ItemBuilder.of(Material.ARROW)
                    .setName(
                        UbiquitousMessages.INVENTORY_PREVIOUS_TITLE
                            .with(PREVIOUS_BUTTON_TITLE)
                            .toLegacyText(pl))
                    .setLore(UbiquitousMessages.INVENTORY_PREVIOUS_LORE, NEXT_BUTTON_LORE, pl)
                    .stack())
        .onClick((inv, pl) -> openPage(pl, currentPage + -1, items))
        .setDelay(1)
        .shouldCache(true)
        .build();
  }

  private WalrusInventory createInventory(List<InventoryItem> items, int page) {
    // 28 items in the middle grid (7x4)
    InventoryBuilder clone = new InventoryBuilder(manager, 6).setName(title);
    if (page > 0) {
      // render backbutton
      clone.addItem(5, 0, backButton(items, page));
    }
    if (items.size() >= 28 * (page + 1)) {
      // render next button
      clone.addItem(5, 8, nextButton(items, page));
    }

    int start = 28 * page;
    for (int i = 28 * page; i < 28 * (page + 1) && i < items.size(); i++) {
      int realIndex = i - start;
      int y = 1 + (realIndex / 7);
      int x = 1 + (realIndex % 7);
      clone.addItem(y, x, items.get(i));
    }

    // add Back Button
    clone.addItem(5, 4, InventoryItemBuilder.BACK_BUTTON);
    return clone.createInventory();
  }

  @Override
  public void open(Player player) {
    open(player, true);
  }

  /**
   * Opens a certain page in the inventory
   *
   * @param player the player opening the inventory
   * @param page the page number to open
   */
  public void openPage(Player player, int page) {
    List<InventoryItem> items = func.apply(player);
    openPage(player, page, items);
  }

  private void openPage(Player player, int page, List<InventoryItem> items) {
    WalrusInventory inventory = createInventory(items, page);

    if (history.containsKey(player.getUniqueId())) {
      inventory.open(player, history.get(player.getUniqueId()));
    } else {
      inventory.open(player);
    }
  }

  @Override
  public void open(Player player, boolean override) {
    if (override) {
      history.remove(player.getUniqueId());
    }
    openPage(player, 0);
  }

  @Override
  public void open(Player player, WalrusInventory previous) {
    history.put(player.getUniqueId(), previous);
    openPage(player, 0);
  }

  @Override
  public void clickItem(int x, int y, Player player) {
    throw new IllegalStateException(
        player.getDisplayName()
            + " Attempting to click a slot in a paginated inventory ("
            + x
            + ", "
            + y
            + ")");
  }

  @Override
  public boolean hasPrevious(Player player) {
    return history.containsKey(player.getUniqueId());
  }

  @Override
  public void popPrevious(Player player) {
    history.get(player.getUniqueId()).open(player, false);
  }

  @Override
  public void update() {
    // if caching is every implemented on this level (don't think it's needed)
    // do something, at the moment don't do anything
  }

  @Override
  public void update(Player player) {
    // see comment for update()
  }
}

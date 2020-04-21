package network.walrus.ubiquitous.bukkit.inventory.items;

import static network.walrus.utils.core.color.NetworkColorConstants.Inventory.BACK_BUTTON_LORE;
import static network.walrus.utils.core.color.NetworkColorConstants.Inventory.BACK_BUTTON_TITLE;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import network.walrus.ubiquitous.bukkit.UbiquitousMessages;
import network.walrus.ubiquitous.bukkit.inventory.WalrusInventory;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Builder to associate in the creation of a new {@link InventoryItem}
 *
 * @author Matthew Arnold
 */
public class InventoryItemBuilder {

  /**
   * The standard back button that is used for all inventories if the inventory has no inventory to
   * go back to, the item method will return null meaning this item will not be rendered
   *
   * <p>The open method has a delay of 1 tick because it's opening a new inventory, and it's best
   * practice to do that after a tick delay
   */
  public static InventoryItem BACK_BUTTON =
      InventoryItemBuilder.createItem(
              (inv, player) -> {
                if (!inv.hasPrevious(player)) {
                  return null;
                }

                return ItemBuilder.of(Material.WOOL)
                    .setName(
                        UbiquitousMessages.INVENTORY_BACK_TITLE
                            .with(BACK_BUTTON_TITLE)
                            .toLegacyText(player))
                    .setLore(UbiquitousMessages.INVENTORY_BACK_LORE, BACK_BUTTON_LORE, player)
                    .manipulateData(y -> y.setData(DyeColor.RED.getWoolData()))
                    .stack();
              })
          .onClick(WalrusInventory::popPrevious)
          .setDelay(1)
          .build();

  private final BiFunction<WalrusInventory, Player, ItemStack> createItem;
  private boolean cache;
  private BiConsumer<WalrusInventory, Player> onClick;
  private int tickDelay;

  /**
   * Creates a new inventory item builder, from a function that creates the item stack from a
   * specific player
   *
   * @param createItem the create item function
   */
  private InventoryItemBuilder(BiFunction<WalrusInventory, Player, ItemStack> createItem) {
    this.createItem = createItem;
    this.cache = false;
    this.tickDelay = 0;
    this.onClick = (x, y) -> {};
  }

  /**
   * Creates a new {@link InventoryItemBuilder} that is used to create an {@link InventoryItem}
   *
   * @param createItem the create item function that constructs an {@link ItemStack} from the
   *     inventory and the player
   * @return the created {@link InventoryItemBuilder}
   */
  public static InventoryItemBuilder createItem(
      BiFunction<WalrusInventory, Player, ItemStack> createItem) {
    return new InventoryItemBuilder(createItem);
  }

  /**
   * Creates a new {@link InventoryItemBuilder}, used to create inventory items
   *
   * @param item an {@link ItemStack} that should be in the inventory
   * @return the created {@link InventoryItemBuilder}
   */
  public static InventoryItemBuilder createItem(ItemStack item) {
    return new InventoryItemBuilder((x, y) -> item);
  }

  /**
   * Sets the on click callback, the function that is called when the player clicks the item in the
   * inventory
   *
   * @param onClick the callback that takes two parameters, the inventory the item is in and the
   *     player who clicked the item
   * @return the builder
   */
  public InventoryItemBuilder onClick(BiConsumer<WalrusInventory, Player> onClick) {
    this.onClick = onClick;
    return this;
  }

  /**
   * Sets the tick delay, affects the delay before the call back is called after an item is clicked
   * in the inventory
   *
   * @param tickDelay the delay in ticks to run the callback after the item in question is clicked
   * @return the builder
   */
  public InventoryItemBuilder setDelay(int tickDelay) {
    this.tickDelay = tickDelay;
    return this;
  }

  /**
   * Whether or not the item should be cached to improve performance
   *
   * @param shouldCache whether or not the item should be cached
   * @return the builder
   */
  public InventoryItemBuilder shouldCache(boolean shouldCache) {
    this.cache = shouldCache;
    return this;
  }

  /**
   * Creates a new {@link InventoryItem} from the builder
   *
   * @return the {@link InventoryItem} that has been created by this builder
   */
  public InventoryItem build() {
    return new InventoryItemImpl(createItem, onClick, tickDelay, cache);
  }
}

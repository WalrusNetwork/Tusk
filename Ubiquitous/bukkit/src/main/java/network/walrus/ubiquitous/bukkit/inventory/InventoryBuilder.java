package network.walrus.ubiquitous.bukkit.inventory;

import com.google.api.client.util.Lists;
import java.util.List;
import network.walrus.ubiquitous.bukkit.inventory.items.InventoryItem;
import network.walrus.ubiquitous.bukkit.inventory.items.InventoryItemBuilder;
import network.walrus.ubiquitous.bukkit.inventory.items.ItemHolder;
import network.walrus.utils.core.text.UnlocalizedText;
import network.walrus.utils.core.translation.Localizable;

/**
 * Builder utility class used to assist in the creation of a new {@link WalrusInventory}
 *
 * @author Matthew Arnold
 */
public class InventoryBuilder {

  private static final Localizable DEFAULT_NAME = new UnlocalizedText("");

  private final InventoryManager manager;
  private final int rows;
  private final List<ItemHolder> items;
  private Localizable name;

  /**
   * Creates a new inventory builder
   *
   * @param manager the inventory manager
   * @param rows the number of rows the inventory should have
   */
  InventoryBuilder(InventoryManager manager, int rows) {
    this.manager = manager;
    this.rows = rows;
    this.name = DEFAULT_NAME;
    this.items = Lists.newArrayList();
  }

  /**
   * Adds an item to the {@link WalrusInventory} being built by this builder at the next free slot.
   *
   * @param builder the builder of the item to add
   * @return this builder
   */
  public InventoryBuilder addItem(InventoryItemBuilder builder) {
    return addItem(builder.build());
  }

  /**
   * Adds an item to the {@link WalrusInventory} at the next free slot
   *
   * @param item the item to add
   * @return this builder
   */
  public InventoryBuilder addItem(InventoryItem item) {
    for (int y = 0; y < rows; y++) {
      for (int x = 0; x < 9; x++) {
        boolean matched = false;
        for (ItemHolder itemHolder : items) {
          if (itemHolder.x == x && itemHolder.y == y) {
            matched = true;
            break;
          }
        }

        if (!matched) {
          return addItem(y, x, item);
        }
      }
    }

    throw new IllegalStateException("Ran out of space in inventory!");
  }

  /**
   * Adds an item to the {@link WalrusInventory} being built by this builder at a certain coordinate
   *
   * @param y the y coordinate to add the item
   * @param x the x coordinate to add the item
   * @param builder the builder of the item to add
   * @return this builder
   */
  public InventoryBuilder addItem(int y, int x, InventoryItemBuilder builder) {
    return addItem(y, x, builder.build());
  }

  /**
   * Adds an item to the {@link WalrusInventory} being built by this builder at a certain coordinate
   *
   * @param y the y coordinate to add the item
   * @param x the x coordinate to add the item
   * @param item the item to add add to the inventory
   * @return this builder
   */
  public InventoryBuilder addItem(int y, int x, InventoryItem item) {
    items.add(new ItemHolder(y, x, item));
    return this;
  }

  /**
   * Sets the name of the {@link WalrusInventory}
   *
   * @param name the name to set it to
   * @return this builder
   */
  public InventoryBuilder setName(Localizable name) {
    this.name = name;
    return this;
  }

  /**
   * Constructs the {@link WalrusInventory} that this builder has been building
   *
   * @return the constructed inventory
   */
  public WalrusInventory createInventory() {
    return new WalrusInventoryImpl(manager, items, rows, name);
  }
}

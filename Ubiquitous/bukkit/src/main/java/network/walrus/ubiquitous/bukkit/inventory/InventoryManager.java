package network.walrus.ubiquitous.bukkit.inventory;

import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import network.walrus.ubiquitous.bukkit.inventory.items.InventoryItem;
import network.walrus.utils.core.translation.Localizable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

/**
 * An inventory manager that controls the creation and tracking of all {@link WalrusInventory} for
 * the inventory api
 *
 * @author Matthew Arnold
 */
public class InventoryManager {

  private final Map<Inventory, WalrusInventory> invMap;
  private final Map<String, WalrusInventory> idInventories;

  /**
   * Creates a new inventory manager, this should never be called outside of the setup methods in
   * normal use, unless you know explicitly what you are doing
   */
  public InventoryManager() {
    this.invMap = Maps.newHashMap();
    this.idInventories = Maps.newHashMap();
  }

  /**
   * Creates a new {@link WalrusInventory}
   *
   * @param id the id of the inventory
   * @param rows the number of rows the inventory should have
   * @param func a function that manipulates the {@link InventoryBuilder} to design the {@link
   *     WalrusInventory}
   * @return the created {@link WalrusInventory}
   */
  public WalrusInventory createInventory(String id, int rows, Consumer<InventoryBuilder> func) {
    WalrusInventory inventory = createInventory(rows, func);
    idInventories.put(id, inventory);
    return inventory;
  }

  /**
   * Creates a new {@link WalrusInventory}
   *
   * @param rows the number of rows the inventory should have
   * @param func a function that manipulates the {@link InventoryBuilder} to design the {@link
   *     WalrusInventory}
   * @return the created {@link WalrusInventory}
   */
  public WalrusInventory createInventory(int rows, Consumer<InventoryBuilder> func) {
    if (rows > 6) {
      throw new IllegalStateException("There can be at most 6 rows in an inventory");
    }

    InventoryBuilder builder = new InventoryBuilder(this, rows);
    func.accept(builder);
    return builder.createInventory();
  }

  /**
   * Creates a new {@link PaginatedInventory}, used for having an inventory that has next page and
   * previous page options
   *
   * @param id the id of the inventory
   * @param title the title of the inventory
   * @param gather the gather function of the inventory, this function takes a player and produces a
   *     list of items of any type that should fill the inventory
   * @param reducer the reducer function of the inventory, this function takes an item that has been
   *     produced by the gather function and translates it into a {@link InventoryItem}
   * @param <T> the type of item produced by the gather function, and the input to the reducer
   *     function
   * @return the created {@link PaginatedInventory}
   */
  public <T> WalrusInventory createPaginatedInventory(
      String id,
      Localizable title,
      Function<Player, List<? extends T>> gather,
      Function<T, InventoryItem> reducer) {
    WalrusInventory inventory = createPaginatedInventory(title, gather, reducer);
    idInventories.put(id, inventory);
    return inventory;
  }

  /**
   * Creates a new {@link PaginatedInventory}, used for having an inventory that has next page and
   * previous page options
   *
   * @param title the title of the inventory
   * @param gather the gather function of the inventory, this function takes a player and produces a
   *     list of items of any type that should fill the inventory
   * @param reducer the reducer function of the inventory, this function takes an item that has been
   *     produced by the gather function and translates it into a {@link InventoryItem}
   * @param <T> the type of item produced by the gather function, and the input to the reducer
   *     function
   * @return the created {@link PaginatedInventory}
   */
  public <T> WalrusInventory createPaginatedInventory(
      Localizable title,
      Function<Player, List<? extends T>> gather,
      Function<T, InventoryItem> reducer) {
    Function<Player, List<InventoryItem>> fun =
        p -> {
          List<InventoryItem> list = new ArrayList<>();
          for (T t : gather.apply(p)) {
            InventoryItem inventoryItem = reducer.apply(t);
            list.add(inventoryItem);
          }
          return list;
        };
    return createPaginatedInventory(title, fun);
  }

  /**
   * Creates a new {@link PaginatedInventory}, used for having an inventory that has next page and
   * previous page options
   *
   * @param id the id of the inventory
   * @param title the title of the inventory
   * @param func function that produces a list of {@link InventoryItem} from an input of a {@link
   *     Player} these produced items are used to fill the {@link PaginatedInventory}
   * @return the created {@link PaginatedInventory}
   */
  public WalrusInventory createPaginatedInventory(
      String id, Localizable title, Function<Player, List<InventoryItem>> func) {
    WalrusInventory inventory = createPaginatedInventory(title, func);
    idInventories.put(id, inventory);
    return inventory;
  }

  /**
   * Creates a new {@link PaginatedInventory}, used for having an inventory that has next page and
   * previous page options
   *
   * @param title the title of the inventory
   * @param func function that produces a list of {@link InventoryItem} from an input of a {@link
   *     Player} these produced items are used to fill the {@link PaginatedInventory}
   * @return the created {@link PaginatedInventory}
   */
  public WalrusInventory createPaginatedInventory(
      Localizable title, Function<Player, List<InventoryItem>> func) {
    return new PaginatedInventory(this, title, func);
  }

  /**
   * Gets an {@link Optional< WalrusInventory >} from a specific id, the {@link Optional} will
   * contain a {@link WalrusInventory} if one exists for the specified id, otherwise it will be
   * empty
   *
   * @param id the id to look for
   * @return an optional walrus inventory, depending if one exists for the specified id
   */
  public Optional<WalrusInventory> get(String id) {
    if (idInventories.containsKey(id)) {
      return Optional.of(idInventories.get(id));
    }
    return Optional.empty();
  }

  /**
   * Links a {@link Inventory} and a {@link WalrusInventory} together for the listener to track
   *
   * @param bukkitInventory the bukkit inventory to form part of the link
   * @param inventory the walrus inventory to form the other part of the link
   */
  void addInventory(Inventory bukkitInventory, WalrusInventory inventory) {
    invMap.put(bukkitInventory, inventory);
  }

  /**
   * Checks to see whether a {@link Inventory} has a linked {@link WalrusInventory}
   *
   * @param inventory the bukkit inventory
   * @return true if there is a linked {@link WalrusInventory}, false otherwise
   */
  boolean hasInventory(Inventory inventory) {
    return invMap.containsKey(inventory);
  }

  /**
   * Gets a linked {@link WalrusInventory} that is linked with a specified {@link Inventory}
   *
   * @param inventory the {@link Inventory} to look for a connection to
   * @return the connected {@link WalrusInventory}
   */
  WalrusInventory getInventory(Inventory inventory) {
    return invMap.get(inventory);
  }

  /**
   * Removes {@link Inventory} and it's link from the linked mappings
   *
   * @param inventory the inventory to remove
   * @return the walrus inventory this inventory is linked to
   */
  WalrusInventory remove(Inventory inventory) {
    return invMap.remove(inventory);
  }
}

package network.walrus.ubiquitous.bukkit.inventory;

import network.walrus.ubiquitous.bukkit.inventory.items.InventoryItem;
import org.bukkit.entity.Player;

/**
 * A walrus inventory is an abstraction of the inventory api that handles a specific inventory in
 * the api
 *
 * @author Matthew Arnold
 */
public interface WalrusInventory {

  /**
   * Opens the {@link WalrusInventory} with no history, override a past history if one is already
   * present for a specific {@link Player}
   *
   * @param player the player who's opening the inventory
   */
  void open(Player player);

  /**
   * Opens the {@link WalrusInventory} without override the previous history of this inventory (if
   * override is set to false), this is important for when a {@link Player} presses the back button
   * in a {@link WalrusInventory}, this prevents a back button chain
   *
   * @param player the player
   * @param override whether or not this should override the history for this {@link Player} and
   *     this {@link WalrusInventory}
   */
  void open(Player player, boolean override);

  /**
   * Opens the {@link WalrusInventory} and sets another {@link WalrusInventory} to be the history
   * for this {@link Player}
   *
   * @param player the player opening the inventory
   * @param previous the previous inventory, the one to put in this {@link WalrusInventory}'s
   *     history
   */
  void open(Player player, WalrusInventory previous);

  /**
   * Acts as a {@link Player} clicking a {@link InventoryItem} in a specific {@link WalrusInventory}
   *
   * @param x the x coordinate
   * @param y the y coordinate
   * @param player the player clicking the item
   */
  void clickItem(int x, int y, Player player);

  /**
   * Whether or not the {@link Player} has a previous inventory, a history in this inventory
   *
   * @param player the player
   * @return true if the player has a history, false otherwise
   */
  boolean hasPrevious(Player player);

  /**
   * Pops a {@link Player}'s history, opening the inventory in their history
   *
   * @param player the player's who's history is being popped
   */
  void popPrevious(Player player);

  /**
   * Updates all {@link InventoryItem} in the {@link WalrusInventory}, forcing them to be
   * re-rendered and not hit the cache
   */
  void update();

  /**
   * Updates all {@link InventoryItem} in the {@link WalrusInventory} for a specific {@link Player},
   * forcing them to be re-rendered and not hit the cache
   */
  void update(Player player);
}

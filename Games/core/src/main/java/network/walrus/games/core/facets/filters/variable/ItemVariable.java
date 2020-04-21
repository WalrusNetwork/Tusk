package network.walrus.games.core.facets.filters.variable;

import network.walrus.games.core.facets.filters.Variable;
import network.walrus.utils.bukkit.inventory.ScopableItemStack;
import org.bukkit.inventory.ItemStack;

/**
 * The item variable contains information about the type of item that the filter is being performed
 * against. This variable includes all of an item's attributes.
 *
 * @author Avicus Network
 */
public class ItemVariable implements Variable {

  private final ScopableItemStack itemStack;

  /**
   * Constructor.
   *
   * @param itemStack to check against
   */
  public ItemVariable(ItemStack itemStack) {
    this.itemStack = new ScopableItemStack(itemStack);
  }

  public ScopableItemStack getItemStack() {
    return itemStack;
  }
}

package network.walrus.games.core.facets.spawners.spawneritems;

import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

/**
 * Spawns an item
 *
 * @author Matthew Arnold
 */
public class SpawnerItem implements SpawnerEntry {

  private final ItemStack itemStack;

  /** @param itemStack the item to spawn */
  public SpawnerItem(ItemStack itemStack) {
    this.itemStack = itemStack;
  }

  @Override
  public void spawn(Location location, Vector velocity) {
    Item item = location.getWorld().dropItem(location, itemStack);
    item.setVelocity(velocity);
  }
}

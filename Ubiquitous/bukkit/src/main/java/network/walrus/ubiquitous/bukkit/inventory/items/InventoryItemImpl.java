package network.walrus.ubiquitous.bukkit.inventory.items;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import network.walrus.ubiquitous.bukkit.UbiquitousBukkitPlugin;
import network.walrus.ubiquitous.bukkit.inventory.WalrusInventory;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * The default and basic implementation of {@link InventoryItem}, has basic caching on a user
 * specific level
 *
 * @author Matthew Arnold
 */
class InventoryItemImpl implements InventoryItem {

  private final BiFunction<WalrusInventory, Player, ItemStack> func;
  private final BiConsumer<WalrusInventory, Player> onClick;

  private final Map<UUID, ItemStack> cache;
  private final int tickDelay;
  private final boolean shouldCache;

  /**
   * Creates a new {@link InventoryItemImpl}, the default implementation of {@link InventoryItem}
   *
   * @param func the function used to create the item
   * @param onClick the onclick function
   * @param tickDelay the delay between a player clicking an item in the inventory and the callback
   *     being executed
   * @param cache whether or not the item should be cached, true if it should be
   */
  InventoryItemImpl(
      BiFunction<WalrusInventory, Player, ItemStack> func,
      BiConsumer<WalrusInventory, Player> onClick,
      int tickDelay,
      boolean cache) {
    this.func = func;
    this.onClick = onClick;
    this.cache = Maps.newHashMap();
    this.tickDelay = tickDelay;
    this.shouldCache = cache;
  }

  @Override
  public ItemStack item(WalrusInventory inventory, Player player) {
    UUID uuid = player.getUniqueId();
    if (cache.containsKey(uuid)) {
      return cache.get(uuid);
    }
    ItemStack stack = func.apply(inventory, player);
    if (shouldCache) {
      cache.put(uuid, stack);
    }
    return stack;
  }

  @Override
  public void onClick(WalrusInventory inventory, Player player) {
    if (tickDelay > 0) {
      Bukkit.getScheduler()
          .scheduleSyncDelayedTask(
              UbiquitousBukkitPlugin.getInstance(),
              () -> onClick.accept(inventory, player),
              tickDelay);
    } else {
      onClick.accept(inventory, player);
    }
  }

  @Override
  public void updateAll() {
    cache.clear();
  }

  @Override
  public void update(Player player) {
    cache.remove(player.getUniqueId());
  }
}

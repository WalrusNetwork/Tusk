package network.walrus.games.core.facets.items;

import com.google.common.collect.Lists;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import network.walrus.games.core.events.group.PlayerChangedGroupEvent;
import network.walrus.games.core.events.player.PlayerSpawnBeginEvent;
import network.walrus.games.core.facets.filters.Filter;
import network.walrus.games.core.facets.filters.FilterCache;
import network.walrus.games.core.facets.filters.FilterResult;
import network.walrus.games.core.facets.filters.context.FilterContext;
import network.walrus.games.core.facets.filters.variable.LocationVariable;
import network.walrus.games.core.facets.filters.variable.MaterialVariable;
import network.walrus.games.core.facets.filters.variable.PlayerVariable;
import network.walrus.games.core.util.GameTask;
import network.walrus.ubiquitous.bukkit.tracker.event.player.PlayerDeathEvent;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.material.MaterialData;

/**
 * Listener which solely has the responsibility for keeping items and giving them back to players
 * later based on a {@link Filter}.
 *
 * @author Austin Mayes
 * @author Matthew Arnold
 */
public class KeepListener implements Listener {

  private final FacetHolder holder;
  private final Filter keepfilter;
  private final FilterCache<MaterialData> dataCache;
  private final Map<Player, InventoryKeep> savedInventories;

  /**
   * @param holder which this listener is operating inside of
   * @param keepItems filter used to decide which items should be kept on death
   */
  KeepListener(FacetHolder holder, Filter keepItems) {
    this.holder = holder;
    this.keepfilter = keepItems;
    this.savedInventories = new HashMap<>();
    this.dataCache = new FilterCache<>();
  }

  /** Store items on death */
  @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
  public void onPlayerDeath(PlayerDeathEvent event) {
    PlayerInventory inventory = event.getPlayer().getInventory();

    FilterContext context = new FilterContext();
    context.add(new PlayerVariable(event.getPlayer()));
    context.add(new LocationVariable(event.getLocation()));

    ItemStack[] items = checkItems(context, inventory.getContents(), event.getDrops());
    ItemStack[] armor = checkItems(context, inventory.getArmorContents(), event.getDrops());

    this.savedInventories.put(event.getPlayer(), new InventoryKeep(items, armor));
  }

  /** Give back items on spawn */
  @EventHandler
  public void onPlayerSpawn(PlayerSpawnBeginEvent event) {
    InventoryKeep saved = this.savedInventories.get(event.getPlayer());
    PlayerInventory inventory = event.getPlayer().getInventory();

    List<ItemStack> remainders = Lists.newArrayList();

    if (saved == null) {
      return;
    }

    new GameTask("invKeep " + event.getPlayer().getName()) {
      @Override
      public void run() {
        for (int i = 0; i < saved.inventory.length; i++) {
          ItemStack keptStack = saved.inventory[i];

          if (keptStack == null) {
            continue;
          }

          ItemStack curr = inventory.getItem(i);

          if (curr == null) {
            inventory.setItem(i, keptStack);
            continue;
          }

          if (curr.isSimilar(keptStack)) {
            int n = Math.min(keptStack.getAmount(), curr.getMaxStackSize() - curr.getAmount());
            curr.setAmount(curr.getAmount() + n);
            keptStack.setAmount(keptStack.getAmount() - n);
          }
          if (keptStack.getAmount() > 0) {
            remainders.add(keptStack);
          }
        }

        for (ItemStack stack : remainders) {
          inventory.addItem(stack);
        }

        ItemStack[] armorInventory = new ItemStack[4];
        for (int i = 0; i < saved.armor.length; i++) {
          ItemStack armor = saved.armor[i];
          if (armor != null) {
            armorInventory[i] = armor;
          } else {
            armorInventory[i] = inventory.getArmorContents()[i];
          }
        }

        inventory.setArmorContents(armorInventory);
      }
    }.now();
  }

  /** Clear saved inventory on team change. */
  @EventHandler
  public void onPlayerChangeTeam(PlayerChangedGroupEvent event) {
    this.savedInventories.remove(event.getPlayer());
  }

  /** Clear saved inventory on player quit. */
  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent event) {
    this.savedInventories.remove(event.getPlayer());
  }

  /**
   * Calculates what items a player should keep on death from an array of items, if items are kept
   * they are removed from the drops list
   *
   * @param context the filter context
   * @param itemStacks the itemstacks
   * @param drops the drops, items are removed from this list if they are added to the array
   * @return The item stacks the player will keep on death
   */
  private ItemStack[] checkItems(
      FilterContext context, ItemStack[] itemStacks, List<ItemStack> drops) {
    ItemStack[] save = new ItemStack[itemStacks.length];
    for (int i = 0; i < save.length; i++) {
      ItemStack stack = itemStacks[i];
      if (stack == null) {
        continue;
      }

      FilterResult result =
          dataCache.get(
              stack.getData(),
              (d) -> {
                FilterContext duplicated = context.duplicate();
                duplicated.add(new MaterialVariable(d));
                return keepfilter.test(duplicated);
              });

      boolean keep = result.passes();

      if (keep) {
        drops.remove(stack);
        save[i] = stack;
      }
    }

    return save;
  }

  /** Tracks both a player's inventory and armor */
  private static class InventoryKeep {

    private final ItemStack[] inventory;
    private final ItemStack[] armor;

    public InventoryKeep(ItemStack[] inventory, ItemStack[] armor) {
      this.inventory = inventory;
      this.armor = armor;
    }
  }
}

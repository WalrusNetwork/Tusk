package network.walrus.games.core.facets.kits.type;

import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import network.walrus.games.core.facets.kits.Kit;
import network.walrus.utils.bukkit.inventory.ScopableItemStack;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 * Kit used to give items to a player.
 *
 * @author Avicus Network
 */
public class ItemKit extends Kit {

  private final Map<Integer, ScopableItemStack> slotedItems;
  private final List<ScopableItemStack> unslotedItems;

  /**
   * @param force if items should be placed in occupied slots
   * @param parent kit to inherit attributes from
   * @param slotedItems items which are bound to specific slots
   * @param unslotedItems items which can be placed in any inventory slot
   */
  public ItemKit(
      boolean force,
      @Nullable Kit parent,
      Map<Integer, ScopableItemStack> slotedItems,
      List<ScopableItemStack> unslotedItems) {
    super(force, parent);
    this.slotedItems = slotedItems;
    this.unslotedItems = unslotedItems;
  }

  @Override
  public void give(Player player, boolean force) {
    final PlayerInventory inventory = player.getInventory();

    // Items
    for (Map.Entry<Integer, ScopableItemStack> entry : this.slotedItems.entrySet()) {
      int slot = entry.getKey();
      ItemStack stack = entry.getValue().getItemStack(player);

      if (slot >= 100) {
        slot -= 100;
        ItemStack[] armor = player.getInventory().getArmorContents();
        if (this.isForce()
            || force
            || armor[slot] == null
            || armor[slot].getType() == Material.AIR) {
          armor[slot] = stack;
        }
        player.getInventory().setArmorContents(armor);
      } else {
        if (this.isForce() || force || player.getInventory().getItem(slot) == null) {
          player.getInventory().setItem(slot, stack);
        }
      }
    }

    for (ScopableItemStack item : this.unslotedItems) {
      player.getInventory().addItem(item.getItemStack(player));
    }

    player.updateInventory();
  }
}

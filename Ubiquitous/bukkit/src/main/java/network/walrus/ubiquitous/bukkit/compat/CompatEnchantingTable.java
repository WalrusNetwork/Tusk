package network.walrus.ubiquitous.bukkit.compat;

import com.google.common.collect.Sets;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import network.walrus.sportpaper.api.enchantments.PreparedEnchantment;
import network.walrus.ubiquitous.bukkit.UbiquitousBukkitPlugin;
import network.walrus.ubiquitous.bukkit.UbiquitousMessages;
import network.walrus.utils.core.color.NetworkColorConstants.Compat.Enchanting;
import network.walrus.utils.core.text.LocalizedNumber;
import network.walrus.utils.core.text.UnlocalizedText;
import network.walrus.utils.core.translation.Localizable;
import network.walrus.utils.core.translation.TextStyle;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.inventory.EnchantingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 * Compatibility manager for 1.7 clients using the enchanting table.
 *
 * @author Rafi Baum
 */
public class CompatEnchantingTable extends CompatHandler implements Listener {

  private final Set<UUID> enchanterUUID;

  /**
   * Constructor.
   *
   * @param compat
   */
  public CompatEnchantingTable(CompatManager compat) {
    super(compat);
    this.enchanterUUID = Sets.newHashSet();
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onPreEnchant(PrepareItemEnchantEvent event) {
    // Necessary since the event gets triggered twice per enchant for whatever reason
    if (enchanterUUID.contains(event.getEnchanter().getUniqueId())) {
      return;
    }

    enchanterUUID.add(event.getEnchanter().getUniqueId());
    Bukkit.getScheduler()
        .runTaskLater(
            UbiquitousBukkitPlugin.getInstance(),
            () -> {
              enchanterUUID.remove(event.getEnchanter().getUniqueId());
            },
            1);

    EnchantingInventory tableInventory = (EnchantingInventory) event.getInventory();
    PlayerInventory playerInventory = event.getEnchanter().getInventory();

    ItemStack secondary = tableInventory.getSecondary();
    if (secondary == null || isLapis(secondary)) {
      // Collect lapis
      int amount = secondary == null ? 0 : secondary.getAmount();

      if (amount < 3) {
        ItemStack toTake = new ItemStack(Material.INK_SACK, 3 - amount, (short) 4);
        Map<Integer, ItemStack> removed = playerInventory.removeItem(toTake);

        int taken = 3;
        for (Entry<Integer, ItemStack> entry : removed.entrySet()) {
          taken -= entry.getValue().getAmount();
        }

        tableInventory.setSecondary(new ItemStack(Material.INK_SACK, amount + taken, (short) 4));
      }
    }

    if (!isLegacy(event.getEnchanter())) {
      return;
    }

    ItemStack lapisStack = ((EnchantingInventory) event.getInventory()).getSecondary();
    int lapis = lapisStack == null ? 0 : lapisStack.getAmount();
    for (PreparedEnchantment enchantment : event.getEnchantments()) {
      if (enchantment == null || enchantment.getEnchantment() == null) continue;

      TextStyle style;
      if (enchantment.getXpCost() <= event.getEnchanter().getLevel()
          && enchantment.getLapisCost() <= lapis) {
        style = Enchanting.ENCHANTMENT;
      } else {
        style = Enchanting.ENCHANTMENT_DISABLED;
      }

      Localizable name =
          new UnlocalizedText(enchantment.getEnchantment().getName(), Enchanting.ENCHANTMENT_NAME);
      Localizable level = new LocalizedNumber(enchantment.getLevel(), Enchanting.ENCHANTMENT_LEVEL);
      Localizable expCost =
          new LocalizedNumber(enchantment.getXpCost(), Enchanting.ENCHANTMENT_EXP_COST);
      Localizable lapisCost =
          new LocalizedNumber(enchantment.getLapisCost(), Enchanting.ENCHANTMENT_LAPIS_COST);
      event
          .getEnchanter()
          .sendMessage(
              UbiquitousMessages.LEGACY_ENCHANT.with(style, name, level, expCost, lapisCost));
    }
  }

  private boolean isLapis(ItemStack stack) {
    return stack != null && stack.getType() == Material.INK_SACK && stack.getDurability() == 4;
  }
}

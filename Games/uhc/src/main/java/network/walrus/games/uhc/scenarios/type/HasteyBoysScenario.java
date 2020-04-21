package network.walrus.games.uhc.scenarios.type;

import network.walrus.games.core.util.GameTask;
import network.walrus.games.uhc.UHCMessages;
import network.walrus.games.uhc.scenarios.Scenario;
import network.walrus.games.uhc.scenarios.ScenarioAuthorInfo;
import network.walrus.ubiquitous.bukkit.tracker.event.entity.EntityDeathEvent;
import network.walrus.utils.bukkit.inventory.MultiMaterialMatcher;
import network.walrus.utils.core.text.LocalizedFormat;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

/**
 * Scenario which enchants tools with efficiency and durability.
 *
 * @author Austin Mayes
 */
public class HasteyBoysScenario extends Scenario {

  private static final MultiMaterialMatcher TOOLS =
      new MultiMaterialMatcher(
          Material.DIAMOND_AXE,
          Material.GOLD_AXE,
          Material.IRON_AXE,
          Material.STONE_AXE,
          Material.WOOD_AXE,
          Material.DIAMOND_PICKAXE,
          Material.GOLD_PICKAXE,
          Material.IRON_PICKAXE,
          Material.STONE_PICKAXE,
          Material.WOOD_PICKAXE,
          Material.DIAMOND_SPADE,
          Material.GOLD_SPADE,
          Material.IRON_SPADE,
          Material.STONE_SPADE,
          Material.WOOD_SPADE);

  @Override
  public String name() {
    return "HasteyBoys";
  }

  @Override
  public LocalizedFormat[] description() {
    return UHCMessages.SCEN_DESC_HASTEY_BOYS;
  }

  @Override
  public MaterialData icon() {
    return new MaterialData(Material.DIAMOND_PICKAXE);
  }

  @Override
  public ScenarioAuthorInfo authorInfo() {
    return ScenarioAuthorInfo.UNKNOWN;
  }

  /** Enchant items */
  @EventHandler(priority = EventPriority.HIGHEST)
  public void onCraft(CraftItemEvent event) {
    enchantItem(event.getInventory().getResult());
  }

  /*
   * Because Bukkit is horrible, CraftItemEvent's don't actually take the result of shift click crafting
   * into account so we have to do this hack instead.
   */
  /** Handle shift clicks */
  @EventHandler(priority = EventPriority.HIGHEST)
  public void onClick(InventoryClickEvent event) {
    if (!event.isShiftClick()) return;
    if (event.getSlotType() != SlotType.RESULT) return;

    InventoryType type = event.getClickedInventory().getType();
    if (type != InventoryType.CRAFTING && type != InventoryType.WORKBENCH) return;
    if (!TOOLS.matches(event.getCurrentItem().getData())) return;

    if (event.isShiftClick()) {
      GameTask.of(
              "hasteyboys-enchant",
              () -> {
                for (ItemStack stack : event.getWhoClicked().getInventory()) {
                  enchantItem(stack);
                }
              })
          .later(1);
    }
  }

  /** Enchant items */
  @EventHandler(priority = EventPriority.HIGHEST)
  public void onDeath(EntityDeathEvent event) {
    for (ItemStack stack : event.getDrops()) {
      enchantItem(stack);
    }
  }

  private void enchantItem(ItemStack stack) {
    if (stack == null) {
      return;
    }

    if (TOOLS.matches(stack.getData()) && !stack.containsEnchantment(Enchantment.DIG_SPEED)) {
      stack.addUnsafeEnchantment(Enchantment.DIG_SPEED, 3);
      stack.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
    }
  }
}

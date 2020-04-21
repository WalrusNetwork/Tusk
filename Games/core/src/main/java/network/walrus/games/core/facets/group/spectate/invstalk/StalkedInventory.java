package network.walrus.games.core.facets.group.spectate.invstalk;

import network.walrus.games.core.GamesCoreMessages;
import network.walrus.utils.core.color.NetworkColorConstants.Games;
import network.walrus.utils.core.text.LocalizedNumber;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Represents any type of inventory that a player is peering into, virtually.
 *
 * @author Austin Mayes
 */
public class StalkedInventory {

  private final Player player;
  private final Inventory original; // the actual inventory
  private final Inventory view; // the virtual inventory (original replica)

  /**
   * @param player who is viewing this inventory
   * @param original inventory which is being stalked
   * @param view virtual inventory window
   */
  StalkedInventory(Player player, Inventory original, Inventory view) {
    this.player = player;
    this.original = original;
    this.view = view;
  }

  boolean isOpen() {
    return this.player.getOpenInventory().equals(this.view)
        || this.player.getOpenInventory().getTopInventory().equals(this.view);
  }

  void open() {
    this.player.openInventory(this.view);
  }

  void close() {
    if (isOpen()) {
      this.player.closeInventory();
    }
  }

  void update() {
    if (this.original instanceof PlayerInventory) {
      Player holder = (Player) this.original.getHolder();
      PlayerInventory inventory = (PlayerInventory) this.original;

      // Health/Food - Top Left
      int health = (int) Math.floor(holder.getHealth());
      int healthMax = (int) Math.floor(holder.getMaxHealth());
      int food = holder.getFoodLevel();

      ItemStack healthItem = new ItemStack(Material.REDSTONE, health);
      ItemMeta meta = healthItem.getItemMeta();
      LocalizedNumber healthNumber = new LocalizedNumber(health);
      LocalizedNumber healthMaxNumber = new LocalizedNumber(healthMax);
      meta.setDisplayName(
          GamesCoreMessages.UI_HEALTH
              .with(Games.Items.HEALTH_NAME, healthNumber, healthMaxNumber)
              .render(player)
              .toLegacyText());
      healthItem.setItemMeta(meta);

      ItemStack foodItem = new ItemStack(Material.MELON, food);
      meta = foodItem.getItemMeta();
      LocalizedNumber foodNumber = new LocalizedNumber(food);
      LocalizedNumber foodMaxNumber = new LocalizedNumber(20);
      meta.setDisplayName(
          GamesCoreMessages.UI_FOOD_LEVEL
              .with(Games.Items.HUNGER_NAME, foodNumber, foodMaxNumber)
              .render(player)
              .toLegacyText());
      foodItem.setItemMeta(meta);

      this.view.setItem(0, healthItem);
      this.view.setItem(1, foodItem);

      // Todo: Active Potion Effects

      // Armor - Top Right
      for (int i = 0; i < inventory.getArmorContents().length; i++) {
        this.view.setItem(
            i + 5, inventory.getArmorContents()[inventory.getArmorContents().length - i - 1]);
      }

      // Inventory - Middle
      for (int i = 9; i < inventory.getSize(); i++) {
        this.view.setItem(i, inventory.getItem(i));
      }

      // Hotbar - Bottom
      for (int i = 0; i < 9; i++) {
        this.view.setItem(i + 36, inventory.getItem(i));
      }
    } else {
      // Other inventories are easy
      for (int i = 0; i < this.original.getSize(); i++) {
        this.view.setItem(i, this.original.getContents()[i]);
      }
    }
  }
}

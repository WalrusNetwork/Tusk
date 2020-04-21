package network.walrus.ubiquitous.bukkit.gizmos.pet;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.SpawnEgg;

/**
 * Utilities for working with pets.
 *
 * @author Austin Mayes
 */
public class PetUtils {

  /**
   * Get the {@link ItemStack} for a specific entity type.
   *
   * @param type tp get the egg for
   * @return a spawn egg based on the entity type
   */
  public static ItemStack entityEgg(EntityType type) {
    switch (type) {
      case EGG:
        return new ItemStack(Material.EGG);
      default:
        ItemStack stack = new ItemStack(Material.MONSTER_EGG);
        stack.setData(new SpawnEgg(type));
        return stack;
    }
  }

  /**
   * Get the {@link ChatColor} designation for a supplied {@link EntityType}.
   *
   * @param type to get the color for
   * @return the color assigned to the entity type
   */
  public static ChatColor entityColor(EntityType type) {
    switch (type) {
      case HORSE:
      case BLAZE:
      case MAGMA_CUBE:
      case FIREBALL:
      case SMALL_FIREBALL:
        return ChatColor.GOLD;
      case ENDERMITE:
      case ENDER_CRYSTAL:
      case ENDER_DRAGON:
      case ENDERMAN:
      case ENDER_PEARL:
      case ENDER_SIGNAL:
        return ChatColor.DARK_PURPLE;
      case SQUID:
      case WITHER:
      case BAT:
      case CAVE_SPIDER:
      case SPIDER:
      case WITHER_SKULL:
        return ChatColor.BLACK;
      case MUSHROOM_COW:
      case PIG:
      case PRIMED_TNT:
        return ChatColor.RED;
      case SLIME:
      case CREEPER:
        return ChatColor.GREEN;
      case GIANT:
      case ZOMBIE:
        return ChatColor.DARK_GREEN;
      case PIG_ZOMBIE:
        return ChatColor.DARK_RED;
      case SKELETON:
      case WOLF:
      case SILVERFISH:
        return ChatColor.GRAY;
      case VILLAGER:
      case EGG:
      case OCELOT:
      case WITCH:
        return ChatColor.YELLOW;
      case GUARDIAN:
        return ChatColor.AQUA;
      case SHEEP:
        return ChatColor.BLUE;
      case COW:
      case RABBIT:
        return ChatColor.DARK_GRAY;
      case CHICKEN:
      case GHAST:
      case SNOWMAN:
      case IRON_GOLEM:
        return ChatColor.WHITE;
      default:
        throw new IllegalArgumentException("Unknown entity type " + type);
    }
  }
}

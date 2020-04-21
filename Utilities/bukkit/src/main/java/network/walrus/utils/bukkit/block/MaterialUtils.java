package network.walrus.utils.bukkit.block;

import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.material.MaterialData;

/**
 * Utilities for working with {@link Material}s.
 *
 * @author Overcast Network
 */
public abstract class MaterialUtils {

  /**
   * Is the given {@link Material} a block that collides with entities?
   *
   * <p>Note that this is not always 100% correct. There are a few blocks for which solidness
   * depends on state, such as fence gates.
   */
  public static boolean isColliding(Material material) {
    if (material == null) {
      return false;
    }

    switch (material) {
        // Missing from Bukkit
      case CARPET:
      case WATER_LILY:
        return true;

        // Incorrectly included by Bukkit
      case SIGN_POST:
      case WALL_SIGN:
      case WOOD_PLATE:
      case STONE_PLATE:
      case IRON_PLATE:
      case GOLD_PLATE:
      case STANDING_BANNER:
      case WALL_BANNER:
        return false;

      default:
        return material.isSolid();
    }
  }

  /** @see #isColliding(Material) */
  public static boolean isColliding(MaterialData material) {
    return isColliding(material.getItemType());
  }

  /** @see #isColliding(Material) */
  public static boolean isColliding(BlockState block) {
    return isColliding(block.getMaterial());
  }
}

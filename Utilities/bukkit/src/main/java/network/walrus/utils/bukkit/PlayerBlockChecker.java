package network.walrus.utils.bukkit;

import org.bukkit.Location;
import org.bukkit.Material;

/**
 * Utilities for determining the actions which can be performed at a specific location given the
 * block type in the world at that location.
 *
 * @author Overcast Network
 */
public class PlayerBlockChecker {

  /**
   * Determine if the block at a specific location is climbable.
   *
   * @param location to get block at
   * @return if a climbable block is at the location
   */
  public static boolean isClimbing(Location location) {
    Material material = location.getBlock().getType();
    return material == Material.LADDER || material == Material.VINE;
  }

  /**
   * Determine if the block at a specific location can be swam inside of.
   *
   * @param location to get block at
   * @param liquidType to search for
   * @return if a block is at the location that can be swam in
   */
  public static boolean isSwimming(Location location, Material liquidType) {
    Material material = location.getBlock().getType();
    switch (liquidType) {
      case WATER:
      case STATIONARY_WATER:
        return material == Material.WATER || material == Material.STATIONARY_WATER;

      case LAVA:
      case STATIONARY_LAVA:
        return material == Material.LAVA || material == Material.STATIONARY_LAVA;

      default:
        return false;
    }
  }
}

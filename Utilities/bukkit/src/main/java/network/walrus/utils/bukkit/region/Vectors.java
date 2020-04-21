package network.walrus.utils.bukkit.region;

import org.bukkit.util.Vector;

/**
 * Vector utils
 *
 * @author Avicus Network
 */
public class Vectors {

  /**
   * Determine if vector A is less than vector B.
   *
   * @param a to be used as base
   * @param b to check for less-ness
   * @return if A is less than B
   */
  public static boolean isLess(Vector a, Vector b) {
    return a.getX() < b.getX() && a.getY() < b.getY() && a.getZ() < b.getZ();
  }

  /**
   * Determine if vector A is greater than vector B.
   *
   * @param a to be used as base
   * @param b to check for greater-ness
   * @return if A is greater than B
   */
  public static boolean isGreater(Vector a, Vector b) {
    return a.getX() > b.getX() && a.getY() > b.getY() && a.getZ() > b.getZ();
  }
}

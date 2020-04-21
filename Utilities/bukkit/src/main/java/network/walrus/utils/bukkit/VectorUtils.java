package network.walrus.utils.bukkit;

import network.walrus.utils.bukkit.block.MaterialUtils;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

/**
 * Utilities for working with vectors.
 *
 * @author Overcast Network
 */
public class VectorUtils {

  /**
   * A very simple deflection model. Check for a solid neighbor block and "bounce" the velocity off
   * of it.
   *
   * @param center point of the reflective force
   * @param toReflect entity being reflected
   * @param power of the reflective force
   */
  public static void deflect(Vector center, Entity toReflect, double power) {
    Vector v = toReflect.getLocation().subtract(center).toVector();
    double distance = v.length();
    v.normalize().multiply(power / Math.max(1d, distance));

    Block block = toReflect.getLocation().getBlock();
    Block west = block.getRelative(BlockFace.WEST);
    Block east = block.getRelative(BlockFace.EAST);
    Block down = block.getRelative(BlockFace.DOWN);
    Block up = block.getRelative(BlockFace.UP);
    Block north = block.getRelative(BlockFace.NORTH);
    Block south = block.getRelative(BlockFace.SOUTH);

    if ((v.getX() < 0 && west != null && MaterialUtils.isColliding(west.getType()))
        || v.getX() > 0 && east != null && MaterialUtils.isColliding(east.getType())) {
      v.setX(-v.getX());
    }

    if ((v.getY() < 0 && down != null && MaterialUtils.isColliding(down.getType()))
        || v.getY() > 0 && up != null && MaterialUtils.isColliding(up.getType())) {
      v.setY(-v.getY());
    }

    if ((v.getZ() < 0 && north != null && MaterialUtils.isColliding(north.getType()))
        || v.getZ() > 0 && south != null && MaterialUtils.isColliding(south.getType())) {
      v.setZ(-v.getZ());
    }

    toReflect.setVelocity(v);
  }
}

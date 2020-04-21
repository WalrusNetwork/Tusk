package network.walrus.utils.bukkit.block;

import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.util.BlockVector;

/**
 * Utilities for working with {@link BlockFace}s.
 *
 * @author Overcast Network
 */
public class BlockFaceUtils {

  public static final BlockFace[] NEIGHBORS = {
    BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.UP, BlockFace.DOWN
  };
  private static final BlockFace[] clockwise =
      new BlockFace[] {
        BlockFace.SOUTH,
        BlockFace.SOUTH_SOUTH_WEST,
        BlockFace.SOUTH_WEST,
        BlockFace.WEST_SOUTH_WEST,
        BlockFace.WEST,
        BlockFace.WEST_NORTH_WEST,
        BlockFace.NORTH_WEST,
        BlockFace.NORTH_NORTH_WEST,
        BlockFace.NORTH,
        BlockFace.NORTH_NORTH_EAST,
        BlockFace.NORTH_EAST,
        BlockFace.EAST_NORTH_EAST,
        BlockFace.EAST,
        BlockFace.EAST_SOUTH_EAST,
        BlockFace.SOUTH_EAST,
        BlockFace.SOUTH_SOUTH_EAST,
      };

  /**
   * Get a {@link BlockVector} representing the location of a block that would be at the position
   * represented by the provided block face.
   *
   * @param pos to use as the center point
   * @param face to modify the position by
   * @return vector representing the block at that position
   */
  public static BlockVector getRelative(BlockVector pos, BlockFace face) {
    return new BlockVector(
        pos.getBlockX() + face.getModX(),
        pos.getBlockY() + face.getModY(),
        pos.getBlockZ() + face.getModZ());
  }

  /**
   * Get the block at the position represented by the provided block face used the supplied block as
   * a center point.
   *
   * @param block to use as the center point
   * @param face to modify the position by
   * @return block at the supplied face
   */
  public static BlockState getRelative(BlockState block, BlockFace face) {
    return block
        .getWorld()
        .getBlockAt(
            block.getX() + face.getModX(),
            block.getY() + face.getModY(),
            block.getZ() + face.getModZ())
        .getState();
  }

  /**
   * Convert a yaw value to the {@link BlockFace} which most closely matches that yaw.
   *
   * @param yaw to convert
   * @return block face closest to the supplied yaw
   */
  public static BlockFace yawToFace(float yaw) {
    return clockwise[Math.round(yaw / 22.5f) & 0xf];
  }

  /**
   * Convert a {@link BlockFace} to the yaw value which would represent the face's location relative
   * to a center position.
   *
   * @param face to get yaw for
   * @return yaw of the supplied face
   */
  public static float faceToYaw(BlockFace face) {
    switch (face) {
      case SOUTH:
        return 0f;
      case SOUTH_SOUTH_WEST:
        return 22.5f;
      case SOUTH_WEST:
        return 45f;
      case WEST_SOUTH_WEST:
        return 67.5f;
      case WEST:
        return 90f;
      case WEST_NORTH_WEST:
        return 112.5f;
      case NORTH_WEST:
        return 135f;
      case NORTH_NORTH_WEST:
        return 157.5f;
      case NORTH:
        return -180f;
      case NORTH_NORTH_EAST:
        return -157.5f;
      case NORTH_EAST:
        return -135f;
      case EAST_NORTH_EAST:
        return -112.5f;
      case EAST:
        return -90f;
      case EAST_SOUTH_EAST:
        return -67.5f;
      case SOUTH_EAST:
        return -45f;
      case SOUTH_SOUTH_EAST:
        return -22.5f;
      default:
        return 0f;
    }
  }
}

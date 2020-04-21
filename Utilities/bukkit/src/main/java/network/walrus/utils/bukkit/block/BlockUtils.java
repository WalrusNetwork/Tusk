package network.walrus.utils.bukkit.block;

import gnu.trove.set.TLongSet;
import gnu.trove.set.hash.TLongHashSet;
import java.util.Collection;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

/**
 * Various utilities for working with block locations.
 *
 * @author Overcast Network
 */
public class BlockUtils {

  static final long ENCODED_NULL_POS = Long.MIN_VALUE;
  /** BlockVector encoding API - pack a BlockVector into a single long */
  private static final int SHIFT = 21;

  private static final long MASK = ~(-1 << SHIFT);
  private static final long SIGN_MASK = 1 << (SHIFT - 1);

  /** Create a block vector from a block state. */
  public static BlockVector position(BlockState block) {
    return new BlockVector(block.getX(), block.getY(), block.getZ());
  }

  /** Get the exact center of a location. */
  public static Location center(Location location) {
    Location center = location.clone();
    center.setX(center.getBlockX() + 0.5);
    center.setY(center.getBlockY() + 0.5);
    center.setZ(center.getBlockZ() + 0.5);
    return center;
  }

  /** @see #center(Location) */
  public static Location center(Block block) {
    return center(block.getLocation());
  }

  /** @see #center(Location) */
  public static Location center(BlockState state) {
    return center(state.getLocation());
  }

  /**
   * Return the "base" {@link Location} of the block at the given location, which is the bottom
   * center point on the block (i.e. the location of any block-shaped entity that is aligned with
   * the block).
   */
  public static Location base(Location location) {
    Location center = location.clone();
    center.setX(center.getBlockX() + 0.5);
    center.setY(center.getBlockY());
    center.setZ(center.getBlockZ() + 0.5);
    return center;
  }

  /** @see #base(Location) */
  public static Location base(Block block) {
    return base(block.getLocation());
  }

  /** @see #base(Location) */
  public static Location base(BlockState state) {
    return base(state.getLocation());
  }

  /** Determine if the supplied point is inside of the block's location. */
  public static boolean isInside(Vector point, Location blockLocation) {
    return blockLocation.getX() <= point.getX()
        && point.getX() <= blockLocation.getX() + 1
        && blockLocation.getY() <= point.getY()
        && point.getY() <= blockLocation.getY() + 1
        && blockLocation.getZ() <= point.getZ()
        && point.getZ() <= blockLocation.getZ() + 1;
  }

  /** Decode a single component from the packed coordinates */
  private static long unpack(long packed, int shift) {
    packed >>= shift;

    // Sign extension
    if ((packed & SIGN_MASK) == 0) {
      packed &= MASK;
    } else {
      packed |= ~MASK;
    }

    return packed;
  }

  /** Apply the decoded position to an existing {@link BlockVector}. */
  public static BlockVector decodePos(long from, BlockVector to) {
    to.setX(unpack(from, 0));
    to.setY(unpack(from, SHIFT));
    to.setZ(unpack(from, SHIFT + SHIFT));
    return to;
  }

  /** Create a {@link BlockVector} using {@link #unpack(long, int)} */
  public static BlockVector decodePos(long encoded) {
    return new BlockVector(
        unpack(encoded, 0), unpack(encoded, SHIFT), unpack(encoded, SHIFT + SHIFT));
  }

  /**
   * Encode an x,y,z coordinate into a single long which can be used to represent the coordinate.
   *
   * @param x coordinate to be encoded
   * @param y coordinate to be encoded
   * @param z coordinate to be encoded
   * @return a single long representing the position
   */
  public static long encodePos(long x, long y, long z) {
    return (x & MASK) | ((y & MASK) << SHIFT) | ((z & MASK) << (SHIFT + SHIFT));
  }

  /** @see #encodePos(long, long, long) */
  public static long encodePos(BlockVector vector) {
    return encodePos(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());
  }

  /** @see #encodePos(long, long, long) */
  public static long encodePos(Block block) {
    return encodePos(block.getX(), block.getY(), block.getZ());
  }

  /** @see #encodePos(long, long, long) */
  public static long encodePos(BlockState block) {
    return encodePos(block.getX(), block.getY(), block.getZ());
  }

  /** @see #encodePos(BlockVector) */
  public static TLongSet encodePosSet(Collection<?> vectors) {
    TLongSet encoded = new TLongHashSet(vectors.size());
    for (Object o : vectors) {
      if (o instanceof BlockVector) {
        encoded.add(encodePos((BlockVector) o));
      }
    }
    return encoded;
  }

  /**
   * Return the encoded location neighboring the given location on the given side. Equivalent to
   * {@link Block#getRelative}.
   */
  public static long neighborPos(long encoded, BlockFace face) {
    return encodePos(
        unpack(encoded, 0) + face.getModX(),
        unpack(encoded, SHIFT) + face.getModY(),
        unpack(encoded, SHIFT + SHIFT) + face.getModZ());
  }

  /** Convenience method to get the block at the supplied location in the specified world. */
  public static Block blockAt(World world, Vector pos) {
    return world.getBlockAt(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ());
  }

  /**
   * Return the {@link Block} in the given {@link World}, at the given encoded location. This method
   * is more efficient than creating an intermediate {@link BlockVector}, and more convenient.
   */
  public static Block blockAt(World world, long encoded) {
    return world.getBlockAt(
        (int) unpack(encoded, 0),
        (int) unpack(encoded, SHIFT),
        (int) unpack(encoded, SHIFT + SHIFT));
  }
}

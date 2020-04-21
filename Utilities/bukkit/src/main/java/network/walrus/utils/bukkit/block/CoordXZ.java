package network.walrus.utils.bukkit.block;

/**
 * A simple storage class for chunk x/z values.
 *
 * @author Brett Flannigan
 */
public class CoordXZ {
  public int x, z;

  /**
   * @param x coordinate
   * @param z coordinate
   */
  public CoordXZ(int x, int z) {
    this.x = x;
    this.z = z;
  }

  /**
   * Convert a block coordinate to a chunk coordinate
   *
   * @param blockVal to convert
   * @return chunk representation of the block value
   */
  public static int blockToChunk(int blockVal) { // 1 chunk is 16x16 blocks
    return blockVal >> 4; // ">>4" == "/16"
  }

  /**
   * Convert a block coordinate to a region coordinate
   *
   * @param blockVal to convert
   * @return region representation of the block value
   */
  public static int blockToRegion(int blockVal) { // 1 region is 512x512 blocks
    return blockVal >> 9; // ">>9" == "/512"
  }

  /**
   * Convert a chunk coordinate to a region coordinate
   *
   * @param chunkVal to convert
   * @return region representation of the chunk value
   */
  public static int chunkToRegion(int chunkVal) { // 1 region is 32x32 chunks
    return chunkVal >> 5; // ">>5" == "/32"
  }

  /**
   * Convert a chunk coordinate to a block coordinate
   *
   * @param chunkVal to convert
   * @return block representation of the chunk value
   */
  public static int chunkToBlock(int chunkVal) {
    return chunkVal << 4; // "<<4" == "*16"
  }

  /**
   * Convert a region coordinate to a block coordinate
   *
   * @param regionVal to convert
   * @return block representation of the region value
   */
  public static int regionToBlock(int regionVal) {
    return regionVal << 9; // "<<9" == "*512"
  }

  /**
   * Convert a region coordinate to a chunk coordinate
   *
   * @param regionVal to convert
   * @return chunk representation of the region value
   */
  public static int regionToChunk(int regionVal) {
    return regionVal << 5; // "<<5" == "*32"
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    else if (obj == null || obj.getClass() != this.getClass()) return false;

    CoordXZ test = (CoordXZ) obj;
    return test.x == this.x && test.z == this.z;
  }

  @Override
  public int hashCode() {
    return (this.x << 9) + this.z;
  }
}

package network.walrus.utils.bukkit.region;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

/**
 * A query-able container holding {@link Vector}s allowing callers to check if locatable objects are
 * currently inside the area the region is specified to occupy.
 *
 * @author Avicus Network
 */
public interface Region {

  /**
   * Determine if this region contains a certain vector.
   *
   * @param vector to check
   * @return if this region contains the vector
   */
  boolean contains(Vector vector);

  /**
   * Get a random position from inside of this region.
   *
   * @param random instance used to generate random position
   * @return a random position from the region
   * @throws PositionUnavailableException if the region type does not support random position lookup
   */
  Vector getRandomPosition(Random random) throws PositionUnavailableException;

  /**
   * Get all non-empty chunks that this region occupies in a certain world.
   *
   * @param world to get chunk data from
   * @return all non-empty chunks that this region occupies
   * @throws PositionUnavailableException if the chunks cannot be determined for the region type
   */
  default Set<Chunk> getChunks(World world) throws PositionUnavailableException {
    Set<Chunk> set = new HashSet<>();
    for (Chunk chunk : world.getLoadedChunks()) {
      if (!chunk.isEmpty()) {
        set.add(chunk);
      }
    }
    return set;
  }

  /** @see #contains(Vector). */
  default boolean contains(Location location) {
    return this.contains(location.toVector());
  }

  /** @see #contains(Vector). */
  default boolean contains(Block block) {
    return this.contains(block.getLocation().toCenterLocation().toVector());
  }

  /** @see #contains(Vector). */
  default boolean contains(BlockState block) {
    return this.contains(block.getLocation().toCenterLocation().toVector());
  }

  /** @see #contains(Vector). */
  default boolean contains(Entity entity) {
    return this.contains(entity.getLocation());
  }
}

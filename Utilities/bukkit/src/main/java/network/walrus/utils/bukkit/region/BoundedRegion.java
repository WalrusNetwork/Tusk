package network.walrus.utils.bukkit.region;

import java.util.HashSet;
import java.util.Set;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.util.Vector;

/**
 * A region which can be defined inside of a minimum and maximum vector.
 *
 * @author Avicus Network
 */
public interface BoundedRegion extends Region, RepelableRegion, Iterable<Vector> {

  /** Minimum bound of the region */
  Vector min();

  /** Maximum bound of the region */
  Vector max();

  default Vector getCenter() {
    return min().getMidpoint(max());
  }

  @Override
  default Set<Chunk> getChunks(World world) {
    Set<Chunk> chunks = new HashSet<>();
    this.iterator().forEachRemaining(vector -> chunks.add(vector.toLocation(world).getChunk()));
    return chunks;
  }

  @Override
  default Vector getRepelVector(Vector from) {
    return from.subtract(getCenter()).normalize();
  }
}

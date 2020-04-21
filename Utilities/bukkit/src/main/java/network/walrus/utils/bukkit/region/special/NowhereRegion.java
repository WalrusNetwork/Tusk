package network.walrus.utils.bukkit.region.special;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import network.walrus.utils.bukkit.region.PositionUnavailableException;
import network.walrus.utils.bukkit.region.Region;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.util.Vector;

/**
 * Returns {@code false} or {@code empty} for all placement checks.
 *
 * @author Avicus Network
 */
public class NowhereRegion implements Region {

  @Override
  public boolean contains(Vector vector) {
    return false;
  }

  @Override
  public Set<Chunk> getChunks(World world) {
    return new HashSet<>();
  }

  @Override
  public Vector getRandomPosition(Random random) throws PositionUnavailableException {
    throw new PositionUnavailableException(this, true);
  }
}

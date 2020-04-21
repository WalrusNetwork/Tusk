package network.walrus.utils.bukkit.region.special;

import java.util.Random;
import network.walrus.utils.bukkit.region.Region;
import org.bukkit.util.Vector;

/**
 * All vectors, period.
 *
 * @author Avicus Network
 */
public class EverywhereRegion implements Region {

  @Override
  public boolean contains(Vector vector) {
    return true;
  }

  @Override
  public Vector getRandomPosition(Random random) {
    return new Vector(
        random.nextInt() - random.nextInt() + random.nextDouble(),
        random.nextInt(255) + random.nextDouble(),
        random.nextInt() - random.nextInt() + random.nextDouble());
  }
}

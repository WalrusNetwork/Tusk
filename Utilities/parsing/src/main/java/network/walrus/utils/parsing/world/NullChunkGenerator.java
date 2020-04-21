package network.walrus.utils.parsing.world;

import java.util.Random;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;

/**
 * Class responsible for generating empty chunks for {@link World}s.
 *
 * @author Avicus Network
 */
@SuppressWarnings("deprecation")
public class NullChunkGenerator extends ChunkGenerator {

  @Override
  public byte[] generate(World world, Random random, int x, int z) {
    return new byte[16 * 16 * 256];
  }
}

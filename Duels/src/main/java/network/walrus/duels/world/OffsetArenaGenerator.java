package network.walrus.duels.world;

import com.google.api.client.util.Sets;
import java.util.Set;
import network.walrus.duels.ArenaProperties;
import network.walrus.utils.bukkit.region.BoundedRegion;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.util.Vector;

/**
 * Utility class which copies a varying number of copies of a region across an axis. Used to
 * generate copies of an arena.
 *
 * @author Rafi Baum
 */
public class OffsetArenaGenerator {

  private static final int CHUNK_OFFSET = 8;
  private static final Vector CHUNK_OFFSET_VECTOR = new Vector(16 * CHUNK_OFFSET, 0, 0);

  private final Set<BlockState> originalStates;
  private final World world;
  private final ArenaProperties properties;
  private int copies = 0;

  /**
   * Constructor.
   *
   * @param world the arena exists in
   * @param properties of the original arena
   * @param copyAir whether the generator should copy air blocks or not
   */
  public OffsetArenaGenerator(World world, ArenaProperties properties, boolean copyAir) {
    this.world = world;
    this.properties = properties;
    this.originalStates = generateBlockStates(world, properties, copyAir);
  }

  /**
   * Generate a number of arenas based on an original copy.
   *
   * @param world world the original exists in
   * @param originalProperties properties of the original
   * @param copies number of copies to create
   * @return collection of arena property copies
   */
  public static Set<ArenaProperties> generateArenas(
      World world, ArenaProperties originalProperties, int copies) {
    return generateArenas(world, originalProperties, copies, false);
  }

  /**
   * Generate a number of arenas based on an original copy, keeping track of air blocks as well.
   *
   * @param world world the original exists in
   * @param originalProperties properties of the original
   * @param copies number of copies to create
   * @return collection of arena property copies
   */
  public static Set<ArenaProperties> generateArenasWithAir(
      World world, ArenaProperties originalProperties, int copies) {
    return generateArenas(world, originalProperties, copies, true);
  }

  private static Set<ArenaProperties> generateArenas(
      World world, ArenaProperties originalProperties, int numCopies, boolean copyAir) {
    Set<BlockState> originStates = generateBlockStates(world, originalProperties, copyAir);

    Set<ArenaProperties> copies = Sets.newHashSet();

    for (int i = 0; i < numCopies; i++) {
      Vector offset = CHUNK_OFFSET_VECTOR.clone().multiply(i + 1);
      copies.add(generateCopy(world, originalProperties, originStates, offset));
    }

    return copies;
  }

  private static ArenaProperties generateCopy(
      World world, ArenaProperties properties, Set<BlockState> original, Vector offset) {
    for (BlockState blockState : original) {
      Vector newLocation = blockState.getLocation().toVector().add(offset);
      world.fastBlockChange(newLocation, blockState.getMaterialData());
    }

    return properties.clone(offset);
  }

  private static void addBlockStates(
      BoundedRegion region,
      World world,
      Set<BlockState> blockStates,
      Set<Vector> visitedLocations,
      boolean copyAir) {
    for (Vector vector : region) {
      Block block = vector.toLocation(world).getBlock();

      if (block == null || !copyAir && block.getType() == Material.AIR) continue;
      if (visitedLocations.contains(vector)) continue;

      blockStates.add(block.getState());
      visitedLocations.add(vector);
    }
  }

  private static Set<BlockState> generateBlockStates(
      World world, ArenaProperties properties, boolean copyAir) {
    Set<BlockState> originStates = Sets.newHashSet();
    Set<Vector> visitedLocations = Sets.newHashSet();

    addBlockStates(properties.getArena(), world, originStates, visitedLocations, copyAir);
    addBlockStates(properties.getSpawn1(), world, originStates, visitedLocations, copyAir);
    addBlockStates(properties.getSpawn2(), world, originStates, visitedLocations, copyAir);
    addBlockStates(properties.getSpecSpawn(), world, originStates, visitedLocations, copyAir);

    return originStates;
  }

  /**
   * Dynamically generates a copy of the arena based on how many copies this generator has
   * previously created.
   *
   * @return properties of the new copy
   */
  public ArenaProperties generateCopy() {
    return generateCopy(
        world, properties, originalStates, new Vector(CHUNK_OFFSET * 16 * (copies + 1), 0, 0));
  }
}

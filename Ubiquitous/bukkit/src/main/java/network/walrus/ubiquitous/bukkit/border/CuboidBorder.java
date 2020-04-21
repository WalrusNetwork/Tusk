package network.walrus.ubiquitous.bukkit.border;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import network.walrus.utils.bukkit.region.shapes.CuboidRegion;
import org.bukkit.Material;
import org.bukkit.util.Vector;

/**
 * Represents a world border with the shape of a cuboid that contains a center point
 *
 * @author David Rodriguez
 */
public class CuboidBorder extends WorldBorder implements BoundedBorder {

  private final CuboidRegion region;
  private final List<Vector> cachedBlocks;
  private final Vector maxDirection;
  private final Vector minDirection;
  private Vector max;
  private Vector min;

  /**
   * Creates a cuboid border
   *
   * @param type the type to be used when applying the border
   * @param passable if the border can be trespassed
   * @param center region used when expanding the border
   * @param region that contains the bounds of the border
   */
  public CuboidBorder(Material type, boolean passable, Vector center, CuboidRegion region) {
    this(type, passable, new CuboidRegion(center, center), region);
  }

  /**
   * Creates a cuboid border
   *
   * @param type the type to be used when applying the border
   * @param passable if the border can be trespassed
   * @param region that contains the bounds of the border
   * @param center region used when expanding the border
   */
  public CuboidBorder(Material type, boolean passable, CuboidRegion center, CuboidRegion region) {
    super(type, passable);
    this.region = region;
    this.cachedBlocks = new ArrayList<>();
    this.maxDirection = center.max().clone().subtract(region.max()).normalize().multiply(-1);
    this.minDirection = center.min().clone().subtract(region.min()).normalize().multiply(-1);
    this.max = region.max().clone();
    this.min = region.min().clone();
    this.calculateBlocks();
  }

  @Override
  public void expand(Vector vector) {
    max.add(maxDirection.clone().multiply(vector));
    min.add(minDirection.clone().multiply(vector));
    calculateBlocks();
  }

  @Override
  public void reset() {
    this.max = region.max().clone();
    this.min = region.min().clone();
    calculateBlocks();
  }

  private void calculateBlocks() {
    cachedBlocks.clear();
    List<Integer> xBlocks = Arrays.asList(min.getBlockX(), max.getBlockX());
    List<Integer> zBlocks = Arrays.asList(min.getBlockZ(), max.getBlockZ());
    for (int x = min.getBlockX(); x <= max.getBlockX(); x++) {
      for (int y = max.getBlockY(); y >= 0; y--) {
        for (int z = min.getBlockZ(); z <= max.getBlockZ(); z++) {
          if (!xBlocks.contains(x) && !zBlocks.contains(z)) continue;
          cachedBlocks.add(new Vector(x, y, z));
        }
      }
    }
  }

  @Override
  public Vector getMax() {
    return max;
  }

  @Override
  public Vector getMin() {
    return min;
  }

  @Override
  public boolean contains(Vector vector) {
    return new CuboidRegion(min, max.add(new Vector(0, 0.5, 0))).contains(vector);
  }

  @Override
  public List<Vector> getCachedVectors() {
    return cachedBlocks;
  }
}

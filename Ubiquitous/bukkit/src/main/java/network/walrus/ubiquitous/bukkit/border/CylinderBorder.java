package network.walrus.ubiquitous.bukkit.border;

import static org.bukkit.util.NumberConversions.square;

import java.util.ArrayList;
import java.util.List;
import network.walrus.utils.bukkit.region.shapes.CylinderRegion;
import org.bukkit.Material;
import org.bukkit.util.Vector;

/**
 * Represents a world border with the shape of a cylinder that contains a center point and a radius
 *
 * @author David Rodriguez
 */
public class CylinderBorder extends WorldBorder {

  private final CylinderRegion region;
  private final List<Vector> cachedBlocks;
  private final Vector center;
  private Vector radius;

  /**
   * Creates a circular border
   *
   * @param type the type to be used when applying the border
   * @param passable if the border can be trespassed
   * @param center point of the border
   * @param radius of the border
   * @param height of the border
   */
  public CylinderBorder(Material type, boolean passable, Vector center, double radius, int height) {
    this(type, passable, center, new Vector(radius, height, radius));
  }

  /* Private constructor to make expanding easier by using a vector as the radius */
  private CylinderBorder(Material type, boolean passable, Vector center, Vector radius) {
    super(type, passable);
    this.region = new CylinderRegion(center, radius.getX(), radius.getBlockY());
    this.cachedBlocks = new ArrayList<>();
    this.center = center;
    this.radius = radius;
    calculateBlocks();
  }

  @Override
  public void expand(Vector vector) {
    radius.add(vector);
    calculateBlocks();
  }

  @Override
  public void reset() {
    radius = new Vector(region.radius(), region.height(), region.radius());
    calculateBlocks();
  }

  private void calculateBlocks() {
    cachedBlocks.clear();
    double centerX = center.getX();
    double centerY = center.getY();
    double centerZ = center.getZ();

    double radius = this.radius.getX();
    double height = this.radius.getY();
    double rSquared = radius * radius;

    for (double x = centerX - radius; x <= centerX + radius; x++) {
      for (double z = centerZ - radius; z <= centerZ + radius; z++) {
        for (double y = centerY; y <= centerY + height; y++) {
          if (square(centerX - x) + square(centerZ - z) >= rSquared) continue;
          Vector position = new Vector(x, y, z);
          // The magic number is the outside border radius. Ok Rafi?
          if (center.clone().setY(y).distanceSquared(position) < square(radius - 1)) continue;
          cachedBlocks.add(position);
        }
      }
    }
  }

  @Override
  public boolean contains(Vector vector) {
    return new CylinderRegion(center, radius.getX(), radius.getBlockY()).contains(vector);
  }

  @Override
  public List<Vector> getCachedVectors() {
    return cachedBlocks;
  }
}

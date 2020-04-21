package network.walrus.utils.bukkit.region.special;

import java.util.Optional;
import java.util.Random;
import network.walrus.utils.bukkit.region.Region;
import org.bukkit.util.Vector;

/**
 * Region which contains all vectors above the specified X, Y, and Z.
 *
 * @author Austin Mayes
 */
public class AboveRegion implements Region {

  private final Optional<Integer> x;
  private final Optional<Integer> y;
  private final Optional<Integer> z;

  /**
   * Constructor.
   *
   * @param x coordinate to be used as the lower bound, or empty to ignore this axis
   * @param y coordinate to be used as the lower bound, or empty to ignore this axis
   * @param z coordinate to be used as the lower bound, or empty to ignore this axis
   */
  public AboveRegion(Optional<Integer> x, Optional<Integer> y, Optional<Integer> z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  @Override
  public boolean contains(Vector vector) {
    boolean x = !this.x.isPresent() || vector.getX() > this.x.get();
    boolean y = !this.y.isPresent() || vector.getY() > this.y.get();
    boolean z = !this.z.isPresent() || vector.getZ() > this.z.get();

    return x && y && z;
  }

  @Override
  public Vector getRandomPosition(Random random) {
    int x = this.x.orElse(random.nextInt());
    int y = this.x.orElse(random.nextInt());
    int z = this.x.orElse(random.nextInt());
    return new Vector(x, y, z)
        .add(
            new Vector(
                this.x.isPresent() ? random.nextInt() : 0,
                this.y.isPresent() ? random.nextInt() : 0,
                this.z.isPresent() ? random.nextInt() : 0));
  }

  @Override
  public String toString() {
    return "AboveRegion{" + "x=" + x + ", y=" + y + ", z=" + z + '}';
  }
}

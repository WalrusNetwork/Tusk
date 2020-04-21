package network.walrus.utils.bukkit.region;

import java.util.Iterator;
import org.bukkit.util.Vector;

/**
 * A generic region iterator for regions that can't iterate nicely.
 *
 * @author Avicus Network
 */
public class BoundedRegionIterator implements Iterator<Vector> {

  private final BoundedRegion region;
  private final Vector min;
  private final Vector max;
  private double nextX;
  private double nextY;
  private double nextZ;

  /**
   * Constructor.
   *
   * @param region that this iterator will iterate over
   */
  public BoundedRegionIterator(BoundedRegion region) {
    this.region = region;

    this.min = region.min();
    this.max = region.max();

    this.nextX = Math.floor(this.min.getX()) + .5;
    this.nextY = this.min.getY();
    this.nextZ = Math.floor(this.min.getBlockZ()) + .5;

    forward();
  }

  private void forward() {
    while (hasNext() && !this.region.contains(new Vector(this.nextX, this.nextY, this.nextZ))) {
      forwardOne();
    }
  }

  private void forwardOne() {
    if (++this.nextX <= this.max.getX()) {
      return;
    }

    this.nextX = Math.floor(this.min.getX()) + .5;

    if (++this.nextY <= this.max.getY()) {
      return;
    }

    this.nextY = this.min.getY();

    if (++this.nextZ <= this.max.getZ()) {
      return;
    }

    // flag
    this.nextX = Integer.MIN_VALUE;
  }

  @Override
  public boolean hasNext() {
    return this.nextX != Integer.MIN_VALUE;
  }

  @Override
  public Vector next() {
    if (!hasNext()) {
      throw new java.util.NoSuchElementException();
    }

    Vector result = new Vector(this.nextX, this.nextY, this.nextZ);

    // step forward one block
    forwardOne();

    // continue forward until a block is found
    forward();

    return result;
  }

  /** @return the region that this object is performing iterations on */
  public BoundedRegion region() {
    return region;
  }
}

package network.walrus.utils.bukkit.block;

import static network.walrus.utils.bukkit.block.BlockUtils.decodePos;
import static network.walrus.utils.bukkit.block.BlockUtils.encodePos;

import gnu.trove.TLongCollection;
import gnu.trove.impl.Constants;
import gnu.trove.iterator.TLongIterator;
import gnu.trove.set.TLongSet;
import gnu.trove.set.hash.TLongHashSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import org.bukkit.util.BlockVector;

/**
 * Optimized implementation of a set of block locations. Coordinates are encoded into a single long
 * integer with bit masking, and stored in a Trove primitive collection.
 *
 * @author Overcast Network
 */
public class BlockVectorSet implements Set<BlockVector> {

  private final TLongSet set;

  /** @param set to initialize this object with */
  BlockVectorSet(TLongSet set) {
    this.set = set;
  }

  /** @param capacity of this set */
  public BlockVectorSet(int capacity) {
    this(new TLongHashSet(capacity, Constants.DEFAULT_LOAD_FACTOR, BlockUtils.ENCODED_NULL_POS));
  }

  /** @param that set of vectors to add to this set */
  public BlockVectorSet(Collection<? extends BlockVector> that) {
    this(that.size());
    addAll(that);
  }

  /** Create the set with a capacity of 10/ */
  public BlockVectorSet() {
    this(Constants.DEFAULT_CAPACITY);
  }

  /**
   * Create (or reuse) a {@link BlockVectorSet} using a collection of {@link BlockVector}s. If the
   * collection is already an instance of {@link BlockVectorSet}, it will be directly returned
   * instead.
   *
   * @param that set of vectors to add to this set
   * @return set with the supplied vectors
   */
  public static BlockVectorSet of(Collection<? extends BlockVector> that) {
    return that instanceof BlockVectorSet ? (BlockVectorSet) that : new BlockVectorSet(that);
  }

  @Override
  public int size() {
    return this.set.size();
  }

  @Override
  public boolean isEmpty() {
    return this.set.isEmpty();
  }

  @Override
  public Iterator<BlockVector> iterator() {
    final TLongIterator iter = this.set.iterator();

    return new Iterator<BlockVector>() {
      @Override
      public boolean hasNext() {
        return iter.hasNext();
      }

      @Override
      public BlockVector next() {
        return decodePos(iter.next());
      }

      @Override
      public void remove() {
        iter.remove();
      }
    };
  }

  /**
   * Return an iterator that reuses a single BlockVector instance, mutating it for each iteration.
   */
  public Iterator<BlockVector> mutableIterator() {
    final TLongIterator iter = set.iterator();
    return new Iterator<BlockVector>() {
      final BlockVector value = new BlockVector();

      @Override
      public boolean hasNext() {
        return iter.hasNext();
      }

      @Override
      public BlockVector next() {
        return decodePos(iter.next(), value);
      }
    };
  }

  /** Determine if this set contains the encoded position */
  public boolean contains(long encoded) {
    return this.set.contains(encoded);
  }

  /** Determine if this set contains the encoded position */
  public boolean contains(int x, int y, int z) {
    return this.contains(encodePos(x, y, z));
  }

  @Override
  public boolean contains(Object o) {
    return o instanceof BlockVector && this.set.contains(encodePos((BlockVector) o));
  }

  /** Add an encoded position to this set. */
  public boolean add(long encoded) {
    return this.set.add(encoded);
  }

  /** Add an encoded position to this set. */
  public boolean add(int x, int y, int z) {
    return this.add(encodePos(x, y, z));
  }

  @Override
  public boolean add(BlockVector vector) {
    return this.add(encodePos(vector));
  }

  /** Remove an encoded position from this set. */
  public boolean remove(long encoded) {
    return this.set.remove(encoded);
  }

  /** Remove an encoded position from this set. */
  public boolean remove(int x, int y, int z) {
    return this.remove(encodePos(x, y, z));
  }

  @Override
  public boolean remove(Object o) {
    return o instanceof BlockVector && this.remove(encodePos((BlockVector) o));
  }

  /** Determine if this set contains all of the encoded vectors. */
  public boolean containsAll(long[] encoded) {
    return this.set.containsAll(encoded);
  }

  /** Determine if this set contains all of the encoded vectors. */
  public boolean containsAll(TLongCollection encoded) {
    return this.set.containsAll(encoded);
  }

  @Override
  public boolean containsAll(Collection<?> c) {
    for (Object o : c) {
      if (!this.contains(o)) {
        return false;
      }
    }
    return true;
  }

  /** Add all of the encoded vectors to the set. */
  public boolean addAll(long[] encoded) {
    return this.set.addAll(encoded);
  }

  /** Add all of the encoded vectors to the set. */
  public boolean addAll(TLongCollection encoded) {
    return this.set.addAll(encoded);
  }

  @Override
  public boolean addAll(Collection<? extends BlockVector> vectors) {
    for (BlockVector v : vectors) {
      this.add(v);
    }
    return false;
  }

  /** Retain only the supplied encoded vectors in the set. */
  public boolean retainAll(long[] encoded) {
    return this.set.retainAll(encoded);
  }

  /** Retain only the supplied encoded vectors in the set. */
  public boolean retainAll(TLongCollection encoded) {
    return this.set.retainAll(encoded);
  }

  @Override
  public boolean retainAll(Collection<?> vectors) {
    return this.retainAll(BlockUtils.encodePosSet(vectors));
  }

  /** Remove all of the supplied encoded vectors from the set. */
  public boolean removeAll(TLongCollection encoded) {
    return set.removeAll(encoded);
  }

  /** Remove all of the supplied encoded vectors from the set. */
  public boolean removeAll(long[] encoded) {
    return set.removeAll(encoded);
  }

  @Override
  public boolean removeAll(Collection<?> vectors) {
    return this.removeAll(BlockUtils.encodePosSet(vectors));
  }

  @Override
  public void clear() {
    this.set.clear();
  }

  @Override
  public Object[] toArray() {
    throw new UnsupportedOperationException();
  }

  @Override
  public <T> T[] toArray(T[] a) {
    throw new UnsupportedOperationException();
  }

  /** Pick a random vector from the set. */
  public BlockVector chooseRandom(Random random) {
    // The Trove set uses a sparse array, so there isn't really any
    // faster way to do this, not even by messing with Trove internals.
    final TLongIterator iterator = set.iterator();
    long encoded = 0;
    for (int n = random.nextInt(size()); n >= 0; n--) {
      encoded = iterator.next();
    }
    return decodePos(encoded);
  }
}

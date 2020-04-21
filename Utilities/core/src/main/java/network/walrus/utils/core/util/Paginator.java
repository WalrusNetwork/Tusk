package network.walrus.utils.core.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * Pagintes a collection of items.
 *
 * @param <T> type of object being paginated
 * @author Avicus Network
 */
public class Paginator<T> {

  private Collection<T> collection;
  private int perPage;

  /**
   * @param collection The full collection of items.
   * @param perPage The number of items per page.
   */
  public Paginator(Collection<T> collection, int perPage) {
    this.collection = new ArrayList<>(collection);
    this.perPage = perPage;
  }

  /**
   * Get the full collection of items.
   *
   * @return The full collection of items.
   */
  public Collection<T> getCollection() {
    return this.collection;
  }

  /**
   * Set the full collection of items.
   *
   * @param list The full collection of items.
   */
  public void setCollection(Collection<T> list) {
    this.collection = new ArrayList<>(list);
  }

  /**
   * Get the number of items per page.
   *
   * @return The number of items per page.
   */
  public int getPerPage() {
    return this.perPage;
  }

  /**
   * Set the number of items per page.
   *
   * @param perPage The number of items per page.
   */
  public void setPerPage(int perPage) {
    this.perPage = perPage;
  }

  /**
   * Get the number of pages.
   *
   * @return The number of pages.
   */
  public int getPageCount() {
    if (this.collection.isEmpty()) {
      return 0;
    }
    return (int) Math.ceil((double) this.collection.size() / (double) this.perPage);
  }

  /**
   * Get the index of an item.
   *
   * @param item The item.
   * @return The index of the item.
   * @throws IllegalArgumentException If the item is not in the collection.
   */
  public int getIndex(T item) throws IllegalArgumentException {
    if (!this.collection.contains(item)) {
      throw new IllegalArgumentException("Item is not in the list.");
    }
    Iterator<T> iterator = this.collection.iterator();
    int index = 0;
    while (iterator.hasNext()) {
      T next = iterator.next();
      if (Objects.equals(next, item)) {
        break;
      }
      index++;
    }
    return index;
  }

  /**
   * Get the index of an item.
   *
   * @param item The item.
   * @return The page index of the item.
   * @throws IllegalArgumentException If the item is not in the collection.
   */
  public int getPageIndex(T item) throws IllegalArgumentException {
    int index = getIndex(item);
    return index / this.perPage;
  }

  /**
   * Get the page of items that contains the given item.
   *
   * @param item The item.
   * @return The collection of items.
   * @throws IllegalArgumentException If the item is not in the collection.
   */
  public Collection<T> getPage(T item) throws IllegalArgumentException {
    int index = getPageIndex(item);
    return getPage(index);
  }

  /** Check if a page exists. */
  public boolean hasPage(int pageIndex) {
    return pageIndex >= 0 && pageIndex < getPageCount();
  }

  /**
   * Get the page at the page index.
   *
   * @param pageIndex The index of the page (0 = first)
   * @return The collection of items.
   * @throws IllegalArgumentException If the page is invalid.
   */
  public Collection<T> getPage(int pageIndex) throws IllegalArgumentException {
    if (!hasPage(pageIndex)) {
      throw new IllegalArgumentException("Invalid page.");
    }

    int from = pageIndex * this.perPage;
    int to = Math.min(this.collection.size(), from + this.perPage);

    List<T> list = new ArrayList<>(this.collection);
    return list.subList(from, to);
  }
}

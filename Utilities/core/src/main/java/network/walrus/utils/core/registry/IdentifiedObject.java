package network.walrus.utils.core.registry;

/**
 * Container for an object which can be referenced by ID.
 *
 * @param <T> type of object
 * @author Avicus Network
 */
public class IdentifiedObject<T> implements Identifiable<T> {

  private final String id;
  private final T object;

  /**
   * Constructor
   *
   * @param id of the object
   * @param object that this object is serving as a wrapper for
   */
  public IdentifiedObject(String id, T object) {
    this.id = id;
    this.object = object;
  }

  @Override
  public String id() {
    return id;
  }

  @Override
  public T object() {
    return object;
  }
}

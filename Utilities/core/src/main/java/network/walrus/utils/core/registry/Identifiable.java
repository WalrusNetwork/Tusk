package network.walrus.utils.core.registry;

/**
 * A referential container allowing {@link T} to be referenced by an ID.
 *
 * @param <T> type of object
 * @author Avicus Network
 */
public interface Identifiable<T> {

  /** @return the globally unique ID of this object */
  String id();

  /** @return the object which the wrapper represents */
  T object();
}

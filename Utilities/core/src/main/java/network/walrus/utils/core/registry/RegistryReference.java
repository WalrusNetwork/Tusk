package network.walrus.utils.core.registry;

import java.util.Optional;

/**
 * Refers to an object by its "id".
 *
 * @param <T> type of object that this reference holds
 * @author Avicus Network
 */
public class RegistryReference<T> implements WeakReference<T> {

  private final Registry registry;
  private final Class<T> type;
  private final String id;
  private T result; // limits # of expensive calls to the registry

  /**
   * Constructor
   *
   * @param registry where this reference is being stored
   * @param type of object this one is wrapping
   * @param id of the object this one is wrapping
   */
  public RegistryReference(Registry registry, Class<T> type, String id) {
    this.registry = registry;
    this.type = type;
    this.id = id;
  }

  public Optional<T> getObject() {
    if (this.result != null) {
      return Optional.of(this.result);
    }

    try {
      Optional<T> optional = this.registry.get(this.type, this.id, false);
      if (optional.isPresent()) {
        this.result = optional.get();
        return optional;
      } else {
        // warning, not found
        // TODO: log this
        return Optional.empty();
      }
    } catch (Exception e) {
      // warning, wrong type found
      // TODO: log this
      return Optional.empty();
    }
  }

  @Override
  public String toString() {
    return "RegistryReference{"
        + "type="
        + type
        + ", id='"
        + id
        + '\''
        + ", result="
        + result
        + '}';
  }
}

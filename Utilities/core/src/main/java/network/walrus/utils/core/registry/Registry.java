package network.walrus.utils.core.registry;

import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;

/**
 * Object which holds a map of objects by their ID and allows for lookup.
 *
 * @author Avicus Network
 */
public class Registry {

  private final Map<String, Identifiable> objects = Maps.newHashMap();

  /**
   * Add an object to the registry.
   *
   * @param component to add
   * @throws RegistryException if an object with the same ID is already in the registry
   */
  public void add(Identifiable component) throws RegistryException {
    Optional<Object> existing = get(Object.class, component.id(), false);
    if (existing.isPresent()) {
      throw new RegistryException("Tried to register id \"" + component.id() + "\" twice.");
    }
    this.objects.put(component.id(), component);
  }

  /**
   * Add an object to the registry.
   *
   * @param id of the object
   * @param object to add
   * @param <T> type of object being added
   * @throws RegistryException if an object with the same ID is already in the registry
   */
  public <T> void add(String id, T object) throws RegistryException {
    Optional<Object> existing = get(Object.class, id, false);
    if (existing.isPresent()) {
      throw new RegistryException("Tried to register id \"" + id + "\" twice.");
    }
    this.objects.put(id, new IdentifiedObject<>(id, object));
  }

  /**
   * Add a collection of objects to the registry.
   *
   * @param objects to add
   * @throws RegistryException if an object with the same ID as any of the objects is already in the
   *     registry
   */
  public void add(List<? extends Identifiable> objects) throws RegistryException {
    for (Identifiable object : objects) {
      add(object);
    }
  }

  /**
   * Get an object from the registry by it's ID and type.
   *
   * @param type of object expected to be returned
   * @param id of the object to search for
   * @param required if an exception should be thrown if an object with the requested ID is not
   *     found
   * @param <T> type of object being requested
   * @return the object, if one exists in the registry
   * @throws RegistryException if the object was required and not found, or if the object with the
   *     requested ID is not an instance of {@link T}
   */
  @SuppressWarnings("unchecked")
  public <T> Optional<T> get(Class<T> type, String id, boolean required) throws RegistryException {
    @Nullable Identifiable found = this.objects.get(id);

    if (found == null) {
      if (required) {
        throw new RegistryException(
            "Unable to find required " + type.getSimpleName() + " for id \"" + id + "\".");
      }
      return Optional.empty();
    }

    // check the type is correct (found.getObject() instanceof type)
    if (type.isAssignableFrom(found.object().getClass())) {
      return Optional.of((T) found.object());
    }

    throw new RegistryException(
        "Registry mismatch for id \""
            + id
            + "\". Found "
            + found.object().getClass().getSimpleName()
            + " but expected "
            + type.getSimpleName()
            + ".");
  }

  /**
   * Get a {@link RegistryReference} wrapping the requested object.
   *
   * @param type of object expected to be returned
   * @param id of the object to search for
   * @param <T> type of object being requested
   * @return a reference to the object requested, regardless of presence
   */
  public <T> WeakReference<T> getReference(Class<T> type, String id) {
    Optional<T> optional = get(type, id, false);

    return optional
        .<WeakReference<T>>map(StaticReference::new)
        .orElseGet(() -> new RegistryReference<>(this, type, id));
  }

  public Map<String, Identifiable> getObjects() {
    return objects;
  }

  @Override
  public String toString() {
    return "Registry{" + "objects=" + objects + '}';
  }
}

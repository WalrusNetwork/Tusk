package network.walrus.utils.core.registry;

import java.util.Optional;
import javax.annotation.Nonnull;

/**
 * Refers to an object directly.
 *
 * @param <T> type of object that this reference holds
 * @author Avicus Network
 */
public class StaticReference<T> implements WeakReference<T> {

  private final T object;

  /**
   * Constructor.
   *
   * @param object which this reference is for
   */
  public StaticReference(@Nonnull T object) {
    this.object = object;
  }

  @Override
  public Optional<T> getObject() {
    return Optional.of(this.object);
  }

  @Override
  public String toString() {
    return "StaticReference{" + "object=" + object + '}';
  }
}

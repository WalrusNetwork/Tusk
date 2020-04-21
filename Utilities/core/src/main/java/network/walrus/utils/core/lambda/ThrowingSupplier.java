package network.walrus.utils.core.lambda;

import com.google.common.base.Throwables;
import java.util.function.Supplier;

/**
 * Supplier which will propagate exceptions thrown inside the call block to the parent caller.
 *
 * @param <T> type that this supplier returns
 * @param <E> type of exception this supplier can throw
 * @author Overcast Network
 */
@FunctionalInterface
public interface ThrowingSupplier<T, E extends Throwable> extends Supplier<T> {

  T getThrows() throws E;

  @Override
  default T get() {
    try {
      return getThrows();
    } catch (Throwable throwable) {
      throw Throwables.propagate(throwable);
    }
  }
}

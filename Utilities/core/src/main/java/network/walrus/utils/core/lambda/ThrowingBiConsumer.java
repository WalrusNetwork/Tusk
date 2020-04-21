package network.walrus.utils.core.lambda;

import com.google.common.base.Throwables;
import java.util.function.BiConsumer;

/**
 * @param <T> the type of the first argument to the operation
 * @param <U> the type of the second argument to the operation
 * @param <E> type of exception this class should catch
 * @author Overcast Network
 * @see ThrowingConsumer
 */
@FunctionalInterface
public interface ThrowingBiConsumer<T, U, E extends Throwable> extends BiConsumer<T, U> {

  @SuppressWarnings("JavaDoc")
  void acceptThrows(T t, U u) throws E;

  @Override
  default void accept(T t, U u) {
    try {
      acceptThrows(t, u);
    } catch (Throwable throwable) {
      throw Throwables.propagate(throwable);
    }
  }
}

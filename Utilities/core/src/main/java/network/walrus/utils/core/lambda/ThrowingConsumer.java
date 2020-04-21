package network.walrus.utils.core.lambda;

import com.google.common.base.Throwables;
import com.google.common.util.concurrent.UncheckedExecutionException;
import java.util.function.Consumer;

/**
 * A {@link Consumer} that can throw anything. Call {@link #acceptThrows} directly if you want to
 * handle the exceptions, or call {@link #accept} to have them wrapped in a {@link
 * UncheckedExecutionException}.
 *
 * @param <T> type of argument the function takes
 * @param <E> type of exception this class should catch
 * @author Overcast Network
 */
@FunctionalInterface
public interface ThrowingConsumer<T, E extends Throwable> extends Consumer<T> {

  @SuppressWarnings("JavaDoc")
  void acceptThrows(T t) throws E;

  @Override
  default void accept(T t) {
    try {
      acceptThrows(t);
    } catch (Throwable throwable) {
      throw Throwables.propagate(throwable);
    }
  }
}

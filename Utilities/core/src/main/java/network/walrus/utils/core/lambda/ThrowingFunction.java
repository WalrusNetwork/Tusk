package network.walrus.utils.core.lambda;

import com.google.common.base.Throwables;
import com.google.common.util.concurrent.UncheckedExecutionException;
import java.util.function.Function;

/**
 * A {@link Function} that can throw anything. Call {@link #applyThrows} directly if you want to
 * handle the exceptions, or call {@link #apply} to have them wrapped in a {@link
 * UncheckedExecutionException}.
 *
 * <p>TODO: Catches everything, not just {@link E}.. not ideal
 *
 * @param <T> type of argument the function takes
 * @param <R> type that the function returns
 * @param <E> type of exception this class should catch
 * @author Overcast Network
 */
@FunctionalInterface
public interface ThrowingFunction<T, R, E extends Throwable> extends Function<T, R> {

  @SuppressWarnings("JavaDoc")
  R applyThrows(T t) throws E;

  @Override
  default R apply(T t) {
    try {
      return applyThrows(t);
    } catch (Throwable throwable) {
      throw Throwables.propagate(throwable);
    }
  }
}

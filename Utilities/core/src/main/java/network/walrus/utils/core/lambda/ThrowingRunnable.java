package network.walrus.utils.core.lambda;

import com.google.common.base.Throwables;

/**
 * Runnable which will propagate exceptions thrown inside the call block to the parent caller.
 *
 * @param <E> type of exception this supplier can throw
 * @author Overcast Network
 */
public interface ThrowingRunnable<E extends Throwable> extends Runnable {

  @SuppressWarnings("JavaDoc")
  void runThrows() throws E;

  @Override
  default void run() {
    try {
      runThrows();
    } catch (Throwable throwable) {
      throw Throwables.propagate(throwable);
    }
  }
}

package network.walrus.utils.core.lambda;

import java.util.function.Consumer;
import java.util.function.UnaryOperator;

/**
 * Function utilities
 *
 * @author Overcast Network
 */
public interface FunctionUtils {

  /**
   * Return a {@link UnaryOperator} that passes its operand to the given {@link Consumer} only if it
   * is not null and then returns it.
   */
  static <T> UnaryOperator<T> tapUnlessNull(Consumer<? super T> consumer) {
    return t -> {
      if (t != null) {
        consumer.accept(t);
      }
      return t;
    };
  }
}

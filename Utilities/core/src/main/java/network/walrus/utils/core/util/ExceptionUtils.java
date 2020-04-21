package network.walrus.utils.core.util;

import com.google.common.util.concurrent.UncheckedExecutionException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import network.walrus.utils.core.lambda.ThrowingRunnable;
import network.walrus.utils.core.lambda.ThrowingSupplier;

/**
 * Utilities for making exceptions less hard to work with inside of lambdas.
 *
 * @author Overcast Network
 */
public class ExceptionUtils {

  /**
   * Perform trace formatting using {@link #formatStackTrace(StackTraceElement[], Predicate)} and
   * keep every element.
   */
  public static String formatStackTrace(@Nullable StackTraceElement[] trace) {
    return formatStackTrace(trace, (e) -> false);
  }

  /**
   * Format the elements of a stack trace, and ignore any elements using a predicate.
   *
   * @param trace to format
   * @param skipWhile used to check if elements should be ignored
   * @return formatted trace with the ignored elements removed
   */
  public static String formatStackTrace(
      @Nullable StackTraceElement[] trace, Predicate<StackTraceElement> skipWhile) {
    if (trace == null || trace.length == 0) {
      return "";
    }

    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    int i = 0;
    for (; i < trace.length && skipWhile.test(trace[i]); i++) {}
    for (; i < trace.length; i++) {
      pw.println("\tat " + trace[i]);
    }
    return sw.toString();
  }

  /**
   * Try to unwrap any checked exceptions to their unchecked base, and remove useless wrapper
   * exceptions which make the trace harder to debug.
   *
   * @param e to propagate
   * @return nothing
   */
  public static RuntimeException propagate(Throwable e) {
    // If exception is not checked, throw it directly
    if (e instanceof RuntimeException) {
      throw (RuntimeException) e;
    } else if (e instanceof Error) {
      throw (Error) e;
    }

    // Unwrap uninteresting wrappers
    if (e instanceof InvocationTargetException || e instanceof ExecutionException) {
      return propagate(e.getCause());
    }

    // Replace reflection exceptions with their unchecked equivalents
    if (e instanceof InstantiationException) {
      throw new InstantiationError(e.getMessage());
    } else if (e instanceof IllegalAccessException) {
      throw new IllegalAccessError(e.getMessage());
    } else if (e instanceof NoSuchFieldException) {
      throw new NoSuchFieldError(e.getMessage());
    } else if (e instanceof NoSuchMethodException) {
      throw new NoSuchMethodError(e.getMessage());
    } else if (e instanceof ClassNotFoundException) {
      throw new NoClassDefFoundError(e.getMessage());
    }

    // Last resort, use an unchecked wrapper
    throw new UncheckedExecutionException(e);
  }

  /**
   * Execute the code block in the supplied {@link ThrowingRunnable}, and propagate any exceptions
   * that the code execution throws.
   *
   * @param block to run and propagate exceptions from
   */
  public static void propagate(ThrowingRunnable<Throwable> block) {
    propagate(
        () -> {
          block.runThrows();
          return null;
        });
  }

  /**
   * Execute the code block in the supplied {@link ThrowingSupplier}, and propagate any exceptions
   * that the code execution throws.
   *
   * @param block to run and propagate exceptions from
   * @param <T> type that the supplier returns
   * @return the result of the supplier
   */
  public static <T> T propagate(ThrowingSupplier<T, Throwable> block) {
    try {
      return block.getThrows();
    } catch (Throwable e) {
      throw propagate(e);
    }
  }

  /**
   * Execute the code block in the supplied {@link ThrowingSupplier}, and propagate any exceptions
   * that the code execution throws.
   *
   * @param ex to throw directly
   * @param block to be executed
   * @param <E> type of exception which should be directly thrown
   * @throws E exception thrown by the runnable
   */
  public static <E extends Throwable> void propagate(Class<E> ex, ThrowingRunnable<Throwable> block)
      throws E {
    propagate(
        ex,
        () -> {
          block.runThrows();
          return null;
        });
  }

  /**
   * Run the block of code inside of a {@link ThrowingSupplier}. If the block throws an exception,
   * and the type thrown matches the type argument, it will be re-thrown directly and not
   * propagated.
   *
   * @param ex to throw directly
   * @param block to be executed
   * @param <T> type that the supplier should return
   * @param <E> type of exception which should be directly thrown
   * @return the result of the supplier
   * @throws E exception thrown by the supplier
   */
  public static <T, E extends Throwable> T propagate(
      Class<E> ex, ThrowingSupplier<T, Throwable> block) throws E {
    try {
      return block.getThrows();
    } catch (Throwable e) {
      if (ex.isInstance(e)) {
        throw (E) e;
      } else {
        throw propagate(e);
      }
    }
  }

  /**
   * Run the block of code inside of a {@link ThrowingRunnable}. If the block throws an exception,
   * and the type thrown matches the type argument, it will be added the supplied list of errors and
   * not propagated.
   *
   * @param type of exception to look for when collection errors
   * @param errors list that should be added to if the exception type matches
   * @param block to be executed
   * @param <E> type of exception being collected
   */
  public static <E extends Throwable> void collect(
      Class<E> type, Collection<? super E> errors, ThrowingRunnable<? extends E> block) {
    try {
      block.runThrows();
    } catch (Throwable e) {
      if (type.isInstance(e)) {
        errors.add((E) e);
      } else {
        throw (RuntimeException) e;
      }
    }
  }

  /**
   * Run the block of code inside of a {@link ThrowingSupplier}, and attempt to return the result.
   * If the block throws an exception, and the type thrown matches the type argument, it will be
   * added the supplied list of errors and not propagated.
   *
   * @param type of exception to look for when collection errors
   * @param errors list that should be added to if the exception type matches
   * @param block to be executed
   * @param <T> type that the supplier will return
   * @param <E> type of exception being collected
   * @return the result of the supplier, if it executed without exception
   */
  public static <T, E extends Throwable> Optional<T> collect(
      Class<E> type, Collection<? super E> errors, ThrowingSupplier<T, ? extends E> block) {
    try {
      return Optional.ofNullable(block.getThrows());
    } catch (Throwable e) {
      if (type.isInstance(e)) {
        errors.add((E) e);
        return Optional.empty();
      } else {
        throw (RuntimeException) e;
      }
    }
  }

  /**
   * Run the block of code inside of a {@link ThrowingSupplier}, and attempt to return the result.
   * If the block throws an exception, and the type thrown matches the type argument, it will be
   * added the supplied list of errors and not propagated.
   *
   * @param type of exception to look for when collection errors
   * @param errors list that should be added to if the exception type matches
   * @param block to be executed
   * @param <T> type that the supplier will return
   * @param <E> type of exception being collected
   * @return the result of the supplier, if it executed without exception and returned a result
   */
  public static <T, E extends Throwable> Optional<T> flatCollect(
      Class<E> type,
      Collection<? super E> errors,
      ThrowingSupplier<Optional<T>, ? extends E> block) {
    try {
      return block.getThrows();
    } catch (Throwable e) {
      if (type.isInstance(e)) {
        errors.add((E) e);
        return Optional.empty();
      } else {
        throw (RuntimeException) e;
      }
    }
  }
}

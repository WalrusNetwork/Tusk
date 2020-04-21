package network.walrus.utils.core.util;

import java.util.Optional;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import network.walrus.utils.core.lambda.ThrowingRunnable;
import network.walrus.utils.core.lambda.ThrowingSupplier;

/**
 * @param <T> type of value which is unique to each thread
 * @author Overcast Network
 */
public class Threadable<T> extends ThreadLocal<T> {

  private final @Nullable Supplier<T> initialValue;

  /** Constructor with no initial value. */
  public Threadable() {
    this(null);
  }

  /**
   * Constructor.
   *
   * @param initialValue supplier to generate an initial value
   */
  public Threadable(@Nullable Supplier<T> initialValue) {
    this.initialValue = initialValue;
  }

  @Override
  protected T initialValue() {
    return initialValue != null ? initialValue.get() : super.initialValue();
  }

  /** @return wrapped version of the value of this variable in an optional */
  public Optional<T> value() {
    return Optional.ofNullable(get());
  }

  /** Try to return {@link #get()}, and throw an error if the value is {@code null}. */
  public T need() {
    final T t = get();
    if (t == null) {
      throw new IllegalStateException("No value present");
    }
    return t;
  }

  /**
   * Run a block of code assuring that the value of this object matches a specific value {@link T}.
   *
   * @param value to set this object to, if not already set
   * @param block to run given the set value
   * @param <E> type of exception the runnable could throw
   * @throws E if the runnable throws an exception
   */
  public <E extends Throwable> void let(T value, ThrowingRunnable<E> block) throws E {
    if (value == get()) {
      block.runThrows();
    } else {
      try (CheckedCloseable x = let(value)) {
        block.runThrows();
      }
    }
  }

  /**
   * Run a block of supplier code assuring that the value of this object matches a specific value
   * {@link T}.
   *
   * @param value to set this object to, if not already set
   * @param block to run given the set value
   * @param <U> type of object the supplier will return
   * @param <E> type of exception the runnable could throw
   * @return the result of the supplier
   * @throws E if the supplier throws an exception
   */
  public <U, E extends Throwable> U let(T value, ThrowingSupplier<U, E> block) throws E {
    if (value == get()) {
      return block.getThrows();
    } else {
      try (CheckedCloseable x = let(value)) {
        return block.getThrows();
      }
    }
  }

  /**
   * Set the object's value to a certain value {@link T}, and reset to the old value when the {@link
   * CheckedCloseable} is closed.
   *
   * @param value to temporarily set the object to
   * @return closable resource that will set the old value when closed
   */
  public CheckedCloseable let(T value) {
    final T old = get();
    set(value);
    return () -> set(old);
  }
}

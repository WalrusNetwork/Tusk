package network.walrus.utils.core.math;

/**
 * Interface used to facilitate comparisons between two double values.
 *
 * @author Avicus Network
 */
public interface NumberComparator {

  NumberComparator EQUALS = (a, b) -> a == b;
  NumberComparator LESS_THAN = (a, b) -> a < b;
  NumberComparator LESS_THAN_EQUAL = (a, b) -> a <= b;
  NumberComparator GREATER_THAN = (a, b) -> a > b;
  NumberComparator GREATER_THAN_EQUAL = (a, b) -> a >= b;

  /**
   * Create a new {@link ModuloNumberComparator} with the supplied value.
   *
   * @param value to use as the modulus of the comparator
   * @return comparator using the supplied value
   */
  static NumberComparator MODULO(int value) {
    return new ModuloNumberComparator(value);
  }

  /**
   * Perform the comparator on two values.
   *
   * @return True if the comparator passes
   */
  boolean perform(double a, double b);
}

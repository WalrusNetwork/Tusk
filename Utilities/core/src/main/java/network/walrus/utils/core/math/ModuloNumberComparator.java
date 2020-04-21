package network.walrus.utils.core.math;

/**
 * Comparator which will perform a modulo operation of a predefined amount on a number and compare
 * the result to another number.
 *
 * @author Avicus Network
 */
public class ModuloNumberComparator implements NumberComparator {

  private final int mod;

  /**
   * Constructor.
   *
   * @param mod to perform on the original number
   */
  public ModuloNumberComparator(int mod) {
    this.mod = mod;
  }

  @Override
  public boolean perform(double a, double b) {
    return a % this.mod == b;
  }
}

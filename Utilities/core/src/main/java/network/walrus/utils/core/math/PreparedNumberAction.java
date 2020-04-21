package network.walrus.utils.core.math;

/**
 * A number action wrapper used to perform a {@link NumberAction} using a pre-defined number as the
 * modifier value.
 *
 * @author Avicus Network
 */
public class PreparedNumberAction {

  private final Number value;
  private final NumberAction action;

  /**
   * Constructor.
   *
   * @param value of the base number to be acted upon
   * @param action to be performed
   */
  public PreparedNumberAction(Number value, NumberAction action) {
    this.value = value;
    this.action = action;
  }

  /**
   * Execute the action on a value, using the modifier value defined in this class.
   *
   * @param current value to be modified
   * @return the modified value
   */
  public double perform(double current) {
    return this.action.perform(current, this.value.doubleValue());
  }

  /**
   * Execute the action on a value, using the modifier value defined in this class.
   *
   * @param current value to be modified
   * @return the modified value
   */
  public float perform(float current) {
    return this.action.perform(current, this.value.floatValue());
  }

  /**
   * Execute the action on a value, using the modifier value defined in this class.
   *
   * @param current value to be modified
   * @return the modified value
   */
  public long perform(long current) {
    return this.action.perform(current, this.value.longValue());
  }

  /**
   * Execute the action on a value, using the modifier value defined in this class.
   *
   * @param current value to be modified
   * @return the modified value
   */
  public int perform(int current) {
    return this.action.perform(current, this.value.intValue());
  }

  @Override
  public String toString() {
    return "PreparedNumberAction{" + "value=" + value + ", action=" + action + '}';
  }
}

package network.walrus.games.core.facets.filters;

/**
 * The possible results that a filter can return.
 *
 * @author Avicus Network
 */
public enum FilterResult {
  /** If the filter passes */
  ALLOW,
  /** If the filter does not match */
  DENY,
  /** The filter has neither passed nor failed. (ie. it is not relevant to the situation) */
  IGNORE;

  /**
   * Get a filter result based on a supplied boolean.
   *
   * @param val boolean to convert
   * @return value based on boolean
   */
  public static FilterResult valueOf(boolean val) {
    return val ? ALLOW : DENY;
  }

  /**
   * If the filter passes. Will also return true if the result is ignored.
   *
   * @return if the filter passes, or is ignored
   */
  public boolean passes() {
    return this == ALLOW;
  }

  /**
   * If the filter fails.
   *
   * @return if the filter fails
   */
  public boolean fails() {
    return this == DENY;
  }
}

package network.walrus.utils.bukkit.distance;

/**
 * A metric which used to calculate the distance between an object and a {@link LocatableObject}.
 *
 * @author Avicus Network
 */
public class DistanceCalculationMetric {

  public final Type type;
  public final boolean horizontal;

  /**
   * Constructor.
   *
   * @param type of metric to use during calculation
   * @param horizontal if horizontal distance should play a factor during calculation
   */
  public DistanceCalculationMetric(Type type, boolean horizontal) {
    this.type = type;
    this.horizontal = horizontal;
  }

  /** @return description of the metric being used for calculation */
  public String description() {
    if (this.horizontal) {
      return this.type.description + " (horizontal)";
    } else {
      return this.type.description;
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof DistanceCalculationMetric)) {
      return false;
    }
    DistanceCalculationMetric that = (DistanceCalculationMetric) o;
    return this.type == that.type && this.horizontal == that.horizontal;
  }

  @Override
  public int hashCode() {
    int result = type.hashCode();
    result = 31 * result + (horizontal ? 1 : 0);
    return result;
  }

  /** The metric type which should be used during distance calculation. */
  public enum Type {
    PLAYER("closest player"),
    BLOCK("closest block"),
    KILL("closest kill"),
    STATIC("relation to point");
    public final String description;

    Type(String description) {
      this.description = description;
    }
  }
}

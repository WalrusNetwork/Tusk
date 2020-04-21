package network.walrus.games.octc.ctf.flags;

import javax.annotation.Nullable;
import network.walrus.games.core.facets.objectives.locatable.DistanceMetrics;
import network.walrus.utils.bukkit.distance.DistanceCalculationMetric;
import network.walrus.utils.core.config.Node;

/**
 * Class that parses and manages the carrying {@link DistanceCalculationMetric} for {@link
 * FlagObjective}s.
 *
 * @author Austin Mayes
 */
public class FlagDistanceMetrics extends DistanceMetrics {

  private @Nullable final DistanceCalculationMetric carryingMetric;

  /**
   * @param preComplete metric to be used before the objective has been touched
   * @param postCompleteMetric metric to be used after the objective has been completed
   * @param carryingMetric metric to use while to objective is being carried
   */
  public FlagDistanceMetrics(
      DistanceCalculationMetric preComplete,
      DistanceCalculationMetric postCompleteMetric,
      DistanceCalculationMetric carryingMetric) {
    super(preComplete, postCompleteMetric);
    this.carryingMetric = carryingMetric;
  }

  @Nullable
  public DistanceCalculationMetric getCarryingMetric() {
    return carryingMetric;
  }

  /** Helper class used to build the distance metrics during parsing */
  public static class Builder extends DistanceMetrics.Builder {

    private @Nullable DistanceCalculationMetric carryMetric;

    /** @see #parse(Node, DistanceCalculationMetric) */
    public Builder carry(Node node, @Nullable DistanceCalculationMetric def) {
      return carry(node, "", def);
    }

    /** @see #parse(Node, String, DistanceCalculationMetric) */
    public Builder carry(Node node, String prefix, @Nullable DistanceCalculationMetric def) {
      this.carryMetric = parse(node, prefix, def);
      return this;
    }

    /** @see #parse(Node, DistanceCalculationMetric) */
    @Override
    public Builder preComplete(Node node, @Nullable DistanceCalculationMetric def) {
      return (Builder) super.preComplete(node, def);
    }

    /** @see #parse(Node, String, DistanceCalculationMetric) */
    @Override
    public Builder preComplete(Node node, String prefix, @Nullable DistanceCalculationMetric def) {
      return (Builder) super.preComplete(node, prefix, def);
    }

    /** @see #parse(Node, DistanceCalculationMetric) */
    @Override
    public Builder postComplete(Node node, @Nullable DistanceCalculationMetric def) {
      return (Builder) super.postComplete(node, def);
    }

    /** @see #parse(Node, String, DistanceCalculationMetric) */
    @Override
    public Builder postComplete(Node node, String prefix, @Nullable DistanceCalculationMetric def) {
      return (Builder) super.postComplete(node, prefix, def);
    }

    public FlagDistanceMetrics build() {
      return new FlagDistanceMetrics(preComplete, postCompleteMetric, carryMetric);
    }
  }
}

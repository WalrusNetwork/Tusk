package network.walrus.games.core.facets.objectives.touchable;

import javax.annotation.Nullable;
import network.walrus.games.core.facets.objectives.locatable.DistanceMetrics;
import network.walrus.utils.bukkit.distance.DistanceCalculationMetric;
import network.walrus.utils.core.config.Node;

/**
 * Class that parses and manages the post touch {@link DistanceCalculationMetric} for {@link
 * TouchableObjective}s.
 *
 * @author Austin Mayes
 */
public class TouchableDistanceMetrics extends DistanceMetrics {

  private @Nullable final DistanceCalculationMetric postTouchMetric;

  /**
   * @param preComplete metric to be used before the objective has been touched
   * @param postCompleteMetric metric to be used after the objective has been completed
   * @param postTouchMetric metric to be used after the objective has been touched
   */
  private TouchableDistanceMetrics(
      DistanceCalculationMetric preComplete,
      DistanceCalculationMetric postCompleteMetric,
      DistanceCalculationMetric postTouchMetric) {
    super(preComplete, postCompleteMetric);
    this.postTouchMetric = postTouchMetric;
  }

  @Nullable
  public DistanceCalculationMetric getPostTouchMetric() {
    return postTouchMetric;
  }

  /** Helper class used to build the distance metrics during parsing */
  public static class Builder extends DistanceMetrics.Builder {

    private @Nullable DistanceCalculationMetric postTouchMetric;

    /** @see #parse(Node, DistanceCalculationMetric) */
    public Builder postTouch(Node el, @Nullable DistanceCalculationMetric def) {
      return postTouch(el, "", def);
    }

    /** @see #parse(Node, String, DistanceCalculationMetric) */
    public Builder postTouch(Node el, String prefix, @Nullable DistanceCalculationMetric def) {
      this.postTouchMetric = parse(el, prefix, def);
      return this;
    }

    /** @see #parse(Node, DistanceCalculationMetric) */
    @Override
    public Builder preComplete(Node el, @Nullable DistanceCalculationMetric def) {
      return (Builder) super.preComplete(el, def);
    }

    /** @see #parse(Node, String, DistanceCalculationMetric) */
    @Override
    public Builder preComplete(Node el, String prefix, @Nullable DistanceCalculationMetric def) {
      return (Builder) super.preComplete(el, prefix, def);
    }

    /** @see #parse(Node, DistanceCalculationMetric) */
    @Override
    public Builder postComplete(Node el, @Nullable DistanceCalculationMetric def) {
      return (Builder) super.postComplete(el, def);
    }

    /** @see #parse(Node, String, DistanceCalculationMetric) */
    @Override
    public Builder postComplete(Node el, String prefix, @Nullable DistanceCalculationMetric def) {
      return (Builder) super.postComplete(el, prefix, def);
    }

    public TouchableDistanceMetrics build() {
      return new TouchableDistanceMetrics(preComplete, postCompleteMetric, postTouchMetric);
    }
  }
}

package network.walrus.games.core.facets.objectives.locatable;

import javax.annotation.Nullable;
import network.walrus.utils.bukkit.distance.DistanceCalculationMetric;
import network.walrus.utils.bukkit.parse.BukkitParserRegistry;
import network.walrus.utils.core.config.Node;
import network.walrus.utils.core.config.ParsingException;
import network.walrus.utils.core.parse.SimpleParser;

/**
 * Base class that parses and manages the pre and post completion {@link DistanceCalculationMetric}s
 * for {@link LocatableObjective}s.
 *
 * @author Austin Mayes
 */
public class DistanceMetrics {

  private static final SimpleParser<DistanceCalculationMetric.Type> TYPE_PARSER =
      BukkitParserRegistry.ofEnum(DistanceCalculationMetric.Type.class);
  private @Nullable final DistanceCalculationMetric preCompleteMetric;
  private @Nullable final DistanceCalculationMetric postCompleteMetric;

  /**
   * @param preCompleteMetric metric to be used before the objective has been completed
   * @param postCompleteMetric metric to be used after the objective has been completed
   */
  public DistanceMetrics(
      DistanceCalculationMetric preCompleteMetric, DistanceCalculationMetric postCompleteMetric) {
    this.preCompleteMetric = preCompleteMetric;
    this.postCompleteMetric = postCompleteMetric;
  }

  /**
   * Parse a {@link DistanceCalculationMetric} from a {@link Node} using no prefix.
   *
   * @param node to parse the metric from
   * @param def to fall back on if the node doesn't contain the needed data
   * @return a metric parsed from the node, or the default
   * @throws ParsingException if the node contains errors
   */
  public static DistanceCalculationMetric parse(Node node, DistanceCalculationMetric def)
      throws ParsingException {
    return parse(node, "", def);
  }

  /**
   * Parse a {@link DistanceCalculationMetric} from a {@link Node} with no fallback default.
   *
   * @param node to parse the metric from
   * @param prefix to append to the beginning of node lookup fields
   * @return a metric parsed from the node, or null if the node is lacking data
   * @throws ParsingException if the node contains errors
   */
  public static DistanceCalculationMetric parse(Node node, String prefix) throws ParsingException {
    return parse(node, prefix, null);
  }

  /**
   * Parse a {@link DistanceCalculationMetric} from a {@link Node}.
   *
   * @param node to parse the metric from
   * @param prefix to append to the beginning of node lookup fields
   * @param def to fall back on if the node doesn't contain the needed data
   * @return a metric parsed from the node, or the default if the node is lacking data
   * @throws ParsingException if the node contains errors
   */
  public static DistanceCalculationMetric parse(
      Node node, String prefix, DistanceCalculationMetric def) throws ParsingException {
    if (!prefix.isEmpty()) {
      prefix = prefix + "-";
    }

    if (def != null) {
      return new DistanceCalculationMetric(
          TYPE_PARSER.parse(node.attribute(prefix + "dist-metric")).orElse(def.type),
          BukkitParserRegistry.booleanParser()
              .parse(node.attribute(prefix + "dist-horiz"))
              .orElse(def.horizontal));
    } else {
      if (!node.hasAttribute(prefix + "dist-metric")) {
        return null;
      }

      return new DistanceCalculationMetric(
          TYPE_PARSER.parseRequired(node.attribute(prefix + "dist-metric")),
          BukkitParserRegistry.booleanParser()
              .parse(node.attribute(prefix + "dist-horiz"))
              .orElse(false));
    }
  }

  @Nullable
  public DistanceCalculationMetric getPreCompleteMetric() {
    return preCompleteMetric;
  }

  @Nullable
  public DistanceCalculationMetric getPostCompleteMetric() {
    return postCompleteMetric;
  }

  /** Helper class used to build the distance metrics during parsing */
  public static class Builder {

    @Nullable protected DistanceCalculationMetric preComplete;
    @Nullable protected DistanceCalculationMetric postCompleteMetric;

    /** @see #parse(Node, DistanceCalculationMetric) */
    public Builder preComplete(Node node, @Nullable DistanceCalculationMetric def) {
      return preComplete(node, "", def);
    }

    /** @see #parse(Node, String, DistanceCalculationMetric) */
    public Builder preComplete(Node node, String prefix, @Nullable DistanceCalculationMetric def) {
      this.preComplete = parse(node, prefix, def);
      return this;
    }

    /** @see #parse(Node, DistanceCalculationMetric) */
    public Builder postComplete(Node node, @Nullable DistanceCalculationMetric def) {
      return postComplete(node, "", def);
    }

    /** @see #parse(Node, String, DistanceCalculationMetric) */
    public Builder postComplete(Node node, String prefix, @Nullable DistanceCalculationMetric def) {
      this.postCompleteMetric = parse(node, prefix, def);
      return this;
    }

    /** @return metrics object using the data gathered from this class */
    public DistanceMetrics build() {
      return new DistanceMetrics(preComplete, postCompleteMetric);
    }
  }
}

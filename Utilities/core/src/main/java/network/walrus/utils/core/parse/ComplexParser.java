package network.walrus.utils.core.parse;

import network.walrus.utils.core.config.Node;
import network.walrus.utils.core.config.ParsingException;

/**
 * An object which takes the input from a fully formed {@link Node} and attempts to convert it into
 * a {@link T}.
 *
 * @param <T> type of object this parser returns
 * @author Austin Mayes
 */
public interface ComplexParser<T> {

  /**
   * Build a {@link T} from the contents of the supplied node, or throw an error if this is not
   * possible.
   *
   * @param node to get the object data from
   * @return the parsed object
   * @throws ParsingException if parsing fails due to invalid input
   */
  T parse(Node node) throws ParsingException;
}

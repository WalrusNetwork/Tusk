package network.walrus.utils.bukkit.parse.simple;

import java.util.List;
import network.walrus.utils.core.config.ParsingException;
import network.walrus.utils.core.config.StringHolder;
import network.walrus.utils.core.parse.SimpleParser;
import network.walrus.utils.core.parse.simple.ListParser;
import org.bukkit.util.Vector;

/**
 * Parses {@link Vector}s from {@link StringHolder}s.
 *
 * @author Austin Mayes
 */
public class VectorParser implements SimpleParser<Vector> {

  private final SimpleParser<Double> doubleParser;
  private final ListParser listParser;

  /**
   * Constructor.
   *
   * @param doubleParser used to parse the individual vector parts
   * @param listParser used to separate the vector strings
   */
  public VectorParser(SimpleParser<Double> doubleParser, ListParser listParser) {
    this.doubleParser = doubleParser;
    this.listParser = listParser;
  }

  @Override
  public Vector parseRequired(StringHolder holder) throws ParsingException {
    List<StringHolder> values = listParser.parseRequiredList(holder, ",", true);
    if (values.size() != 3) {
      throw new ParsingException(
          holder.parent(), "Vectors must contain only three elements: x, y and z.");
    }

    return new Vector(
        doubleParser.parseRequired(values.get(0)),
        doubleParser.parseRequired(values.get(1)),
        doubleParser.parseRequired(values.get(2)));
  }

  /**
   * Parse a 2D {@link Vector} from a {@link StringHolder}.
   *
   * @param holder to parse the vector from
   * @return a 2D vector from a string holder
   * @throws ParsingException if the vector fails to parse
   */
  public Vector parse2D(StringHolder holder) throws ParsingException {
    List<StringHolder> values = listParser.parseRequiredList(holder, ",", true);
    if (values.size() != 2) {
      throw new ParsingException(
          holder.parent(), "2D vectors must contain only two elements: x and z.");
    }

    return new Vector(
        doubleParser.parseRequired(values.get(0)), 0, doubleParser.parseRequired(values.get(1)));
  }
}

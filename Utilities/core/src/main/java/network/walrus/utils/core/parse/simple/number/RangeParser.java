package network.walrus.utils.core.parse.simple.number;

import com.google.common.collect.Range;
import network.walrus.utils.core.config.ParsingException;
import network.walrus.utils.core.config.StringHolder;
import network.walrus.utils.core.parse.SimpleParser;
import network.walrus.utils.core.parse.simple.NumberFactory;

/**
 * Parser using to parse any type of numeric range from a {@link StringHolder}.
 *
 * @param <T> type of range being parsed
 * @author Austin Mayes
 */
public class RangeParser<T extends Number & Comparable> implements SimpleParser<Range<T>> {

  private final NumberFactory<T> factory;

  /**
   * Constructor
   *
   * @param factory used to parse numbers
   */
  public RangeParser(NumberFactory<T> factory) {
    this.factory = factory;
  }

  @Override
  public Range<T> parseRequired(StringHolder holder) throws ParsingException {
    final String[] parts = holder.asRequiredString().split("\\.\\.");

    switch (parts.length) {
      case 1:
        return Range.singleton(factory.parse(parts[0]));
      case 2:
        return Range.closed(factory.parse(parts[0]), factory.parse(parts[1]));
    }

    throw new ParsingException(holder.parent(), "Ranges must contain one to two numbers.");
  }
}

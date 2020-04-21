package network.walrus.utils.core.parse.simple.number;

import network.walrus.utils.core.config.ParsingException;
import network.walrus.utils.core.config.StringHolder;
import network.walrus.utils.core.parse.SimpleParser;
import network.walrus.utils.core.parse.simple.NumberFactory;

/**
 * Parser used to parse instances of a number {@link T} from {@link StringHolder}s.
 *
 * @param <T> type of number being parsed
 * @author Austin Mayes
 */
public class NumberParser<T extends Number> implements SimpleParser<T> {

  private final NumberFactory<T> factory;

  /**
   * Constructor.
   *
   * @param factory used to parse the specific number type
   */
  public NumberParser(NumberFactory<T> factory) {
    this.factory = factory;
  }

  @Override
  public T parseRequired(StringHolder holder) throws ParsingException {
    try {
      return factory.parse(holder.asRequiredString());
    } catch (NumberFormatException e) {
      throw new ParsingException(holder.parent(), e.getMessage());
    }
  }
}

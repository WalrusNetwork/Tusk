package network.walrus.utils.core.parse.simple;

import network.walrus.utils.core.config.ParsingException;
import network.walrus.utils.core.config.StringHolder;
import network.walrus.utils.core.parse.SimpleParser;

/**
 * Parses {@link Enum}s from {@link StringHolder}s.
 *
 * @param <E> type of enum this parser parses
 * @author Austin Mayes
 */
public class EnumParser<E extends Enum<E>> implements SimpleParser<E> {

  private final Class<E> type;

  /**
   * Constructor.
   *
   * @param type of enum that this parser is responsible for
   */
  public EnumParser(Class<E> type) {
    this.type = type;
  }

  @Override
  public E parseRequired(StringHolder holder) throws ParsingException {
    String text = holder.asRequiredString().toUpperCase().replace(" ", "_").replace("-", "_");
    try {
      // First, try the fast way
      return Enum.valueOf(type, text);
    } catch (IllegalArgumentException ex) {
      // If that fails, search for a case-insensitive match, without assuming enums are always
      // uppercase
      for (E value : type.getEnumConstants()) {
        if (value.name().equalsIgnoreCase(text)) {
          return value;
        }
      }
      throw new ParsingException(holder.parent(), ex);
    }
  }
}

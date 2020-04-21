package network.walrus.utils.core.parse.simple.number;

import java.text.NumberFormat;
import java.text.ParseException;
import network.walrus.utils.core.config.ParsingException;
import network.walrus.utils.core.config.StringHolder;
import network.walrus.utils.core.parse.SimpleParser;

/**
 * Parses {@link Number}s from {@link StringHolder}s. This should only be used when any type of
 * {@link Number} can be used for a value.
 *
 * @author Austin Mayes
 */
public class BaseNumberParser implements SimpleParser<Number> {

  @Override
  public Number parseRequired(StringHolder holder) throws ParsingException {
    try {
      return NumberFormat.getInstance().parse(holder.asRequiredString());
    } catch (ParseException e) {
      throw new ParsingException(holder.parent(), e);
    }
  }
}

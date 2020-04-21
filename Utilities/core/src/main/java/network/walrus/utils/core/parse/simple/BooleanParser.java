package network.walrus.utils.core.parse.simple;

import network.walrus.utils.core.config.ParsingException;
import network.walrus.utils.core.config.StringHolder;
import network.walrus.utils.core.parse.SimpleParser;

/**
 * Parses {@link Boolean}s from {@link StringHolder}s.
 *
 * @author Austin Mayes
 */
public class BooleanParser implements SimpleParser<Boolean> {

  @Override
  public Boolean parseRequired(StringHolder holder) throws ParsingException {
    String text = holder.asRequiredString().toLowerCase();

    switch (text) {
      case "true":
      case "yes":
      case "allow":
      case "on":
        return true;
      case "false":
      case "no":
      case "deny":
      case "off":
        return false;
      default:
        throw new ParsingException(holder.parent(), "Invalid boolean.");
    }
  }
}

package network.walrus.utils.core.parse.simple;

import java.time.Duration;
import java.time.format.DateTimeParseException;
import network.walrus.utils.core.config.ParsingException;
import network.walrus.utils.core.config.StringHolder;
import network.walrus.utils.core.parse.SimpleParser;
import network.walrus.utils.core.util.TimeUtils;

/**
 * Parses {@link Duration}s from {@link StringHolder}s.
 *
 * @author Austin Mayes
 */
public class DurationParser implements SimpleParser<Duration> {

  @Override
  public Duration parseRequired(StringHolder holder) throws ParsingException {
    try {
      return TimeUtils.parseDurationOrSeconds(holder.asRequiredString());
    } catch (DateTimeParseException e) {
      throw new ParsingException(holder.parent(), "Invalid date format supplied.");
    }
  }
}

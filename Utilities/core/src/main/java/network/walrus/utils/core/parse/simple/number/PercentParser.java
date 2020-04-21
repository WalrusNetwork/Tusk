package network.walrus.utils.core.parse.simple.number;

import network.walrus.utils.core.config.GenericStringHolder;
import network.walrus.utils.core.config.ParsingException;
import network.walrus.utils.core.config.StringHolder;
import network.walrus.utils.core.parse.CoreParserRegistry;
import network.walrus.utils.core.parse.SimpleParser;

/**
 * Parses fields which should be interpreted as percentage values and returns them in decimal form
 * from 0.0 to 1.0.
 *
 * @author Rafi Baum
 */
public class PercentParser implements SimpleParser<Double> {

  /**
   * This method understands percentage values in the form 0.5 or 50% and returns an equivalent
   * result for either form.
   *
   * @param holder to get the percentage value from
   * @return the percent in decimal form within [0.0, 1.0]
   * @throws ParsingException if supplied data is not numeric
   */
  @Override
  public Double parseRequired(StringHolder holder) throws ParsingException {
    String value = holder.asRequiredString();

    double percent;
    if (value.endsWith("%")) {
      // Parse as percent value
      value = value.substring(0, value.length() - 1);
      percent =
          CoreParserRegistry.doubleParser()
                  .parseRequired(new GenericStringHolder(value, holder.parent()))
              / 100.0;
    } else {
      // Parse as decimal value
      percent = CoreParserRegistry.doubleParser().parseRequired(holder);
    }

    return percent;
  }
}

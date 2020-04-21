package network.walrus.utils.core.parse;

import java.util.Optional;
import network.walrus.utils.core.config.ParsingException;
import network.walrus.utils.core.config.StringHolder;

/**
 * An object which takes the input from a single {@link StringHolder} and attempts to convert it
 * into a {@link T}.
 *
 * @param <T> type of object this parser returns
 * @author Austin Mayes
 */
public interface SimpleParser<T> {

  /**
   * Build a {@link T} from the contents of the supplied holder, or throw an error if this is not
   * possible.
   *
   * @param holder to get the object data from
   * @return the parsed object
   * @throws ParsingException if parsing fails due to invalid input
   */
  T parseRequired(StringHolder holder) throws ParsingException;

  /**
   * Call {@link #parseRequired(StringHolder)} if the holder contains a value, or else return {@link
   * Optional#empty()}
   *
   * @param holder to get the object data from
   * @return the parsed {@link T} if the holder is non-empty
   */
  default Optional<T> parse(StringHolder holder) {
    if (holder.isValuePresent()) {
      return Optional.of(parseRequired(holder));
    }
    return Optional.empty();
  }
}

package network.walrus.utils.core.config;

import java.util.Optional;

/**
 * Something that has the potential to hold a string value.
 *
 * @author Austin Mayes
 */
public interface StringHolder {

  /** Check if this representation contains any data. */
  boolean isValuePresent();

  /**
   * Convenience method for {@link #value()} that will throw an error if no value is present.
   *
   * @throws ParsingException jf no text is present
   */
  String asRequiredString() throws ParsingException;

  /** The value of this attribute, if it exists. */
  Optional<String> value();

  /** The {@link Node} which this holder is inside of. This is never null. */
  Node parent();
}

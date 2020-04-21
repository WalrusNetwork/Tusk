package network.walrus.utils.core.parse.simple;

import com.google.common.base.Splitter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import network.walrus.utils.core.config.GenericStringHolder;
import network.walrus.utils.core.config.ParsingException;
import network.walrus.utils.core.config.StringHolder;
import network.walrus.utils.core.parse.SimpleParser;

/**
 * Parses {@link List}s from {@link StringHolder}s.
 *
 * @author Austin Mayes
 */
public class ListParser implements SimpleParser<List<StringHolder>> {

  @Override
  public List<StringHolder> parseRequired(StringHolder holder) throws ParsingException {
    return parseRequiredList(holder, ",", true);
  }

  /** Wrapper for {@link #parseRequiredList(StringHolder, String, boolean)}. */
  public Optional<List<StringHolder>> parseList(
      StringHolder attribute, String separator, boolean removeSpaces) {
    if (attribute.isValuePresent()) {
      return Optional.of(parseRequiredList(attribute, separator, removeSpaces));
    }
    return Optional.empty();
  }

  /**
   * Parses an Attribute's value into a list of attributes based on which type of Attribute is
   * passed. Needs additional check implementation if more language support is added in the future.
   *
   * @param attribute The attribute to be parsed.
   * @param separator String to split the Attributes by.
   * @param removeSpaces Whether or not spaces should be removed before splitting the string.
   * @return List of Attributes of the same type as the {@code attribute} parameter
   * @throws ParsingException If the {@code attribute} parameter is not an instance of a
   *     recognizable class that implements Attribute (Should never be thrown)
   */
  public List<StringHolder> parseRequiredList(
      StringHolder attribute, String separator, boolean removeSpaces) {
    String complete = attribute.asRequiredString();

    List<String> list = Splitter.on(separator).splitToList(complete);
    List<StringHolder> result = new ArrayList<>();
    for (String text : list) {
      GenericStringHolder holder =
          new GenericStringHolder(removeSpaces ? text.trim() : text, attribute.parent());
      result.add(holder);
    }
    return result;
  }
}

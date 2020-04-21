package network.walrus.utils.core.text;

import net.md_5.bungee.api.ChatColor;
import network.walrus.utils.core.translation.Localizable;
import network.walrus.utils.core.translation.TextStyle;

/**
 * Represents a format that takes arguments for localization.
 *
 * @param <T> type of arguments that this format can substitute in for placeholders
 * @author Avicus Network
 */
public interface LocalizableFormat<T extends Localizable> {

  /**
   * Create an instance of {@link T} with the supplied styling and arguments.
   *
   * @param style of the text
   * @param arguments to be substituted in the placeholders
   * @return {@link T} with the supplied styling and arguments
   */
  T with(TextStyle style, Localizable... arguments);

  /**
   * Create an instance of {@link T} with the supplied arguments.
   *
   * @param arguments to be substituted in the placeholders
   * @return {@link T} with the supplied arguments
   */
  T with(Localizable... arguments);

  /** @return the format, with no arguments or styling */
  default T with() {
    return with(Localizable.EMPTY);
  }

  /**
   * Create an instance of {@link T} with the supplied color and arguments.
   *
   * @param color of the text
   * @param arguments to be substituted in the placeholders
   * @return {@link T} with the supplied color and arguments
   */
  default T with(ChatColor color, Localizable... arguments) {
    return with(TextStyle.ofColor(color), arguments);
  }

  /**
   * Create an instance of {@link T} with the supplied color.
   *
   * @param color of the text
   * @return {@link T} with the supplied color
   */
  default T with(ChatColor color) {
    return with(color, new String[] {});
  }

  /**
   * Create an instance of {@link T} with the supplied styling.
   *
   * @param style of the text
   * @return {@link T} with the supplied styling
   */
  default T with(TextStyle style) {
    return with(style, Localizable.EMPTY);
  }

  /**
   * Create an instance of {@link T} with the supplied styling and arguments.
   *
   * @param style of the text
   * @param arguments to be substituted in the placeholders
   * @return {@link T} with the supplied styling and arguments
   */
  default T with(TextStyle style, String... arguments) {
    T result = with(arguments);
    result.style().reset().inherit(style);
    return result;
  }

  /**
   * Create an instance of {@link T} with the supplied color and arguments.
   *
   * @param color of the text
   * @param arguments to be substituted in the placeholders
   * @return {@link T} with the supplied color and arguments
   */
  default T with(ChatColor color, String... arguments) {
    return with(TextStyle.ofColor(color), arguments);
  }

  /**
   * Create an instance of {@link T} with the supplied arguments.
   *
   * @param arguments to be substituted in the placeholders
   * @return {@link T} with the supplied arguments
   */
  default T with(String... arguments) {
    Localizable[] localized = new Localizable[arguments.length];
    for (int i = 0; i < localized.length; i++) {
      localized[i] = new UnlocalizedText(arguments[i]);
    }
    return with(localized);
  }
}

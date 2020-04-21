package network.walrus.utils.core.translation;

import network.walrus.common.CommandSender;
import network.walrus.common.text.PersonalizedComponent;

/**
 * Represents anything that can be translated and sent to players.
 *
 * @author Avicus Network
 */
public interface Localizable extends PersonalizedComponent {

  Localizable[] EMPTY = new Localizable[0];

  /** Get the style of this. */
  TextStyle style();

  /** Copy this and its styles. */
  Localizable duplicate();

  /**
   * Renders the localizable for the command sender as legacy text (with legacy formatting codes).
   *
   * @param sender to render for
   * @return text in legacy format
   */
  default String toLegacyText(CommandSender sender) {
    return render(sender).toLegacyText();
  }
}

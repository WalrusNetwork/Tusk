package network.walrus.utils.core.text;

import network.walrus.utils.core.translation.Localizable;
import network.walrus.utils.core.translation.TextStyle;

/**
 * Represents a format that takes arguments for localization with {@link UnlocalizedText}s.
 *
 * @author Avicus Network
 */
public class UnlocalizedFormat implements LocalizableFormat<UnlocalizedText> {

  private final String text;

  /**
   * Constructor.
   *
   * @param text containing placeholders to insert the arguments into
   */
  public UnlocalizedFormat(String text) {
    this.text = text;
  }

  @Override
  public UnlocalizedText with(TextStyle style, Localizable... arguments) {
    return new UnlocalizedText(this.text, style, arguments);
  }

  @Override
  public UnlocalizedText with(Localizable... arguments) {
    return new UnlocalizedText(this.text, arguments);
  }
}

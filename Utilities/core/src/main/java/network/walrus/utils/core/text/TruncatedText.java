package network.walrus.utils.core.text;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import network.walrus.common.CommandSender;
import network.walrus.utils.core.translation.Localizable;
import network.walrus.utils.core.translation.TextStyle;

/**
 * Localizable which wraps translated text and truncates it to a specific length.
 *
 * @author Rafi Baum
 */
public class TruncatedText implements Localizable {

  private final Localizable localized;
  private final int maxLength;

  /**
   * Constructor.
   *
   * @param localized localized text to truncate
   * @param maxLength length to truncate to
   */
  public TruncatedText(Localizable localized, int maxLength) {
    this.localized = localized;
    this.maxLength = maxLength;
  }

  @Override
  public TextStyle style() {
    return localized.style();
  }

  @Override
  public Localizable duplicate() {
    return new TruncatedText(localized.duplicate(), maxLength);
  }

  @Override
  public BaseComponent render(CommandSender commandSender) {
    BaseComponent component = localized.render(commandSender);
    int length = 0;

    length = truncateComponent(component, length);
    for (BaseComponent extra : component.getExtra()) {
      length = truncateComponent(extra, length);
    }

    return component;
  }

  private int truncateComponent(BaseComponent component, int length) {
    int maxLength = this.maxLength - length;

    if (component instanceof TextComponent) {
      TextComponent textComponent = (TextComponent) component;
      if (maxLength <= 0) {
        textComponent.setText("");
        return length;
      } else {
        String text = textComponent.getText();
        String shortened = text.substring(0, Math.min(text.length(), maxLength));
        textComponent.setText(shortened);
        return length + shortened.length();
      }
    } else {
      return length;
    }
  }
}

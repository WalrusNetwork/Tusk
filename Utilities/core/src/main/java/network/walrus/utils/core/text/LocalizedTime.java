package network.walrus.utils.core.text;

import java.util.Date;
import net.md_5.bungee.api.chat.BaseComponent;
import network.walrus.common.CommandSender;
import network.walrus.utils.core.translation.Localizable;
import network.walrus.utils.core.translation.TextStyle;
import network.walrus.utils.core.util.TimeUtils;

/**
 * A date object that can be translated to any locale with a specific style in the _ago format.
 *
 * <p>EXAMPLES: 5 seconds ago, 1 day from now, moments ago, etc.
 *
 * @author Avicus Network
 */
public class LocalizedTime implements Localizable {

  private final Date date;
  private final TextStyle style;

  /**
   * @param date to format
   * @param style of the text
   */
  public LocalizedTime(Date date, TextStyle style) {
    this.date = date;
    this.style = style;
  }

  /** @param date to format */
  public LocalizedTime(Date date) {
    this(date, TextStyle.create());
  }

  @Override
  public BaseComponent render(CommandSender sender) {
    String time = TimeUtils.prettyTime(sender.getLocale()).format(this.date);
    return new UnlocalizedText(time, this.style).render(sender);
  }

  @Override
  public TextStyle style() {
    return this.style;
  }

  @Override
  public LocalizedTime duplicate() {
    return new LocalizedTime(this.date, this.style.duplicate());
  }
}

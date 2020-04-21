package network.walrus.utils.core.text;

import java.text.DateFormat;
import java.util.Date;
import net.md_5.bungee.api.chat.BaseComponent;
import network.walrus.common.CommandSender;
import network.walrus.utils.core.translation.Localizable;
import network.walrus.utils.core.translation.TextStyle;

/**
 * A date object that can be translated to any locale with a specific style.
 *
 * @author Avicus Network
 */
public class LocalizedDate implements Localizable {

  private final Date date;
  private final TextStyle style;

  /**
   * Constructor.
   *
   * @param date to render
   * @param style of the translated date
   */
  public LocalizedDate(Date date, TextStyle style) {
    this.date = date;
    this.style = style;
  }

  /**
   * Constructor.
   *
   * @param date to render
   */
  public LocalizedDate(Date date) {
    this(date, TextStyle.create());
  }

  @Override
  public BaseComponent render(CommandSender viewer) {
    DateFormat format =
        DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, viewer.getLocale());
    return new UnlocalizedText(format.format(this.date), this.style).render(viewer);
  }

  @Override
  public TextStyle style() {
    return this.style;
  }

  @Override
  public LocalizedDate duplicate() {
    return new LocalizedDate(this.date, this.style.duplicate());
  }
}

package network.walrus.utils.core.text;

import java.text.NumberFormat;
import net.md_5.bungee.api.chat.BaseComponent;
import network.walrus.common.CommandSender;
import network.walrus.utils.core.translation.Localizable;
import network.walrus.utils.core.translation.TextStyle;

/**
 * A number object that can be translated into any locale with min/max decimals and a style.
 *
 * @author Avicus Network
 */
public class LocalizedNumber implements Localizable {

  private final Number number;
  private final int minDecimals;
  private final int maxDecimals;
  private final boolean groupingUsed;
  private final TextStyle style;

  /**
   * Constructor.
   *
   * @param number to render
   * @param minDecimals decimal minimum
   * @param maxDecimals decimal maximum
   * @param groupingUsed if number should be grouped
   * @param style of the number
   */
  public LocalizedNumber(
      Number number, int minDecimals, int maxDecimals, boolean groupingUsed, TextStyle style) {
    this.number = number;
    this.minDecimals = minDecimals;
    this.maxDecimals = maxDecimals;
    this.groupingUsed = groupingUsed;
    this.style = style;
  }

  /**
   * Constructor.
   *
   * @param number to render
   * @param minDecimals decimal minimum
   * @param maxDecimals decimal maximum
   * @param style of the number
   */
  public LocalizedNumber(Number number, int minDecimals, int maxDecimals, TextStyle style) {
    this(number, minDecimals, maxDecimals, true, style);
  }

  /**
   * Constructor.
   *
   * @param number to render
   * @param minDecimals decimal minimum
   * @param maxDecimals decimal maximum
   */
  public LocalizedNumber(Number number, int minDecimals, int maxDecimals) {
    this(number, minDecimals, maxDecimals, TextStyle.create());
  }

  /**
   * Constructor with a max of 3 decimals.
   *
   * @param number to render
   * @param style of the number
   */
  public LocalizedNumber(Number number, TextStyle style) {
    this(number, 0, 3, style);
  }

  /**
   * Constructor with a max of 3 decimals.
   *
   * @param number to render
   */
  public LocalizedNumber(Number number) {
    this(number, TextStyle.create());
  }

  @Override
  public BaseComponent render(CommandSender viewer) {
    NumberFormat format = NumberFormat.getInstance(viewer.getLocale());
    format.setMinimumFractionDigits(this.minDecimals);
    format.setMaximumFractionDigits(this.maxDecimals);
    format.setGroupingUsed(this.groupingUsed);

    return new UnlocalizedText(format.format(this.number), this.style).render(viewer);
  }

  @Override
  public TextStyle style() {
    return this.style;
  }

  @Override
  public Localizable duplicate() {
    return new LocalizedNumber(
        this.number, this.minDecimals, this.maxDecimals, this.style.duplicate());
  }
}

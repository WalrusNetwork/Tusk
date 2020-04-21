package network.walrus.utils.core.text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.md_5.bungee.api.chat.BaseComponent;
import network.walrus.common.CommandSender;
import network.walrus.utils.core.translation.LocaleBundle;
import network.walrus.utils.core.translation.Localizable;
import network.walrus.utils.core.translation.TextStyle;

/**
 * A {@link LocalizedText} that can have a translation inside of it to allow for double translation.
 *
 * @author Avicus Network
 */
public class LocalizedTextFormat implements Localizable {

  private final LocaleBundle bundle;
  private final Localizable format;
  private final List<Localizable> arguments;
  private final TextStyle style;

  /**
   * Constructor.
   *
   * @param bundle to pull localized strings from
   * @param format of the base message
   * @param arguments to substitute in for placeholders
   */
  public LocalizedTextFormat(LocaleBundle bundle, Localizable format, Localizable... arguments) {
    this(
        bundle,
        format,
        TextStyle.create(),
        arguments.length == 0 ? Collections.emptyList() : Arrays.asList(arguments));
  }

  /**
   * Constructor.
   *
   * @param bundle to pull localized strings from
   * @param format of the base message
   * @param style to apply to the final message
   * @param arguments to substitute in for placeholders
   */
  public LocalizedTextFormat(
      LocaleBundle bundle, Localizable format, TextStyle style, Localizable... arguments) {
    this(
        bundle,
        format,
        style,
        arguments.length == 0 ? Collections.emptyList() : Arrays.asList(arguments));
  }

  /**
   * Constructor.
   *
   * @param bundle to pull localized strings from
   * @param format of the base message
   * @param arguments to substitute in for placeholders
   */
  public LocalizedTextFormat(LocaleBundle bundle, Localizable format, List<Localizable> arguments) {
    this(bundle, format, TextStyle.create(), arguments);
  }

  /**
   * Constructor.
   *
   * @param bundle to pull localized strings from
   * @param format of the base message
   * @param style to apply to the final message
   * @param arguments to substitute in for placeholders
   */
  public LocalizedTextFormat(
      LocaleBundle bundle, Localizable format, TextStyle style, List<Localizable> arguments) {
    this.bundle = bundle;
    this.format = format;
    this.style = style;
    this.arguments = arguments;
  }

  @Override
  public BaseComponent render(CommandSender viewer) {
    String text = this.format.render(viewer).toLegacyText();
    UnlocalizedText sneaky = new UnlocalizedText(text, this.style, this.arguments);
    sneaky.style().inherit(this.style);

    return sneaky.render(viewer);
  }

  @Override
  public TextStyle style() {
    return this.style;
  }

  @Override
  public LocalizedTextFormat duplicate() {
    List<Localizable> arguments = new ArrayList<>();
    for (Localizable argument : this.arguments) {
      Localizable duplicate = argument.duplicate();
      arguments.add(duplicate);
    }

    return new LocalizedTextFormat(this.bundle, this.format, this.style.duplicate(), arguments);
  }
}

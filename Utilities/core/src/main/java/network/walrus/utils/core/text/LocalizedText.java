package network.walrus.utils.core.text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import network.walrus.common.CommandSender;
import network.walrus.utils.core.translation.LocaleBundle;
import network.walrus.utils.core.translation.Localizable;
import network.walrus.utils.core.translation.TextStyle;
import network.walrus.utils.core.util.StringUtils;

/**
 * Represents a locatable text sting inside of a {@link LocaleBundle}.
 *
 * @author Avicus Network
 */
public class LocalizedText implements Localizable {

  private final LocaleBundle bundle;
  private final String key;
  private final List<Localizable> arguments;
  private final TextStyle style;

  /**
   * Constructor.
   *
   * @param bundle to pull localized strings from
   * @param key corresponding to the message in the bundle
   * @param arguments to substitute in for placeholders
   */
  public LocalizedText(LocaleBundle bundle, String key, Localizable... arguments) {
    this(
        bundle,
        key,
        TextStyle.create(),
        arguments.length == 0 ? Collections.emptyList() : Arrays.asList(arguments));
  }

  /**
   * Constructor.
   *
   * @param bundle to pull localized strings from
   * @param key corresponding to the message in the bundle
   * @param style to apply to the final message
   * @param arguments to substitute in for placeholders
   */
  public LocalizedText(LocaleBundle bundle, String key, TextStyle style, Localizable... arguments) {
    this(
        bundle,
        key,
        style,
        arguments.length == 0
            ? Collections.emptyList()
            : new ArrayList<>(Arrays.asList(arguments)));
  }

  /**
   * Constructor.
   *
   * @param bundle to pull localized strings from
   * @param key corresponding to the message in the bundle
   * @param arguments to substitute in for placeholders
   */
  public LocalizedText(LocaleBundle bundle, String key, List<Localizable> arguments) {
    this(bundle, key, TextStyle.create(), arguments);
  }

  /**
   * Constructor.
   *
   * @param bundle to pull localized strings from
   * @param key corresponding to the message in the bundle
   * @param style to apply to the final message
   * @param arguments to substitute in for placeholders
   */
  public LocalizedText(
      LocaleBundle bundle, String key, TextStyle style, List<Localizable> arguments) {
    this.bundle = bundle;
    this.key = key;
    this.style = style;
    this.arguments = arguments;
  }

  @Override
  public BaseComponent render(CommandSender viewer) {
    Optional<String> text = this.bundle.get(viewer.getLocale(), this.key);
    if (!text.isPresent()) {
      return new TextComponent(
          "translation: '"
              + this.key
              + "'"
              + (!arguments.isEmpty()
                  ? ", arguments: "
                      + StringUtils.join(arguments, ", ", (l) -> l.render(viewer).toLegacyText())
                  : ""));
    }

    // sneakily use unlocalized text to do translation
    UnlocalizedText sneaky = new UnlocalizedText(text.get(), this.style, this.arguments);
    sneaky.style().inherit(this.style);

    return sneaky.render(viewer);
  }

  @Override
  public TextStyle style() {
    return this.style;
  }

  @Override
  public LocalizedText duplicate() {
    List<Localizable> arguments = new ArrayList<>();
    for (Localizable argument : this.arguments) {
      Localizable duplicate = argument.duplicate();
      arguments.add(duplicate);
    }

    return new LocalizedText(this.bundle, this.key, this.style.duplicate(), arguments);
  }
}

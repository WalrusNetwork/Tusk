package network.walrus.utils.bukkit.translation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import network.walrus.common.CommandSender;
import network.walrus.utils.core.text.LocalizableFormat;
import network.walrus.utils.core.text.UnlocalizedFormat;
import network.walrus.utils.core.translation.Localizable;
import network.walrus.utils.core.translation.TextStyle;
import org.bukkit.Bukkit;

/**
 * Represents a localized string that comes from a user-defined configuration file.
 *
 * @author Avicus Network
 */
public class LocalizedConfigurationProperty {

  private final Localizable text;

  /**
   * Constructor.
   *
   * @param arguments to be displayed
   */
  public LocalizedConfigurationProperty(List<Localizable> arguments) {
    StringBuilder textFormat = new StringBuilder();
    for (int i = 0; i < arguments.size(); i++) {
      textFormat.append("{").append(i).append("}");
      if (i < arguments.size() - 2) {
        textFormat.append(", ");
      }
      if (i == arguments.size() - 2) {
        textFormat.append(" and ");
      }
    }
    this.text =
        new UnlocalizedFormat(textFormat.toString())
            .with(arguments.toArray(new Localizable[arguments.size()]));
  }

  /**
   * Constructor.
   *
   * @param format of the base message
   * @param arguments to be substituted in for placeholders
   */
  public LocalizedConfigurationProperty(String format, Localizable... arguments) {
    this(format, new ArrayList<>(Arrays.asList(arguments)));
  }

  /**
   * Constructor.
   *
   * @param format of the base message
   * @param arguments to be substituted in for placeholders
   */
  public LocalizedConfigurationProperty(LocalizableFormat format, Localizable... arguments) {
    this(format, new ArrayList<>(Arrays.asList(arguments)));
  }

  /**
   * Constructor.
   *
   * @param format of the base message
   * @param arguments to be substituted in for placeholders
   */
  public LocalizedConfigurationProperty(String format, List<Localizable> arguments) {
    this(new UnlocalizedFormat(format), arguments);
  }

  /**
   * Constructor.
   *
   * @param format of the base message
   * @param arguments to be substituted in for placeholders
   */
  public LocalizedConfigurationProperty(LocalizableFormat format, List<Localizable> arguments) {
    this.text = format.with(arguments.toArray(new Localizable[arguments.size()]));
  }

  /**
   * Translate the defined message for a certain locale.
   *
   * @param viewer to render the message for
   * @return translated message
   */
  public String render(CommandSender viewer) {
    BaseComponent component = this.text.render(viewer);
    // toPlainText() is misleading in this situation
    // colors will be present since we put in colors straight into
    // the string with "^colorcode".
    return component.toPlainText();
  }

  /**
   * Translate the defined message for the default locale.
   *
   * @return translated message
   */
  public String translateDefault() {
    return render(Bukkit.getConsoleSender());
  }

  /** @return a base {@link Localizable} representation of this object */
  public Localizable toText() {
    return this.text.duplicate();
  }

  /**
   * Get a base {@link Localizable} representation of this object with the supplied color.
   *
   * @param color to make the text
   * @return a base representation of this object
   */
  public Localizable toText(ChatColor color) {
    return toText(TextStyle.ofColor(color));
  }

  /**
   * Get a base {@link Localizable} representation of this object with the supplied styling.
   *
   * @param style to apply to the text
   * @return a base representation of this object
   */
  public Localizable toText(TextStyle style) {
    Localizable text = toText();
    text.style().inherit(style);
    return text;
  }

  @Override
  public String toString() {
    return "LocalizedConfigurationProperty(text=" + render(Bukkit.getConsoleSender()) + ")";
  }
}

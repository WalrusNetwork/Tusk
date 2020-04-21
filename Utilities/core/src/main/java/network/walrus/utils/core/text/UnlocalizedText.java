package network.walrus.utils.core.text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import net.md_5.bungee.api.chat.TextComponent;
import network.walrus.common.CommandSender;
import network.walrus.utils.core.translation.Localizable;
import network.walrus.utils.core.translation.TextStyle;

/**
 * A simple non-translatable string that can still take arguments and styling.
 *
 * @author Avicus Network
 */
public class UnlocalizedText implements Localizable {

  private final String text;
  private final List<Localizable> arguments;
  private final TextStyle style;

  /** @see #UnlocalizedText(String, TextStyle, List) */
  public UnlocalizedText(String text, Localizable... arguments) {
    this(text, TextStyle.create(), arguments);
  }

  /** @see #UnlocalizedText(String, TextStyle, List) */
  public UnlocalizedText(String text, TextStyle style, Localizable... arguments) {
    this(
        text,
        style,
        arguments.length == 0
            ? Collections.emptyList()
            : new ArrayList<>(Arrays.asList(arguments)));
  }

  /** @see #UnlocalizedText(String, TextStyle, List) */
  public UnlocalizedText(String text, ChatColor color) {
    this(text, TextStyle.ofColor(color));
  }

  /**
   * Constructor.
   *
   * @param text containing placeholders to insert the arguments into
   * @param style of the text
   * @param arguments to be translated
   */
  public UnlocalizedText(String text, TextStyle style, List<Localizable> arguments) {
    this.text = text;
    this.arguments = arguments;
    this.style = style;
  }

  @Override
  public TextComponent render(CommandSender viewer) {
    String format = this.text;

    List<BaseComponent> parts = new ArrayList<>();

    for (int i = 0; i < this.arguments.size(); i++) {
      Localizable curr = this.arguments.get(i);

      if (format.contains("{" + i + "}")) {
        String[] split = format.split("\\{" + i + "\\}");

        if (split.length > 0) {
          parts.add(this.style.apply(split[0]));
        }

        // Allows null arguments to be converted to ""
        if (curr != null) {
          curr.style().inherit(this.style);
          parts.add(curr.render(viewer));
        }

        if (split.length > 1) {
          format = split[1];
        } else {
          format = "";
        }
      }
    }

    if (format.length() > 0) {
      parts.add(this.style.apply(format));
    }

    TextComponent result;

    if (parts.size() > 0) {
      result = new TextComponent(parts.get(0));
      for (int i = 1; i < parts.size(); i++) {
        result.addExtra(parts.get(i));
      }
    } else {
      result = new TextComponent("");
    }

    if (style.hoverText() != null) {
      result.setHoverEvent(
          new HoverEvent(Action.SHOW_TEXT, new BaseComponent[] {style.hoverText().render(viewer)}));
    }

    return result;
  }

  @Override
  public TextStyle style() {
    return this.style;
  }

  @Override
  public UnlocalizedText duplicate() {
    List<Localizable> arguments = new ArrayList<>();
    for (Localizable argument : this.arguments) {
      Localizable duplicate = argument.duplicate();
      arguments.add(duplicate);
    }

    return new UnlocalizedText(this.text, this.style.duplicate(), arguments);
  }
}

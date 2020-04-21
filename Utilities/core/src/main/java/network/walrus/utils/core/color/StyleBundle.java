package network.walrus.utils.core.color;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import network.walrus.utils.core.config.GenericStringHolder;
import network.walrus.utils.core.parse.CoreParserRegistry;
import network.walrus.utils.core.translation.TextStyle;
import network.walrus.utils.core.util.FileBackedKVSet;

/**
 * A collection of {@link TextStyle}s mapped by key.
 *
 * @author Austin Mayes
 */
public class StyleBundle extends FileBackedKVSet<TextStyle> {

  private void parseSimple(TextStyle style, String part) {
    switch (part) {
      case "bold":
      case "b":
        style.bold();
        break;
      case "italic":
      case "i":
        style.italic();
        break;
      case "underline":
      case "u":
        style.underlined();
        break;
      case "magic":
      case "m":
        style.magic();
        break;
      case "strike":
      case "s":
        style.strike();
        break;
      default:
        throw new IllegalStateException("Unknown style data " + part);
    }
  }

  private void parseComplex(TextStyle style, String part) {
    String id = part.split(":")[0];
    String value = part.split(":")[1];
    switch (id) {
      case "color":
      case "c":
        style.color(
            CoreParserRegistry.ofEnum(ChatColor.class)
                .parseRequired(new GenericStringHolder(value, null)));
        break;
      case "click":
      case "clickevent":
        style.click(parseClick(value));
        break;
      default:
        throw new IllegalStateException("Unknown style data " + part);
    }
  }

  private ClickEvent parseClick(String raw) {
    Action action =
        CoreParserRegistry.ofEnum(Action.class)
            .parseRequired(new GenericStringHolder(raw.split("-")[0], null));
    String value = raw.split("-")[1];
    return new ClickEvent(action, value);
  }

  @Override
  protected TextStyle parse(String raw) {
    raw = raw.toLowerCase();
    if (raw.trim().isEmpty()) return null;
    if (raw.trim().equalsIgnoreCase("none")) {
      return StyleInjector.$NULL$.duplicate();
    }
    TextStyle style = TextStyle.create();
    for (String part : raw.split(";")) {
      if (part.contains(":")) {
        parseComplex(style, part);
      } else parseSimple(style, part);
    }
    return style;
  }
}

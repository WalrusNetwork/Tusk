package network.walrus.utils.core.chat;

import com.google.common.primitives.Chars;
import java.util.Iterator;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * Utilities for making chat messages look nicer.
 *
 * @author Austin Mayes
 */
public class ChatUtils {

  /**
   * Get a single side of padding which would be used to center a block of text in chat.
   *
   * @param text to get padding for
   * @param padChar which is used for padding
   * @return single side of padding which would be used to center the text
   */
  public static String paddingFor(String text, String padChar) {
    return com.google.common.base.Strings.repeat(
        padChar, (55 - ChatColor.stripColor(text).length() - 2) / (padChar.length() * 2));
  }

  /**
   * Create a line in chat with the specified color.
   *
   * @param color of the line
   * @return a line of the specified color
   */
  public static BaseComponent blankLine(ChatColor color) {
    TextComponent line = new TextComponent(com.google.common.base.Strings.repeat(" ", 59));
    line.setStrikethrough(true);
    line.setColor(color);
    return line;
  }

  /**
   * Copy all styles from one {@link BaseComponent} and apply it to another.
   *
   * @param source to get style from
   * @param destination to apply style to
   */
  public static void copyStyle(BaseComponent source, BaseComponent destination) {
    destination.setColor(source.getColorRaw());
    destination.setBold(source.isBoldRaw());
    destination.setItalic(source.isItalicRaw());
    destination.setUnderlined(source.isUnderlinedRaw());
    destination.setStrikethrough(source.isStrikethroughRaw());
    destination.setObfuscated(source.isObfuscatedRaw());
    destination.setInsertion(source.getInsertion());
    destination.setClickEvent(source.getClickEvent());
    destination.setHoverEvent(source.getHoverEvent());
  }

  /**
   * Remove duplicate color codes from a string.
   *
   * @param string to clean the codes from
   * @return the string with duplicate color codes removed
   */
  public static String cleanColorCodes(String string) {
    StringBuilder builder = new StringBuilder();

    Iterator<Character> iterator = Chars.asList(string.toCharArray()).iterator();

    Character lastCode = null;
    while (iterator.hasNext()) {
      char c = iterator.next();

      if (c == '§') {
        // Read in code
        Character code = iterator.next();
        if (code == null) {
          builder.append(c);
        } else if (lastCode != code) {
          // Code is different from last one, set last code and put in string
          lastCode = code;
          builder.append(c);
          builder.append(code);
        }
      } else {
        builder.append(c);
      }
    }

    return builder.toString();
  }
}

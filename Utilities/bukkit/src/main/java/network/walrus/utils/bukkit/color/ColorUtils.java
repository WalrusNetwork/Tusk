package network.walrus.utils.bukkit.color;

import com.google.common.base.Preconditions;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.DyeColor;

/**
 * Various utilities for working with colors.
 *
 * @author Austin Mayes
 */
public class ColorUtils {

  /**
   * Add {@link ChatColor}s to a string using {@code ^} as an alternate color code.
   *
   * @param message to colorize
   * @return colorized message
   */
  public static String addColors(String message) {
    return message == null ? null : ChatColor.translateAlternateColorCodes('^', message);
  }

  /**
   * Remove all {@link ChatColor}s from a string.
   *
   * @param message to remove colors from
   * @return plain message
   */
  public static String removeColors(String message) {
    return message == null ? null : ChatColor.stripColor(message);
  }

  /**
   * Convert a {@link DyeColor} to a {@link ChatColor}.
   *
   * @param color to convert
   * @return chat color based on dye color
   */
  public static ChatColor toChatColor(DyeColor color) {
    switch (color) {
      case WHITE:
        return ChatColor.WHITE;
      case ORANGE:
        return ChatColor.GOLD;
      case MAGENTA:
        return ChatColor.LIGHT_PURPLE;
      case LIGHT_BLUE:
        return ChatColor.BLUE;
      case YELLOW:
        return ChatColor.YELLOW;
      case LIME:
        return ChatColor.GREEN;
      case PINK:
        return ChatColor.RED;
      case GRAY:
        return ChatColor.DARK_GRAY;
      case SILVER:
        return ChatColor.GRAY;
      case CYAN:
        return ChatColor.DARK_AQUA;
      case PURPLE:
        return ChatColor.DARK_PURPLE;
      case BLUE:
        return ChatColor.BLUE;
      case BROWN:
        return ChatColor.GOLD;
      case GREEN:
        return ChatColor.DARK_GREEN;
      case RED:
        return ChatColor.DARK_RED;
      case BLACK:
        return ChatColor.GRAY;
      default:
        throw new IllegalArgumentException();
    }
  }

  /**
   * Convert a proportion (0-1) to a {@link ChatColor} representing how filled (closest to 1) it is.
   *
   * @param proportion to represent via color
   * @return color representing the proportion
   */
  public static ChatColor toChatColor(double proportion) {
    Preconditions.checkArgument(proportion >= 0.0D && proportion <= 1.0D);
    if (proportion <= 0.15D) {
      return ChatColor.RED;
    } else {
      return proportion <= 0.5D ? ChatColor.YELLOW : ChatColor.GREEN;
    }
  }
}

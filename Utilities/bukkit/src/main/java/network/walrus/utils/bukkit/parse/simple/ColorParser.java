package network.walrus.utils.bukkit.parse.simple;

import network.walrus.utils.core.config.ParsingException;
import network.walrus.utils.core.config.StringHolder;
import network.walrus.utils.core.parse.SimpleParser;
import org.bukkit.Color;

/**
 * Parser used to parse {@link Color}s from {@link StringHolder}s.
 *
 * <p>This supports color strings and hex values.
 *
 * @author Austin Mayes
 */
public class ColorParser implements SimpleParser<Color> {

  @Override
  public Color parseRequired(StringHolder holder) throws ParsingException {
    String text = holder.asRequiredString();

    // If a hexadecimal color representation is passed
    if (text.startsWith("#")) {
      String hex = text.substring(1);
      try {
        int red = Integer.valueOf(hex.substring(0, 2), 16);
        int green = Integer.valueOf(hex.substring(2, 4), 16);
        int blue = Integer.valueOf(hex.substring(4, 6), 16);
        return Color.fromRGB(red, green, blue);
      } catch (Exception e) {
        throw new ParsingException(holder.parent(), "Invalid hex code.");
      }
    }
    // Else a text color representation (red, blue, green, etc.)
    else {
      switch (text.toLowerCase()) {
        case "red":
          return Color.RED;
        case "green":
          return Color.GREEN;
        case "blue":
          return Color.BLUE;
        case "aqua":
          return Color.AQUA;
        case "black":
          return Color.BLACK;
        case "fuchsia":
        case "pink":
          return Color.FUCHSIA;
        case "gray":
        case "grey":
          return Color.GRAY;
        case "lime":
        case "light_green":
          return Color.LIME;
        case "maroon":
          return Color.MAROON;
        case "navy":
        case "dark_blue":
          return Color.NAVY;
        case "olive":
        case "dark_yellow":
          return Color.OLIVE;
        case "orange":
          return Color.ORANGE;
        case "purple":
        case "dark_purple":
          return Color.PURPLE;
        case "silver":
          return Color.SILVER;
        case "teal":
          return Color.TEAL;
        case "white":
          return Color.WHITE;
        case "yellow":
          return Color.YELLOW;

        default:
          throw new ParsingException(holder.parent(), "Invalid color provided.");
      }
    }
  }
}

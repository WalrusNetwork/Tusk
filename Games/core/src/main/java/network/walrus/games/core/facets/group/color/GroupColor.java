package network.walrus.games.core.facets.group.color;

import com.google.common.collect.Maps;
import java.util.Map;
import net.md_5.bungee.api.ChatColor;
import network.walrus.utils.core.translation.TextStyle;
import org.bukkit.Color;
import org.bukkit.DyeColor;

/**
 * Represents the possible colors that a team can have.
 *
 * @author Avicus Network
 */
public class GroupColor {

  public static final GroupColor BLACK = new GroupColor(ChatColor.BLACK, DyeColor.BLACK);
  public static final GroupColor DARK_BLUE = new GroupColor(ChatColor.DARK_BLUE, DyeColor.BLUE);
  public static final GroupColor GREEN = new GroupColor(ChatColor.DARK_GREEN, DyeColor.GREEN);
  public static final GroupColor CYAN = new GroupColor(ChatColor.DARK_AQUA, DyeColor.CYAN);
  public static final GroupColor RED = new GroupColor(ChatColor.DARK_RED, DyeColor.RED);
  public static final GroupColor PURPLE = new GroupColor(ChatColor.DARK_PURPLE, DyeColor.PURPLE);
  public static final GroupColor ORANGE = new GroupColor(ChatColor.GOLD, DyeColor.ORANGE);
  public static final GroupColor GRAY = new GroupColor(ChatColor.GRAY, DyeColor.SILVER);
  public static final GroupColor DARK_GRAY = new GroupColor(ChatColor.DARK_GRAY, DyeColor.GRAY);
  public static final GroupColor BLUE = new GroupColor(ChatColor.BLUE, DyeColor.BLUE);
  public static final GroupColor LIME = new GroupColor(ChatColor.GREEN, DyeColor.LIME);
  public static final GroupColor AQUA = new GroupColor(ChatColor.AQUA, DyeColor.LIGHT_BLUE);
  public static final GroupColor LIGHT_RED = new GroupColor(ChatColor.RED, DyeColor.RED);
  public static final GroupColor PINK = new GroupColor(ChatColor.LIGHT_PURPLE, DyeColor.PINK);
  public static final GroupColor YELLOW = new GroupColor(ChatColor.YELLOW, DyeColor.YELLOW);
  public static final GroupColor WHITE = new GroupColor(ChatColor.WHITE, DyeColor.WHITE);
  public static final GroupColor[] COLORS =
      new GroupColor[] {
        BLACK, DARK_BLUE, GREEN, CYAN, RED, PURPLE, ORANGE, GRAY, DARK_GRAY, BLUE, LIME, AQUA,
        LIGHT_RED, PINK, YELLOW, WHITE
      };
  public static final Map<String, GroupColor> BY_NAME = Maps.newHashMap();

  static {
    BY_NAME.put("black", BLACK);
    BY_NAME.put("dark blue", DARK_BLUE);
    BY_NAME.put("green", GREEN);
    BY_NAME.put("cyan", CYAN);
    BY_NAME.put("red", RED);
    BY_NAME.put("purple", PURPLE);
    BY_NAME.put("orange", ORANGE);
    BY_NAME.put("gray", GRAY);
    BY_NAME.put("dark gray", DARK_GRAY);
    BY_NAME.put("blue", BLUE);
    BY_NAME.put("lime", LIME);
    BY_NAME.put("aqua", AQUA);
    BY_NAME.put("light red", LIGHT_RED);
    BY_NAME.put("pink", PINK);
    BY_NAME.put("yellow", YELLOW);
    BY_NAME.put("white", WHITE);
  }

  private final ChatColor chatColor;
  private final DyeColor dyeColor;
  private boolean bold;
  private boolean italic;
  private boolean underline;
  private boolean strike;

  /**
   * @param chatColor used to represent the color
   * @param dyeColor used to represent the color
   */
  public GroupColor(ChatColor chatColor, DyeColor dyeColor) {
    this(chatColor, dyeColor, false, false, false);
  }

  /**
   * @param chatColor used to represent the color
   * @param dyeColor used to represent the color
   * @param bold if the color should be bold
   * @param italic if the color should be italic
   * @param underline if the color should be underline
   */
  public GroupColor(
      ChatColor chatColor, DyeColor dyeColor, boolean bold, boolean italic, boolean underline) {
    this.chatColor = chatColor;
    this.dyeColor = dyeColor;
    this.bold = bold;
    this.italic = italic;
    this.underline = underline;
    this.strike = false;
  }

  public ChatColor getChatColor() {
    return chatColor;
  }

  public DyeColor getDyeColor() {
    return dyeColor;
  }

  public Color getColor() {
    return this.dyeColor.getColor();
  }

  public Color getFireworkColor() {
    return this.dyeColor.getFireworkColor();
  }

  public String getPrefix() {
    return getChatColor()
        + (italic ? ChatColor.ITALIC.toString() : "")
        + (bold ? ChatColor.BOLD.toString() : "")
        + (underline ? ChatColor.UNDERLINE.toString() : "")
        + (strike ? ChatColor.STRIKETHROUGH.toString() : "");
  }

  public boolean isBold() {
    return bold;
  }

  /**
   * Change bold status for the color
   *
   * @param bold if the color should be bold
   * @return the modified color
   */
  public GroupColor bold(boolean bold) {
    this.bold = bold;
    return this;
  }

  public boolean isItalic() {
    return italic;
  }

  /**
   * Change italic status for the color
   *
   * @param italic if the color should be italic
   * @return the modified color
   */
  public GroupColor italic(boolean italic) {
    this.italic = italic;
    return this;
  }

  public boolean isUnderline() {
    return underline;
  }

  /**
   * Change underline status for the color
   *
   * @param underline if the color should be underline
   * @return the modified color
   */
  public GroupColor underline(boolean underline) {
    this.underline = underline;
    return this;
  }

  public boolean isStrike() {
    return strike;
  }

  /**
   * Change strikethrough status for the color
   *
   * @param strike if the color should be strikethrough
   * @return the modified color
   */
  public GroupColor strike(boolean strike) {
    this.strike = strike;
    return this;
  }

  /** @return a text style made up of all of this object's attributes */
  public TextStyle style() {
    return TextStyle.ofColor(getChatColor())
        .bold(isBold())
        .italic(isItalic())
        .underlined(isUnderline())
        .strike(isStrike());
  }

  public GroupColor clone() {
    return new GroupColor(getChatColor(), getDyeColor(), isBold(), isItalic(), isUnderline());
  }
}

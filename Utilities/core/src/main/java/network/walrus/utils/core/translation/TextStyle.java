package network.walrus.utils.core.translation;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import network.walrus.utils.core.chat.ChatUtils;

/**
 * Wrapper class to aid in the styling of {@link BaseComponent}s.
 *
 * @author Avicus Network
 */
@SuppressWarnings("unchecked")
public class TextStyle {

  private static final TextStyle PAD_DEFAULT = new TextStyle().strike();

  private ChatColor color = null;
  private Boolean bold = null;
  private Boolean italic = null;
  private Boolean underlined = null;
  private Boolean magic = null;
  private Boolean strike = null;
  private ClickEvent click = null;
  private HoverEvent hover = null;
  private Localizable hoverText = null;
  private Boolean padded = null;
  private String padChar = "-";
  private TextStyle padStyle = PAD_DEFAULT;

  protected TextStyle() {}

  /** @see #TextStyle() and {@link #bold()}. */
  public static TextStyle ofBold() {
    return new TextStyle().bold();
  }

  /** @see #TextStyle() and {@link #color(ChatColor)}. */
  public static TextStyle ofColor(ChatColor color) {
    return new TextStyle().color(color);
  }

  /** @see #TextStyle() */
  public static TextStyle create() {
    return new TextStyle();
  }

  /**
   * Create a {@link TextStyle} from information defined in a {@link BaseComponent}.
   *
   * @param component to get information from
   * @return a style based off of the data defined in the component
   */
  public static TextStyle from(BaseComponent component) {
    final TextStyle style = TextStyle.create();
    style.color = component.getColorRaw();
    style.bold = component.isBoldRaw();
    style.italic = component.isItalicRaw();
    style.underlined = component.isUnderlinedRaw();
    style.magic = component.isObfuscatedRaw();
    style.strike = component.isStrikethroughRaw();
    style.click(component.getClickEvent());
    style.hover(component.getHoverEvent());
    return style;
  }

  /** Create an exact copy of this style. */
  public TextStyle duplicate() {
    return new TextStyle().inherit(this);
  }

  /** @see #apply(BaseComponent) */
  public BaseComponent apply(String text) {
    return this.apply(new TextComponent(text));
  }

  /**
   * Apply this style to a component.
   *
   * @param message to apply this style to
   * @return the component with all the styling defined in this object
   */
  public BaseComponent apply(BaseComponent message) {
    if (this.color != null) {
      message.setColor(this.color);
    }

    if (this.bold != null) {
      message.setBold(this.bold);
    }
    if (this.italic != null) {
      message.setItalic(this.italic);
    }
    if (this.underlined != null) {
      message.setUnderlined(this.underlined);
    }
    if (this.magic != null) {
      message.setObfuscated(this.magic);
    }
    if (this.strike != null) {
      message.setStrikethrough(this.strike);
    }

    if (this.click != null) {
      message.setClickEvent(this.click);
    }
    if (this.hover != null) {
      message.setHoverEvent(this.hover);
    }

    if (this.padded == null || !this.padded) return message;

    TextComponent padding = new TextComponent(ChatUtils.paddingFor(message.toPlainText(), padChar));
    this.padStyle.apply(padding);

    BaseComponent text = new TextComponent(" ");
    text.addExtra(message);
    text.addExtra(" ");

    return new TextComponent(padding, text, padding);
  }

  /**
   * Inherit any data that this style does not have from a parent style.
   *
   * @param parent to inherit attributes from
   */
  public TextStyle inherit(TextStyle parent) {
    this.color = this.color != null ? this.color : parent.color;
    this.bold = this.bold != null ? this.bold : parent.bold;
    this.italic = this.italic != null ? this.italic : parent.italic;
    this.underlined = this.underlined != null ? this.underlined : parent.underlined;
    this.magic = this.magic != null ? this.magic : parent.magic;
    this.strike = this.strike != null ? this.strike : parent.strike;
    this.click = this.click != null ? this.click : parent.click;
    this.hover = this.hover != null ? this.hover : parent.hover;
    this.padded = this.padded != null ? this.padded : parent.padded;
    this.padChar = this.padChar != null ? this.padChar : parent.padChar;
    this.padStyle = this.padStyle != null ? this.padStyle : parent.padStyle;
    return this;
  }

  /** Reset all data of this style back to the default values. */
  public TextStyle reset() {
    this.color = null;
    this.bold = null;
    this.italic = null;
    this.underlined = null;
    this.magic = null;
    this.strike = null;
    this.click = null;
    this.hover = null;
    this.padded = null;
    this.padChar = " ";
    this.padStyle = TextStyle.create().strike();
    return this;
  }

  /**
   * Set the color of this style.
   *
   * @param color that the text of this style should be
   */
  public TextStyle color(ChatColor color) {
    this.color = color;
    return this;
  }

  /** @return the color of this style */
  public ChatColor color() {
    return color;
  }

  /**
   * Set if the text of this style should have the bold effect.
   *
   * @param bold if the text of this style should have the bold effect
   */
  public TextStyle bold(boolean bold) {
    this.bold = bold;
    return this;
  }

  /** Bold the text of this style. */
  public TextStyle bold() {
    return bold(true);
  }

  /**
   * Set if the text of this style should be italicized.
   *
   * @param italic if the text of this style should be italicized
   */
  public TextStyle italic(boolean italic) {
    this.italic = italic;
    return this;
  }

  /** Italicize the text of this style. */
  public TextStyle italic() {
    return italic(true);
  }

  /**
   * Set if the text of this style should have a line under it.
   *
   * @param underlined if the text of this style should have a line under it
   */
  public TextStyle underlined(boolean underlined) {
    this.underlined = underlined;
    return this;
  }

  /** Add a line underneath the text of this style. */
  public TextStyle underlined() {
    return underlined(true);
  }

  /**
   * Set if the "magic" effect should be applied for the text of this style.
   *
   * @param magic if the text should be replaced with random characters
   */
  public TextStyle magic(boolean magic) {
    this.magic = magic;
    return this;
  }

  /** Enable the "magic" effect for the text of this style. */
  public TextStyle magic() {
    return magic(true);
  }

  /**
   * Set if the text of the style should have a strike through it.
   *
   * @param strike if the text should have a strike through it
   */
  public TextStyle strike(boolean strike) {
    this.strike = strike;
    return this;
  }

  /** Set the text of the style to have a strike through it. */
  public TextStyle strike() {
    return strike(true);
  }

  /**
   * Set the click event of this style.
   *
   * @param event to be performed when this component is clicked
   */
  public TextStyle click(ClickEvent event) {
    this.click = event;
    return this;
  }

  /**
   * Set the hover event of this style.
   *
   * @param event to be performed when this component is hovered over
   */
  public TextStyle hover(HoverEvent event) {
    this.hover = event;
    return this;
  }

  /** Get the hover event of this style. */
  public HoverEvent hover() {
    return this.hover;
  }

  /** Get the click event of this style. */
  public ClickEvent click() {
    return this.click;
  }

  /**
   * Set the hover text of this style.
   *
   * @param text to be displayed when this component is hovered over
   */
  public TextStyle hover(Localizable text) {
    this.hoverText = text;
    return this;
  }

  /** Get the hover text of this style. */
  public Localizable hoverText() {
    return this.hoverText;
  }

  /** Set the text to be padded */
  public TextStyle padded() {
    return padded(true);
  }

  /**
   * Set if the text of this style should have a padding around it.
   *
   * @param padded if the text should have a padding around it
   */
  public TextStyle padded(Boolean padded) {
    this.padded = padded;
    return this;
  }

  /** @return the character(s) which should be used as a padding */
  public String padChar() {
    return padChar;
  }

  /**
   * Set the character(s) which should be used as a padding.
   *
   * @param padChar the character(s) which should be used as a padding
   */
  public TextStyle padChar(String padChar) {
    this.padChar = padChar;
    return this;
  }

  /** @return the style to be applied to the padding */
  public TextStyle padStyle() {
    return padStyle;
  }

  /**
   * Set the style to be applied to the padding.
   *
   * @param padStyle to be applied to the padding
   */
  public TextStyle padStyle(TextStyle padStyle) {
    this.padStyle = padStyle;
    return this;
  }
}

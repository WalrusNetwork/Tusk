package network.walrus.utils.core.player;

import net.md_5.bungee.api.chat.BaseComponent;
import network.walrus.utils.core.translation.TextStyle;

/**
 * Additional properties of {@link TextStyle}s which apply to {@link PersonalizedPlayer}s.
 *
 * @author Austin Mayes
 */
public class PlayerTextStyle extends TextStyle {

  private Boolean showFullName = true;
  private PrefixType prefixType = null;

  /** @return a new player style */
  public static PlayerTextStyle create() {
    return new PlayerTextStyle();
  }

  @Override
  public PlayerTextStyle duplicate() {
    return new PlayerTextStyle().inherit(this);
  }

  @Override
  public BaseComponent apply(BaseComponent message) {
    return super.apply(message);
  }

  @Override
  public PlayerTextStyle inherit(TextStyle parent) {
    super.inherit(parent);
    if (parent instanceof PlayerTextStyle) {
      this.showFullName =
          this.showFullName == null ? ((PlayerTextStyle) parent).showFullName : this.showFullName;
      this.prefixType =
          this.prefixType == null ? ((PlayerTextStyle) parent).prefixType : this.prefixType;
    }
    return this;
  }

  @Override
  public TextStyle reset() {
    super.reset();
    this.showFullName = true;
    this.prefixType = null;
    return this;
  }

  /**
   * @return if the full name (fake name, along with the real name) should be shown if the viewer
   *     can see it
   */
  public Boolean showFullName() {
    return showFullName;
  }

  /**
   * @param seeThroughNick if the full name (fake name, along with the real name) should be shown if
   *     the viewer can see it
   */
  public PlayerTextStyle showFullName(Boolean seeThroughNick) {
    this.showFullName = seeThroughNick;
    return this;
  }

  /** @return style of the network-wide prefix (rank, for example) */
  public PrefixType prefixType() {
    return prefixType == null ? PrefixType.CONDENSED : prefixType;
  }

  /** @param prefixType style of the network-wide prefix (rank, for example) */
  public PlayerTextStyle prefixType(PrefixType prefixType) {
    this.prefixType = prefixType;
    return this;
  }

  public enum PrefixType {
    LONG,
    CONDENSED,
    NONE
  }
}

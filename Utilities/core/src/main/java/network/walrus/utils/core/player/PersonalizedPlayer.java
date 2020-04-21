package network.walrus.utils.core.player;

import java.util.function.Function;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import network.walrus.common.CommandSender;
import network.walrus.utils.core.player.PlayerTextStyle.PrefixType;
import network.walrus.utils.core.translation.Localizable;
import network.walrus.utils.core.translation.TextStyle;

/**
 * A {@link network.walrus.common.text.PersonalizedComponent} which renders players to viewers
 * individually allowing for additional properties (like real name, if the target is nicked) to be
 * shown to players who should see these attributes on the fly without any additional work.
 *
 * @author Austin Mayes
 */
public abstract class PersonalizedPlayer implements Localizable {

  public static Function<CommandSenderRelationInfo, HoverEvent> HOVER_FUNC = null;
  public static Function<CommandSenderRelationInfo, ClickEvent> CLICK_FUNC = null;
  public static PrefixGenerator PREFIX_GENERATOR = null;
  protected final CommandSender sender;
  protected final TextStyle style;

  /**
   * @param sender that is being rendered
   * @param style of the rendered text
   */
  public PersonalizedPlayer(CommandSender sender, TextStyle style) {
    this.sender = sender;
    this.style = style;
  }

  @Override
  public TextStyle style() {
    return this.style;
  }

  protected void addCustomStyling(CommandSenderRelationInfo info, BaseComponent component) {
    PlayerTextStyle playerStyle = (PlayerTextStyle) style;
    if (PREFIX_GENERATOR != null
        && playerStyle.prefixType() != PrefixType.NONE
        && (info.trueIdentityPrimary() || info.canSeeThroughFakeIdentity())) {
      if (playerStyle.prefixType() == PrefixType.LONG)
        component.addExtra(PREFIX_GENERATOR.renderLong(info));
      else component.addExtra(PREFIX_GENERATOR.renderCondensed(info));
    }
    if (playerStyle.showFullName()
        && !info.trueIdentityPrimary()
        && info.canSeeThroughFakeIdentity()) {
      component.addExtra(ChatColor.STRIKETHROUGH + info.realName());
    }
  }

  protected void applyCustomClickHover(CommandSenderRelationInfo info) {
    if (HOVER_FUNC != null && this.style.hover() == null && this.style.hoverText() == null) {
      this.style.hover(HOVER_FUNC.apply(info));
    }
    if (CLICK_FUNC != null && this.style.click() == null) {
      this.style.click(CLICK_FUNC.apply(info));
    }
  }
}

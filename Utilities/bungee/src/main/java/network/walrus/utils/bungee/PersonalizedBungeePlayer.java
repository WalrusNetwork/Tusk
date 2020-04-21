package network.walrus.utils.bungee;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import network.walrus.utils.core.player.CommandSenderRelationInfo;
import network.walrus.utils.core.player.PersonalizedPlayer;
import network.walrus.utils.core.player.PlayerTextStyle;
import network.walrus.utils.core.translation.Localizable;
import network.walrus.utils.core.translation.TextStyle;

/**
 * Bungee implementation of {@link PersonalizedPlayer}.
 *
 * @author Austin Mayes
 */
public class PersonalizedBungeePlayer extends PersonalizedPlayer {

  /**
   * @param sender that is being rendered
   * @param style of the rendered text
   */
  public PersonalizedBungeePlayer(CommandSender sender, TextStyle style) {
    super(sender, style);
  }

  /** @param sender that is being rendered */
  public PersonalizedBungeePlayer(CommandSender sender) {
    this(sender, PlayerTextStyle.create());
  }

  @Override
  public BaseComponent render(network.walrus.common.CommandSender viewer) {
    TextComponent component = new TextComponent("");
    CommandSenderRelationInfo info = new CommandSenderRelationInfo(this.sender, viewer);
    if (style instanceof PlayerTextStyle && this.sender instanceof ProxiedPlayer) {
      addCustomStyling(info, component);
    }
    component.addExtra(info.visibleName());
    applyCustomClickHover(info);
    style.apply(component);
    return component;
  }

  @Override
  public Localizable duplicate() {
    return new PersonalizedBungeePlayer((CommandSender) sender, style);
  }
}

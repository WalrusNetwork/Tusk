package network.walrus.utils.bukkit.translation.text.player;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import network.walrus.utils.core.player.CommandSenderRelationInfo;
import network.walrus.utils.core.player.PersonalizedPlayer;
import network.walrus.utils.core.player.PlayerTextStyle;
import network.walrus.utils.core.translation.Localizable;
import network.walrus.utils.core.translation.TextStyle;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Bukkit implementation of {@link PersonalizedPlayer}.
 *
 * @author Austin Mayes
 */
public class PersonalizedBukkitPlayer extends PersonalizedPlayer {

  /**
   * @param sender that is being rendered
   * @param style of the rendered text
   */
  public PersonalizedBukkitPlayer(CommandSender sender, TextStyle style) {
    super(sender, style);
  }

  /** @param sender that is being rendered */
  public PersonalizedBukkitPlayer(CommandSender sender) {
    this(sender, PlayerTextStyle.create());
  }

  @Override
  public BaseComponent render(network.walrus.common.CommandSender viewer) {
    TextComponent component = new TextComponent("");
    CommandSenderRelationInfo info = new CommandSenderRelationInfo(this.sender, viewer);
    if (style instanceof PlayerTextStyle && this.sender instanceof Player) {
      addCustomStyling(info, component);
    }
    component.addExtra(info.visibleName());
    applyCustomClickHover(info);
    style.apply(component);
    return component;
  }

  @Override
  public Localizable duplicate() {
    return new PersonalizedBukkitPlayer((CommandSender) sender, style);
  }
}

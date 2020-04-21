package network.walrus.games.core.facets.chat;

import app.ashcon.intake.Command;
import app.ashcon.intake.bukkit.parametric.annotation.Sender;
import network.walrus.games.core.GamesCoreMessages;
import network.walrus.games.core.facets.chat.ChatFacet.QuickChatMode;
import network.walrus.utils.bukkit.sound.NetworkSoundConstants.Games.Chat;
import network.walrus.utils.core.color.NetworkColorConstants.Games;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import network.walrus.utils.parsing.facet.parse.configurator.command.FacetCommandContainer;
import org.bukkit.entity.Player;

/**
 * Command used to toggle quick chat.
 *
 * @author Austin Mayes
 */
public class QuickChatCommands extends FacetCommandContainer<ChatFacet> {

  /**
   * @param holder which this object is inside of
   * @param facet which this object is bound to
   */
  public QuickChatCommands(FacetHolder holder, ChatFacet facet) {
    super(holder, facet);
  }

  /** Toggle quick chat for a player (if they are allowed to). */
  @Command(
      aliases = {"quickchat", "qc", "toggleqc", "togglequickchat"},
      desc = "Toggle quick chat.")
  public void toggle(@Sender Player player) {
    if (!getFacet().canToggleQuickChat(player)) {
      Chat.QUICKCHAT_CANNOT_DISABLE.play(player);
      player.sendMessage(
          GamesCoreMessages.QUICKCHAT_CANNOT_SWITCH.with(Games.Chats.QUICKCHAT_ERROR));
      return;
    }

    if (getFacet().isUsingQuickChat(player)) {
      getFacet().setChatMode(player, QuickChatMode.OFF);
      player.sendMessage(GamesCoreMessages.QUICKCHAT_DISABLED.with(Games.Chats.QUICKCHAT_SWITCHED));
    } else {
      getFacet().setChatMode(player, QuickChatMode.ON);
      player.sendMessage(GamesCoreMessages.QUICKCHAT_ENABLED.with(Games.Chats.QUICKCHAT_SWITCHED));
      getFacet().broadcastOptions(player);
    }
    Chat.QUICKCHAT_SWITCHED.play(player);
  }
}

package network.walrus.games.core.facets.chat;

import app.ashcon.intake.Command;
import app.ashcon.intake.bukkit.parametric.annotation.Sender;
import app.ashcon.intake.parametric.annotation.Text;
import javax.annotation.Nullable;
import network.walrus.games.core.GamesCoreMessages;
import network.walrus.utils.core.color.NetworkColorConstants.Games;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import network.walrus.utils.parsing.facet.parse.configurator.command.FacetCommandContainer;
import org.bukkit.entity.Player;

/**
 * Commands for talking in and switching between different channels.
 *
 * @author Austin Mayes
 */
public class ChannelCommands extends FacetCommandContainer<ChatFacet> {

  /**
   * @param holder that the commands are operating in
   * @param facet used to get channel information from
   */
  public ChannelCommands(FacetHolder holder, ChatFacet facet) {
    super(holder, facet);
  }

  /** Talk in or toggle global chat. */
  @Command(
      aliases = {"g", "shout"},
      desc = "Talk in or toggle global chat.",
      anyFlags = true)
  public void global(@Sender Player player, @Text @Nullable String message) {
    if (message != null) {
      if (!isGlobalAllowed(player)) {
        return;
      }
      getFacet().chatToGlobal(player, message);
    } else {
      toggle(player);
    }
  }

  /** Talk in or toggle team chat. */
  @Command(
      aliases = {"t"},
      desc = "Talk in or toggle team chat.",
      anyFlags = true)
  public void team(@Sender Player player, @Text @Nullable String message) {
    if (message != null) {
      if (!isTeamAllowed(player)) {
        return;
      }
      getFacet().chatToTeam(player, message);
    } else {
      toggle(player);
    }
  }

  private void toggle(Player player) {
    ChatChannel currentChannel = getFacet().getChatChannel(player);

    if (currentChannel == ChatChannel.TEAM) {
      if (!isGlobalAllowed(player)) {
        return;
      }
      player.sendMessage(
          GamesCoreMessages.GENERIC_CHAT_GLOBAL_ENABLED.with(Games.Chats.CHAT_SWITCHED));
      getFacet().setChatChannel(player, ChatChannel.GLOBAL);
    } else {
      if (!isTeamAllowed(player)) {
        return;
      }
      player.sendMessage(
          GamesCoreMessages.GENERIC_CHAT_TEAM_ENABLED.with(Games.Chats.CHAT_SWITCHED));
      getFacet().setChatChannel(player, ChatChannel.TEAM);
    }
  }

  private boolean isGlobalAllowed(Player player) {
    if (!getFacet().isGlobalChatAllowed()) {
      player.sendMessage(
          GamesCoreMessages.ERROR_CHAT_GLOBAL_DISABLED.with(Games.Chats.CHAT_DISABLED));
      return false;
    } else {
      return true;
    }
  }

  private boolean isTeamAllowed(Player player) {
    if (!getFacet().isTeamChatAllowed()) {
      player.sendMessage(
          GamesCoreMessages.ERROR_CHAT_TEAM_DISABLED.with(Games.Chats.CHAT_DISABLED));
      return false;
    } else {
      return true;
    }
  }
}

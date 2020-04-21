package network.walrus.games.core.facets.chat;

import network.walrus.games.core.round.GameRound;
import network.walrus.utils.parsing.facet.parse.configurator.FacetConfigurator;

/**
 * Configurator which is used to enable team and global chat functionality.
 *
 * <p>Implementations must register this configurator specifically in order to enable the
 * functionality. This is done in order for different game types to determine if team/global chat
 * should be enabled.
 *
 * @author Austin Mayes
 */
public class ChatConfigurator implements FacetConfigurator {

  private final boolean teamChat;
  private final boolean globalChat;

  /**
   * @param teamChat if team chat is enabled
   * @param globalChat if global chat is enabled
   */
  public ChatConfigurator(boolean teamChat, boolean globalChat) {
    this.teamChat = teamChat;
    this.globalChat = globalChat;
  }

  @Override
  public void configure() {
    bindFacetDirect(
        ChatFacet.class,
        new Class[] {boolean.class, boolean.class},
        new Object[] {teamChat, globalChat},
        (h) -> h instanceof GameRound);
    bindFacetCommands(ChannelCommands.class, ChatFacet.class);
    bindFacetCommands(QuickChatCommands.class, ChatFacet.class);
  }
}

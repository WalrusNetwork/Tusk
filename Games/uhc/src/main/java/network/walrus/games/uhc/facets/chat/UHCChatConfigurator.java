package network.walrus.games.uhc.facets.chat;

import network.walrus.games.core.facets.chat.ChatConfigurator;
import network.walrus.games.core.facets.chat.ChatFacet;

/**
 * Configures additional features on top of the {@link ChatConfigurator}.
 *
 * @author Austin Mayes
 */
public class UHCChatConfigurator extends ChatConfigurator {

  /** Constructor */
  public UHCChatConfigurator() {
    super(true, true);
  }

  @Override
  public void configure() {
    super.configure();
    bindFacetCommands(TeamChatCommands.class, ChatFacet.class);
  }
}

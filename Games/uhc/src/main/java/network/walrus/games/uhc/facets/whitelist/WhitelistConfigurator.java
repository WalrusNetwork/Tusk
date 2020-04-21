package network.walrus.games.uhc.facets.whitelist;

import network.walrus.utils.parsing.facet.parse.configurator.FacetConfigurator;
import network.walrus.utils.parsing.lobby.Lobby;

/**
 * Configurator for the {@link WhitelistAutomationFacet}.
 *
 * @author Austin Mayes
 */
public class WhitelistConfigurator implements FacetConfigurator {

  @Override
  public void configure() {
    bindFacetDirect(WhitelistAutomationFacet.class, (h) -> h instanceof Lobby);
    bindFacetCommands(WhitelistCommands.class, WhitelistAutomationFacet.class);
  }
}

package network.walrus.ubiquitous.bukkit.lobby.facets.pads;

import network.walrus.utils.parsing.facet.parse.configurator.FacetConfigurator;
import network.walrus.utils.parsing.lobby.Lobby;

/**
 * Configures the {@link JumpPadsManager}.
 *
 * @author Austin Mayes
 */
public class JumpPadsConfigurator implements FacetConfigurator {

  @Override
  public void configure() {
    bindParser(JumpPadsParser.class, (h) -> h instanceof Lobby);
  }
}

package network.walrus.utils.parsing.lobby.facets.spawns;

import network.walrus.utils.parsing.facet.parse.configurator.FacetConfigurator;
import network.walrus.utils.parsing.lobby.Lobby;

/**
 * Configures the {@link LobbySpawnManager}.
 *
 * @author Austin Mayes
 */
public class LobbySpawnsConfigurator implements FacetConfigurator {

  @Override
  public void configure() {
    bindParser(LobbySpawnsParser.class, (h) -> h instanceof Lobby);
  }
}

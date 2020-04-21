package network.walrus.ubiquitous.bukkit.lobby.facets.sterile;

import network.walrus.utils.parsing.facet.parse.configurator.FacetConfigurator;
import network.walrus.utils.parsing.lobby.facets.spawns.LobbySpawnManager;

/**
 * Configures listeners which help keep the lobby sterile.
 *
 * @author Austin Mayes
 */
public class LobbySterilizationConfigurator implements FacetConfigurator {

  @Override
  public void configure() {
    bindFacetListener(PlayerInteractionListener.class, LobbySpawnManager.class);
    bindFacetListener(WorldProtectionListener.class, LobbySpawnManager.class);
  }
}

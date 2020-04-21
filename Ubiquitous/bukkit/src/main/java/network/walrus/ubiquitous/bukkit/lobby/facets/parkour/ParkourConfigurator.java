package network.walrus.ubiquitous.bukkit.lobby.facets.parkour;

import network.walrus.utils.parsing.facet.parse.configurator.FacetConfigurator;
import network.walrus.utils.parsing.lobby.Lobby;

/**
 * Configures the {@link ParkourManager}.
 *
 * @author Austin Mayes
 */
public class ParkourConfigurator implements FacetConfigurator {

  @Override
  public void configure() {
    bindParser(ParkourParser.class, (h) -> h instanceof Lobby);
  }
}

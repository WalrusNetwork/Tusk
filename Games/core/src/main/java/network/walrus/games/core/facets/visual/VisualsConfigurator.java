package network.walrus.games.core.facets.visual;

import network.walrus.games.core.round.GameRound;
import network.walrus.utils.parsing.facet.parse.configurator.FacetConfigurator;

/**
 * Configures all visual facets.
 *
 * @author Austin Mayes
 */
public class VisualsConfigurator implements FacetConfigurator {

  @Override
  public void configure() {
    bindFacetDirect(SidebarFacet.class, (h) -> h instanceof GameRound);
    bindFacetDirect(TabListFacet.class, (h) -> h instanceof GameRound);
    bindFacetCommands(ScoreboardCommands.class, SidebarFacet.class);
  }
}

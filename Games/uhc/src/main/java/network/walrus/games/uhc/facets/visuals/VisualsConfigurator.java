package network.walrus.games.uhc.facets.visuals;

import network.walrus.games.core.facets.visual.ScoreboardCommands;
import network.walrus.games.core.facets.visual.SidebarFacet;
import network.walrus.games.uhc.UHCRound;
import network.walrus.utils.parsing.facet.parse.configurator.FacetConfigurator;

/**
 * Configurator for visual aspects of UHCs.
 *
 * @author Austin Mayes
 */
public class VisualsConfigurator implements FacetConfigurator {

  @Override
  public void configure() {
    bindFacetDirect(UHCSidebarFacet.class, (h) -> h instanceof UHCRound);
    bindFacetDirect(TabListFacet.class, (h) -> h instanceof UHCRound);
    bindFacetCommands(ScoreboardCommands.class, SidebarFacet.class);
    bindFacetListener(UHCScoreboardListener.class, SidebarFacet.class);
    SidebarFacet.PANE_CREATE_FUNCTION = UHCScoreboardCreator::create;
  }
}

package network.walrus.games.uhc.facets.tpall;

import network.walrus.games.uhc.UHCRound;
import network.walrus.utils.parsing.facet.parse.configurator.FacetConfigurator;

/**
 * Configurator for the {@link TpAllFacet}.
 *
 * @author Rafi Baum
 */
public class TpAllConfigurator implements FacetConfigurator {

  @Override
  public void configure() {
    bindFacetDirect(TpAllFacet.class, (h) -> (h instanceof UHCRound));
    bindFacetCommands(TpAllCommands.class, TpAllFacet.class);
  }
}

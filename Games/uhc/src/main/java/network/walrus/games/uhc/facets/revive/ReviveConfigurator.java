package network.walrus.games.uhc.facets.revive;

import network.walrus.games.uhc.UHCRound;
import network.walrus.utils.parsing.facet.parse.configurator.FacetConfigurator;

/**
 * Configurator for {@link ReviveFacet}.
 *
 * @author Rafi Baum
 */
public class ReviveConfigurator implements FacetConfigurator {

  @Override
  public void configure() {
    bindFacetDirect(ReviveFacet.class, (h) -> h instanceof UHCRound);
    bindFacetCommands(ReviveCommands.class, ReviveFacet.class);
  }
}

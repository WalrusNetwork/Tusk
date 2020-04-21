package network.walrus.games.uhc.facets.endgame;

import network.walrus.games.uhc.UHCRound;
import network.walrus.utils.parsing.facet.parse.configurator.FacetConfigurator;

/**
 * Configures the {@link GameEndFacet}
 *
 * @author Austin Mayes
 */
public class EndGameConfigurator implements FacetConfigurator {

  @Override
  public void configure() {
    bindFacetDirect(GameEndFacet.class, (h) -> h instanceof UHCRound);
  }
}

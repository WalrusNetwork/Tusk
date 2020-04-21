package network.walrus.games.uhc.facets.potions;

import network.walrus.games.uhc.UHCRound;
import network.walrus.utils.parsing.facet.parse.configurator.FacetConfigurator;

/**
 * Configures the {@link PotionControlFacet}.
 *
 * @author Austin Mayes
 */
public class PotionsConfigurator implements FacetConfigurator {

  @Override
  public void configure() {
    bindFacetDirect(PotionControlFacet.class, (h) -> h instanceof UHCRound);
  }
}

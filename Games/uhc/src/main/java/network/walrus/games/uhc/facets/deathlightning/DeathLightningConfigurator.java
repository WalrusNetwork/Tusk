package network.walrus.games.uhc.facets.deathlightning;

import network.walrus.games.uhc.UHCRound;
import network.walrus.utils.parsing.facet.parse.configurator.FacetConfigurator;

/**
 * Configurator for {@link DeathLightningFacet}.
 *
 * @author Rafi Baum
 */
public class DeathLightningConfigurator implements FacetConfigurator {

  @Override
  public void configure() {
    bindFacetDirect(DeathLightningFacet.class, (h) -> h instanceof UHCRound);
  }
}

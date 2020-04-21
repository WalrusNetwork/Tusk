package network.walrus.games.core.facets.hunger;

import network.walrus.utils.parsing.facet.parse.configurator.FacetConfigurator;

/**
 * Configurator for {@link HungerFacet}.
 *
 * @author Rafi Baum
 */
public class HungerFacetConfigurator implements FacetConfigurator {

  @Override
  public void configure() {
    bindParser(HungerFacetParser.class);
  }
}

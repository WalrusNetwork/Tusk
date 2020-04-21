package network.walrus.games.core.facets.crafting;

import network.walrus.utils.parsing.facet.parse.configurator.FacetConfigurator;

/**
 * Configures craft protect class.
 *
 * @author Rafi Baum
 */
public class CraftingConfigurator implements FacetConfigurator {

  @Override
  public void configure() {
    bindFacetDirect(CraftProtectFacet.class);
  }
}

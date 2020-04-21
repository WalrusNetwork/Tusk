package network.walrus.games.core.facets.crafting;

import network.walrus.utils.parsing.facet.parse.configurator.FacetConfigurator;

/**
 * Configures disabled crafting classes.
 *
 * @author Wesley Smith
 */
public class DisableCraftingConfigurator implements FacetConfigurator {

  @Override
  public void configure() {
    bindParser(DisableCraftingParser.class);
  }
}

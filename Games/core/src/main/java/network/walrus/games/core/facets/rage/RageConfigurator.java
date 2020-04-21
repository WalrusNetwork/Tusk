package network.walrus.games.core.facets.rage;

import network.walrus.utils.parsing.facet.parse.configurator.FacetConfigurator;

/**
 * Configures the rage facet
 *
 * @author Wesley Smith
 */
public class RageConfigurator implements FacetConfigurator {

  @Override
  public void configure() {
    bindParser(RageParser.class);
  }
}

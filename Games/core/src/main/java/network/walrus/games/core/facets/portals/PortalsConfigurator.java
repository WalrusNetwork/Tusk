package network.walrus.games.core.facets.portals;

import network.walrus.utils.parsing.facet.parse.configurator.FacetConfigurator;

/**
 * Configures the {@link PortalsParser}.
 *
 * @author Austin Mayes
 */
public class PortalsConfigurator implements FacetConfigurator {

  @Override
  public void configure() {
    bindParser(PortalsParser.class);
  }
}

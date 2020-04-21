package network.walrus.utils.parsing.facet.facets.region;

import network.walrus.utils.parsing.facet.parse.configurator.FacetConfigurator;

/**
 * Configures aspects of the core region system.
 *
 * @author Austin Mayes
 */
public class RegionsConfigurator implements FacetConfigurator {

  @Override
  public void configure() {
    bindParser(RegionsFacetParser.class);
  }
}

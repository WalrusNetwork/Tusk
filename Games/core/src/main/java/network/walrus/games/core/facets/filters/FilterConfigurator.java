package network.walrus.games.core.facets.filters;

import network.walrus.utils.parsing.facet.parse.configurator.FacetConfigurator;

/**
 * Configures aspects of the core filter system.
 *
 * @author Austin Mayes
 */
public class FilterConfigurator implements FacetConfigurator {

  @Override
  public void configure() {
    bindParser(FilterDefinitionParser.class);
  }
}

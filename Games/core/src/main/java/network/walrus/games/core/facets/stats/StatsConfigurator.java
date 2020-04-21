package network.walrus.games.core.facets.stats;

import network.walrus.utils.parsing.facet.parse.configurator.FacetConfigurator;

public class StatsConfigurator implements FacetConfigurator {

  @Override
  public void configure() {
    bindFacetCommands(StatsCommand.class, StatsFacet.class);
  }
}

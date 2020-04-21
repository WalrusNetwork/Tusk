package network.walrus.games.octc.global.stats;

import network.walrus.games.core.facets.stats.StatsConfigurator;

/**
 * Configurator for {@link OCNStatsFacet}.
 *
 * @author Rafi Baum
 */
public class OCNStatsConfigurator extends StatsConfigurator {

  @Override
  public void configure() {
    super.configure();
    bindFacetDirect(OCNStatsFacet.class);
  }
}

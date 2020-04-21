package network.walrus.games.core.facets.spawners;

import network.walrus.utils.parsing.facet.parse.configurator.ActiveTime;
import network.walrus.utils.parsing.facet.parse.configurator.FacetConfigurator;

/**
 * Configures the {@link SpawnerParser}.
 *
 * @author Matthew Arnold
 */
public class SpawnerConfigurator implements FacetConfigurator {

  @Override
  public void configure() {
    bindParser(SpawnerParser.class);
    bindFacetListener(SpawnerListener.class, SpawnerFacet.class, ActiveTime.ENABLED);
  }
}

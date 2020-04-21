package network.walrus.games.uhc.facets.delay;

import network.walrus.games.core.round.GameRound;
import network.walrus.utils.parsing.facet.parse.configurator.FacetConfigurator;

/**
 * Configures the {@link DelayedActionsFacet}.
 *
 * @author Austin Mayes
 */
public class DelayConfigurator implements FacetConfigurator {

  @Override
  public void configure() {
    bindFacetDirect(DelayedActionsFacet.class, (h) -> h instanceof GameRound);
  }
}

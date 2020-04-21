package network.walrus.games.core.facets.broadcasts;

import network.walrus.utils.parsing.facet.parse.configurator.FacetConfigurator;

/**
 * Binds the {@link BroadcastsParser}.
 *
 * @author Rafi Baum
 */
public class BroadcastsConfigurator implements FacetConfigurator {

  @Override
  public void configure() {
    bindParser(BroadcastsParser.class);
  }
}

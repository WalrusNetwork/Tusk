package network.walrus.games.core.facets.block36;

import network.walrus.utils.parsing.facet.parse.configurator.FacetConfigurator;

/**
 * Binds a {@link Block36Facet} directly to a FacetHolder. This facet should always be loaded if you
 * want to get rid of block 36 within a map.
 *
 * @author Rafi Baum
 */
public class Block36Configurator implements FacetConfigurator {

  @Override
  public void configure() {
    bindFacetDirect(Block36Facet.class);
  }
}

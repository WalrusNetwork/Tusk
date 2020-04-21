package network.walrus.welcomemat.facets.spawn;

import network.walrus.utils.parsing.facet.parse.configurator.FacetConfigurator;

/**
 * Configurator for {@link SpawnFacet}.
 *
 * @author Rafi Baum
 */
public class SpawnConfigurator implements FacetConfigurator {

  @Override
  public void configure() {
    bindFacetDirect(SpawnFacet.class);
  }
}

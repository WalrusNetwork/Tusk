package network.walrus.games.core.facets.modifyprojectile;

import network.walrus.utils.parsing.facet.parse.configurator.FacetConfigurator;

/**
 * Configurator for {@link ModifyProjectileFacet}.
 *
 * @author Rafi Baum
 */
public class ModifyProjectileConfigurator implements FacetConfigurator {

  @Override
  public void configure() {
    bindParser(ModifyProjectileParser.class);
  }
}

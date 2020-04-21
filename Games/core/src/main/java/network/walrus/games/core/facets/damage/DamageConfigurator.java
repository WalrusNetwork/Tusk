package network.walrus.games.core.facets.damage;

import network.walrus.utils.parsing.facet.parse.configurator.FacetConfigurator;

/**
 * Configures the {@link DamageParser}.
 *
 * @author ShinyDialga
 */
public class DamageConfigurator implements FacetConfigurator {

  @Override
  public void configure() {
    bindParser(DamageParser.class);
  }
}

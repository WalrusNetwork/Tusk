package network.walrus.games.core.facets.kits;

import network.walrus.utils.parsing.facet.parse.configurator.FacetConfigurator;

/**
 * Configures the {@link KitsParser}.
 *
 * @author Austin Mayes
 */
public class KitsConfigurator implements FacetConfigurator {

  @Override
  public void configure() {
    bindParser(KitsParser.class);
  }
}

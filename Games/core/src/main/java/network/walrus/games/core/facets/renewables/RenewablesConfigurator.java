package network.walrus.games.core.facets.renewables;

import network.walrus.utils.parsing.facet.parse.configurator.FacetConfigurator;

/**
 * Configures the {@link RenewablesParser}.
 *
 * @author Austin Mayes
 */
public class RenewablesConfigurator implements FacetConfigurator {

  @Override
  public void configure() {
    bindParser(RenewablesParser.class);
  }
}

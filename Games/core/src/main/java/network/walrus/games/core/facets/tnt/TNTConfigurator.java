package network.walrus.games.core.facets.tnt;

import network.walrus.utils.parsing.facet.parse.configurator.FacetConfigurator;

/**
 * Configure the {@link TNTParser}.
 *
 * @author Austin Mayes
 */
public class TNTConfigurator implements FacetConfigurator {

  @Override
  public void configure() {
    bindParser(TNTParser.class);
  }
}

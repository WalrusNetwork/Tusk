package network.walrus.games.core.facets.items;

import network.walrus.utils.parsing.facet.parse.configurator.FacetConfigurator;

/**
 * Configures the {@link ItemsParser}.
 *
 * @author Austin Mayes
 */
public class ItemsConfigurator implements FacetConfigurator {

  @Override
  public void configure() {
    bindParser(ItemsParser.class);
  }
}

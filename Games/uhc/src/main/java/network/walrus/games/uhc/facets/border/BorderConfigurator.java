package network.walrus.games.uhc.facets.border;

import network.walrus.games.uhc.UHCRound;
import network.walrus.utils.parsing.facet.parse.configurator.FacetConfigurator;

/**
 * Configures the {@link BorderFacet}.
 *
 * @author ShinyDialga
 */
public class BorderConfigurator implements FacetConfigurator {

  @Override
  public void configure() {
    bindFacetDirect(BorderFacet.class, (h) -> h instanceof UHCRound);
    bindFacetCommands(BorderCommands.class, BorderFacet.class);
  }
}

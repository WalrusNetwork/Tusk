package network.walrus.games.uhc.facets.xray;

import network.walrus.games.uhc.UHCRound;
import network.walrus.utils.parsing.facet.parse.configurator.FacetConfigurator;

/**
 * Configures the {@link XRayNotificationFacet}.
 *
 * @author Austin Mayes
 */
public class XRayConfigurator implements FacetConfigurator {

  @Override
  public void configure() {
    bindFacetDirect(XRayNotificationFacet.class, (h) -> h instanceof UHCRound);
  }
}

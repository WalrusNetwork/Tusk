package network.walrus.games.uhc.facets.healthindicator;

import network.walrus.games.uhc.UHCRound;
import network.walrus.ubiquitous.bukkit.settings.PlayerSettings;
import network.walrus.utils.parsing.facet.parse.configurator.FacetConfigurator;

/**
 * Configures the {@link HealthIndicatorFacet}.
 *
 * @author Austin Mayes
 */
public class HealthIndicatorConfigurator implements FacetConfigurator {

  @Override
  public void configure() {
    bindFacetDirect(HealthIndicatorFacet.class, (h) -> h instanceof UHCRound);
    PlayerSettings.register(HealthIndicatorFacet.SHOW_HEALTH_SETTING);
  }
}

package network.walrus.games.uhc.facets.combatlog;

import network.walrus.games.uhc.UHCRound;
import network.walrus.utils.parsing.facet.parse.configurator.FacetConfigurator;

/**
 * Configures the {@link CombatLogTracker}.
 *
 * @author Austin Mayes
 */
public class CombatLogConfigurator implements FacetConfigurator {

  @Override
  public void configure() {
    bindFacetDirect(CombatLogTracker.class, (h) -> h instanceof UHCRound);
  }
}

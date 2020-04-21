package network.walrus.games.core.facets.death;

import network.walrus.games.core.round.GameRound;
import network.walrus.ubiquitous.bukkit.settings.PlayerSettings;
import network.walrus.utils.parsing.facet.parse.configurator.FacetConfigurator;

/**
 * Configures all death handling code.
 *
 * @author Austin Mayes
 */
public class DeathsConfigurator implements FacetConfigurator {

  @Override
  public void configure() {
    bindParser(DeathsParser.class, (h) -> h instanceof GameRound);
    PlayerSettings.register(DeathMessage.SETTING);
  }
}

package network.walrus.games.octc.global.spawns;

import network.walrus.games.core.round.GameRound;
import network.walrus.utils.parsing.facet.parse.configurator.FacetConfigurator;

/**
 * Configures aspects of the core group system.
 *
 * @author Austin Mayes
 */
public class SpawnConfigurator implements FacetConfigurator {

  @Override
  public void configure() {
    bindParser(SpawnsParser.class, (h) -> h instanceof GameRound);
    bindFacetListener(SpawnListener.class, OCNSpawnManager.class);
  }
}

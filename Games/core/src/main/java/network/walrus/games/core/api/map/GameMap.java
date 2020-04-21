package network.walrus.games.core.api.map;

import network.walrus.games.core.GamesPlugin;
import network.walrus.games.core.api.game.Game;
import network.walrus.games.core.map.MapInfo;
import network.walrus.games.core.round.GameRound;
import network.walrus.utils.core.config.Node;
import network.walrus.utils.core.stage.Stage;
import network.walrus.utils.parsing.world.config.FacetConfigurationSource;

/**
 * A map of a single {@link Game} which can be used to create multiple {@link GameRound}s.
 *
 * <p>At the point where this is used, all that is known is the game which this map is for, the
 * parent {@link Node} of the configuration document, and some general map information like name and
 * version.
 *
 * @author Austin Mayes
 */
public interface GameMap extends FacetConfigurationSource {

  /** @return simple information about this map */
  MapInfo mapInfo();

  /**
   * Set the {@link Game} which this map was designed for.
   *
   * @param game which this map is for
   */
  void game(Game game);

  /**
   * @return the {@link Game} which this map is designed for. This will never be null after the
   *     initial parsing stage.
   */
  Game game();

  default Stage environment() {
    return GamesPlugin.getStage();
  }
}

package network.walrus.games.octc;

import java.util.logging.Logger;
import network.walrus.games.core.GamesPlugin;
import network.walrus.games.core.api.map.GameMap;
import network.walrus.games.core.round.GameRound;

/**
 * A round of an {@link OCNGame}.
 *
 * @author Austin Mayes
 */
public class Match extends GameRound {

  /**
   * Constructor.
   *
   * @param map which this match is for
   */
  Match(GameMap map, Logger logger) {
    super(map);
  }

  @Override
  public void end() {
    super.end();
    if (GamesPlugin.instance.getConfig().getBoolean("shutdown-after-end", true)) {
      return;
    }

    OCNGameManager.instance.selectNextMap();
  }
}

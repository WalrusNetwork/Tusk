package network.walrus.games.core.api.round;

import network.walrus.games.core.api.map.GameMap;
import network.walrus.games.core.round.GameRound;

/**
 * Factory used to generate {@link GameRound}s using a {@link GameMap}.
 *
 * @param <R> type of round being created
 * @author Austin Mayes
 */
public interface RoundFactory<R extends GameRound> {

  /**
   * Construct a round using a {@link GameMap}.
   *
   * @param map that the round is for
   * @return a created round
   */
  R create(GameMap map);
}

package network.walrus.games.core.api.game;

import network.walrus.games.core.api.map.GameMap;
import network.walrus.games.core.round.GameRound;
import network.walrus.utils.parsing.facet.Facet;
import network.walrus.utils.parsing.facet.parse.GlobalParser;

/**
 * Represents a game which users can join and interact with. Each {@link GameMap} defines one game,
 * which is determined by the {@link GlobalParser} using {@link GameParser}s. This object is used to
 * gather data independent of a certain {@link GameRound}, and should be queried first before any
 * data is retrieved directly from a round. THis object also contains server-wide values which get
 * applied to the entire server while a round of this game is in progress. Only one of these should
 * be created for each game type, and not for each round.
 *
 * @param <R> type of round that this game generates
 * @author Austin Mayes
 */
public interface Game<R extends GameRound> {

  /**
   * Pre-check run before players are allowed to even join the server to determine if users are
   * allowed to join while a round of this game is in progress. This should always return the same
   * value, and is only a global pre-join check. If this is not true, users (who aren't staff) won't
   * be able to join this server while a round is running. If this is true, it is not, and should
   * not be assumed that, users can actually play in the round. Pre-join checks can also be ran by
   * the round itself.
   *
   * @param premium if the request is being issued by a premium user
   */
  boolean canJoinMidMatch(boolean premium);

  /**
   * Determine if users are allowed to spectate rounds of this game while they are running. If this
   * is false, non-staff users will not be able to join this server while the round is in progress.
   */
  boolean canSpectateMidMatch();

  /**
   * Unique slug of this game type, for internal use. This is used for backend services such as
   * storage and messaging, and should not change.
   */
  String slug();

  /**
   * The name of the game, used for UI. This is intentionally not localized since all game names
   * should be uniform across locales.
   */
  String name();

  /**
   * Create a single {@link GameRound round} instance of this game type. This is used after initial
   * parsing to construct the round which {@link Facet}s are registered inside of.
   *
   * @param map that the round is for
   */
  R constructRound(GameMap map);
}

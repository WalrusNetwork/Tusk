package network.walrus.games.core;

import network.walrus.games.core.facets.group.Group;

/**
 * A constants class containing all of the permissions players can have that allow them to perform
 * functions in the games core plugin environment. Each external component should have their own
 * versions of this class to help make permissions references consistent throughout the game
 * architecture.
 *
 * <p>Permissions should do *one* and *only one* thing per node, and no "group" permission nodes
 * (such as {@code walrus.staff}) should be used. This is so we can fine-tune permissions later at
 * any time, without having to refactor a lot of code.
 *
 * @author Austin Mayes
 */
public class GamesCorePermissions {

  /** Permission needed to start the round. */
  public static final String START_ROUND = "walrus.game.start";
  /** Permission needed to list all regions a player is currently in. */
  public static final String LIST_REGIONS = "walrus.game.dev.regions";
  /** Permission needed to log filter interactions. */
  public static final String LOG_FILTERS = "walrus.game.dev.logfilters";
  /** Permission needed to play all sounds. */
  public static final String PLAY_ALL_SOUNDS = "walrus.game.dev.pas";
  /**
   * The permission players need in order to join full (but under overfill) {@link Group}s in a
   * round. All this allows the player to do is join a group when the size is above the max players,
   * below the max overfill, and will not create an imbalance (if that is enabled).
   */
  public static String JOIN_FULL = "walrus.game.join.full";
  /**
   * Permission which allows users to use the {@code @all} query term to select all of a certain
   * object when running search and execution queries.
   */
  public static String QUERY_ALL = "walrus.game.query.all";
  /** The permissions which allows users to receive chat alerts about map configuration errors. */
  static String VIEW_ERRORS_MAPS = "walrus.game.errors.maps";
}

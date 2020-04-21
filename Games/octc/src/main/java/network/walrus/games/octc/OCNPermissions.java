package network.walrus.games.octc;

import network.walrus.games.core.facets.group.Group;

/**
 * A constants class containing all of the permissions players can have that allow them to perform
 * functions in the OCN component
 *
 * <p>Permissions should do *one* and *only one* thing per node, and no "group" permission nodes
 * (such as {@code walrus.staff}) should be used. This is so we can fine-tune permissions later at
 * any time, without having to refactor a lot of code.
 *
 * @author Austin Mayes
 */
public class OCNPermissions {

  /**
   * Permission which allows players to pick their own team using the join command or via the menu.
   */
  public static final String JOIN_PICK = "walrus.game.join.pick";
  /** Permission needed for players to join past the {@link Group#getMaxOverfill()} limit. */
  public static final String JOIN_OVERFILL = "walrus.games.join.overfill";
  /** Permission needed for players to force another player onto a team */
  public static final String TEAM_FORCE = "walrus.game.team.force";
  /** Permission needed for players to set the max size of a team */
  public static final String TEAM_SIZE = "walrus.game.team.size";
  /** Permission needed for players to set the alias of a team */
  public static final String TEAM_ALIAS = "walrus.game.team.alias";

  /**
   * The permission players need in order to join full (but under overfill) {@link Group}s in a
   * round. All this allows the player to do is join a group when the size is above the max players,
   * below the max overfill, and will not create an imbalance (if that is enabled).
   */
  public static String JOIN_FULL = "walrus.game.join.full";
}

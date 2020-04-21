package network.walrus.games.core.facets.group;

import java.util.Collection;
import network.walrus.games.core.GamesCorePermissions;
import network.walrus.games.core.facets.group.color.GroupColor;
import network.walrus.games.core.facets.group.spectate.Spectators;
import network.walrus.utils.bukkit.distance.PlayerStore;
import network.walrus.utils.bukkit.translation.LocalizedConfigurationProperty;
import network.walrus.utils.core.registry.Identifiable;
import org.bukkit.entity.Player;

/**
 * A collection of players who share common attributes such as interaction rules, overhead/armor
 * colors, and general abilities.
 *
 * <p>A player is *never* not in a group, and at the same time is never in more than one group at
 * once. All code hereafter should be written that assumes this fact, and exceptions should be
 * thrown whenever these conditions are not met. In the join event chain, one of the first things
 * that should happen is that a player should be added to the {@link Spectators} group, or added to
 * a properly chosen group based on the current game state. In games where players are in the same
 * groups the entire round, players should be added to their respective groups during the lobby
 * phase, to avoid player confusion.
 *
 * @author Avicus Network
 */
public interface Group extends Identifiable<Group>, PlayerStore {

  /**
   * The *current* name of the group. This is updated whenever the name of a group changes (to
   * support dynamic aliasing) and is what should be used for any places where the UI references a
   * group by name.
   */
  LocalizedConfigurationProperty getName();

  /**
   * Set the name of the group, updating any UIs and notifying players of a name change. A copy of
   * the original name should always be stored separately, to allow for legacy compatibility with
   * things that may use the original name.
   *
   * @param name to change the group name to
   */
  void setName(LocalizedConfigurationProperty name);

  /**
   * The original name of the group, as defined in the configuration file. This is here to provide
   * support for instances that use (or allow support for) the original name, such as a join
   * command, or a database table used for statistics tracking.
   */
  LocalizedConfigurationProperty getOriginalName();

  /**
   * The color of the group.
   *
   * <p>This should be used anywhere the group is referenced in the UI, including overhead names and
   * in the tab list. If players spawn with leather armor, this is the color that the armor should
   * be, and thus allows for uniqueness in armor selection.
   */
  GroupColor getColor();

  /**
   * The color associated with the player.
   *
   * <p>In many cases this will match the color of the group, but there are some cases where a
   * player's color would not match that of the group, like in free for all groups.
   *
   * @param player
   * @return the color of the player
   */
  default GroupColor getColor(Player player) {
    return getColor();
  }

  /**
   * Add a player to this group.
   *
   * <p>Callers need to ensure that the player is removed from their previous group *before* they
   * are added to this one, and that a UI update is called right after this call is finished
   * processing.
   *
   * <p>Callers should check {@link #isFull(boolean)} before normally adding a player to the group,
   * to ensure that overfill rules are being followed. This call assumes that all necessary checks
   * have been performed, and will simply add them to the group.
   *
   * @param player to add
   */
  void add(Player player);

  /**
   * Remove a player from this group.
   *
   * @param player to remove
   */
  void remove(Player player);

  /**
   * Check if the group is currently in an observation state. In general terms, an observing team
   * should not be able to interact with the game environment at all, and should not be seen by
   * playing players.
   */
  boolean isObserving();

  /**
   * Set the group's observation state.
   *
   * @param observing if the group is observing
   */
  void setObserving(boolean observing);

  /**
   * Check if members of the group should be able to damage each other.
   *
   * <p>It is up to implementations to define how deeply this should affect game mechanics, from
   * simply disallowing damage to adding the extra protection of not applying harmful potions and
   * returning projectiles shot at fellow members.
   */
  boolean isFriendlyFireEnabled();

  /**
   * Check if this group's member count is equal to, or exceeding, the number of players allowed
   * inside of it. Max overfill is generally used to allow premium users to use reserved slots in
   * the group, and should be false for normal users.
   *
   * @param withOverfill to take into account the max overfill rate
   */
  boolean isFull(boolean withOverfill);

  /**
   * Helper method to determine if the group is full in the context of the supplied player.
   *
   * @param player to check permissions of
   * @return if the group is full for the player
   */
  default boolean isFull(Player player) {
    return isFull(player.hasPermission(GamesCorePermissions.JOIN_FULL));
  }

  /** All of the {@link GroupMember}s that are inside of the group. */
  Collection<? extends GroupMember> getMembers();

  /**
   * Check if a player is currently a member of this group.
   *
   * @param player to check
   */
  boolean isMember(Player player);

  /** Helper method to check if this group is the default spectating group, */
  default boolean isSpectator() {
    return this instanceof Spectators;
  }

  /** The current amount of members in the group. */
  int size();

  /**
   * The minimum number of players that this group requires in order for a round to start. This can
   * be ignored in cases where group counts may be dynamic.
   */
  int getMinPlayers();

  /**
   * The maximum number of allowed members that this group officially supports. Keep in mind that,
   * internally, groups have no conceivable member limit. This is purely for show and to aid in
   * automated balancing algorithms.
   */
  int getMaxPlayers();

  /**
   * Set the number of max players (and overfill) that should be allowed in this group.
   *
   * <p>Keep in mind that maximum players is calculated using max + overfill.
   *
   * @param max number of normal users allowed
   * @param overfill number of slots to reserve for special users
   */
  void setMaxPlayers(int max, int overfill);

  /** {@link #getMaxPlayers()} with additional overfill slots added on. */
  int getMaxOverfill();

  /** Helper method to get the portion of the group that is filled. */
  default double filledPortion() {
    if (getMaxPlayers() <= 0) {
      return 1;
    }
    return (double) size() / (double) getMaxPlayers();
  }
}

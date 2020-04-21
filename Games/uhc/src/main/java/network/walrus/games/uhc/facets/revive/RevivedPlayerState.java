package network.walrus.games.uhc.facets.revive;

import network.walrus.games.core.facets.group.Group;
import network.walrus.ubiquitous.bukkit.tracker.tag.PlayerState;

/**
 * Wrapper class to track player state and UHC-specific attributes.
 *
 * @author Rafi Baum
 */
public class RevivedPlayerState {

  private final PlayerState state;
  private final Group group;

  /**
   * Constructs a player state for the purposes of reviving them and restoring their group.
   *
   * @param state of the player
   * @param group prior to death
   */
  public RevivedPlayerState(PlayerState state, Group group) {
    this.state = state;
    this.group = group;
  }

  /** @return player's state */
  public PlayerState getState() {
    return state;
  }

  /** @return player's group prior to death */
  public Group getGroup() {
    return group;
  }
}

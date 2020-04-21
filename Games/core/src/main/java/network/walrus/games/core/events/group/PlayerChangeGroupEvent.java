package network.walrus.games.core.events.group;

import java.util.Optional;
import network.walrus.games.core.facets.group.Group;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * An event that is fired when a player attempts to change to a {@link Group}.
 *
 * @author Avicus Network
 */
public class PlayerChangeGroupEvent extends PlayerEvent implements Cancellable {

  /** Event handlers. */
  private static final HandlerList handlers = new HandlerList();
  /** If the event was canceled. */
  boolean cancelled;
  /** Group the player is changing from. */
  private Optional<Group> groupFrom;
  /** Group the player is transitioning to. */
  private Group group;
  /** If the player should be (re)spawned in this group's spawn location. (Also gives kit) */
  private boolean spawnTriggered;
  /** If the player should be teleported to this group's spawn location. */
  private boolean teleportTriggered;

  /**
   * Constructor.
   *
   * @param player player that is changing groups
   * @param groupFrom group the player is changing from
   * @param group group the player is transitioning to
   * @param triggerSpawn if the player should be (re)spawned in this group's spawn location
   * @param triggerTeleport if the player should be teleported to this group's spawn location
   */
  public PlayerChangeGroupEvent(
      Player player,
      Optional<Group> groupFrom,
      Group group,
      boolean triggerSpawn,
      boolean triggerTeleport) {
    super(player);
    this.groupFrom = groupFrom;
    this.group = group;
    this.spawnTriggered = triggerSpawn;
    this.teleportTriggered = triggerTeleport;
  }

  /**
   * Get the handlers of the event.
   *
   * @return the handlers of the event
   */
  public static HandlerList getHandlerList() {
    return handlers;
  }

  /**
   * Get the handlers of the event.
   *
   * @return the handlers of the event
   */
  @Override
  public HandlerList getHandlers() {
    return handlers;
  }

  @Override
  public boolean isCancelled() {
    return cancelled;
  }

  @Override
  public void setCancelled(boolean cancelled) {
    this.cancelled = cancelled;
  }

  public Optional<Group> getGroupFrom() {
    return groupFrom;
  }

  public void setGroupFrom(Optional<Group> groupFrom) {
    this.groupFrom = groupFrom;
  }

  public Group getGroup() {
    return group;
  }

  public void setGroup(Group group) {
    this.group = group;
  }

  public boolean isSpawnTriggered() {
    return spawnTriggered;
  }

  public void setSpawnTriggered(boolean spawnTriggered) {
    this.spawnTriggered = spawnTriggered;
  }

  public boolean isTeleportTriggered() {
    return teleportTriggered;
  }

  /**
   * Set if the event should trigger a player teleport.
   *
   * <p>
   *
   * <p>NOTE: A spawn must be triggered in order to trigger teleporation.
   *
   * @param triggerTeleport if the event should trigger a player teleport
   */
  public void setTeleportTriggered(boolean triggerTeleport) {
    if (!spawnTriggered && triggerTeleport) {
      throw new RuntimeException("cannot trigger teleport when spawn is not triggered");
    }
    this.teleportTriggered = triggerTeleport;
  }
}

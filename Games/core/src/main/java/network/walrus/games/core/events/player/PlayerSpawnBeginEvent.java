package network.walrus.games.core.events.player;

import network.walrus.games.core.api.spawns.Spawn;
import network.walrus.games.core.facets.group.Group;
import network.walrus.games.core.round.GameRound;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * An event that is fired when a player spawns into a {@link GameRound}. Event is called before they
 * are teleported or given a kit.
 *
 * @author Avicus Network
 */
public class PlayerSpawnBeginEvent extends PlayerEvent {

  /** Event handlers. */
  private static final HandlerList handlers = new HandlerList();
  /** Group the player is a member of. */
  private final Group group;
  /** Spawn location the player is placed. */
  private final Spawn spawn;
  /** If a kit should be applied to the player. */
  private final boolean giveKit;
  /** If the player should be teleported. */
  private final boolean teleportPlayer;

  /**
   * Constructor.
   *
   * @param player player that is spawning
   * @param group group the player is a member of
   * @param spawn spawn location the player is placed
   * @param giveKit if a kit should be applied to the player
   * @param teleportPlayer if the player should be teleported
   */
  public PlayerSpawnBeginEvent(
      Player player, Group group, Spawn spawn, boolean giveKit, boolean teleportPlayer) {
    super(player);
    this.group = group;
    this.spawn = spawn;
    this.giveKit = giveKit;
    this.teleportPlayer = teleportPlayer;
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

  public Group getGroup() {
    return group;
  }

  public Spawn getSpawn() {
    return spawn;
  }

  public boolean isGiveKit() {
    return giveKit;
  }

  public boolean isTeleportPlayer() {
    return teleportPlayer;
  }
}

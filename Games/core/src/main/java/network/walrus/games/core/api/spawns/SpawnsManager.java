package network.walrus.games.core.api.spawns;

import co.aikar.timings.Timing;
import co.aikar.timings.Timings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import network.walrus.games.core.GamesPlugin;
import network.walrus.games.core.events.player.PlayerSpawnBeginEvent;
import network.walrus.games.core.events.player.PlayerSpawnCompleteEvent;
import network.walrus.games.core.facets.group.Group;
import network.walrus.games.core.facets.group.GroupsManager;
import network.walrus.games.core.round.GameRound;
import network.walrus.games.core.util.EventUtil;
import network.walrus.ubiquitous.bukkit.UbiquitousBukkitPlugin;
import network.walrus.utils.bukkit.PlayerUtils;
import network.walrus.utils.bukkit.sound.NetworkSoundConstants.Games.Spawns;
import network.walrus.utils.parsing.facet.Facet;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import org.bukkit.entity.Player;

/**
 * Core management class used to send players to the correct spawn locations.
 *
 * @param <S> type of spawns this manager manager
 * @author Austin Mayes
 */
public abstract class SpawnsManager<S extends Spawn> extends Facet {

  protected final GameRound holder;
  protected final List<S> spawns;
  private final Map<Player, Boolean> deadPlayers;
  private final List<Player> spawningPlayers;
  private final Logger mapLogger;
  private final Timing spawnTimer = Timings.of(GamesPlugin.instance, "Spawn");
  private final Timing selectionTimer =
      Timings.of(GamesPlugin.instance, "Spawn location selection", spawnTimer);

  /**
   * Constructor.
   *
   * @param holder which this manager is for
   * @param spawns which can be selected from
   * @param mapLogger to log spawn errors to
   */
  public SpawnsManager(GameRound holder, List<S> spawns, Logger mapLogger) {
    this.holder = holder;
    this.spawns = spawns;
    this.deadPlayers = Maps.newHashMap();
    this.spawningPlayers = Lists.newArrayList();
    this.mapLogger = mapLogger;
  }

  /**
   * Spawn a player in the would and fire wrapping spawn events.
   *
   * <p>The spawn chain works as follows: - Select the desired spawn, using {@link #getSpawn(Group,
   * Player)} - Reset the player - Call a {@link PlayerSpawnBeginEvent} - If the event indicates
   * teleportation, teleport the player using {@link Spawn#selectLocation(FacetHolder, Player)}. If
   * a spawn location cannot be selected, either due to improper configuration or none of the spawn
   * rules being met, the player will remain where they are. If this happens, an error will be sent
   * to the {@link #mapLogger}. - Call a {@link PlayerSpawnCompleteEvent}.
   *
   * @param group to get the spawn for
   * @param player to use for spawn filtering, and to actually spawn
   * @param giveKit if the player should receive the kit attached to the specific spawn
   * @param teleportPlayer if the player should be teleported to the selected spawn location
   */
  public void spawn(Group group, Player player, boolean giveKit, boolean teleportPlayer) {
    try (Timing time = spawnTimer.startClosable()) {
      this.spawningPlayers.add(player);
      S spawn = getSpawn(group, player);

      if (giveKit) {
        PlayerUtils.reset(player);
      }

      PlayerSpawnBeginEvent call =
          new PlayerSpawnBeginEvent(player, group, spawn, giveKit, teleportPlayer);
      EventUtil.call(call);

      UbiquitousBukkitPlugin.getInstance()
          .getTrackerSupervisor()
          .getLifetimeManager()
          .newLifetime(player);
      if (call.isTeleportPlayer()) {
        try (Timing selectTime = selectionTimer.startClosable()) {
          player.teleport(spawn.selectLocation(this.holder, player));
        } catch (SpawnLocationUnavailableException e) {
          mapLogger.warning("Failed to generate a safe spawn location for " + player.getName());
        }
      }

      if (call.isGiveKit() && spawn.kit().isPresent()) {
        spawn.kit().get().apply(player);
      }

      PlayerSpawnCompleteEvent completeEvent = new PlayerSpawnCompleteEvent(call);
      EventUtil.call(completeEvent);

      Spawns.SELF.play(player);

      this.spawningPlayers.remove(player);
    }
  }

  /**
   * Spawn a player using their current group, and give them a spawn kit.
   *
   * @see #spawn(Group, Player, boolean, boolean)
   */
  public void spawn(Player player) {
    spawn(player, true);
  }

  /**
   * Spawn a player using their current group.
   *
   * @see #spawn(Group, Player, boolean, boolean)
   */
  public void spawn(Player player, boolean kit) {
    Group group = holder.getFacetRequired(GroupsManager.class).getGroup(player);
    spawn(group, player, kit, true);
  }

  /**
   * Set a player's current alive status.
   *
   * @param player to set dead status for
   * @param dead if the player is dead
   */
  public void setDead(Player player, boolean dead) {
    if (dead) {
      this.deadPlayers.put(player, true);
    } else {
      this.deadPlayers.remove(player);
    }
  }

  /**
   * Get the spawn which the specified player should spawn at, given the player's current (or
   * upcoming) group.
   *
   * @param group to be used for spawn filtering
   * @param player to get the specific spawn for, if more than one spawn is defined for the group
   * @return the spawn which the player should spawn at
   */
  public abstract S getSpawn(Group group, Player player);

  /**
   * Determine if the player is currently dead.
   *
   * @param player to check status for
   * @return if the player is currently dead
   */
  public boolean isDead(Player player) {
    return this.deadPlayers.containsKey(player);
  }

  /**
   * Determine if the player is currently in a spawning state. This state is extremely short, and
   * only lasts between the calls of the {@link PlayerSpawnBeginEvent} and {@link
   * PlayerSpawnCompleteEvent}. Players in this state may be at any point of the spawn chain, and
   * thus their state could be quite unstable.
   *
   * @param player to check status for
   * @return if the player is currently in a spawning state
   */
  public boolean isSpawning(Player player) {
    return this.spawningPlayers.contains(player);
  }
}

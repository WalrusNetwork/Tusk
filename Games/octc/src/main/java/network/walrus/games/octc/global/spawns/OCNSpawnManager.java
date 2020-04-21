package network.walrus.games.octc.global.spawns;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import network.walrus.games.core.api.spawns.SpawnsManager;
import network.walrus.games.core.facets.filters.Filter;
import network.walrus.games.core.facets.filters.FilterResult;
import network.walrus.games.core.facets.filters.context.FilterContext;
import network.walrus.games.core.facets.filters.variable.GroupVariable;
import network.walrus.games.core.facets.filters.variable.PlayerVariable;
import network.walrus.games.core.facets.group.Group;
import network.walrus.games.core.facets.group.GroupsManager;
import network.walrus.games.core.round.GameRound;
import network.walrus.games.core.util.GameTask;
import network.walrus.games.octc.Match;
import network.walrus.utils.parsing.facet.FacetLoadException;
import org.bukkit.entity.Player;

/**
 * Main class responsible for managing all {@link OCNSpawn}s for a {@link Match}.
 *
 * @author Austin Mayes
 */
public class OCNSpawnManager extends SpawnsManager<OCNSpawn> {

  private final Map<Player, RespawnTask> respawnTasks;
  private final RespawnOptions respawnOptions;

  /**
   * Constructor.
   *
   * @param holder which this manager is inside of
   * @param spawns which can be selected from in order to provide locations
   * @param respawnOptions options used to configure how respawn mechanics should handled
   */
  public OCNSpawnManager(
      GameRound holder, List<OCNSpawn> spawns, Logger mapLogger, RespawnOptions respawnOptions) {
    super(holder, spawns, mapLogger);
    this.respawnTasks = Maps.newHashMap();
    this.respawnOptions = respawnOptions;
  }

  @Override
  public void load() throws FacetLoadException {
    super.load();
    List<String> notFound = Lists.newArrayList();
    for (Group group : holder.getFacetRequired(GroupsManager.class).getGroups()) {
      boolean hasValidGroup = false;
      for (OCNSpawn s : spawns) {
        if (s.getGroup().id().equals(group.id())) {
          hasValidGroup = true;
          break;
        }
      }
      if (!hasValidGroup) {
        notFound.add(group.id());
      }
    }

    if (!notFound.isEmpty()) {
      throw new FacetLoadException(
          getClass(), "No spawns defined for: " + Joiner.on(",").join(notFound));
    }
  }

  @Override
  public void unload() {
    for (RespawnTask respawnTask : this.respawnTasks.values()) {
      respawnTask.reset();
    }
  }

  @Override
  public OCNSpawn getSpawn(Group group, Player player) {
    for (OCNSpawn spawn : this.spawns) {
      if (!spawn.getGroup().id().equals(group.id())) {
        continue;
      }

      if (spawn.getCheck().isPresent()) {
        Filter filter = spawn.getCheck().get();
        FilterContext context =
            FilterContext.of(new GroupVariable(group), new PlayerVariable(player));
        FilterResult result = filter.test(context);
        if (result.fails()) {
          continue;
        }
      }

      return spawn;
    }

    throw new RuntimeException("Spawn not found for group.");
  }

  /**
   * Start a respawn task for a specific player using the configured {@link #respawnOptions}.
   *
   * @param player to start the task for
   */
  public void startRespawnTask(Player player) {
    if (respawnTasks.containsKey(player)) {
      throw new IllegalStateException("Player is already respawning!");
    }

    Instant when = Instant.now().plus(this.respawnOptions.respawnTime);
    RespawnTask task =
        new RespawnTask((Match) super.holder, this, player, when, this.respawnOptions);
    this.respawnTasks.put(player, task.start());
  }

  /**
   * Stop a currently running respawn task for a given player, and mark them as alive
   *
   * @param player to stop the task for
   */
  public void stopRespawnTask(Player player) {
    stopRespawnTask(player, true);
  }

  /**
   * Stop a currently running respawn task for a given player, optionally setting them to alive
   *
   * @param player to stop the task for
   * @param unDead if the player should now be considered alive
   */
  public void stopRespawnTask(Player player, boolean unDead) {
    RespawnTask task = this.respawnTasks.remove(player);
    if (task != null) {
      task.reset();
    }
    if (unDead) {

      GameTask.of(
              "delayed-visibility",
              () -> {
                setDead(player, false);
                super.holder.getFacetRequired(GroupsManager.class).refreshObserver(player);
              })
          .later(1);
    }
  }

  /**
   * Determine if a player is currently respawning.
   *
   * @param player to check
   * @return if the player is respawning
   */
  public boolean isRespawning(Player player) {
    return this.respawnTasks.containsKey(player);
  }

  /**
   * If the given player is currenly inside of a respawn task, force the player to automatically
   * respawn when the task completes.
   *
   * @param player to queue a respawn for
   */
  public void queueAutoRespawn(Player player) {
    RespawnTask task = this.respawnTasks.get(player);

    if (task == null) {
      return;
    }

    task.setAutoRespawn(true);
  }
}

package network.walrus.games.core.facets.spawners;

import co.aikar.timings.Timing;
import co.aikar.timings.Timings;
import java.util.Set;
import network.walrus.games.core.GamesPlugin;
import network.walrus.games.core.events.group.PlayerChangedGroupEvent;
import network.walrus.games.core.facets.group.GroupsManager;
import network.walrus.ubiquitous.bukkit.tracker.event.player.PlayerCoarseMoveEvent;
import network.walrus.ubiquitous.bukkit.tracker.event.player.PlayerDeathEvent;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import network.walrus.utils.parsing.facet.parse.configurator.listener.FacetListener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * The facet listener for spawners, detects when a player has entered and left a spawner's player
 * region
 *
 * @author Matthew Arnold
 */
public class SpawnerListener extends FacetListener<SpawnerFacet> {

  private final Timing spawnerIngress = Timings.of(GamesPlugin.instance, "Spawner move check");

  private final Set<Spawner> spawners;
  private final GroupsManager groupsManager;

  /**
   * Creates a new spawner listener
   *
   * @param holder the facet holder
   * @param spawnerFacet the spawner facet itself
   */
  public SpawnerListener(FacetHolder holder, SpawnerFacet spawnerFacet) {
    super(holder);
    this.spawners = spawnerFacet.spawners();
    this.groupsManager = holder.getFacetRequired(GroupsManager.class);
  }

  /** Checks a player to see when they move onto the spawner */
  @EventHandler
  public void onMove(PlayerCoarseMoveEvent event) {
    if (groupsManager.isObservingOrDead(event.getPlayer())) {
      return;
    }

    Player player = event.getPlayer();
    try (Timing timing = spawnerIngress.startClosable()) {
      for (Spawner spawner : this.spawners) {
        if (spawner.contains(event.getTo())) {
          spawner.addPlayer(player);
        } else {
          spawner.removePlayer(player);
        }
      }
    }
  }

  /** Remove players when they leave. */
  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent event) {
    for (Spawner spawner : this.spawners) {
      spawner.removePlayer(event.getPlayer());
    }
  }

  /** Remove players when they leave. */
  @EventHandler
  public void onChangeTeam(PlayerChangedGroupEvent event) {
    if (!event.getGroupFrom().isPresent() || event.getGroupFrom().get().isSpectator()) {
      return;
    }

    for (Spawner spawner : this.spawners) {
      spawner.removePlayer(event.getPlayer());
    }
  }

  /** Remove players when they die. */
  @EventHandler
  public void onPlayerDeath(PlayerDeathEvent event) {
    for (Spawner spawner : this.spawners) {
      spawner.removePlayer(event.getPlayer());
    }
  }
}

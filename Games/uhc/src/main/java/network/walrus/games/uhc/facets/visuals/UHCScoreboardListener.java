package network.walrus.games.uhc.facets.visuals;

import com.google.api.client.util.Sets;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import network.walrus.games.core.events.competitor.PlayerChangeCompetitorEvent;
import network.walrus.games.core.events.round.RoundStateChangeEvent;
import network.walrus.games.core.facets.group.Competitor;
import network.walrus.games.core.facets.group.GroupsManager;
import network.walrus.games.core.facets.visual.SidebarFacet;
import network.walrus.games.core.round.states.RoundState;
import network.walrus.games.uhc.UHCManager;
import network.walrus.games.uhc.facets.groups.UHCGroupsManager;
import network.walrus.ubiquitous.bukkit.tracker.event.player.PlayerDeathByPlayerEvent;
import network.walrus.ubiquitous.bukkit.tracker.event.player.PlayerDeathEvent;
import network.walrus.ubiquitous.bukkit.tracker.event.tag.TaggedPlayerDeathByPlayerEvent;
import network.walrus.ubiquitous.bukkit.tracker.event.tag.TaggedPlayerDeathEvent;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import network.walrus.utils.parsing.facet.parse.configurator.listener.FacetListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class UHCScoreboardListener extends FacetListener<SidebarFacet> {

  private static final Set<UUID> playersPlayed = Sets.newHashSet();

  private final FacetHolder holder;
  private final SidebarFacet sidebar;
  private boolean alternateEnabled;

  public UHCScoreboardListener(FacetHolder holder, SidebarFacet facet) {
    super(holder, facet);
    this.holder = holder;
    playersPlayed.clear();
    this.sidebar = facet;
    alternateEnabled = false;
  }

  public static int getTotalPlayers() {
    return playersPlayed.size();
  }

  @EventHandler
  public void onRoundStateChange(RoundStateChangeEvent event) {
    UHCGroupsManager groupsManager = holder.getFacetRequired(UHCGroupsManager.class);
    if (!event.getFrom().orElse(RoundState.IDLE).starting()
        && event.getTo().orElse(RoundState.IDLE).starting()) {
      for (Player player : groupsManager.playingPlayers()) {
        UUID uniqueId = player.getUniqueId();
        playersPlayed.add(uniqueId);
      }
    }

    if (!event.getFrom().orElse(RoundState.IDLE).started()
        && event.getTo().orElse(RoundState.IDLE).started()) {

      sidebar.recreateSpectatorPane();
      for (Player player : groupsManager.getSpectators().getPlayers()) {
        sidebar.refreshSpectatorPane(player);
      }

      for (Competitor competitor : groupsManager.getCompetitors()) {
        sidebar.recreatePane(competitor);
        for (Player player : competitor.getPlayers()) {
          sidebar.refreshPane(player, competitor);
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onPlayerByPlayerDeath(PlayerDeathByPlayerEvent event) {
    handleKill(event.getCause());
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void taggedPlayerbyPlayerDeath(TaggedPlayerDeathByPlayerEvent event) {
    handleKill(event.getCause());
  }

  private void handleKill(Player cause) {
    Optional<Competitor> maybeCompetitor =
        holder.getFacetRequired(GroupsManager.class).getCompetitorOf(cause);
    if (maybeCompetitor.isPresent()) {
      for (Player p : maybeCompetitor.get().getPlayers()) {
        sidebar.displayManager.update(p, "kills");
      }
    } else {
      sidebar.displayManager.update(cause, "kills");
    }

    sidebar.displayManager.update("top-kills");
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onPlayerDeath(PlayerDeathEvent event) {
    handlePlayerChange();
    enableTopKillsBoard();
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onTaggedPlayerDeath(TaggedPlayerDeathEvent event) {
    handlePlayerChange();
    enableTopKillsBoard();
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onCompetitorChange(PlayerChangeCompetitorEvent event) {
    handlePlayerChange();
    if (!event.getCompetitorFrom().isPresent() && event.getCompetitorTo().isPresent()) {
      RoundState state = UHCManager.instance.getUHC().getState();
      if (state.starting() || state.started()) {
        playersPlayed.add(event.getPlayer().getUniqueId());
      }

      if (alternateEnabled) {
        sidebar.getCurrent(event.getPlayer()).setAlternateDefault(true);
        sidebar
            .getCurrent(event.getPlayer())
            .refreshAlternating(event.getPlayer(), sidebar.displayManager);
      }
    }
  }

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
    sidebar.displayManager.update("top-kills");
  }

  @EventHandler
  public void playerQuitEvent(PlayerQuitEvent event) {
    sidebar.displayManager.update("top-kills");
  }

  private void handlePlayerChange() {
    sidebar.displayManager.update("players");
  }

  private void enableTopKillsBoard() {
    alternateEnabled = true;
    for (Player player : Bukkit.getOnlinePlayers()) {
      sidebar.getCurrent(player).setAlternateDefault(true);
      sidebar.getCurrent(player).refreshAlternating(player, sidebar.displayManager);
    }
  }
}

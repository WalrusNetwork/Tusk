package network.walrus.games.core.facets.group;

import java.util.Optional;
import network.walrus.games.core.api.spawns.SpawnsManager;
import network.walrus.games.core.events.group.PlayerObserverStateChangeEvent;
import network.walrus.games.core.events.round.RoundCloseEvent;
import network.walrus.games.core.events.round.RoundOpenEvent;
import network.walrus.games.core.events.round.RoundStateChangeEvent;
import network.walrus.games.core.facets.group.spectate.Spectators;
import network.walrus.games.core.util.EventUtil;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import network.walrus.utils.parsing.facet.parse.configurator.listener.FacetListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Listener responsible for providing entry and exit points from the Bukkit event system to the
 * {@link GroupsManager}. This handles the full lifecycle of server sessions in relation to the
 * manager, and is vital to it's operation.
 *
 * @author Austin Mayes
 */
@SuppressWarnings("JavaDoc")
public class GroupsListener extends FacetListener<GroupsManager> {

  private final SpawnsManager spawnsManager;

  public GroupsListener(FacetHolder holder, GroupsManager manager) {
    super(holder, manager);
    this.spawnsManager = holder.getFacetRequired(SpawnsManager.class);
  }

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onPlayerJoin(PlayerJoinEvent event) {
    this.getFacet()
        .changeGroup(
            event.getPlayer(), Optional.empty(), this.getFacet().getSpectators(), true, true);
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onRoundOpen(RoundOpenEvent event) {
    for (Player player : Bukkit.getOnlinePlayers()) {
      this.getFacet()
          .changeGroup(player, Optional.empty(), this.getFacet().getSpectators(), true, true);
    }
    getFacet().setLoaded(true);
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onRoundClose(RoundCloseEvent event) {
    for (Player player : Bukkit.getOnlinePlayers()) {
      Group group = this.getFacet().getGroup(player);
      this.getFacet().removeMember(group, player);
    }
    getFacet().setLoaded(false);
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPlayerQuit(PlayerQuitEvent event) {
    Group group = this.getFacet().getGroup(event.getPlayer());
    this.getFacet().removeMember(group, event.getPlayer());
  }

  @EventHandler
  public void onRoundStateChange(RoundStateChangeEvent event) {
    if (!event.getTo().isPresent()) {
      return;
    }

    boolean observing = !event.getTo().get().playing();
    for (Group team : this.getFacet().getGroups()) {
      if (!team.isSpectator()) {
        team.setObserving(observing);
      }
    }

    for (Group team : this.getFacet().getGroups()) {
      if (!team.isSpectator()) {
        for (Player player : team.getPlayers()) {
          EventUtil.call(new PlayerObserverStateChangeEvent(player, observing));
        }
      }
    }

    Spectators spectators = this.getFacet().getSpectators();

    if (event.isChangeToPlaying()) {
      for (Group group : this.getFacet().getGroups()) {
        if (group.isSpectator()) {
          continue;
        }

        for (Player player : group.getPlayers()) {
          spawnsManager.spawn(group, player, true, true);
        }
      }
    } else if (event.isChangeToNotPlaying()) {
      for (Group group : this.getFacet().getGroups()) {
        if (group.isSpectator()) {
          continue;
        }

        for (Player player : group.getPlayers()) {
          spawnsManager.spawn(spectators, player, true, false);
        }
      }
    }
  }
}

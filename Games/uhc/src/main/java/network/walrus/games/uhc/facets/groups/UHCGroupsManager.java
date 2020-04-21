package network.walrus.games.uhc.facets.groups;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import network.walrus.games.core.events.group.PlayerObserverStateChangeEvent;
import network.walrus.games.core.events.round.RoundStateChangeEvent;
import network.walrus.games.core.facets.group.Group;
import network.walrus.games.core.facets.group.GroupsManager;
import network.walrus.games.core.facets.group.spectate.Spectators;
import network.walrus.games.core.util.EventUtil;
import network.walrus.games.core.util.GameTask;
import network.walrus.games.uhc.facets.combatlog.CombatLogTracker;
import network.walrus.games.uhc.facets.scatter.ScatterCountdown;
import network.walrus.ubiquitous.bukkit.UbiquitousBukkitPlugin;
import network.walrus.ubiquitous.bukkit.tracker.event.player.PlayerDeathEvent;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Manager for groups inside of UHCs
 *
 * @author Austin Mayes
 */
public abstract class UHCGroupsManager extends GroupsManager implements Listener {

  private final AfterlifeTeam afterlife;
  private final Spectators spectators;
  private final List<Group> groups;
  private Optional<CombatLogTracker> logTracker = Optional.empty();

  /** @param holder which this manager is operating inside of */
  public UHCGroupsManager(FacetHolder holder) {
    super(holder);
    this.afterlife = new AfterlifeTeam();
    this.spectators = new Spectators();
    this.groups = Lists.newArrayList();
    this.groups.add(0, spectators); // add it first
    this.groups.add(afterlife);
  }

  @Override
  public void load() {
    super.load();
    logTracker = getHolder().getFacet(CombatLogTracker.class);
  }

  @Override
  public double getMaxGroupImbalance() {
    return Integer.MAX_VALUE;
  }

  @Override
  public boolean isDead(Player player) {
    return false;
  }

  @Override
  public boolean isSpawning(Player player) {
    return UbiquitousBukkitPlugin.getInstance()
        .getCountdownManager()
        .isRunning(ScatterCountdown.class);
  }

  @Override
  public Collection<Group> getGroups() {
    return this.groups;
  }

  @Override
  public Group getGroup(Player player) {
    if (logTracker.isPresent()) {
      Optional<Group> group = logTracker.get().getGroup(player.getUniqueId());
      if (group.isPresent()) {
        return group.get();
      }
    }

    return super.getGroup(player);
  }

  /**
   * Add a player to the default playing group.
   *
   * @param player to add to the game
   */
  public abstract void addPlayer(Player player);

  /** Move players to spectators on death */
  @EventHandler
  public void onDeath(PlayerDeathEvent event) {
    if (!getGroup(event.getPlayer()).isObserving()) {
      GameTask.of("Death group change", () -> changeGroup(event.getPlayer(), afterlife, true, true))
          .later(2);
    }
  }

  /** Remove players from groups on death */
  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPlayerQuit(PlayerQuitEvent event) {
    Group group = getGroup(event.getPlayer());
    removeMember(group, event.getPlayer());
  }

  /** Update observing status based on round state. */
  @EventHandler
  public void onRoundStateChange(RoundStateChangeEvent event) {
    if (!event.getTo().isPresent()) {
      return;
    }

    for (Group team : getGroups()) {
      boolean observing = !event.getTo().get().playing();
      team.setObserving(observing);
      for (Player player : team.getPlayers()) {
        EventUtil.call(new PlayerObserverStateChangeEvent(player, observing));
      }
    }
  }
}

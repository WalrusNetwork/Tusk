package network.walrus.games.uhc.facets.combatlog;

import com.google.common.collect.Maps;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;
import network.walrus.games.core.events.round.RoundStateChangeEvent;
import network.walrus.games.core.facets.group.Competitor;
import network.walrus.games.core.facets.group.Group;
import network.walrus.games.core.facets.group.GroupMember;
import network.walrus.games.core.util.EventUtil;
import network.walrus.games.core.util.GameTask;
import network.walrus.games.uhc.UHCManager;
import network.walrus.games.uhc.UHCRound;
import network.walrus.games.uhc.facets.endgame.GameEndFacet;
import network.walrus.games.uhc.facets.groups.UHCGroupsManager;
import network.walrus.ubiquitous.bukkit.events.player.PlayerJoinDelayedEvent;
import network.walrus.ubiquitous.bukkit.tracker.tag.CombatLoggerState;
import network.walrus.utils.parsing.facet.Facet;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Tracks {@link CombatLoggerState}s fpr all actively participating players and kills them based on
 * the configured death duration.
 *
 * @author Austin Mayes
 */
public class CombatLogTracker extends Facet implements Listener {

  private final UHCRound holder;
  private final Map<UUID, Competitor> competitors = Maps.newHashMap();
  private final Map<UUID, CombatLoggerState> loggers = Maps.newHashMap();
  private final Map<UUID, Long> logOffTimes = Maps.newHashMap();
  private final GameTask killTask;
  private UHCGroupsManager groupsManager;
  private boolean enabled = false;

  /** @param holder containing this object */
  public CombatLogTracker(FacetHolder holder) {
    this.holder = (UHCRound) holder;
    killTask =
        GameTask.of(
            "Logger kill",
            () -> {
              for (Entry<UUID, Long> entry : new HashMap<>(logOffTimes).entrySet()) {
                UUID u = entry.getKey();
                Long i = entry.getValue();
                if (Instant.ofEpochMilli(i)
                    .plus(UHCManager.instance.getConfig().timeoutDelay.get())
                    .isBefore(Instant.now())) {
                  logOffTimes.remove(u);
                  CombatLoggerState state = loggers.remove(u);
                  EventUtil.call(state.createDeathEvent());
                  state.die();
                  for (GroupMember member : competitors.remove(u).getGroup().getMembers()) {
                    member.getPlayers().removeIf(p -> p.getUniqueId().equals(u));
                  }
                  holder.getFacetRequired(GameEndFacet.class).check();
                }
              }
            });
  }

  @EventHandler
  public void onScatter(RoundStateChangeEvent event) {
    if (!event.getTo().isPresent() || !event.getTo().get().starting()) {
      return;
    }

    this.groupsManager = holder.getFacetRequired(UHCGroupsManager.class);
    enabled = true;
    killTask.repeat(20, 20);
  }

  @Override
  public void disable() {
    enabled = false;
    killTask.reset();
    for (CombatLoggerState combatLoggerState : this.loggers.values()) {
      combatLoggerState.die();
    }
  }

  public void spawn(Competitor competitor, Location location) {
    for (Entry<UUID, Competitor> entry : competitors.entrySet()) {
      if (competitor.equals(entry.getValue())) {
        CombatLoggerState state = loggers.get(entry.getKey());
        state.teleport(location);
      }
    }
  }

  public void thawAll() {
    for (CombatLoggerState state : loggers.values()) {
      state.setFrozen(false);
    }
  }

  /**
   * Provides the group of the player at their time of death.
   *
   * @param uuid of the player
   * @return group of player
   */
  public Optional<Group> getGroup(UUID uuid) {
    if (competitors.containsKey(uuid)) {
      return Optional.of(competitors.get(uuid).getGroup());
    } else {
      return Optional.empty();
    }
  }

  /** Apply saved state on join. */
  @EventHandler(priority = EventPriority.HIGHEST)
  public void onJoin(PlayerJoinDelayedEvent event) {
    if (!enabled) {
      return;
    }

    CombatLoggerState state = loggers.remove(event.getPlayer().getUniqueId());
    if (state == null || state.isDead()) {
      return;
    }

    state.apply(event.getPlayer());
    logOffTimes.remove(event.getPlayer().getUniqueId());
    groupsManager.changeGroup(
        event.getPlayer(),
        competitors.remove(event.getPlayer().getUniqueId()).getGroup(),
        false,
        false);
  }

  /** Save current state on leave. */
  @EventHandler(priority = EventPriority.LOWEST)
  public void onLeave(PlayerQuitEvent event) {
    if (!enabled) {
      return;
    }

    Optional<Competitor> competitor = groupsManager.getCompetitorOf(event.getPlayer());
    if (!competitor.isPresent()) {
      return;
    }

    competitors.put(event.getPlayer().getUniqueId(), competitor.get());
    CombatLoggerState state = new CombatLoggerState(event.getPlayer());
    state.spawn();
    loggers.put(event.getPlayer().getUniqueId(), state);
    logOffTimes.put(event.getPlayer().getUniqueId(), Instant.now().toEpochMilli());
  }
}

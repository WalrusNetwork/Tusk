package network.walrus.games.core.facets.group;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import network.walrus.games.core.events.competitor.PlayerChangeCompetitorEvent;
import network.walrus.games.core.events.group.PlayerChangedGroupEvent;
import network.walrus.games.core.events.round.RoundCloseEvent;
import network.walrus.games.core.events.round.RoundOpenEvent;
import network.walrus.games.core.facets.group.color.GroupColor;
import network.walrus.games.core.util.GameTask;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import network.walrus.utils.parsing.facet.parse.configurator.listener.FacetListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

/**
 * Manager which sets up {@link Scoreboard} teams for each competitor and group.
 *
 * @author Austin Mayes
 */
@SuppressWarnings("ALL")
public class ScoreboardHandler extends FacetListener<GroupsManager> {

  public static boolean SHOW_HEALTH = false;
  private final GameTask healthUpdater;
  private List<Group> spectators;
  private ArrayListMultimap<Scoreboard, Competitor> competitors;

  /**
   * @param holder which this object is inside of
   * @param facet which this object is bound to
   */
  public ScoreboardHandler(FacetHolder holder, GroupsManager facet) {
    super(holder, facet);
    healthUpdater =
        GameTask.of(
            "Health update",
            () -> {
              Map<String, Double> health = Maps.newHashMap();
              for (Player player : Bukkit.getOnlinePlayers()) {
                health.put(player.getName(), player.getHealth());
              }
              for (Player player : Bukkit.getOnlinePlayers()) {
                Scoreboard scoreboard = player.getScoreboard();
                for (Entry<String, Double> entry : health.entrySet()) {
                  String p = entry.getKey();
                  Double h = entry.getValue();
                  scoreboard.getObjective("healthTab").getScore(p).setScore(h.intValue());
                  scoreboard.getObjective("healthName").getScore(p).setScore(h.intValue());
                }
              }
            });
  }

  public void onClose(RoundCloseEvent event) {
    healthUpdater.reset();
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onMatchOpen(RoundOpenEvent event) {
    this.spectators =
        event.getHolder().getFacetRequired(GroupsManager.class).getGroups().stream()
            .filter(Group::isSpectator)
            .collect(Collectors.toList());
    this.competitors = ArrayListMultimap.create();
    for (Player player : Bukkit.getOnlinePlayers()) {
      resetScoreboardTeams(player);
    }
    if (SHOW_HEALTH) healthUpdater.repeat(0, 10);
  }

  @EventHandler(priority = EventPriority.LOW)
  public void onPlayerJoin(PlayerJoinEvent event) {
    resetScoreboardTeams(event.getPlayer());
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onPlayerChangeCompetitor(PlayerChangeCompetitorEvent event) {
    if (event.getCompetitorFrom().isPresent()) {
      Competitor competitor = event.getCompetitorFrom().get();
      removeCompetitorPlayer(competitor, event.getPlayer());
    }
    if (event.getCompetitorTo().isPresent()) {
      registerScoreboardCompetitor(event.getCompetitorTo().get());
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onPlayerChangeGroup(PlayerChangedGroupEvent event) {
    if (event.getGroupFrom().isPresent() && this.spectators.contains(event.getGroupFrom().get())) {
      removeSpectatorPlayer(event.getPlayer());
    }

    // Only handle spectators, competitor changes are handled in PlayerChangeCompetitorEvent
    if (this.spectators.contains(event.getGroup())) {
      addSpectatorPlayer(event.getPlayer());
    }
  }

  /**
   * Add the health objective to the specified scoreboard.
   *
   * @param scoreboard to add the objective tp
   */
  public void addHealth(Scoreboard scoreboard) {
    scoreboard.registerNewObjective("healthTab", "dummy");
    scoreboard.registerNewObjective("healthName", "dummy");

    Objective objective = scoreboard.getObjective("healthTab");
    objective.setDisplaySlot(DisplaySlot.PLAYER_LIST);

    objective = scoreboard.getObjective("healthName");
    objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
    objective.setDisplayName(ChatColor.RED + "❤");
  }

  private void removeCompetitorPlayer(Competitor competitor, Player player) {
    for (Player target : Bukkit.getOnlinePlayers()) {
      removeCompetitorPlayer(target.getScoreboard(), competitor, player);
    }
  }

  private void removeCompetitorPlayer(Scoreboard scoreboard, Competitor competitor, Player player) {
    org.bukkit.scoreboard.Team team = scoreboard.getTeam(competitor.id());
    team.removeEntry(player.getName());
  }

  private void removeSpectatorPlayer(Player player) {
    for (Player target : Bukkit.getOnlinePlayers()) {
      removeSpectatorPlayer(target.getScoreboard(), player);
    }
  }

  private void removeSpectatorPlayer(Scoreboard scoreboard, Player player) {
    for (Group group : spectators) {
      org.bukkit.scoreboard.Team team = scoreboard.getTeam(group.id());
      if (team.hasEntry(player.getName())) team.removeEntry(player.getName());
    }
  }

  private void addSpectatorPlayer(Player player) {
    for (Player target : Bukkit.getOnlinePlayers()) {
      try {
        addSpectatorPlayer(target.getScoreboard(), player);
      } catch (IllegalArgumentException e) {
        // TODO - we literally have 2mins to start this please send help
      }
    }
  }

  private void addSpectatorPlayer(Scoreboard scoreboard, Player player) {
    Group group =
        spectators.stream()
            .filter(g -> g.isMember(player))
            .findFirst()
            .orElseThrow(
                () ->
                    new IllegalStateException(
                        player.getName() + " is not on a registerd spectator group"));
    org.bukkit.scoreboard.Team team = scoreboard.getTeam(group.id());
    team.addEntry(player.getName());
  }

  private void registerCompetitors(Scoreboard scoreboard) {
    List<Competitor> competitors = this.competitors.get(scoreboard);

    for (Competitor competitor : getFacet().getCompetitors()) {
      if (competitors.contains(competitor)) {
        continue;
      }

      registerScoreboardCompetitor(scoreboard, competitor);
      competitors.add(competitor);
    }
  }

  public void resetScoreboardTeams(Player player) {
    Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
    player.setScoreboard(scoreboard);

    for (Group spectator : spectators) {
      registerScoreboardTeam(
          scoreboard, spectator.id(), spectator.getColor(), spectator.getPlayers(), false);
    }
    registerCompetitors(scoreboard);
    if (SHOW_HEALTH) {
      addHealth(scoreboard);
    }
  }

  private void registerScoreboardCompetitor(Competitor competitor) {
    for (Player player : Bukkit.getOnlinePlayers()) {
      registerScoreboardCompetitor(player.getScoreboard(), competitor);
    }
  }

  private void registerScoreboardCompetitor(Scoreboard scoreboard, Competitor competitor) {
    registerScoreboardTeam(
        scoreboard,
        competitor.id(),
        competitor.getColor(),
        competitor.getPlayers(),
        competitor.getGroup().isFriendlyFireEnabled());
  }

  private void registerScoreboardTeam(
      Scoreboard scoreboard,
      String id,
      GroupColor color,
      Set<Player> members,
      boolean friendlyFire) {
    org.bukkit.scoreboard.Team bukkit = scoreboard.getTeam(id);
    if (bukkit == null) {
      bukkit = scoreboard.registerNewTeam(id);
      bukkit.setCanSeeFriendlyInvisibles(true);
      bukkit.setSuffix(ChatColor.RESET.toString());
      bukkit.setPrefix(color.getPrefix());
      bukkit.setAllowFriendlyFire(friendlyFire);
    }

    Set<String> entries = bukkit.getEntries();
    Set<String> memberStrings = Sets.newHashSet();
    Set<String> toAdd = Sets.newHashSet();
    Set<String> toRemove = Sets.newHashSet();

    for (Player member : members) {
      String name = member.getName();
      memberStrings.add(name);

      if (!entries.contains(name)) {
        toAdd.add(name);
      }
    }

    for (String entry : entries) {
      if (!memberStrings.contains(entry)) {
        toRemove.add(entry);
      }
    }

    for (String s : toAdd) {
      bukkit.addEntry(s);
    }
    for (String s : toRemove) {
      bukkit.removeEntry(s);
    }
  }
}

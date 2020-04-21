package network.walrus.games.octc.global.results;

import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import network.walrus.games.core.api.results.ResultUtils;
import network.walrus.games.core.api.results.RoundEndCountdown;
import network.walrus.games.core.api.results.scenario.EndScenario;
import network.walrus.games.core.api.results.scenario.TieScenario;
import network.walrus.games.core.events.round.RoundStateChangeEvent;
import network.walrus.games.core.facets.group.Competitor;
import network.walrus.games.core.facets.group.GroupsManager;
import network.walrus.games.core.facets.objectives.GlobalObjective;
import network.walrus.games.core.facets.objectives.Objective;
import network.walrus.games.core.round.GameRound;
import network.walrus.ubiquitous.bukkit.UbiquitousBukkitPlugin;

/**
 * Calculator for all objective-based game types used to intelligently calculate which {@link
 * Competitor}(s) should receive a win.
 *
 * @author Austin Mayes
 */
public class WinCalculator {

  private final GameRound round;
  private final List<Objective> objectives;
  private final List<EndScenario> scenarios;
  private final List<EndScenario> timeBased;

  /**
   * @param round which this class is managing wins for
   * @param objectives which are being checked for completeness
   * @param scenarios to fall back on when winners cannot be easily determined
   */
  public WinCalculator(GameRound round, List<Objective> objectives, List<EndScenario> scenarios) {
    this.round = round;
    this.objectives = objectives;
    this.scenarios = scenarios;
    List<EndScenario> timed = new ArrayList<>();
    for (EndScenario e : scenarios) {
      if (e.getCountdown().isPresent()) {
        timed.add(e);
      }
    }
    this.timeBased = timed;
  }

  /** Reset all time based scenarios. */
  public void updateTimeBased() {
    this.timeBased.clear();
    List<EndScenario> timed = new ArrayList<>();
    for (EndScenario e : scenarios) {
      if (e.getCountdown().isPresent()) {
        timed.add(e);
      }
    }
    this.timeBased.addAll(timed);
  }

  /**
   * The main win calculation check method.
   *
   * <p>Logic works as follows: - Calculate all objectives which have been completed either globally
   * or by a certain {@link Competitor} - If there are no clear winners: - Check each {@link
   * EndScenario} and execute it if the filter attached to it passes - If none pass - If no one has
   * done anything OR the top competitors have the exact same amount of stuff done - Execute a tie -
   * If a clear winner is found, end the round for them
   */
  public void check() {
    if (!this.round.getState().playing()) {
      return;
    }

    List<Competitor> winners = new ArrayList<>();

    GroupsManager groups = this.round.getFacetRequired(GroupsManager.class);

    int completed = 0;
    Set<Objective> needed = Sets.newHashSet();

    List<CompetitorCompletionState> states = new ArrayList<>();

    for (Objective objective : objectives) {
      if (objective instanceof GlobalObjective && ((GlobalObjective) objective).isCompleted()) {
        completed++;
      }
    }

    for (Competitor competitor : groups.getCompetitors()) {
      CompetitorCompletionState completionState =
          new CompetitorCompletionState(this.objectives, competitor);
      states.add(completionState);
      if (completionState.shouldWin()) {
        winners.add(competitor);
      }

      for (Objective objective : objectives) {
        if (!(objective instanceof GlobalObjective)
            && objective.canComplete(competitor)
            && objective.isCompleted(competitor)) {
          completed++;
        }
      }

      needed.addAll(completionState.getNeededObjectives());
    }

    if (winners.isEmpty()) {
      if (!this.scenarios.isEmpty()) {
        for (EndScenario scenario : this.scenarios) {
          if (scenario.test()) {
            scenario.execute();
            return;
          }
        }
        return;
      } else if (completed >= needed.size()) {
        Map.Entry<Integer, List<CompetitorCompletionState>> highest =
            CompetitorCompletionState.getRankedCompletions(states).firstEntry();
        if (highest != null && highest.getKey() > 0) {
          for (CompetitorCompletionState competitor : highest.getValue()) {
            winners.add(competitor.getCompetitor());
          }
        } else {
          new TieScenario(round).execute();
          return;
        }
      } else {
        return;
      }
    }

    this.round.end();

    ResultUtils.broadcastWinners(round, winners);
  }

  /**
   * Notify the calculator that the state changed.
   *
   * @param event that was called by the state change
   */
  public void stateChanged(RoundStateChangeEvent event) {
    if (getEndingCountdown().isPresent()) {
      if (event.isChangeToPlaying()) {
        UbiquitousBukkitPlugin.getInstance()
            .getCountdownManager()
            .start(this.getEndingCountdown().get());
      } else {
        UbiquitousBukkitPlugin.getInstance()
            .getCountdownManager()
            .cancel(this.getEndingCountdown().get());
      }
    }
  }

  /** Cancel the ending countdown. */
  public void disable() {
    if (getEndingCountdown().isPresent()) {
      UbiquitousBukkitPlugin.getInstance()
          .getCountdownManager()
          .cancel(this.getEndingCountdown().get());
    }
  }

  private Optional<RoundEndCountdown> getEndingCountdown() {
    if (!this.scenarios.isEmpty()) {
      for (EndScenario scenario : this.scenarios) {
        if (scenario.getCountdown().isPresent()) {
          return scenario.getCountdown();
        }
      }
    }

    return Optional.empty();
  }
}

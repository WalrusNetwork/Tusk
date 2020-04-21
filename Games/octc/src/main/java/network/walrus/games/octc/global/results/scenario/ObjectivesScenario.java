package network.walrus.games.octc.global.results.scenario;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;
import network.walrus.games.core.api.results.RankingDisplay;
import network.walrus.games.core.api.results.ResultUtils;
import network.walrus.games.core.api.results.scenario.EndScenario;
import network.walrus.games.core.api.results.scenario.TieScenario;
import network.walrus.games.core.facets.filters.Filter;
import network.walrus.games.core.facets.group.Competitor;
import network.walrus.games.core.facets.group.GroupsManager;
import network.walrus.games.core.facets.objectives.IntegerObjective;
import network.walrus.games.core.facets.objectives.Objective;
import network.walrus.games.core.round.GameRound;
import network.walrus.games.octc.global.results.CompetitorCompletionState;

/**
 * A scenario which will reward the {@link Competitor} who has done the most with a win.
 *
 * @author Austin Mayes
 */
public class ObjectivesScenario extends EndScenario {

  private final List<Objective> objectives;
  private final EndScenario alternativeScenario;

  /**
   * @param round that the scenario is executing in
   * @param filter that should be executed to see if this scenario should execute
   * @param places to show winners by
   * @param objectives that this scenario will track
   */
  public ObjectivesScenario(
      GameRound round, Filter filter, int places, List<Objective> objectives) {
    super(round, filter, places);
    this.objectives = objectives;
    this.alternativeScenario = new TieScenario(round);
  }

  /**
   * @param round that the scenario is executing in
   * @param filter that should be executed to see if this scenario should execute
   * @param places to show winners by
   * @param objectives that this scenario will track
   * @param alternativeScenario alternative scenario executed when there is no clear winner
   */
  public ObjectivesScenario(
      GameRound round,
      Filter filter,
      int places,
      List<Objective> objectives,
      EndScenario alternativeScenario) {
    super(round, filter, places);
    this.objectives = objectives;
    this.alternativeScenario = alternativeScenario;
  }

  @Override
  public void execute() {
    TreeMap<Integer, HashSet<Competitor>> scores = new TreeMap<>(Collections.reverseOrder());
    scores.put(0, Sets.newHashSet());

    List<CompetitorCompletionState> states = new ArrayList<>();

    GroupsManager groups = getRound().getFacetRequired(GroupsManager.class);

    for (Competitor competitor : groups.getCompetitors()) {
      states.add(new CompetitorCompletionState(this.objectives, competitor));

      int score = 0;
      scores.get(0).add(competitor);
      // Objectives that this competitor can complete
      List<IntegerObjective> completable = new ArrayList<>();
      for (Objective o : this.objectives) {
        if (o instanceof IntegerObjective) {
          IntegerObjective integerObjective = (IntegerObjective) o;
          if (integerObjective.canComplete(competitor)) {
            completable.add(integerObjective);
          }
        }
      }

      if (completable.isEmpty()) {
        continue;
      }

      for (IntegerObjective objective : completable) {
        score += objective.getPoints(competitor);
      }

      if (score != 0) {
        scores.putIfAbsent(score, Sets.newHashSet());

        scores.get(0).remove(competitor);
        scores.get(score).add(competitor);
      }
    }

    TreeMap<Integer, List<CompetitorCompletionState>> ranked =
        CompetitorCompletionState.getRankedCompletions(states);

    // Empty round
    if (ranked.isEmpty()) {
      new TieScenario(getRound()).execute();
      return;
    }

    // This means no one has done anything.
    if (!ranked.firstEntry().getValue().get(0).hasDoneAnything()
        && scores.firstEntry().getKey() == 0) {
      alternativeScenario.execute();
      return;
    }

    if (scores.firstEntry().getKey() != 0) {
      processWin(scores);
    } else {
      processStatesWin(ranked);
    }
  }

  private void processWin(TreeMap<Integer, HashSet<Competitor>> ranked) {
    if (getPlaces() == 1) {
      if (ranked.firstEntry().getValue().size() > 1) {
        alternativeScenario.execute();
        return;
      } else {
        ResultUtils.handleWin(getRound(), ranked.firstEntry().getValue().iterator().next());
        getRound().end();
        return;
      }
    }

    getRound().end();

    RankingDisplay display = new RankingDisplay(getPlaces(), ranked);
    ResultUtils.handleMultiWin(getRound(), display);
  }

  private void processStatesWin(TreeMap<Integer, List<CompetitorCompletionState>> completions) {
    if (getPlaces() == 1) {
      if (completions.firstEntry().getValue().isEmpty()
          || completions.firstEntry().getValue().size() > 1) {
        alternativeScenario.execute();
        return;
      }
      if (completions.firstEntry().getValue().size() > 1
          && !completions.firstEntry().getValue().get(0).hasDoneAnything()) {
        alternativeScenario.execute();
        return;
      } else {
        ResultUtils.handleWin(
            getRound(), completions.firstEntry().getValue().get(0).getCompetitor());
        getRound().end();
        return;
      }
    }

    getRound().end();

    TreeMap<Integer, HashSet<Competitor>> normalizedRanks = Maps.newTreeMap();

    for (Entry<Integer, List<CompetitorCompletionState>> entry : completions.entrySet()) {
      Integer i = entry.getKey();
      List<CompetitorCompletionState> l = entry.getValue();
      List<Competitor> competitors = new ArrayList<>();
      for (CompetitorCompletionState competitorCompletionState : l) {
        Competitor competitor = competitorCompletionState.getCompetitor();
        competitors.add(competitor);
      }
      normalizedRanks.put(i, Sets.newHashSet(competitors));
    }

    RankingDisplay display = new RankingDisplay(getPlaces(), normalizedRanks);
    ResultUtils.handleMultiWin(getRound(), display);
  }
}

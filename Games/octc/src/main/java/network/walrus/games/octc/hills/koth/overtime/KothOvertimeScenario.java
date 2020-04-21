package network.walrus.games.octc.hills.koth.overtime;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import network.walrus.games.core.api.results.ResultUtils;
import network.walrus.games.core.api.results.scenario.EndScenario;
import network.walrus.games.core.facets.filters.FilterResult;
import network.walrus.games.core.facets.filters.StaticResultFilter;
import network.walrus.games.core.facets.group.Competitor;
import network.walrus.games.core.facets.group.GroupsManager;
import network.walrus.games.core.round.GameRound;
import network.walrus.games.core.util.EventUtil;
import network.walrus.games.octc.hills.HillObjective;
import network.walrus.games.octc.hills.HillUtils;
import network.walrus.games.octc.hills.overtime.OvertimeFacet;
import network.walrus.games.octc.hills.overtime.OvertimeStartEvent;
import network.walrus.games.octc.hills.overtime.OvertimeState;
import network.walrus.games.octc.score.ScoreObjective;

/**
 * The scenario used for the KOTH overtime, this scenario is used to make sure that when a team
 * reaches the score limit they also have control over the majority of the hills on the map.
 *
 * @author Matthew Arnold
 */
public class KothOvertimeScenario extends EndScenario {

  private final List<HillObjective> hills;
  private final OvertimeFacet overtimeFacet;

  private final ScoreObjective scoreObjective;
  private final GroupsManager groupsManager;

  /**
   * Creates the new KOTH hill scenario
   *
   * @param round the game round
   * @param overtimeFacet the overtime facet
   * @param hills the list of hills
   */
  public KothOvertimeScenario(
      GameRound round,
      OvertimeFacet overtimeFacet,
      List<HillObjective> hills,
      ScoreObjective scoreObjective,
      GroupsManager groupsManager) {
    super(round, new StaticResultFilter(FilterResult.ALLOW), 1);
    this.hills = hills;
    this.overtimeFacet = overtimeFacet;
    this.scoreObjective = scoreObjective;
    this.groupsManager = groupsManager;
  }

  @Override
  public void execute() {
    if (overtimeFacet.state() == OvertimeState.NORMAL) {
      boolean scoreCompleted = false;
      for (Competitor competitor1 : groupsManager.getCompetitors()) {
        if (scoreObjective.isCompleted(competitor1)) {
          scoreCompleted = true;
          break;
        }
      }
      if (scoreCompleted) {
        // check if also majority owner, if not start overtime
        Optional<Competitor> competitor = HillUtils.majorityOwner(hills);
        if (competitor.isPresent() && Objects.equals(competitor, scoreObjective.leader())) {
          // both majority owner and scoreLeader, therefore win game
          ResultUtils.handleWin(getRound(), competitor.get());
          scoreObjective.setLimit(scoreObjective.highestScore());
          getRound().end();
        } else {
          // otherwise start overtime
          EventUtil.call(new OvertimeStartEvent());
        }
      }
    } else {
      // in overtime already
      Optional<Competitor> competitor = HillUtils.majorityOwner(hills);
      if (competitor.isPresent() && Objects.equals(competitor, scoreObjective.leader())) {
        ResultUtils.handleWin(getRound(), competitor.get());
        scoreObjective.setLimit(scoreObjective.highestScore());
        getRound().end();
      }
    }
  }
}

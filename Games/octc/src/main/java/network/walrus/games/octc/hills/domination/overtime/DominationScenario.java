package network.walrus.games.octc.hills.domination.overtime;

import com.google.common.base.Objects;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import network.walrus.games.core.api.results.ResultUtils;
import network.walrus.games.core.api.results.scenario.EndScenario;
import network.walrus.games.core.facets.filters.FilterResult;
import network.walrus.games.core.facets.filters.StaticResultFilter;
import network.walrus.games.core.facets.filters.types.TimeFilter;
import network.walrus.games.core.facets.group.Competitor;
import network.walrus.games.core.round.GameRound;
import network.walrus.games.core.util.EventUtil;
import network.walrus.games.octc.global.results.scenario.ObjectivesScenario;
import network.walrus.games.octc.hills.HillObjective;
import network.walrus.games.octc.hills.HillUtils;
import network.walrus.games.octc.hills.overtime.OvertimeStartEvent;
import network.walrus.utils.core.math.NumberComparator;

/**
 * The domination scenario, acts as the default timelimit scenario when overtime is enabled. It
 * checks to see that once the time limit is expired whether a team should win or the game should
 * enter overtime instead
 *
 * @author Matthew Arnold
 */
public class DominationScenario extends EndScenario {

  private final DominationOvertimeFacet domination;
  private final List<HillObjective> hills;

  /**
   * Creates a new domination scenario
   *
   * @param round the game round for this scenario
   * @param domination the domination facet
   * @param duration the duration of the time limit
   * @param hills the hills in this game
   */
  public DominationScenario(
      GameRound round,
      DominationOvertimeFacet domination,
      Duration duration,
      List<HillObjective> hills) {
    super(round, new TimeFilter(round, duration, NumberComparator.EQUALS), 1);
    this.domination = domination;
    this.hills = hills;
  }

  @Override
  public void execute() {
    DomOvertimeState time = domination.dominationTime();
    // now start overtime if: team that has mid doesn't have dom timer
    Optional<Competitor> majOwner = HillUtils.majorityOwner(hills);
    if (!Objects.equal(majOwner, time.owner)) {
      // don't end game yet, start overtime
      EventUtil.call(new OvertimeStartEvent());
    } else if (time.owner.isPresent()) {
      // award the win
      ResultUtils.handleWin(getRound(), time.owner.get());
      getRound().end();
    } else {
      new ObjectivesScenario(
              getRound(), new StaticResultFilter(FilterResult.ALLOW), 1, new ArrayList<>(hills))
          .execute();
    }
  }
}

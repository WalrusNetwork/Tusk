package network.walrus.games.octc.hills.domination.overtime;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import network.walrus.games.core.api.results.ResultUtils;
import network.walrus.games.core.api.results.scenario.EndScenario;
import network.walrus.games.core.facets.filters.FilterResult;
import network.walrus.games.core.facets.filters.types.LambdaFilter;
import network.walrus.games.core.facets.group.Competitor;
import network.walrus.games.core.round.GameRound;
import network.walrus.games.octc.hills.HillObjective;
import network.walrus.games.octc.hills.HillUtils;

/**
 * The dominatino overtime scenario, calculates who should win once the match enters overtime
 *
 * @author Matthew Arnold
 */
public class DominationOvertimeScenario extends EndScenario {

  private final DominationOvertimeFacet domination;
  private final List<HillObjective> hills;

  /**
   * Creates a new domination overtime scenario
   *
   * @param round the gameround
   * @param domination the domination overtime facet
   * @param hills the list of hills
   */
  public DominationOvertimeScenario(
      GameRound round, DominationOvertimeFacet domination, List<HillObjective> hills) {
    super(
        round, new LambdaFilter(filterContext -> FilterResult.valueOf(domination.inOvertime())), 1);
    this.domination = domination;
    this.hills = hills;
  }

  @Override
  public void execute() {
    // end if owning team has majority amount of hills

    DomOvertimeState time = domination.dominationTime();
    Optional<Competitor> majOwner = HillUtils.majorityOwner(hills);
    if (time.owner.isPresent()) {
      if (Objects.equals(majOwner, time.owner)) {
        ResultUtils.handleWin(getRound(), time.owner.get());
        getRound().end();
      }
    } else if (majOwner.isPresent()) {
      ResultUtils.handleWin(getRound(), majOwner.get());
      getRound().end();
    }
  }
}

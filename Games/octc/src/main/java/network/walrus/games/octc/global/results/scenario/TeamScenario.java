package network.walrus.games.octc.global.results.scenario;

import network.walrus.games.core.api.results.ResultUtils;
import network.walrus.games.core.api.results.scenario.EndScenario;
import network.walrus.games.core.facets.filters.Filter;
import network.walrus.games.core.facets.group.Competitor;
import network.walrus.games.core.facets.group.GroupsManager;
import network.walrus.games.core.round.GameRound;
import network.walrus.games.octc.global.groups.teams.Team;

/**
 * A scenario which will always reward the specified {@link Team} the win.
 *
 * @author Austin Mayes
 */
public class TeamScenario extends EndScenario {

  private final Team team;

  /**
   * @param round that the scenario is executing in
   * @param filter that should be executed to see if this scenario should execute
   * @param places to show winners by
   * @param team which should win
   */
  public TeamScenario(GameRound round, Filter filter, int places, Team team) {
    super(round, filter, places);
    this.team = team;
  }

  @Override
  public void execute() {
    getRound().end();

    for (Competitor competitor :
        getRound().getFacetRequired(GroupsManager.class).getCompetitors(this.team)) {
      ResultUtils.handleWin(getRound(), competitor);
    }
  }
}

package network.walrus.games.octc.tdm.overtime;

import java.util.Optional;
import network.walrus.games.core.api.results.ResultUtils;
import network.walrus.games.core.api.results.scenario.EndScenario;
import network.walrus.games.core.facets.filters.FilterResult;
import network.walrus.games.core.facets.filters.types.LambdaFilter;
import network.walrus.games.core.facets.group.Competitor;
import network.walrus.games.core.facets.group.GroupsManager;
import network.walrus.games.core.round.GameRound;
import network.walrus.ubiquitous.bukkit.UbiquitousBukkitPlugin;
import network.walrus.ubiquitous.bukkit.freeze.FreezeManager;
import org.bukkit.entity.Player;

/**
 * Scenario that is executed whenever the match is on overtime and there is only one team remaining
 *
 * @author David Rodriguez
 */
public class BlitzOvertimeScenario extends EndScenario {
  /** @param round that the scenario is executing in */
  public BlitzOvertimeScenario(GameRound round) {
    super(
        round,
        new LambdaFilter(
            filterContext -> {
              int count = 0;
              for (Competitor competitor :
                  round.getFacetRequired(GroupsManager.class).getCompetitors()) {
                if (competitor.getPlayers().size() >= 1) {
                  count++;
                }
              }
              return FilterResult.valueOf(
                  count == 1 && round.getFacetRequired(BlitzOvertimeFacet.class).isActive());
            }),
        1);
  }

  @Override
  public void execute() {
    GroupsManager groupsManager = getRound().getFacetRequired(GroupsManager.class);

    // Unfreeze spectators that were frozen during overtime
    FreezeManager freezeManager = UbiquitousBukkitPlugin.getInstance().getFreezeManager();
    for (Player player : groupsManager.getSpectators().getPlayers()) {
      if (!freezeManager.isFrozen(player)) continue;
      freezeManager.thaw(player);
    }

    Optional<Competitor> found = Optional.empty();
    for (Competitor competitor :
        getRound().getFacetRequired(GroupsManager.class).getCompetitors()) {
      if (competitor.getPlayers().size() >= 1) {
        found = Optional.of(competitor);
        break;
      }
    }
    ResultUtils.handleWin(getRound(), found.get());
    getRound().end();
  }
}

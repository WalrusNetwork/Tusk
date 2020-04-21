package network.walrus.games.uhc.facets.endgame;

import network.walrus.games.core.api.results.ResultUtils;
import network.walrus.games.core.api.results.scenario.EndScenario;
import network.walrus.games.core.facets.filters.FilterResult;
import network.walrus.games.core.facets.filters.StaticResultFilter;
import network.walrus.games.core.facets.group.Competitor;
import network.walrus.games.core.round.GameRound;
import network.walrus.games.core.round.states.RoundState;
import network.walrus.utils.bukkit.sound.NetworkSoundConstants.Games.UHC.EndGame;

/**
 * End scenario used to end all UHC rounds.
 *
 * @author Austin Mayes
 */
public class UHCEndScenario extends EndScenario {

  private final Competitor winner;

  /**
   * @param round that the scenario is executing in
   * @param winner who won the UHC
   */
  public UHCEndScenario(GameRound round, Competitor winner) {
    super(round, new StaticResultFilter(FilterResult.ALLOW), 1);
    this.winner = winner;
  }

  @Override
  public void execute() {
    ResultUtils.handleWin(getRound(), this.winner);
    getRound().setState(RoundState.FINISHED);
    getRound().getContainer().broadcast(EndGame.END);
  }
}

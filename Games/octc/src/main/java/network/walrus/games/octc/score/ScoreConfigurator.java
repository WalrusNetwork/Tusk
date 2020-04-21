package network.walrus.games.octc.score;

import java.util.function.Predicate;
import network.walrus.games.core.round.GameRound;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import network.walrus.utils.parsing.facet.parse.configurator.FacetConfigurator;

/**
 * Configures stuff for Score based gamemodes.
 *
 * @author Matthew Arnold
 */
public class ScoreConfigurator implements FacetConfigurator {

  // predicate for the score configurator, game must implement point based game for this to be
  // active
  private static final Predicate<FacetHolder> PREDICATE =
      (h) -> h instanceof GameRound && ((GameRound) h).map().game() instanceof PointBasedGame;

  @Override
  public void configure() {
    bindParser(ScoreParser.class, PREDICATE);
  }
}

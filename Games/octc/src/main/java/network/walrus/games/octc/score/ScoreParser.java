package network.walrus.games.octc.score;

import java.util.Optional;
import network.walrus.games.core.round.GameRound;
import network.walrus.utils.bukkit.parse.BukkitParserRegistry;
import network.walrus.utils.core.config.Node;
import network.walrus.utils.core.config.ParsingException;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import network.walrus.utils.parsing.facet.parse.FacetParser;

/**
 * Parser for the {@link ScoreFacet}.
 *
 * @author Austin Mayes
 */
public class ScoreParser implements FacetParser<ScoreFacet> {

  @Override
  public boolean required() {
    // Required since only active when the map is set to a score gamemode
    return true;
  }

  @Override
  public Optional<ScoreFacet> parse(FacetHolder holder, Node<?> node) throws ParsingException {
    if (!node.hasChild("score")) {
      return Optional.empty();
    }

    Node<?> scoreNod = node.childRequired("score");

    Optional<Integer> limit =
        BukkitParserRegistry.integerParser().parse(scoreNod.attribute("limit"));
    Optional<Integer> kills =
        BukkitParserRegistry.integerParser().parse(scoreNod.attribute("kills"));
    Optional<Integer> deaths =
        BukkitParserRegistry.integerParser().parse(scoreNod.attribute("deaths"));
    ScoreObjective objective = new ScoreObjective((GameRound) holder, limit, kills, deaths);

    return Optional.of(new ScoreFacet((GameRound) holder, objective));
  }
}

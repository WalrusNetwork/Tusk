package network.walrus.games.octc.hills.koth;

import static network.walrus.utils.core.color.NetworkColorConstants.Games.OCN.CP;

import java.util.List;
import java.util.Optional;
import network.walrus.games.core.round.GameRound;
import network.walrus.games.octc.OCNMessages;
import network.walrus.games.octc.hills.HillObjective;
import network.walrus.games.octc.hills.HillProperties;
import network.walrus.games.octc.hills.HillUtils;
import network.walrus.games.octc.hills.overtime.OvertimeFacet;
import network.walrus.utils.bukkit.parse.BukkitParserRegistry;
import network.walrus.utils.core.config.Node;
import network.walrus.utils.core.config.ParsingException;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import network.walrus.utils.parsing.facet.parse.FacetParser;

/**
 * Parser for the king of the hill facet
 *
 * @author Matthew Arnold
 */
public class KothParser implements FacetParser<KothFacet> {

  // special default koth default values, different to the normal defaults
  public static final boolean KOTH_NEUTRAL_STATE = true;
  public static final double KOTH_TIME_MULTIPLER = 0.1;
  public static final int KOTH_POINTS = 1;
  public static final double KOTH_RECOVERY_RATE = 1;
  public static final double KOTH_DECAY_RATE = 1;
  public static final boolean KOTH_DEFUALT_SEQUENTIAL = false;
  public static final HillProperties.CaptureRule KOTH_DEFAULT_CAPTURE_RULE =
      HillProperties.CaptureRule.MAJORITY;

  // the default options for domination points
  private static HillProperties DEFAULTS =
      new HillProperties(
          KOTH_NEUTRAL_STATE,
          KOTH_TIME_MULTIPLER,
          KOTH_POINTS,
          KOTH_RECOVERY_RATE,
          KOTH_DECAY_RATE,
          KOTH_DEFUALT_SEQUENTIAL,
          KOTH_DEFAULT_CAPTURE_RULE);

  @Override
  public Optional<KothFacet> parse(FacetHolder holder, Node<?> node) throws ParsingException {
    if (!node.hasChild("hills")) {
      return Optional.empty();
    }

    GameRound gameRound = (GameRound) holder;
    List<HillObjective> hills =
        HillUtils.parseHills(gameRound, node.childRequired("hills"), DEFAULTS);

    if (hills.size() == 0) {
      return Optional.empty();
    }

    boolean overtime =
        BukkitParserRegistry.booleanParser()
            .parse(node.childRequired("hills").attribute("overtime"))
            .orElse(false);

    gameRound.addFacet(
        new OvertimeFacet(
            gameRound,
            OCNMessages.KOTH_OVERTIME_BOSSBAR.with(CP.OVERTIME_BROADCAST),
            OCNMessages.KOTH_OVERTIME_BROADCAST.with(CP.OVERTIME_BROADCAST)));

    return Optional.of(new KothFacet(gameRound, hills, overtime));
  }

  @Override
  public boolean required() {
    return true;
  }
}

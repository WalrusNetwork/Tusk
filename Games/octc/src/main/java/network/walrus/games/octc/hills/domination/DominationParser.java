package network.walrus.games.octc.hills.domination;

import static network.walrus.utils.core.color.NetworkColorConstants.Games.OCN.CP;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import network.walrus.games.core.api.results.scenario.EndScenario;
import network.walrus.games.core.facets.filters.FilterResult;
import network.walrus.games.core.facets.filters.StaticResultFilter;
import network.walrus.games.core.facets.filters.types.TimeFilter;
import network.walrus.games.core.facets.objectives.Objective;
import network.walrus.games.core.facets.visual.PaneGroup;
import network.walrus.games.core.round.GameRound;
import network.walrus.games.octc.OCNMessages;
import network.walrus.games.octc.global.results.WinCalculator;
import network.walrus.games.octc.global.results.scenario.ObjectivesScenario;
import network.walrus.games.octc.hills.HillObjective;
import network.walrus.games.octc.hills.HillProperties;
import network.walrus.games.octc.hills.HillUtils;
import network.walrus.games.octc.hills.domination.overtime.DominationOvertimeFacet;
import network.walrus.games.octc.hills.overtime.OvertimeFacet;
import network.walrus.utils.bukkit.parse.BukkitParserRegistry;
import network.walrus.utils.core.config.Node;
import network.walrus.utils.core.config.ParsingException;
import network.walrus.utils.core.math.NumberComparator;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import network.walrus.utils.parsing.facet.parse.FacetParser;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Parser for the domination point facet
 *
 * @author Matthew Arnold
 */
public class DominationParser implements FacetParser<DomFacet> {

  // the default options for domination points
  private static HillProperties DEFAULTS =
      new HillProperties(
          HillProperties.DEFAULT_NEUTRAL_STATE,
          HillProperties.DEFAULT_TIME_MULTIPLIER,
          HillProperties.DEFAULT_POINTS,
          HillProperties.DEFAULT_RECOVERY_RATE,
          HillProperties.DEFAULT_DECAY_RATE,
          HillProperties.DEFAULT_SEQUENTIAL,
          HillProperties.DEFAULT_CAPTURE_RULE);

  @Override
  public Optional<DomFacet> parse(FacetHolder holder, Node<?> node) throws ParsingException {
    GameRound gameRound = (GameRound) holder;
    List<HillObjective> hills =
        HillUtils.parseHills(gameRound, node.childRequired("hills"), DEFAULTS);

    if (hills.size() == 0) {
      return Optional.empty();
    }

    Optional<Duration> duration = HillUtils.parseDuration(node);
    Optional<DominationOvertimeFacet> domOvertime =
        BukkitParserRegistry.booleanParser()
            .parse(node.childRequired("hills").attribute("overtime"))
            .filter(x -> x && duration.isPresent())
            .map(x -> new DominationOvertimeFacet(gameRound, duration.get(), hills));

    domOvertime.ifPresent(
        x -> {
          gameRound.addFacet(x);
          gameRound.addFacet(
              new OvertimeFacet(
                  gameRound,
                  OCNMessages.DOMINATION_OVERTIME_BOSSBAR.with(CP.OVERTIME_BOSSBAR),
                  OCNMessages.DOMINATION_OVERTIME_BROADCAST.with(CP.OVERTIME_BROADCAST)));
        });
    WinCalculator winCalculator = winCalculator(gameRound, domOvertime, hills, duration);
    return Optional.of(
        new DomFacet(
            gameRound,
            hills,
            winCalculator,
            (m, c) ->
                new PaneGroup(Pair.of("hills", new DomPane(hills, domOvertime, gameRound, m)))));
  }

  @Override
  public boolean required() {
    return true;
  }

  /**
   * Creates a new win calculator, depending on whether overtime is enabled or disabled
   *
   * @param gameRound the game round
   * @param domOvertime the domination overtime facet
   * @param hills the hills
   * @param duration the timer duration
   * @return the created win calculator
   */
  private WinCalculator winCalculator(
      GameRound gameRound,
      Optional<DominationOvertimeFacet> domOvertime,
      List<HillObjective> hills,
      Optional<Duration> duration) {
    WinCalculator winCalculator;
    if (domOvertime.isPresent()) {
      winCalculator = domOvertime.get().winCalculator();
    } else {
      List<EndScenario> scenarios;
      // get the hills needed to win the game, (the ones that are required)
      List<Objective> hillsUsed = new ArrayList<>();
      for (HillObjective x : hills) {
        if (x.options().required) {
          hillsUsed.add(x);
        }
      }
      if (duration.isPresent()) {
        scenarios =
            Collections.singletonList(
                new ObjectivesScenario(
                    gameRound,
                    new TimeFilter(gameRound, duration.get(), NumberComparator.EQUALS),
                    1,
                    hillsUsed));
      } else {
        scenarios =
            Collections.singletonList(
                new ObjectivesScenario(
                    gameRound, new StaticResultFilter(FilterResult.DENY), 1, hillsUsed));
      }
      winCalculator = new WinCalculator(gameRound, hillsUsed, scenarios);
    }
    return winCalculator;
  }
}

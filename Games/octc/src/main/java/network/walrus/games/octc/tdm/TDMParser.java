package network.walrus.games.octc.tdm;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import network.walrus.games.core.facets.filters.Filter;
import network.walrus.games.core.facets.filters.FilterResult;
import network.walrus.games.core.facets.filters.StaticResultFilter;
import network.walrus.games.core.round.GameRound;
import network.walrus.games.octc.tdm.overtime.BlitzOvertimeFacet;
import network.walrus.games.octc.tdm.overtime.BlitzOvertimeParser;
import network.walrus.utils.bukkit.parse.BukkitParserRegistry;
import network.walrus.utils.bukkit.region.BoundedRegion;
import network.walrus.utils.core.config.Node;
import network.walrus.utils.core.config.ParsingException;
import network.walrus.utils.core.math.NumberAction;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import network.walrus.utils.parsing.facet.parse.FacetParser;

/**
 * Parses a TDM facet
 *
 * @author Matthew Arnold
 * @author Austin Mayes
 */
public class TDMParser implements FacetParser<TDMFacet> {

  @Override
  public Optional<TDMFacet> parse(FacetHolder holder, Node<?> node) throws ParsingException {
    Optional<Duration> timeLimit = Optional.empty();
    if (node.hasChild("time-limit")) {
      timeLimit =
          BukkitParserRegistry.durationParser().parse(node.childRequired("time-limit").text());
    }

    boolean interactive = false;
    if (node.hasChild("interactive")) {
      interactive =
          BukkitParserRegistry.booleanParser()
              .parseRequired(node.childRequired("interactive").text());
    }

    List<ScoreBox> boxes = new ArrayList<>();
    for (Node<?> scoreNode : node.children("score")) {
      for (Node<?> boxNode : scoreNode.children("box")) {
        ScoreBox scoreBox = parseBox(holder, boxNode);
        boxes.add(scoreBox);
      }
    }

    GameRound gameRound = (GameRound) holder;

    Optional<BlitzOvertimeFacet> overtimeFacet = new BlitzOvertimeParser().parse(gameRound, node);
    overtimeFacet.ifPresent(facet -> gameRound.addFacet(facet));

    return Optional.of(
        new TDMFacet(gameRound, boxes, timeLimit, interactive, overtimeFacet.isPresent()));
  }

  private ScoreBox parseBox(FacetHolder holder, Node<?> box) {
    Integer points = BukkitParserRegistry.integerParser().parseRequired(box.attribute("points"));
    NumberAction pointsAction =
        BukkitParserRegistry.numberActionParser()
            .parse(box.attribute("action"))
            .orElse(NumberAction.ADD);
    BoundedRegion region =
        holder
            .getRegistry()
            .get(BoundedRegion.class, box.attribute("region").asRequiredString(), true)
            .get();
    Filter filter =
        holder
            .getRegistry()
            .get(Filter.class, box.attribute("filter").asRequiredString(), false)
            .orElse(new StaticResultFilter(FilterResult.ALLOW));
    boolean heal = BukkitParserRegistry.booleanParser().parse(box.attribute("heal")).orElse(true);
    return new ScoreBox(points, pointsAction, region, filter, heal);
  }

  @Override
  public boolean required() {
    return true;
  }
}

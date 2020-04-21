package network.walrus.games.core.facets.renewables;

import com.google.common.collect.Lists;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import network.walrus.games.core.facets.filters.Filter;
import network.walrus.games.core.facets.filters.FilterResult;
import network.walrus.games.core.facets.filters.StaticResultFilter;
import network.walrus.games.core.round.GameRound;
import network.walrus.utils.bukkit.parse.BukkitParserRegistry;
import network.walrus.utils.bukkit.region.BoundedRegion;
import network.walrus.utils.core.config.Node;
import network.walrus.utils.core.config.ParsingException;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import network.walrus.utils.parsing.facet.parse.FacetParser;

/**
 * Parser which parses {@link Renewable}s from map configuration documents.
 *
 * @author Austin Mayes
 */
public class RenewablesParser implements FacetParser<RenewablesFacet> {

  @Override
  public Optional<RenewablesFacet> parse(FacetHolder holder, Node<?> node) throws ParsingException {
    if (!node.hasChild("renewables")) {
      return Optional.empty();
    }

    List<Renewable> renewables = Lists.newArrayList();

    for (Node<?> parent : node.children("renewables")) {
      for (Node<?> child : parent.children()) {
        BoundedRegion region =
            holder
                .getRegistry()
                .get(BoundedRegion.class, child.attribute("region").asRequiredString(), true)
                .get();
        Filter renewableBlocks = new StaticResultFilter(FilterResult.ALLOW);
        Filter replaceableBlocks = new StaticResultFilter(FilterResult.ALLOW);
        if (child.hasAttribute("renew-filter")) {
          renewableBlocks =
              holder
                  .getRegistry()
                  .get(Filter.class, child.attribute("renew-filter").asRequiredString(), true)
                  .get();
        }
        if (child.hasAttribute("replace-filter")) {
          replaceableBlocks =
              holder
                  .getRegistry()
                  .get(Filter.class, child.attribute("replace-filter").asRequiredString(), true)
                  .get();
        }

        if (child.hasAttribute("rate") && child.hasAttribute("interval")) {
          throw new ParsingException(child, "Rate and interval cannot be combined.");
        }

        boolean scaled = child.hasAttribute("interval");
        float renewalsPerSecond = 1;

        if (child.hasAttribute("rate")) {
          renewalsPerSecond =
              BukkitParserRegistry.floatParser().parseRequired(child.attribute("rate"));
        } else if (child.hasAttribute("interval")) {
          Duration interval =
              BukkitParserRegistry.durationParser().parseRequired(child.attribute("interval"));
          renewalsPerSecond = 1000f / interval.toMillis();
        }

        boolean grow =
            BukkitParserRegistry.booleanParser().parse(child.attribute("grow")).orElse(true);
        boolean natural =
            BukkitParserRegistry.booleanParser().parse(child.attribute("natural")).orElse(true);
        double avoidPlayersRange =
            BukkitParserRegistry.doubleParser().parse(node.attribute("avoid-players")).orElse(2d);

        RenewableOptions options =
            new RenewableOptions(
                region,
                renewableBlocks,
                replaceableBlocks,
                renewalsPerSecond,
                scaled,
                grow,
                natural,
                avoidPlayersRange);

        renewables.add(new Renewable((GameRound) holder, options));
      }
    }

    return Optional.of(new RenewablesFacet(holder, renewables));
  }
}

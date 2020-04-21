package network.walrus.games.core.facets.broadcasts;

import com.google.common.collect.Lists;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import network.walrus.games.core.facets.filters.Filter;
import network.walrus.utils.bukkit.parse.BukkitParserRegistry;
import network.walrus.utils.bukkit.translation.LocalizedConfigurationProperty;
import network.walrus.utils.core.config.GenericStringHolder;
import network.walrus.utils.core.config.Node;
import network.walrus.utils.core.config.ParsingException;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import network.walrus.utils.parsing.facet.parse.FacetParser;

/**
 * Parser which creates a {@link BroadcastsFacet}.
 *
 * @author Rafi Baum
 */
public class BroadcastsParser implements FacetParser<BroadcastsFacet> {

  @Override
  public Optional<BroadcastsFacet> parse(FacetHolder holder, Node<?> node) throws ParsingException {
    if (!node.hasChild("broadcasts")) {
      return Optional.empty();
    }

    List<Broadcast> broadcasts = Lists.newArrayList();
    for (Node<?> parent : node.children("broadcasts")) {
      for (Node child : parent.children()) {
        broadcasts.add(buildBroadcast(holder, child));
      }
    }

    return Optional.of(new BroadcastsFacet(holder, broadcasts));
  }

  private Broadcast buildBroadcast(FacetHolder holder, Node node) {
    Type type =
        BukkitParserRegistry.ofEnum(Type.class)
            .parseRequired(new GenericStringHolder(node.name(), node));
    LocalizedConfigurationProperty message =
        BukkitParserRegistry.localizedPropertyParser().parseRequired(node.text());

    int after =
        (int)
            (BukkitParserRegistry.durationParser()
                    .parseRequired(node.attribute("after"))
                    .getSeconds()
                * 20);

    Optional<Duration> everyDur =
        BukkitParserRegistry.durationParser().parse(node.attribute("every"));
    Optional<Integer> every = Optional.empty();
    if (everyDur.isPresent()) {
      every = Optional.of((int) everyDur.get().getSeconds() * 20);
    }

    Optional<Integer> count = BukkitParserRegistry.integerParser().parse(node.attribute("count"));

    Optional<Filter> filter = Optional.empty();
    if (node.hasAttribute("filter")) {
      filter =
          holder
              .getRegistry()
              .get(Filter.class, node.attribute("filter").asRequiredString(), false);
    }

    return new Broadcast(holder, type, message, after, every, count, filter);
  }
}

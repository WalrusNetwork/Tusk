package network.walrus.games.core.facets.applicators;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import network.walrus.games.core.GamesCoreMessages;
import network.walrus.games.core.facets.filters.Filter;
import network.walrus.games.core.facets.filters.FilterResult;
import network.walrus.games.core.facets.filters.StaticResultFilter;
import network.walrus.games.core.facets.kits.Kit;
import network.walrus.utils.bukkit.parse.BukkitParserRegistry;
import network.walrus.utils.bukkit.region.Region;
import network.walrus.utils.bukkit.region.special.AboveRegion;
import network.walrus.utils.bukkit.region.special.EverywhereRegion;
import network.walrus.utils.bukkit.translation.LocalizedConfigurationProperty;
import network.walrus.utils.core.config.Node;
import network.walrus.utils.core.config.ParsingException;
import network.walrus.utils.core.text.LocalizedNumber;
import network.walrus.utils.parsing.facet.facets.region.RegionsFacetParser;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import network.walrus.utils.parsing.facet.parse.DocumentParser;
import network.walrus.utils.parsing.facet.parse.FacetParser;
import org.bukkit.util.Vector;

/**
 * Parser which parses {@link Applicator}s.
 *
 * @author Austin Mayes
 */
public class ApplicatorsParser implements FacetParser<ApplicatorsFacet> {

  // defaults to everywhere region
  private static final Region DEFAULT_REGION = new EverywhereRegion();

  @Override
  public Optional<ApplicatorsFacet> parse(FacetHolder holder, Node node) throws ParsingException {
    List<Node<?>> children = node.children("applicators");

    if (children.isEmpty()) {
      return Optional.empty();
    }

    List<Applicator> parsed = Lists.newArrayList();

    if (node.hasChild("max-build-height")) {
      int height =
          BukkitParserRegistry.integerParser()
              .parseRequired(node.childRequired("max-build-height").text());
      Region region = new AboveRegion(Optional.empty(), Optional.of(height), Optional.empty());
      parsed.add(
          new Applicator(
              region,
              Optional.empty(),
              Optional.empty(),
              Optional.of(new StaticResultFilter(FilterResult.DENY)),
              Optional.of(new StaticResultFilter(FilterResult.DENY)),
              Optional.empty(),
              Optional.of(
                  new LocalizedConfigurationProperty(
                      GamesCoreMessages.ERROR_MAX_BUILD_HEIGHT, new LocalizedNumber(height))),
              Optional.empty(),
              Optional.empty()));
    }

    for (Node<?> c : children) {
      for (Node<?> applicatorNode : c.children()) {
        applicatorNode.inheritAttributes("applicators");
        parsed.add(parseApplicator(applicatorNode, holder));
      }
    }

    return Optional.of(new ApplicatorsFacet(parsed));
  }

  private Applicator parseApplicator(Node appNode, FacetHolder holder) {
    Region region =
        DocumentParser.getParser(RegionsFacetParser.class)
            .resolveRegionAs(
                Region.class, holder.getRegistry(), appNode.attribute("region"), Optional.empty())
            .orElse(DEFAULT_REGION);
    Optional<Filter> enter =
        appNode
            .attribute("enter")
            .value()
            .flatMap(n -> holder.getRegistry().get(Filter.class, n, true));
    Optional<Filter> leave =
        appNode
            .attribute("leave")
            .value()
            .flatMap(n -> holder.getRegistry().get(Filter.class, n, true));
    Optional<Filter> block =
        appNode
            .attribute("block")
            .value()
            .flatMap(n -> holder.getRegistry().get(Filter.class, n, true));
    Optional<Filter> blockPlace =
        appNode
            .attribute("block-place")
            .value()
            .flatMap(n -> holder.getRegistry().get(Filter.class, n, true));
    Optional<Filter> blockBreak =
        appNode
            .attribute("block-break")
            .value()
            .flatMap(n -> holder.getRegistry().get(Filter.class, n, true));

    if (block.isPresent()) {
      blockPlace = block;
      blockBreak = block;
    }

    Optional<Filter> use =
        appNode
            .attribute("use")
            .value()
            .flatMap(n -> holder.getRegistry().get(Filter.class, n, true));
    Optional<LocalizedConfigurationProperty> message =
        BukkitParserRegistry.localizedPropertyParser().parse(appNode.attribute("message"));
    Optional<Vector> velocity =
        BukkitParserRegistry.vectorParser().parse(appNode.attribute("velocity"));
    Optional<Kit> kit =
        appNode.attribute("kit").value().flatMap(n -> holder.getRegistry().get(Kit.class, n, true));

    return new Applicator(
        region, enter, leave, blockPlace, blockBreak, use, message, velocity, kit);
  }
}

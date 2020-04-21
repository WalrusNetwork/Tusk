package network.walrus.games.core.facets.portals;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import network.walrus.games.core.GamesCoreMessages;
import network.walrus.games.core.facets.applicators.Applicator;
import network.walrus.games.core.facets.applicators.ApplicatorsFacet;
import network.walrus.games.core.facets.applicators.ApplicatorsParser;
import network.walrus.games.core.facets.filters.Filter;
import network.walrus.games.core.facets.filters.FilterResult;
import network.walrus.games.core.facets.filters.StaticResultFilter;
import network.walrus.utils.bukkit.parse.BukkitParserRegistry;
import network.walrus.utils.bukkit.points.AngleProvider;
import network.walrus.utils.bukkit.points.ProviderParsingUtils;
import network.walrus.utils.bukkit.region.Region;
import network.walrus.utils.bukkit.region.modifiers.TranslateRegion;
import network.walrus.utils.bukkit.translation.LocalizedConfigurationProperty;
import network.walrus.utils.core.config.Node;
import network.walrus.utils.core.config.ParsingException;
import network.walrus.utils.core.parse.SimpleParser;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import network.walrus.utils.parsing.facet.parse.FacetParser;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.World;
import org.bukkit.util.Vector;

/**
 * Parses {@link Portal}s from the map configuration document.
 *
 * <p>This parser will also create {@link Applicator}s for the {@link Portal#getRegion()} and {@link
 * Portal#getDestination()} regions in order to block all modification of those areas. As such, this
 * parser *must* be defined after the {@link ApplicatorsParser}.
 *
 * @author Austin Mayes
 */
public class PortalsParser implements FacetParser<PortalsFacet> {

  private SimpleParser<Float> floatParser = BukkitParserRegistry.floatParser();

  @Override
  public Optional<PortalsFacet> parse(FacetHolder holder, Node<?> node) throws ParsingException {
    if (!node.hasChild("portals")) {
      return Optional.empty();
    }

    List<Portal> portals = Lists.newArrayList();
    for (Node<?> parent : node.children("portals")) {
      for (Node<?> child : parent.children()) {
        portals.add(buildPortal(child, holder));
      }
    }

    return Optional.of(new PortalsFacet(holder, portals));
  }

  private Portal buildPortal(Node node, FacetHolder holder) {
    Region region =
        holder
            .getRegistry()
            .get(Region.class, node.attribute("region").asRequiredString(), true)
            .get();
    Region destination;
    if (node.hasAttribute("x") || node.hasAttribute("y") || node.hasAttribute("z")) {
      float x = floatParser.parse(node.attribute("x")).orElse(0f);
      float y = floatParser.parse(node.attribute("y")).orElse(0f);
      float z = floatParser.parse(node.attribute("z")).orElse(0f);
      destination = new TranslateRegion(region, new Vector(x, y, z));
    } else if (node.hasAttribute("destination")) {
      destination =
          holder
              .getRegistry()
              .get(Region.class, node.attribute("destination").asRequiredString(), true)
              .get();
    } else {
      throw new ParsingException(
          node, "Portals must have either x,y,z relative values or a destination region.");
    }

    Optional<Filter> filter = Optional.empty();
    if (node.hasAttribute("filter")) {
      filter =
          holder.getRegistry().get(Filter.class, node.attribute("filter").asRequiredString(), true);
    }

    Pair<Optional<AngleProvider>, Optional<AngleProvider>> yawPitch =
        ProviderParsingUtils.parseYawPitch(node);

    boolean observers =
        BukkitParserRegistry.booleanParser().parse(node.attribute("observers")).orElse(true);
    boolean bidirectional =
        BukkitParserRegistry.booleanParser().parse(node.attribute("bidirectional")).orElse(false);
    boolean sound =
        BukkitParserRegistry.booleanParser().parse(node.attribute("sound")).orElse(true);
    boolean smooth =
        BukkitParserRegistry.booleanParser().parse(node.attribute("smooth")).orElse(false);

    return new Portal(
        region,
        destination,
        filter,
        observers,
        yawPitch.getKey(),
        yawPitch.getValue(),
        bidirectional,
        sound,
        smooth);
  }

  // post parsing method, using this to add the applicator stuff
  @Override
  public Optional<PortalsFacet> parse(FacetHolder holder, World world, PortalsFacet facet)
      throws ParsingException {
    // Pull in the applicators facet
    // This parsing method MUST be run after the one in the applicator parser in order for these to
    // stick
    ApplicatorsFacet applicatorsFacet;
    if (holder.hasFacet(ApplicatorsFacet.class)) {
      applicatorsFacet = holder.getFacetRequired(ApplicatorsFacet.class);
    } else {
      // If no applicators were defined by the user, the facet will not be loaded so we need to
      // create one and load it
      applicatorsFacet = new ApplicatorsFacet(Lists.newArrayList());
      holder.addFacet(applicatorsFacet);
    }

    for (Portal p : facet.portals()) {
      // Add applicators for region and destination of each portal to block modification
      // Applicators are added at index 0 to ensure they are tested first in the applicator
      // filtering logic
      applicatorsFacet
          .getApplicators()
          .add(
              0,
              new Applicator(
                  p.getRegion(),
                  Optional.empty(),
                  Optional.empty(),
                  Optional.of(new StaticResultFilter(FilterResult.DENY)),
                  Optional.of(new StaticResultFilter(FilterResult.DENY)),
                  Optional.empty(),
                  Optional.of(
                      new LocalizedConfigurationProperty(GamesCoreMessages.CANNOT_MODIFY_PORTALS)),
                  Optional.empty(),
                  Optional.empty()));
      applicatorsFacet
          .getApplicators()
          .add(
              0,
              new Applicator(
                  p.getDestination(),
                  Optional.empty(),
                  Optional.empty(),
                  Optional.of(new StaticResultFilter(FilterResult.DENY)),
                  Optional.of(new StaticResultFilter(FilterResult.DENY)),
                  Optional.empty(),
                  Optional.of(
                      new LocalizedConfigurationProperty(GamesCoreMessages.CANNOT_MODIFY_PORTALS)),
                  Optional.empty(),
                  Optional.empty()));
    }

    return Optional.of(facet);
  }
}

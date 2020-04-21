package network.walrus.games.core.facets.tnt;

import java.time.Duration;
import java.util.Optional;
import network.walrus.utils.bukkit.parse.BukkitParserRegistry;
import network.walrus.utils.core.config.Node;
import network.walrus.utils.core.config.ParsingException;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import network.walrus.utils.parsing.facet.parse.FacetParser;

/**
 * Parses the {@link TNTFacet}.
 *
 * @author Austin Mayes
 */
public class TNTParser implements FacetParser<TNTFacet> {

  @Override
  public Optional<TNTFacet> parse(FacetHolder holder, Node<?> node) throws ParsingException {
    if (node.hasChild("tnt")) {
      Node<?> tnt = node.childRequired("tnt");
      boolean instant =
          tnt.child("instantignite")
              .map(n -> BukkitParserRegistry.booleanParser().parseRequired(n.text()))
              .orElse(false);
      boolean damage =
          tnt.child("blockdamage")
              .map(n -> BukkitParserRegistry.booleanParser().parseRequired(n.text()))
              .orElse(false);
      Float yield =
          tnt.child("yield")
              .map(n -> BukkitParserRegistry.floatParser().parseRequired(n.text()))
              .orElse(null);
      Float power =
          tnt.child("power")
              .map(n -> BukkitParserRegistry.floatParser().parseRequired(n.text()))
              .orElse(null);
      Duration fuse =
          tnt.child("fuse")
              .map(n -> BukkitParserRegistry.durationParser().parseRequired(n.text()))
              .orElse(null);
      Integer dispenserLimit =
          tnt.child("dispenser-tnt-limit")
              .map(n -> BukkitParserRegistry.integerParser().parseRequired(n.text()))
              .orElse(null);
      Float dispenserMultiplier =
          tnt.child("dispenser-tnt-multiplier")
              .map(n -> BukkitParserRegistry.floatParser().parseRequired(n.text()))
              .orElse(null);

      return Optional.of(
          new TNTFacet(
              holder, instant, damage, yield, power, fuse, dispenserLimit, dispenserMultiplier));
    }

    return Optional.empty();
  }
}

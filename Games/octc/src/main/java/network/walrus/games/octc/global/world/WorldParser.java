package network.walrus.games.octc.global.world;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Optional;
import network.walrus.utils.bukkit.parse.BukkitParserRegistry;
import network.walrus.utils.core.config.Node;
import network.walrus.utils.core.config.ParsingException;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import network.walrus.utils.parsing.facet.parse.FacetParser;

/**
 * Parser for the {@link WorldFacet}.
 *
 * <p>THis will always return a parsed facet regardless if the configuration document contains
 * custom data.
 *
 * @author Austin Mayes
 */
public class WorldParser implements FacetParser<WorldFacet> {

  @Override
  public Optional<WorldFacet> parse(FacetHolder holder, Node<?> node) throws ParsingException {
    boolean lockTime =
        node.child("timelock")
            .map(e -> BukkitParserRegistry.booleanParser().parseRequired(e.text()))
            .orElse(false);

    Map<GameRule, String> rules = Maps.newHashMap();
    node.child("gamerules")
        .ifPresent(
            n -> {
              for (Node<?> c : n.children()) {
                rules.put(GameRule.valueOf(c.name()), c.text().asRequiredString());
              }
            });

    return Optional.of(new WorldFacet(holder, lockTime, rules));
  }
}

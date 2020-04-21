package network.walrus.games.core.facets.damage;

import java.util.Optional;
import network.walrus.games.core.facets.filters.Filter;
import network.walrus.games.core.facets.filters.FilterDefinitionParser;
import network.walrus.utils.core.config.Node;
import network.walrus.utils.core.config.ParsingException;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import network.walrus.utils.parsing.facet.parse.DocumentParser;
import network.walrus.utils.parsing.facet.parse.FacetParser;

/**
 * Parser which parses filters for disabling damage
 *
 * @author ShinyDialga
 */
public class DamageParser implements FacetParser<DamageFacet> {

  @Override
  public Optional<DamageFacet> parse(FacetHolder holder, Node root) throws ParsingException {
    Optional<Node> element = root.child("disabledamage");

    if (!element.isPresent()) {
      return Optional.empty();
    }

    FilterDefinitionParser filterParser = DocumentParser.getParser(FilterDefinitionParser.class);
    Optional<Filter> damage = parseFilter(holder, filterParser, element.get());

    return Optional.of(new DamageFacet(holder, damage));
  }

  private Optional<Filter> parseFilter(
      FacetHolder holder, FilterDefinitionParser parser, Node<?> node) {
    if (node.children().isEmpty() || node.children().size() > 1) {
      throw new ParsingException(node, "Damage tags only support one filter child.");
    }
    return Optional.of(parser.parseFilter(holder, node.children().get(0)));
  }
}

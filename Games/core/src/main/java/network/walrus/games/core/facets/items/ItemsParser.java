package network.walrus.games.core.facets.items;

import java.util.List;
import java.util.Optional;
import network.walrus.games.core.facets.filters.Filter;
import network.walrus.games.core.facets.filters.FilterDefinitionParser;
import network.walrus.utils.core.config.Node;
import network.walrus.utils.core.config.ParsingException;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import network.walrus.utils.parsing.facet.parse.DocumentParser;
import network.walrus.utils.parsing.facet.parse.FacetParser;

/**
 * Parser which parses filters for item repair, remove, and keep rules.
 *
 * @author Austin Mayes
 */
public class ItemsParser implements FacetParser<ItemsFacet> {

  @Override
  public Optional<ItemsFacet> parse(FacetHolder holder, Node root) throws ParsingException {
    List<Node> elements = root.children("items");

    if (elements.isEmpty()) {
      return Optional.empty();
    }

    Optional<Filter> removeDrops = Optional.empty();
    Optional<Filter> keepItems = Optional.empty();
    Optional<Filter> repairTools = Optional.empty();
    Optional<Filter> deathDrop = Optional.empty();

    FilterDefinitionParser filterParser = DocumentParser.getParser(FilterDefinitionParser.class);

    for (Node element : elements) {
      removeDrops = parseFilter(holder, filterParser, element, "remove");
      keepItems = parseFilter(holder, filterParser, element, "keep");
      repairTools = parseFilter(holder, filterParser, element, "repair");
      deathDrop = parseFilter(holder, filterParser, element, "death-drop");
    }

    return Optional.of(new ItemsFacet(holder, removeDrops, keepItems, repairTools, deathDrop));
  }

  private Optional<Filter> parseFilter(
      FacetHolder holder, FilterDefinitionParser parser, Node<?> node, String id) {
    if (node.child(id).isPresent()) {
      Node<?> child = node.childRequired(id);
      if (child.children().isEmpty() || child.children().size() > 1) {
        throw new ParsingException(child, "Item tags only support one filter child.");
      }
      return Optional.of(parser.parseFilter(holder, child.children().get(0)));
    }

    return Optional.empty();
  }
}

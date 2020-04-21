package network.walrus.games.core.facets.hunger;

import java.util.Optional;
import network.walrus.utils.core.config.Node;
import network.walrus.utils.core.config.ParsingException;
import network.walrus.utils.core.parse.CoreParserRegistry;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import network.walrus.utils.parsing.facet.parse.FacetParser;

/**
 * Parser for {@link HungerFacet}.
 *
 * @author Rafi Baum
 */
public class HungerFacetParser implements FacetParser<HungerFacet> {

  @Override
  public Optional<HungerFacet> parse(FacetHolder holder, Node<?> node) throws ParsingException {
    Optional<? extends Node<?>> hungerNode = node.child("hunger");
    if (!hungerNode.isPresent() || !hungerNode.get().hasText()) {
      return Optional.empty();
    }

    boolean hunger = CoreParserRegistry.booleanParser().parseRequired(hungerNode.get().text());

    if (!hunger) {
      return Optional.of(new HungerFacet(holder));
    } else {
      return Optional.empty();
    }
  }
}

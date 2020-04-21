package network.walrus.games.core.facets.rage;

import java.util.Optional;
import network.walrus.utils.core.config.Node;
import network.walrus.utils.core.config.ParsingException;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import network.walrus.utils.parsing.facet.parse.FacetParser;

/**
 * * Parses the <rage/> tag
 *
 * @author Wesley Smith
 */
public class RageParser implements FacetParser<RageFacet> {

  @Override
  public Optional<RageFacet> parse(FacetHolder holder, Node root) throws ParsingException {
    Optional<Node> rageNode = root.child("rage");
    if (rageNode.isPresent()) {
      return Optional.of(new RageFacet());
    }
    return Optional.empty();
  }
}

package network.walrus.utils.parsing.facet.parse;

import java.util.List;
import java.util.Optional;
import network.walrus.utils.core.config.Node;
import network.walrus.utils.core.config.ParsingException;
import network.walrus.utils.parsing.facet.Facet;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import network.walrus.utils.parsing.facet.holder.FacetHolderParser;
import network.walrus.utils.parsing.facet.parse.DocumentParser.FacetWithParser;
import org.bukkit.World;

/**
 * Simple implementation of {@link FacetHolderParser}.
 *
 * @param <F> base holder type that this parser is responsible for providing configuration data for
 * @author Austin Mayes
 */
public class SimpleHolderParser<F extends FacetHolder> implements FacetHolderParser<F> {

  @Override
  public void preParse(F holder, Node parent) throws ParsingException {}

  @Override
  public void postParse(F holder, List<FacetWithParser> parsed) throws ParsingException {
    boolean loaded = holder.loadWorld();
    if (!loaded) {
      throw new ParsingException(
          holder.getSource().parent(), "Failed to load game world for parsing!");
    }
    World world = holder.getContainer().mainWorld();
    for (FacetWithParser p : parsed) {
      Optional<? extends Facet> facet = p.parser.parse(holder, world, p.parsed);
      if (facet.isPresent()) {
        Facet complete = facet.get();
        holder.addFacet(complete);
      } else if (p.parser.required()) {
        throw new ParsingException(
            "Missing required facet: " + p.getClass().getSimpleName().replace("Parser", ""));
      }
    }
  }
}

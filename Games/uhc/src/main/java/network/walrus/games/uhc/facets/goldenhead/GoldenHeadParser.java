package network.walrus.games.uhc.facets.goldenhead;

import java.util.Optional;
import network.walrus.games.uhc.UHCManager;
import network.walrus.utils.core.config.Node;
import network.walrus.utils.core.config.ParsingException;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import network.walrus.utils.parsing.facet.parse.FacetParser;

/**
 * Instanciate the {@link GoldenHeadFacet} if it is enabled in the config.
 *
 * @author Austin Mayes
 */
public class GoldenHeadParser implements FacetParser<GoldenHeadFacet> {

  @Override
  public Optional<GoldenHeadFacet> parse(FacetHolder holder, Node<?> node) throws ParsingException {
    if (UHCManager.instance.getConfig().goldenHead.get())
      return Optional.of(new GoldenHeadFacet(UHCManager.instance.getUHC()));

    return Optional.empty();
  }
}

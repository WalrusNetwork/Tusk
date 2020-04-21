package network.walrus.games.uhc.facets.goldenhead;

import network.walrus.games.uhc.UHCRound;
import network.walrus.utils.parsing.facet.parse.configurator.FacetConfigurator;

/**
 * Configures the {@link GoldenHeadParser}
 *
 * @author Austin Mayes
 */
public class GoldenHeadConfigurator implements FacetConfigurator {

  @Override
  public void configure() {
    bindParser(GoldenHeadParser.class, (h) -> h instanceof UHCRound);
  }
}

package network.walrus.games.octc.hills.koth;

import network.walrus.games.core.api.game.GameBoundConfigurator;
import network.walrus.games.octc.hills.HillListener;

/**
 * A configurator for the king of the hill facet
 *
 * @author Matthew Arnold
 */
public class KothConfigurator implements GameBoundConfigurator {

  @Override
  public String[] gameSlug() {
    return new String[] {"koth"};
  }

  @Override
  public void configure() {
    bindParser(KothParser.class);
    bindFacetListener(HillListener.class, KothFacet.class);
  }
}

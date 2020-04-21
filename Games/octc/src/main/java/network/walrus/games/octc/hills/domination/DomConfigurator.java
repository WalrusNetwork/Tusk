package network.walrus.games.octc.hills.domination;

import network.walrus.games.core.api.game.GameBoundConfigurator;
import network.walrus.games.octc.hills.HillListener;

/**
 * A game bound configurator for domination-point
 *
 * @author Matthew Arnold
 */
public class DomConfigurator implements GameBoundConfigurator {

  @Override
  public void configure() {
    bindParser(DominationParser.class);
    bindFacetListener(HillListener.class, DomFacet.class);
  }

  @Override
  public String[] gameSlug() {
    return new String[] {"dom"};
  }
}

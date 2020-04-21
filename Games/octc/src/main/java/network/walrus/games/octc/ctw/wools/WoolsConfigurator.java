package network.walrus.games.octc.ctw.wools;

import network.walrus.games.core.api.game.GameBoundConfigurator;

/**
 * Configurator for the wools facet.
 *
 * @author Austin Mayes
 */
public class WoolsConfigurator implements GameBoundConfigurator {

  @Override
  public String[] gameSlug() {
    return new String[] {"ctw"};
  }

  @Override
  public void configure() {
    bindParser(WoolsParser.class);
    bindFacetListener(WoolListener.class, WoolsFacet.class);
  }
}

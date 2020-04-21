package network.walrus.games.octc.ctf.flags;

import network.walrus.games.core.api.game.GameBoundConfigurator;

/**
 * Configurator for the flags facet.
 *
 * @author Austin Mayes
 */
public class FlagsConfigurator implements GameBoundConfigurator {

  @Override
  public String[] gameSlug() {
    return new String[] {"ctf"};
  }

  @Override
  public void configure() {
    bindParser(FlagsParser.class);
    bindFacetListener(FlagListener.class, FlagsFacet.class);
  }
}

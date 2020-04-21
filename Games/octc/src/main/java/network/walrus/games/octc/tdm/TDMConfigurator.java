package network.walrus.games.octc.tdm;

import network.walrus.games.core.api.game.GameBoundConfigurator;
import network.walrus.games.octc.tdm.overtime.BlitzOvertimeFacet;
import network.walrus.games.octc.tdm.overtime.BlitzOvertimeListener;

/**
 * Configurator for the TDM gamemode
 *
 * @author Matthew Arnold
 * @author Austin Mayes
 */
public class TDMConfigurator implements GameBoundConfigurator {

  @Override
  public String[] gameSlug() {
    return new String[] {"tdm"};
  }

  @Override
  public void configure() {
    bindParser(TDMParser.class);
    bindFacetListener(BlitzOvertimeListener.class, BlitzOvertimeFacet.class);
  }
}

package network.walrus.games.octc.destroyables.objectives;

import network.walrus.games.core.api.game.GameBoundConfigurator;
import network.walrus.games.octc.destroyables.objectives.cores.CoreListener;
import network.walrus.games.octc.destroyables.objectives.monuments.MonumentListener;

/**
 * Configurator for the cores facet.
 *
 * @author ShinyDialga
 */
public class DestroyablesConfigurator implements GameBoundConfigurator {

  @Override
  public String[] gameSlug() {
    return new String[] {"dtc", "dtm", "dtcm"};
  }

  @Override
  public void configure() {
    bindParser(DestroyableObjectivesParser.class);
    bindFacetListener(CoreListener.class, DestroyablesFacet.class);
    bindFacetListener(MonumentListener.class, DestroyablesFacet.class);
  }
}

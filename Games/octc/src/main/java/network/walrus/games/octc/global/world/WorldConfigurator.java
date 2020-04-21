package network.walrus.games.octc.global.world;

import network.walrus.utils.parsing.facet.parse.configurator.ActiveTime;
import network.walrus.utils.parsing.facet.parse.configurator.FacetConfigurator;

/**
 * Configures the OCN-style world parser and registers some useful listeners.
 *
 * @author Austin Mayes
 */
public class WorldConfigurator implements FacetConfigurator {

  @Override
  public void configure() {
    bindParser(WorldParser.class);
    bindFacetListener(MobsListener.class, WorldFacet.class);
    bindFacetListener(ItemListener.class, WorldFacet.class, ActiveTime.ENABLED);
    bindFacetListener(PhysicsListener.class, WorldFacet.class);
    bindFacetListener(PlayerListener.class, WorldFacet.class);
  }
}

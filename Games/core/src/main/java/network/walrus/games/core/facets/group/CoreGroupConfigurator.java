package network.walrus.games.core.facets.group;

import network.walrus.games.core.facets.group.spectate.ObserverListener;
import network.walrus.games.core.facets.group.spectate.SpectatorListener;
import network.walrus.games.core.facets.group.spectate.invstalk.InventoryStalkListener;
import network.walrus.utils.parsing.facet.parse.configurator.FacetConfigurator;

/**
 * Configures aspects of the core group system.
 *
 * @author Austin Mayes
 */
public class CoreGroupConfigurator implements FacetConfigurator {

  @Override
  public void configure() {
    bindFacetListener(GroupsListener.class, GroupsManager.class);
    bindFacetListener(ObserverListener.class, GroupsManager.class);
    bindFacetListener(SpectatorListener.class, GroupsManager.class);
    bindFacetListener(ScoreboardHandler.class, GroupsManager.class);
    bindFacetListener(InventoryStalkListener.class, GroupsManager.class);
    bindFacetListener(FriendlyFireListener.class, GroupsManager.class);
  }
}

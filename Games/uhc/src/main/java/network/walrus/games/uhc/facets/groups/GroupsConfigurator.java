package network.walrus.games.uhc.facets.groups;

import network.walrus.games.core.facets.group.GroupsManager;
import network.walrus.games.core.facets.group.ScoreboardHandler;
import network.walrus.games.core.facets.group.spectate.ObserverListener;
import network.walrus.games.core.facets.group.spectate.invstalk.InventoryStalkListener;
import network.walrus.games.uhc.UHCRound;
import network.walrus.utils.parsing.facet.parse.configurator.FacetConfigurator;

/**
 * Configures group actions for UHC.
 *
 * @author Austin Mayes
 */
public class GroupsConfigurator implements FacetConfigurator {

  @Override
  public void configure() {
    bindParser(GroupsParser.class, (h) -> h instanceof UHCRound);
    bindFacetListener(ObserverListener.class, GroupsManager.class);
    bindFacetListener(ScoreboardHandler.class, GroupsManager.class);
    bindFacetListener(InventoryStalkListener.class, GroupsManager.class);
    bindFacetCommands(TeamGameCommands.class, UHCGroupsManager.class);
    bindFacetCommands(GeneralTeamCommands.class, UHCGroupsManager.class);
  }
}

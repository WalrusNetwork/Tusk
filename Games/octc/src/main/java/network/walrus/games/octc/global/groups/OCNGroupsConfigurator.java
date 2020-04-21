package network.walrus.games.octc.global.groups;

import network.walrus.games.core.facets.group.CoreGroupConfigurator;
import network.walrus.games.core.round.GameRound;

/**
 * Configures aspects of the ocn style group system.
 *
 * @author Austin Mayes
 */
public class OCNGroupsConfigurator extends CoreGroupConfigurator {

  @Override
  public void configure() {
    super.configure();
    bindParser(GroupsParser.class, (h) -> h instanceof GameRound);
    bindFacetCommands(GroupCommands.class, OCNGroupsManager.class);
    bindFacetListener(JoinListener.class, OCNGroupsManager.class);
  }
}

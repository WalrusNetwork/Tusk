package network.walrus.games.octc.tdm.overtime;

import java.util.List;
import network.walrus.games.core.api.results.scenario.EndScenario;
import network.walrus.games.core.facets.filters.Filter;
import network.walrus.games.core.facets.group.Competitor;
import network.walrus.games.core.facets.group.GroupsManager;
import network.walrus.games.core.facets.visual.PaneGroup;
import network.walrus.games.core.facets.visual.SidebarFacet;
import network.walrus.games.core.round.GameRound;
import network.walrus.games.octc.OCNMessages;
import network.walrus.games.octc.global.spawns.OCNSpawnManager;
import network.walrus.games.octc.tdm.ScoreBox;
import network.walrus.utils.bukkit.sound.NetworkSoundConstants.Games.OCN.TDM.Overtime;
import network.walrus.utils.core.color.NetworkColorConstants.Games.OCN.TDM;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.entity.Player;

/**
 * Scenario executed when the match goes in to overtime. This scenario clears the score boxes,
 * changes the display pane, announces the overtime and enables the overtime facet
 *
 * @author David Rodriguez
 */
public class BlitzScenario extends EndScenario {
  private final Runnable boxCleaner;

  /**
   * @param round that the scenario is executing in
   * @param boxes the map has
   */
  public BlitzScenario(GameRound round, Filter filter, final List<ScoreBox> boxes) {
    super(round, filter, 1);
    this.boxCleaner = boxes::clear;
  }

  @Override
  public void execute() {
    boxCleaner.run();
    revivePlayers();
    changePane();
    sendAnnouncement();

    // Enable facet
    getRound().getFacetRequired(BlitzOvertimeFacet.class).setEnabled();
  }

  private void revivePlayers() {
    OCNSpawnManager spawnsManager = getRound().getFacetRequired(OCNSpawnManager.class);

    for (Player player : getRound().playingPlayers()) {
      if (!spawnsManager.isRespawning(player)) continue;
      spawnsManager.stopRespawnTask(player);
    }
  }

  private void changePane() {
    SidebarFacet.PANE_CREATE_FUNCTION =
        (m, c) -> new PaneGroup(Pair.of("tdm-alive", new AliveDisplay(m, getRound(), c)));

    SidebarFacet sidebarFacet = getRound().getFacetRequired(SidebarFacet.class);

    sidebarFacet.recreateSpectatorPane();
    for (Competitor competitor :
        getRound().getFacetRequired(GroupsManager.class).getCompetitors()) {
      sidebarFacet.recreatePane(competitor);
      for (Player player : getRound().getContainer().players()) {
        sidebarFacet.refreshPane(player, competitor);
      }
    }
  }

  private void sendAnnouncement() {
    getRound()
        .getContainer()
        .broadcast(OCNMessages.TDM_OVERTIME_BROADCAST.with(TDM.Overtime.BROADCAST));
    getRound().getContainer().broadcast(Overtime.STARTED);
  }
}

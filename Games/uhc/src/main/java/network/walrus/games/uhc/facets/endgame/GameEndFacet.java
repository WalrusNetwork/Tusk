package network.walrus.games.uhc.facets.endgame;

import java.util.List;
import network.walrus.games.core.api.results.scenario.TieScenario;
import network.walrus.games.core.events.group.PlayerChangedGroupEvent;
import network.walrus.games.core.facets.group.Competitor;
import network.walrus.games.core.util.GameTask;
import network.walrus.games.uhc.UHCRound;
import network.walrus.games.uhc.facets.groups.UHCGroupsManager;
import network.walrus.ubiquitous.bukkit.tracker.event.player.PlayerDeathEvent;
import network.walrus.ubiquitous.bukkit.tracker.event.tag.TaggedPlayerDeathEvent;
import network.walrus.utils.parsing.facet.Facet;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 * Facet used to end games.
 *
 * @author Austin Mayes
 */
public class GameEndFacet extends Facet implements Listener {

  private final UHCRound round;
  private UHCGroupsManager groupsManager;
  private boolean over = false;

  /** @param holder which end checking is being acted on */
  public GameEndFacet(FacetHolder holder) {
    this.round = (UHCRound) holder;
  }

  @Override
  public void load() {
    this.groupsManager = this.round.getFacetRequired(UHCGroupsManager.class);
  }

  @Override
  public void disable() {
    over = true;
  }

  /** Check on player death. */
  @EventHandler(priority = EventPriority.MONITOR)
  public void onDeath(PlayerDeathEvent event) {
    check();
  }

  /** Check on tagged player death. */
  @EventHandler(priority = EventPriority.MONITOR)
  public void onDeath(TaggedPlayerDeathEvent event) {
    check();
  }

  @EventHandler
  public void onGroupChange(PlayerChangedGroupEvent event) {
    check();
  }

  /** Run the end check and end the game if it passes. */
  public void check() {
    GameTask.of(
            "Round end check",
            () -> {
              if (!round.getState().playing() || over) {
                return;
              }
              List<Competitor> aliveCompetitors = (List<Competitor>) groupsManager.getCompetitors();
              aliveCompetitors.removeIf(c -> c.getPlayers().isEmpty());
              if (aliveCompetitors.size() == 1) {
                handleWin(aliveCompetitors.get(0));
              } else if (aliveCompetitors.isEmpty()) {
                handleTie();
              }
            })
        .later(20);
  }

  private void handleTie() {
    over = true;
    new TieScenario(round).execute();
  }

  private void handleWin(Competitor competitor) {
    over = true;
    new UHCEndScenario(round, competitor).execute();
  }
}

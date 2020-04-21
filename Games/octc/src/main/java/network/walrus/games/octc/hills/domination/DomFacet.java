package network.walrus.games.octc.hills.domination;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import network.walrus.games.core.events.round.RoundStateChangeEvent;
import network.walrus.games.core.facets.group.Competitor;
import network.walrus.games.core.facets.visual.PaneGroup;
import network.walrus.games.core.facets.visual.SidebarFacet;
import network.walrus.games.core.round.GameRound;
import network.walrus.games.core.util.GameTask;
import network.walrus.games.octc.global.results.WinCalculator;
import network.walrus.games.octc.hills.HillFacet;
import network.walrus.games.octc.hills.HillObjective;
import network.walrus.games.octc.hills.events.HillChangeCompletionEvent;
import network.walrus.games.octc.hills.events.HillChangeOwnerEvent;
import network.walrus.utils.bukkit.visual.display.DisplayManager;
import network.walrus.utils.parsing.facet.FacetLoadException;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * The facet for the domination point game mode
 *
 * @author Matthew Arnold
 */
public class DomFacet extends HillFacet implements Listener {

  private final WinCalculator winCalculator;
  private final GameRound gameRound;
  private final GameTask task;

  private SidebarFacet sidebarFacet;

  /**
   * Creates a new domination point facet
   *
   * @param gameRound the game round of this facet
   * @param hills the list of hill objectives for this domination point
   * @param winCalculator the wincalculator for domination points
   * @param controlPaneFunction the function used to create the display pane for domination facet
   */
  public DomFacet(
      GameRound gameRound,
      List<HillObjective> hills,
      WinCalculator winCalculator,
      BiFunction<DisplayManager, Optional<Competitor>, PaneGroup> controlPaneFunction) {
    super(hills);
    this.gameRound = gameRound;
    this.winCalculator = winCalculator;
    SidebarFacet.PANE_CREATE_FUNCTION = controlPaneFunction;

    // game task that is used to tick each hill, 250ms is used because the task is getting ticket 4
    // times a second (1000/4 = 250)
    this.task =
        new GameTask(
            "Control Point Ticker",
            () -> {
              for (HillObjective x : hills) {
                x.tick(Duration.ofMillis(250));
              }
            });
  }

  @Override
  public void load() throws FacetLoadException {
    this.sidebarFacet = gameRound.getFacetRequired(SidebarFacet.class);

    for (HillObjective hillObjective : hills()) {
      hillObjective.initialize();
    }
  }

  @Override
  public void enable() {
    task.repeat(1, 5);
  }

  @EventHandler
  public void onStateChange(RoundStateChangeEvent event) {
    winCalculator.stateChanged(event);
    if (event.isChangeToNotPlaying()) {
      task.reset();
    }
  }

  /** Called when a hill % changes, updates the scoreboard */
  @EventHandler
  public void captureTimeChange(HillChangeCompletionEvent event) {
    sidebarFacet.update(event.getObjective().getName().translateDefault());
  }

  @EventHandler
  public void onCapture(HillChangeOwnerEvent event) {
    winCalculator.check();
    sidebarFacet.update(event.getObjective().getName().translateDefault());
  }

  @Override
  public void unload() {
    winCalculator.disable();
  }
}

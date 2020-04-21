package network.walrus.games.octc.hills.koth;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import network.walrus.games.core.events.round.RoundStateChangeEvent;
import network.walrus.games.core.facets.group.Competitor;
import network.walrus.games.core.facets.group.GroupsManager;
import network.walrus.games.core.facets.visual.PaneGroup;
import network.walrus.games.core.facets.visual.SidebarFacet;
import network.walrus.games.core.round.GameRound;
import network.walrus.games.core.util.GameTask;
import network.walrus.games.octc.global.results.WinCalculator;
import network.walrus.games.octc.hills.HillFacet;
import network.walrus.games.octc.hills.HillObjective;
import network.walrus.games.octc.hills.events.HillChangeCompletionEvent;
import network.walrus.games.octc.hills.events.HillChangeOwnerEvent;
import network.walrus.games.octc.hills.koth.overtime.KothOvertimeScenario;
import network.walrus.games.octc.hills.koth.overtime.NullObjective;
import network.walrus.games.octc.hills.overtime.OvertimeFacet;
import network.walrus.games.octc.hills.overtime.OvertimeState;
import network.walrus.games.octc.score.ScoreFacet;
import network.walrus.games.octc.score.event.PointChangeEvent;
import network.walrus.utils.parsing.facet.FacetLoadException;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * King of the hill facet for the king of the hill gamemode
 *
 * @author Matthew Arnold
 */
public class KothFacet extends HillFacet implements Listener {

  private final GameTask gameTask;
  private final GameRound gameRound;
  private final boolean overtime;
  private GameTask scoreTask;
  private WinCalculator winCalculator;

  private ScoreFacet scoreFacet;
  private SidebarFacet sidebarFacet;
  private OvertimeFacet overtimeFacet;
  private GroupsManager groupsManager;

  /**
   * Creates a new king of the hill facet
   *
   * @param gameRound the game round of the facet
   * @param hills the list of the hills
   * @param overtimeEnabled whether or not overtime is enabled
   */
  public KothFacet(GameRound gameRound, List<HillObjective> hills, boolean overtimeEnabled) {
    super(hills);
    this.gameRound = gameRound;
    this.overtime = overtimeEnabled;
    this.gameTask =
        new GameTask(
            "Hill Ticker",
            () -> {
              for (HillObjective x : hills) {
                this.tickHill(x, Duration.ofMillis(250));
              }
            });
  }

  @Override
  public void enable() {
    gameTask.repeat(1, 5);
    scoreTask.repeat(1, 2);
  }

  @Override
  public void load() throws FacetLoadException {
    for (HillObjective hillObjective : hills()) {
      hillObjective.initialize();
    }
    this.scoreFacet = gameRound.getFacetRequired(ScoreFacet.class);
    this.sidebarFacet = gameRound.getFacetRequired(SidebarFacet.class);
    this.overtimeFacet = gameRound.getFacetRequired(OvertimeFacet.class);
    this.groupsManager = gameRound.getFacetRequired(GroupsManager.class);

    this.scoreTask = new HillScoreTask(this, scoreFacet);

    if (overtime) {
      winCalculator =
          new WinCalculator(
              gameRound,
              Collections.singletonList(new NullObjective()),
              Collections.singletonList(
                  new KothOvertimeScenario(
                      gameRound,
                      overtimeFacet,
                      hills(),
                      scoreFacet.getObjective(),
                      groupsManager)));
    } else {
      winCalculator =
          new WinCalculator(
              gameRound,
              Collections.singletonList(scoreFacet.getObjective()),
              Collections.emptyList());
    }
    SidebarFacet.PANE_CREATE_FUNCTION =
        (m, c) ->
            new PaneGroup(Pair.of("Points", new KothDisplay(hills(), scoreFacet, gameRound, m, c)));
  }

  /**
   * Resets the timers and passes the state change to the win calculator
   *
   * @param event the state change event
   */
  @EventHandler
  public void onEnd(RoundStateChangeEvent event) {
    winCalculator.stateChanged(event);
    if (event.isChangeToNotPlaying()) {
      gameTask.reset();
      scoreTask.reset();
    }
  }

  /** Called when a hill is captured, updates the scoreboard and checks for a winner */
  @EventHandler
  public void captureHillEvent(HillChangeOwnerEvent event) {
    // remove or add owner-points
    sidebarFacet.update(event.getObjective().getName().translateDefault());
    winCalculator.check();
  }

  /** Called when a hill % changes, updates the scoreboard */
  @EventHandler
  public void captureTimeChange(HillChangeCompletionEvent event) {
    sidebarFacet.update(event.getObjective().getName().translateDefault());
  }

  /**
   * Called when a team gets a point, checks the win calculator, low priority to trigger before the
   * score update
   */
  @EventHandler
  public void onChange(PointChangeEvent earnEvent) {
    winCalculator.check();

    // if overtime (and not the end of the game) set the limit to be n + 1 where n is highest score
    if (overtimeFacet.isAfterNormalTime()) {
      if (overtimeFacet.state() == OvertimeState.OVERTIME) {
        scoreFacet.getObjective().setLimit(scoreFacet.getObjective().highestScore() + 1);
      }
      for (Competitor x : groupsManager.getCompetitors()) {
        sidebarFacet.update(x.id());
      }
    }
  }

  private void tickHill(HillObjective hillObjective, Duration duration) {
    hillObjective.tick(duration);
  }
}

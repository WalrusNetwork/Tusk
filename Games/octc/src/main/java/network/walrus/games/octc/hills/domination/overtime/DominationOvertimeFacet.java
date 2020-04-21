package network.walrus.games.octc.hills.domination.overtime;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.md_5.bungee.api.ChatColor;
import network.walrus.games.core.api.results.scenario.EndScenario;
import network.walrus.games.core.events.round.RoundStateChangeEvent;
import network.walrus.games.core.facets.group.Competitor;
import network.walrus.games.core.facets.visual.SidebarFacet;
import network.walrus.games.core.round.GameRound;
import network.walrus.games.core.util.GameTask;
import network.walrus.games.octc.global.results.WinCalculator;
import network.walrus.games.octc.hills.HillObjective;
import network.walrus.games.octc.hills.HillUtils;
import network.walrus.games.octc.hills.events.HillChangeOwnerEvent;
import network.walrus.games.octc.hills.overtime.OvertimeStartEvent;
import network.walrus.games.octc.hills.overtime.OvertimeState;
import network.walrus.utils.core.text.UnlocalizedFormat;
import network.walrus.utils.core.text.UnlocalizedText;
import network.walrus.utils.core.translation.Localizable;
import network.walrus.utils.core.translation.TextStyle;
import network.walrus.utils.parsing.facet.Facet;
import network.walrus.utils.parsing.facet.FacetLoadException;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * The domination overtime facet handles the overtime for domination, it manages updating the
 * domination time and state. Domination overtime means that if, at the end of a time limit the win
 * in a domination gamemode goes to who has been in control for most of the match, rather than who
 * is in control at the end like in normal domination
 *
 * @author Matthew Arnold
 */
public class DominationOvertimeFacet extends Facet implements Listener {

  // The scoreboard title string, used as the id for the renderable on the scoreboard that records
  // domination time
  public static final String SCOREBOARD_TIME = "overtime-scoreboard-state";
  public static final String DOMINATION_ICON = "⌑"; // ⌑ U+2311
  public static final UnlocalizedFormat DOMINATION_FORMAT =
      new UnlocalizedFormat(DOMINATION_ICON + " {0}");

  private final List<HillObjective> hills;
  private final GameRound gameRound;

  private final GameTask dominationTask;
  private final WinCalculator winCalculator;

  private OvertimeState matchState = OvertimeState.NORMAL;
  private DomOvertimeState state = DomOvertimeState.STARTING_TIME;

  private SidebarFacet sidebarFacet;

  /**
   * Creates a new domination overtime facet
   *
   * @param gameRound the gameround this facet is running in
   * @param duration the duration of the timelimit of the game
   * @param hills the hills in the game
   */
  public DominationOvertimeFacet(
      GameRound gameRound, Duration duration, List<HillObjective> hills) {
    this.gameRound = gameRound;
    List<HillObjective> result = new ArrayList<>();
    for (HillObjective x : hills) {
      if (x.options().required) {
        result.add(x);
      }
    }
    this.hills = result;

    Duration dominateDuration = Duration.ofMillis(100);
    this.dominationTask =
        new GameTask(
            "DominationOvertimeFacet Ticker",
            () -> {
              this.tickTime(dominateDuration);
            });

    List<EndScenario> list = new ArrayList<>(2);
    list.add(new DominationScenario(gameRound, this, duration, hills));
    list.add(new DominationOvertimeScenario(gameRound, this, hills));
    this.winCalculator = new WinCalculator(gameRound, new ArrayList<>(hills), list);

    checkDominator();
  }

  @Override
  public void load() throws FacetLoadException {
    this.sidebarFacet = gameRound.getFacetRequired(SidebarFacet.class);
  }

  @Override
  public void enable() {
    dominationTask.repeat(1, 2);
  }

  /**
   * Called at the end of each match, resets the timers
   *
   * @param event the round state change event
   */
  @EventHandler
  public void onEnd(RoundStateChangeEvent event) {
    if (event.isChangeToNotPlaying()) {
      dominationTask.reset();
    }
  }

  /** @return true if the game is in overtime, false otherwise */
  public boolean inOvertime() {
    return matchState == OvertimeState.OVERTIME;
  }

  /** @return the current state of the match */
  public OvertimeState matchState() {
    return matchState;
  }

  /** @return the win calculator */
  public WinCalculator winCalculator() {
    return winCalculator;
  }

  /** @return the current domination overtime state */
  public DomOvertimeState dominationTime() {
    return state;
  }

  /**
   * Ticks the time of the overtime facet, calculates a new overtime state and updates the
   * scoreboard
   *
   * @param duration the duration since this facet was last ticked
   */
  public void tickTime(Duration duration) {
    DomOvertimeState oldTime = state;
    this.state = state.tick(duration);

    if (!Objects.equals(oldTime.duration, state.duration)) {
      sidebarFacet.update(DominationOvertimeFacet.SCOREBOARD_TIME);
    }

    if (!inOvertime()) {
      return;
    }

    if (!Objects.equals(oldTime.owner, state.owner)
        || !Objects.equals(oldTime.dominating, state.dominating)) {
      winCalculator.check();
    }
  }

  /**
   * Sets the state to overtime
   *
   * @param event the overtime event
   */
  @EventHandler
  public void onOvertime(OvertimeStartEvent event) {
    this.matchState = OvertimeState.OVERTIME;
  }

  /**
   * The time text of the domination overtime facet
   *
   * @return the time text
   */
  public Localizable timeText() {
    TextStyle style =
        state.owner.map(x -> x.getColor().style()).orElse(TextStyle.ofColor(ChatColor.WHITE));
    return new UnlocalizedText(
        state.duration.toString().substring(2).toLowerCase().replace("m", "m "), style);
  }

  /**
   * Checks to see if the dominator has changed since a hill has been captured
   *
   * @param event the hill capture event
   */
  @EventHandler
  public void onCapture(HillChangeOwnerEvent event) {
    checkDominator();
  }

  // checks to see if the dominator has changed
  private void checkDominator() {
    Optional<Competitor> dominator = Optional.empty();
    List<Competitor> owning = HillUtils.owning(hills, numberNeeded());
    if (!owning.isEmpty()) dominator = Optional.ofNullable(owning.get(0));
    DomOvertimeState oldTime = state;
    this.state = state.setDominator(dominator);
    if (!Objects.equals(oldTime.dominating, state.dominating)) {
      winCalculator.check();
    }
  }

  // gets the numbed of hills needed
  private int numberNeeded() {
    int size = hills.size();
    if (size < 3) {
      throw new IllegalStateException("There must be at least 3 hills for overtime");
    }
    if (size == 3) {
      return 2;
    }
    return (int) Math.ceil(((double) size) / 2 + 1);
  }
}

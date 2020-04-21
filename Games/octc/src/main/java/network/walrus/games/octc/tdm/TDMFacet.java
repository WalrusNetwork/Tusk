package network.walrus.games.octc.tdm;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import network.walrus.games.core.api.results.scenario.EndScenario;
import network.walrus.games.core.events.player.PlayerSpawnCompleteEvent;
import network.walrus.games.core.events.round.RoundStateChangeEvent;
import network.walrus.games.core.facets.filters.types.TimeFilter;
import network.walrus.games.core.facets.group.Competitor;
import network.walrus.games.core.facets.group.GroupsManager;
import network.walrus.games.core.facets.visual.PaneGroup;
import network.walrus.games.core.facets.visual.SidebarFacet;
import network.walrus.games.core.round.GameRound;
import network.walrus.games.octc.OCNMessages;
import network.walrus.games.octc.global.results.WinCalculator;
import network.walrus.games.octc.global.results.scenario.ObjectivesScenario;
import network.walrus.games.octc.global.spawns.OCNSpawnManager;
import network.walrus.games.octc.score.DefaultScoreDisplay;
import network.walrus.games.octc.score.ScoreFacet;
import network.walrus.games.octc.score.event.PointChangeEvent;
import network.walrus.games.octc.tdm.overtime.BlitzOvertimeDeathEvent;
import network.walrus.games.octc.tdm.overtime.BlitzOvertimeFacet;
import network.walrus.games.octc.tdm.overtime.BlitzOvertimeScenario;
import network.walrus.games.octc.tdm.overtime.BlitzScenario;
import network.walrus.ubiquitous.bukkit.tracker.event.player.PlayerCoarseMoveEvent;
import network.walrus.utils.bukkit.PlayerUtils;
import network.walrus.utils.bukkit.sound.NetworkSoundConstants;
import network.walrus.utils.bukkit.translation.text.player.PersonalizedBukkitPlayer;
import network.walrus.utils.core.math.NumberComparator;
import network.walrus.utils.core.text.LocalizedNumber;
import network.walrus.utils.parsing.facet.Facet;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 * Facet to configure the TDM gamemode
 *
 * @author Matthew Arnold
 * @author Austin Mayes
 */
public class TDMFacet extends Facet implements Listener {

  private final GameRound gameRound;
  private final List<ScoreBox> boxes;
  private final boolean interactive;
  private final Optional<Duration> timeLimit;
  private final boolean useOvertime;

  private WinCalculator winCalculator;
  private ScoreFacet scoreFacet;

  private GroupsManager groupsManager;
  private Optional<BlitzOvertimeFacet> overtimeFacet;

  /** Creates a new TDM facet */
  public TDMFacet(
      GameRound gameRound,
      List<ScoreBox> boxes,
      Optional<Duration> timeLimit,
      boolean interactive,
      boolean useOvertime) {
    this.gameRound = gameRound;
    this.boxes = boxes;
    this.interactive = interactive;
    this.timeLimit = timeLimit;
    this.useOvertime = useOvertime;
  }

  @Override
  public void load() {
    this.scoreFacet = gameRound.getFacetRequired(ScoreFacet.class);
    this.groupsManager = gameRound.getFacetRequired(GroupsManager.class);
    this.overtimeFacet = gameRound.getFacet(BlitzOvertimeFacet.class);

    List<EndScenario> scenarios = new ArrayList<>();

    if (useOvertime) {
      timeLimit.ifPresent(
          duration -> {
            scenarios.add(
                new ObjectivesScenario(
                    gameRound,
                    new TimeFilter(gameRound, duration, NumberComparator.EQUALS),
                    1,
                    Collections.singletonList(
                        gameRound.getFacetRequired(ScoreFacet.class).getObjective()),
                    new BlitzScenario(
                        gameRound,
                        new TimeFilter(gameRound, duration, NumberComparator.EQUALS),
                        boxes)));
            scenarios.add(new BlitzOvertimeScenario(gameRound));
          });
    } else {
      timeLimit.ifPresent(
          duration ->
              scenarios.add(
                  new ObjectivesScenario(
                      gameRound,
                      new TimeFilter(gameRound, duration, NumberComparator.EQUALS),
                      1,
                      Collections.singletonList(
                          gameRound.getFacetRequired(ScoreFacet.class).getObjective()))));
    }

    this.winCalculator =
        new WinCalculator(
            gameRound, Collections.singletonList(scoreFacet.getObjective()), scenarios);

    SidebarFacet.PANE_CREATE_FUNCTION =
        (m, c) ->
            new PaneGroup(Pair.of("points", new DefaultScoreDisplay(m, gameRound, scoreFacet, c)));
  }

  @EventHandler
  public void onPoint(PointChangeEvent event) {
    if (!overtimeFacet.isPresent() || !overtimeFacet.get().isActive()) {
      return;
    }
    winCalculator.check();
  }

  /** Check win calculator on player death for overtime */
  @EventHandler(ignoreCancelled = true)
  public void onPlayerDeath(BlitzOvertimeDeathEvent event) {
    if (!overtimeFacet.isPresent() || !overtimeFacet.get().isActive()) {
      return;
    }
    // Refresh pane when player dies
    SidebarFacet sidebarFacet = gameRound.getFacetRequired(SidebarFacet.class);
    sidebarFacet.update("tdm-alive");
    winCalculator.check();
  }

  /** Track score boxes. */
  @EventHandler(priority = EventPriority.MONITOR)
  public void onMove(PlayerCoarseMoveEvent event) {
    if (!gameRound.getState().playing()) {
      return;
    }

    if (groupsManager.isObservingOrDead(event.getPlayer())) {
      return;
    }

    Optional<Competitor> competitor = groupsManager.getCompetitorOf(event.getPlayer());
    if (!competitor.isPresent()) {
      return;
    }

    for (ScoreBox box : this.boxes) {
      boolean from = box.getRegion().contains(event.getFrom());

      boolean to = box.getRegion().contains(event.getTo());

      if (!to || from) {
        continue;
      }

      if (box.canEnter(event.getPlayer())) {
        continue;
      }

      this.scoreFacet
          .getObjective()
          .modify(
              competitor.get(),
              box.getPoints(),
              box.getPointsAction(),
              Optional.of(event.getPlayer()));

      Player player = event.getPlayer();
      gameRound.getFacetRequired(OCNSpawnManager.class).spawn(player, false);
      groupsManager.playScopedSound(
          player,
          NetworkSoundConstants.Games.OCN.TDM.Score.SELF,
          NetworkSoundConstants.Games.OCN.TDM.Score.TEAM,
          NetworkSoundConstants.Games.OCN.TDM.Score.ENEMY,
          NetworkSoundConstants.Games.OCN.TDM.Score.SPECTATOR);
      gameRound
          .getContainer()
          .broadcast(
              OCNMessages.SCOREBOX_POINT.with(
                  new PersonalizedBukkitPlayer(player),
                  competitor.get().getColoredName(),
                  new LocalizedNumber(box.getPoints())));
      PlayerUtils.fullyEject(player);
      player.setRemainingAir(20);
      PlayerUtils.resetVelocity(player);
      if (box.isHeal()) {
        player.setFireTicks(0);
        player.setHealth(player.getMaxHealth());
        player.setSaturation(20);
        player.setFoodLevel(20);
      }

      break;
    }
  }

  /** Don't allow players to break blocks if not interactive. */
  @EventHandler
  public void onSpawnComplete(PlayerSpawnCompleteEvent event) {
    if (!interactive && !event.getGroup().isObserving()) {
      event.getPlayer().setGameMode(GameMode.ADVENTURE);
    }
  }

  /**
   * Start/Stop the win calculator countdown, works the countdown system. Needed to actually get it
   * to work
   */
  @EventHandler
  public void startCountdown(RoundStateChangeEvent event) {
    winCalculator.stateChanged(event);
  }
}

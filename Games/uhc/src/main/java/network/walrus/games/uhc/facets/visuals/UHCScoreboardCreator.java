package network.walrus.games.uhc.facets.visuals;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.time.Duration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import network.walrus.games.core.facets.group.Competitor;
import network.walrus.games.core.facets.group.CompetitorRule;
import network.walrus.games.core.facets.group.GroupsManager;
import network.walrus.games.core.facets.stats.StatsFacet;
import network.walrus.games.core.facets.stats.trackers.KillTracker;
import network.walrus.games.core.facets.visual.PaneGroup;
import network.walrus.games.uhc.UHCManager;
import network.walrus.games.uhc.UHCMessages;
import network.walrus.games.uhc.facets.border.BorderFacet;
import network.walrus.games.uhc.facets.border.WorldBorder;
import network.walrus.games.uhc.scenarios.Scenario;
import network.walrus.utils.bukkit.visual.display.DisplayManager;
import network.walrus.utils.bukkit.visual.display.DisplayPane;
import network.walrus.utils.bukkit.visual.renderable.StaticRenderable;
import network.walrus.utils.bukkit.visual.renderable.TargetedRenderable;
import network.walrus.utils.core.chat.ChatUtils;
import network.walrus.utils.core.color.NetworkColorConstants.Games.UHC;
import network.walrus.utils.core.color.NetworkColorConstants.Games.UHC.Scoreboard;
import network.walrus.utils.core.text.LocalizedNumber;
import network.walrus.utils.core.text.TruncatedText;
import network.walrus.utils.core.text.UnlocalizedFormat;
import network.walrus.utils.core.text.UnlocalizedText;
import network.walrus.utils.core.translation.Localizable;
import network.walrus.utils.core.util.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.entity.Player;

/**
 * Helper class used to create {@link DisplayPane}s for UHCs.
 *
 * @author Austin Mayes
 */
public class UHCScoreboardCreator {

  /**
   * Create a pane for a specific competitor.
   *
   * @param manager to create the pane in
   * @param competitor who is viewing the pane
   * @return the created pane
   */
  public static PaneGroup create(DisplayManager manager, Optional<Competitor> competitor) {
    PaneGroup pane;
    if (UHCManager.instance.getUHC() != null && UHCManager.instance.getUHC().getState().started()) {
      if (competitor.isPresent()) {
        pane =
            new PaneGroup(
                Pair.of("game-info", new CompetitorInfo(manager, competitor.get())),
                Pair.of("top-kills", new TopKills(manager)));
      } else {
        pane =
            new PaneGroup(
                Pair.of("game-info", new SpectatorInfo(manager)),
                Pair.of("top-kills", new TopKills(manager)));
      }
    } else {
      pane = new PaneGroup(Pair.of("game-info", new GameInfo(manager)));
    }

    return pane;
  }

  abstract static class AbstractGameInfo extends DisplayPane {

    private final BorderFacet borders;

    public AbstractGameInfo(DisplayManager manager) {
      super(manager, 40);
      setTitle(Scoreboard.TITLE.apply(UHCManager.GAME_NAME));
      borders = UHCManager.instance.getUHC().getFacetRequired(BorderFacet.class);
    }

    protected void addGameTime() {
      addElement(
          new StaticRenderable("game-time") {
            @Override
            public Localizable[] text() {
              return new Localizable[] {
                UHCMessages.SCOREBOARD_TIME.with(
                    UHC.Scoreboard.TIME_TEXT,
                    new UnlocalizedText(
                        StringUtils.secondsToClock(
                            (int) UHCManager.instance.getUHC().getPlayingDuration().getSeconds()),
                        UHC.Scoreboard.TIME_CLOCK))
              };
            }
          });
    }

    protected void addCurrentBorder() {
      addElement(
          new StaticRenderable("current-border") {
            @Override
            public Localizable[] text() {
              return new Localizable[] {
                UHCMessages.SCOREBOARD_BORDER.with(
                    UHC.Scoreboard.BORDER_TEXT,
                    new LocalizedNumber(
                        UHCManager.instance
                            .getUHC()
                            .getFacetRequired(BorderFacet.class)
                            .getActiveBorder()
                            .radius,
                        UHC.Scoreboard.BORDER_SIZE))
              };
            }
          });
    }

    protected void addNextBorder() {
      addElement(
          new StaticRenderable("next-border") {
            @Override
            public Localizable[] text() {
              Optional<WorldBorder> next = borders.getNextBorder();
              if (!next.isPresent()) {
                return Localizable.EMPTY;
              }

              return new Localizable[] {
                UHCMessages.SCOREBOARD_NEXT_BORDER.with(
                    UHC.Scoreboard.NEXT_BORDER_TEXT,
                    new LocalizedNumber(next.get().radius, UHC.Scoreboard.NEXT_BORDER_SIZE),
                    new UnlocalizedText(timeToBorder(next.get()), Scoreboard.NEXT_BORDER_TIME))
              };
            }
          });
    }

    private String timeToBorder(WorldBorder border) {
      Duration borderDur = border.duration.minus(UHCManager.instance.getUHC().getPlayingDuration());

      if (borderDur.getSeconds() == 0) {
        return "";
      } else if (borderDur.getSeconds() < 60) {
        return borderDur.getSeconds() + "s";
      } else {
        return borderDur.toMinutes() + "m";
      }
    }

    protected void addPlayerCount(boolean showTotalPlayers) {
      if (showTotalPlayers) {
        addElement(
            new StaticRenderable("players") {
              @Override
              public Localizable[] text() {
                return new Localizable[] {
                  UHCMessages.SCOREBOARD_PLAYERS_TOTAL.with(
                      Scoreboard.PLAYERS_TEXT,
                      new LocalizedNumber(
                          UHCManager.instance.getUHC().playingPlayers().size(),
                          0,
                          0,
                          Scoreboard.PLAYERS_COUNT),
                      new LocalizedNumber(
                          UHCScoreboardListener.getTotalPlayers(), 0, 0, Scoreboard.PLAYERS_TOTAL))
                };
              }
            });
      } else {
        addPlayerCount();
      }
    }

    protected void addPlayerCount() {
      addElement(
          new StaticRenderable("players") {
            @Override
            public Localizable[] text() {
              return new Localizable[] {
                UHCMessages.SCOREBOARD_PLAYERS_NO_TOTAL.with(
                    Scoreboard.PLAYERS_TEXT,
                    new LocalizedNumber(
                        UHCManager.instance.getUHC().playingPlayers().size(),
                        0,
                        0,
                        Scoreboard.PLAYERS_COUNT))
              };
            }
          });
    }

    protected void addScenarios() {
      addElement("scen-header", UHCMessages.SCENARIOS_SCOREBOARD.with(Scoreboard.SCENARIO_HEADER));
      addElement(
          new StaticRenderable("scen-list", (byte) 4) {
            @Override
            public Localizable[] text() {
              UnlocalizedText[] res = new UnlocalizedText[5];
              Set<Scenario> active = UHCManager.instance.getScenarioManager().getActive();
              Iterator<Scenario> iterator = active.iterator();
              for (int i = 0; i < Math.min(5, active.size()); i++) {
                Scenario current = iterator.next();
                res[i] = new UnlocalizedText("• " + current.name(), Scoreboard.SCENARIO_NAME);
              }
              return res;
            }
          });
    }
  }

  static class GameInfo extends AbstractGameInfo {

    public GameInfo(DisplayManager manager) {
      super(manager);
      addPlayerCount();
      addCurrentBorder();
      addSpacer();
      addScenarios();
    }
  }

  static class SpectatorInfo extends AbstractGameInfo {

    public SpectatorInfo(DisplayManager manager) {
      super(manager);
      addGameTime();
      addPlayerCount(true);
      addSpacer();
      addCurrentBorder();
      addNextBorder();
    }
  }

  static class CompetitorInfo extends AbstractGameInfo {

    private final Competitor competitor;
    private final KillTracker kills;

    public CompetitorInfo(DisplayManager manager, Competitor competitor) {
      super(manager);
      this.competitor = competitor;
      this.kills =
          UHCManager.instance
              .getUHC()
              .getFacetRequired(StatsFacet.class)
              .getTracker(KillTracker.class)
              .get();
      addGameTime();
      addSpacer();
      addKills();
      GroupsManager groupsManager =
          UHCManager.instance.getUHC().getFacetRequired(GroupsManager.class);
      if (groupsManager.getCompetitorRule() == CompetitorRule.TEAM) {
        addTeamKills();
      }
      addPlayerCount(true);
      addSpacer();
      addCurrentBorder();
      addNextBorder();
    }

    private void addKills() {
      addElement(
          new TargetedRenderable("kills") {
            @Override
            public String[] text(Player player) {
              Localizable text =
                  UHCMessages.SCOREBOARD_PLAYER_KILLS.with(
                      Scoreboard.PLAYER_KILL_TEXT,
                      new LocalizedNumber(
                          kills.getKills(player), 0, 0, Scoreboard.PLAYER_KILL_COUNT));

              return new String[] {ChatUtils.cleanColorCodes(text.toLegacyText(player))};
            }
          });
    }

    private void addTeamKills() {
      addElement(
          new StaticRenderable("team-kills") {
            @Override
            public Localizable[] text() {
              return new Localizable[] {
                UHCMessages.SCOREBOARD_TEAM_KILLS.with(
                    Scoreboard.TEAM_KILL_TEXT,
                    new LocalizedNumber(
                        kills.getKills(competitor), 0, 0, Scoreboard.TEAM_KILL_COUNT))
              };
            }
          });
    }
  }

  static class TopKills extends DisplayPane {

    private final KillTracker killTracker;

    public TopKills(DisplayManager manager) {
      super(manager, 20);
      this.killTracker =
          UHCManager.instance
              .getUHC()
              .getFacetRequired(StatsFacet.class)
              .getTracker(KillTracker.class)
              .get();
      setTitle(UHCMessages.KILL_TOP_HEADER.with(Scoreboard.KILLS_BOARD_HEADER));
      addTopKills();
    }

    private void addTopKills() {
      addElement(
          new StaticRenderable("top-kills", (byte) 6) {
            @Override
            public Localizable[] text() {
              Localizable[] kills = new Localizable[6];
              Set<Entry<Localizable, Integer>> sortedKills = getSortedKills().entrySet();
              int i = 0;
              UnlocalizedFormat entryFormat = new UnlocalizedFormat("{0}: {1}");
              for (Entry<Localizable, Integer> entry : sortedKills) {
                if (entry.getValue() == 0) {
                  continue;
                }

                kills[i] =
                    entryFormat.with(
                        Scoreboard.KILLS_BOARD_TEXT,
                        new TruncatedText(entry.getKey(), 24),
                        new LocalizedNumber(entry.getValue(), Scoreboard.KILLS_BOARD_COUNT));
                i++;
                if (i > kills.length - 1) {
                  break;
                }
              }

              return kills;
            }
          });
    }

    private Map<Localizable, Integer> getSortedKills() {
      Map<Localizable, Integer> kills = killTracker.getOfflineKills();
      kills.put(
          UHCMessages.SCOREBOARD_ENVIRONMENT_DEATH.with(Scoreboard.ENV_KILLS),
          killTracker.getEnvironmentKills());

      List<Entry<Localizable, Integer>> entries = Lists.newArrayList(kills.entrySet());
      entries.sort(Entry.comparingByValue((i1, i2) -> Integer.compare(i2, i1)));

      Map<Localizable, Integer> sortedKills = Maps.newLinkedHashMap();
      for (Entry<Localizable, Integer> entry : entries) {
        sortedKills.put(entry.getKey(), entry.getValue());
      }

      return sortedKills;
    }
  }
}

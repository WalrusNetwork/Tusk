package network.walrus.games.core.api.results;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import net.md_5.bungee.api.chat.BaseComponent;
import network.walrus.games.core.GamesCoreMessages;
import network.walrus.games.core.events.competitor.CompetitorPlaceEvent;
import network.walrus.games.core.events.competitor.CompetitorWinEvent;
import network.walrus.games.core.events.round.RoundCompleteEvent;
import network.walrus.games.core.facets.group.Competitor;
import network.walrus.games.core.facets.group.GroupsManager;
import network.walrus.games.core.round.GameRound;
import network.walrus.games.core.util.EventUtil;
import network.walrus.ubiquitous.bukkit.UbiquitousBukkitPlugin;
import network.walrus.ubiquitous.bukkit.compat.CompatTitleScreen;
import network.walrus.utils.bukkit.sound.NetworkSoundConstants.Games.OCN.Results.Win;
import network.walrus.utils.core.color.NetworkColorConstants.Games;
import network.walrus.utils.core.text.LocalizableFormat;
import network.walrus.utils.core.text.LocalizedNumber;
import network.walrus.utils.core.text.UnlocalizedFormat;
import network.walrus.utils.core.text.UnlocalizedText;
import network.walrus.utils.core.translation.Localizable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.github.paperspigot.Title;

/**
 * Utilies used to render round end results with winners.
 *
 * @author Austin Mayes
 */
public class ResultUtils {

  /**
   * Handle a competitor winning a round, calling the event and sending the UI alerts.
   *
   * @param round the competitor is winning
   * @param competitor who won
   */
  public static void handleWin(GameRound round, Competitor competitor) {
    CompetitorWinEvent event = new CompetitorWinEvent(round, competitor);
    EventUtil.call(event);

    broadcastWinners(round, Collections.singletonList(competitor));
  }

  private static void broadcastWin(GameRound round, Competitor competitor) {
    Localizable wins = GamesCoreMessages.UI_WINS.with(competitor.getColoredName());

    for (Player player : Bukkit.getOnlinePlayers()) {

      BaseComponent subtitle =
          GamesCoreMessages.UI_SPEC_JOIN_NEXT.with(Games.Results.JOIN_NEXT).render(player);

      if (round.getFacetRequired(GroupsManager.class).getCompetitorOf(player).isPresent()) {
        subtitle = GamesCoreMessages.UI_TEAM_LOST.with(Games.Results.TEAM_LOST).render(player);
      }

      if (competitor.hasPlayer(player)) {
        subtitle = GamesCoreMessages.UI_TEAM_WON.with(Games.Results.TEAM_WON).render(player);
      }

      CompatTitleScreen titleManager =
          UbiquitousBukkitPlugin.getInstance().getCompatManager().getCompatTitleScreen();
      if (!titleManager.isLegacy(player)) {
        Title title =
            Title.builder()
                .title(wins.render(player))
                .subtitle(subtitle)
                .fadeIn(10)
                .stay(60)
                .fadeOut(20)
                .build();
        titleManager.sendTitle(player, title);
      } else {
        player.sendMessage(wins);
        player.sendMessage(subtitle);
      }
    }
    round.getContainer().broadcast(wins);
  }

  /**
   * Broadcast, and call events for, who won a specific {@link GameRound}.
   *
   * @param round which is being won
   * @param winners to broadcast
   */
  public static void broadcastWinners(GameRound round, List<Competitor> winners) {
    List<Player> winSoundReceivers = new ArrayList<>();

    for (Competitor competitor : winners) {
      CompetitorWinEvent event = new CompetitorWinEvent(round, competitor);
      EventUtil.call(event);
      if (winners.size() == 1) {
        broadcastWin(round, competitor);
      }
      winSoundReceivers.addAll(competitor.getPlayers());
    }

    for (Player winSoundReceiver : winSoundReceivers) {
      Win.WIN.play(winSoundReceiver);
    }

    for (Player player : Bukkit.getOnlinePlayers()) {
      if (winSoundReceivers.contains(player)) {
        continue;
      }

      Win.LOST.play(player);
    }

    if (winners.size() > 1) {
      int place = 1;
      List<Localizable> res = new ArrayList<>();
      for (Competitor competitor : winners) {
        LocalizableFormat placeFormat = new UnlocalizedFormat("{0}. ");

        res.add(
            new UnlocalizedText(
                "{0} {1}",
                placeFormat.with(new LocalizedNumber(place)), competitor.getColoredName()));
        place++;
      }
      final Localizable translation = GamesCoreMessages.UI_WINNERS.with(Games.Results.WINNER_TEXT);
      translation.style().padded().padStyle(Games.Results.WINNERS_LINE);
      round.getContainer().broadcast(translation);
      for (Localizable r : res) {
        round.getContainer().broadcast(r);
      }
    }

    RoundCompleteEvent event =
        new RoundCompleteEvent(
            round, round.getFacetRequired(GroupsManager.class).getCompetitors(), winners);
    EventUtil.call(event);
  }

  /**
   * Handle multiple competitors winning a round using data from a {@link RankingDisplay}.
   *
   * @param round which is being won
   * @param display to pull ranking data from
   */
  public static void handleMultiWin(GameRound round, RankingDisplay display) {
    for (Map.Entry<Integer, HashSet<Competitor>> entry : display.getRanking().entrySet()) {
      for (Competitor competitor : entry.getValue()) {
        CompetitorPlaceEvent event =
            new CompetitorPlaceEvent(
                round, competitor, display.getRanking().headMap(entry.getKey()).size());
        EventUtil.call(event);
      }
    }

    final Localizable translation = GamesCoreMessages.UI_WINNERS.with(Games.Results.WINNER_TEXT);
    translation.style().padded().padStyle(Games.Results.WINNERS_LINE);
    round.getContainer().broadcast(translation);
    for (Localizable m : display.getRankDisplay()) {
      round.getContainer().broadcast(m);
    }
  }
}

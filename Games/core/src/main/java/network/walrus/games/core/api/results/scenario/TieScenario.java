package network.walrus.games.core.api.results.scenario;

import network.walrus.games.core.GamesCoreMessages;
import network.walrus.games.core.facets.filters.FilterResult;
import network.walrus.games.core.facets.filters.StaticResultFilter;
import network.walrus.games.core.round.GameRound;
import network.walrus.ubiquitous.bukkit.UbiquitousBukkitPlugin;
import network.walrus.ubiquitous.bukkit.compat.CompatTitleScreen;
import network.walrus.utils.bukkit.sound.NetworkSoundConstants.Games.Results;
import network.walrus.utils.core.color.NetworkColorConstants.Games;
import network.walrus.utils.core.translation.Localizable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.github.paperspigot.Title;

/**
 * Internal scenario to handle when holder passes but no winner can be clearly decided.
 *
 * @author Austin Mayes
 */
public class TieScenario extends EndScenario {

  /** @param round that the scenario is executing in */
  public TieScenario(GameRound round) {
    super(round, new StaticResultFilter(FilterResult.ALLOW), 0);
  }

  @Override
  public void execute() {
    getRound().end();
    Localizable tie = GamesCoreMessages.UI_TIE.with(Games.Results.TIE);

    Results.TIE.play(Bukkit.getOnlinePlayers());

    for (Player player : Bukkit.getOnlinePlayers()) {
      CompatTitleScreen titleManager =
          UbiquitousBukkitPlugin.getInstance().getCompatManager().getCompatTitleScreen();
      if (!titleManager.isLegacy(player)) {
        Title title =
            Title.builder().title(tie.render(player)).fadeIn(10).stay(60).fadeOut(20).build();
        titleManager.sendTitle(player, title);
      } else {
        player.sendMessage(tie);
      }
    }
    getRound().getContainer().broadcast(tie);
  }
}

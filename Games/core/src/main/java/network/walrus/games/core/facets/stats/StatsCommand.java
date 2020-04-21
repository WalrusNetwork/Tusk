package network.walrus.games.core.facets.stats;

import app.ashcon.intake.Command;
import app.ashcon.intake.bukkit.parametric.Type;
import app.ashcon.intake.bukkit.parametric.annotation.Fallback;
import app.ashcon.intake.bukkit.parametric.annotation.Sender;
import gg.walrus.javaapiclient.UserUpdateAresStatsMutation.UpdateAresStats;
import java.util.Optional;
import net.md_5.bungee.api.chat.BaseComponent;
import network.walrus.games.core.GamesCoreMessages;
import network.walrus.games.core.util.GameTask;
import network.walrus.utils.bukkit.translation.text.player.PersonalizedBukkitPlayer;
import network.walrus.utils.core.color.NetworkColorConstants.Games.OCN.Stats;
import network.walrus.utils.core.text.LocalizedNumber;
import network.walrus.utils.core.text.UnlocalizedFormat;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import network.walrus.utils.parsing.facet.parse.configurator.command.FacetCommandContainer;
import org.bukkit.entity.Player;

/**
 * Handles the /stats command.
 *
 * @author Rafi Baum
 */
public class StatsCommand extends FacetCommandContainer<StatsFacet> {

  private static final UnlocalizedFormat statFormat = new UnlocalizedFormat("{0}: {1}");

  private final FacetHolder holder;
  private final StatsFacet facet;

  public StatsCommand(FacetHolder holder, StatsFacet facet) {
    super(holder, facet);
    this.holder = holder;
    this.facet = facet;
  }

  @Command(
      aliases = {"stats"},
      desc = "See a player's stats")
  public void stats(@Sender Player sender, @Fallback(Type.SELF) Player target) {
    // Pre-compute header in case target logs off
    BaseComponent header =
        GamesCoreMessages.STATS_HEADER
            .with(Stats.HEADER, new PersonalizedBukkitPlayer(target))
            .render(sender);

    facet.updateStatsFor(target, Optional.of((stats) -> sendStats(sender, header, stats)));
  }

  private void sendStats(Player receiver, BaseComponent header, UpdateAresStats stats) {
    GameTask.of(
            "send-stats",
            () -> {
              if (!receiver.isOnline()) {
                return;
              }

              receiver.sendMessage(header);
              receiver.sendMessage(
                  statFormat.with(
                      Stats.ITEM,
                      GamesCoreMessages.KILLS.with(Stats.ITEM_NAME),
                      new LocalizedNumber(stats.ares().kills(), Stats.ITEM_VALUE)));
              receiver.sendMessage(
                  statFormat.with(
                      Stats.ITEM,
                      GamesCoreMessages.DEATHS.with(Stats.ITEM_NAME),
                      new LocalizedNumber(stats.ares().deaths(), Stats.ITEM_VALUE)));

              double kdr = stats.ares().kills();
              if (stats.ares().deaths() != 0) {
                kdr /= stats.ares().deaths();
              }
              receiver.sendMessage(
                  statFormat.with(
                      Stats.ITEM,
                      GamesCoreMessages.KDR.with(Stats.ITEM_NAME),
                      new LocalizedNumber(kdr, 1, 3, Stats.ITEM_VALUE)));

              receiver.sendMessage(
                  statFormat.with(
                      Stats.ITEM,
                      GamesCoreMessages.FLAGS.with(Stats.ITEM_NAME),
                      new LocalizedNumber(stats.ares().flags(), Stats.ITEM_VALUE)));
              receiver.sendMessage(
                  statFormat.with(
                      Stats.ITEM,
                      GamesCoreMessages.WOOLS.with(Stats.ITEM_NAME),
                      new LocalizedNumber(stats.ares().wools(), Stats.ITEM_VALUE)));
            })
        .now();
  }
}

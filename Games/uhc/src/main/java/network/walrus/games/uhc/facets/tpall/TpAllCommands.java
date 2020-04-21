package network.walrus.games.uhc.facets.tpall;

import app.ashcon.intake.Command;
import app.ashcon.intake.bukkit.parametric.annotation.Sender;
import java.time.Duration;
import java.util.Optional;
import network.walrus.games.uhc.UHCMessages;
import network.walrus.games.uhc.UHCPermissions;
import network.walrus.utils.core.color.NetworkColorConstants.Games.UHC.TpAll;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import network.walrus.utils.parsing.facet.parse.configurator.command.FacetCommandContainer;
import org.bukkit.entity.Player;

/**
 * Commands for the {@link TpAllFacet}.
 *
 * @author Rafi Baum
 */
public class TpAllCommands extends FacetCommandContainer<TpAllFacet> {

  private final FacetHolder holder;
  private final TpAllFacet tpAll;

  public TpAllCommands(FacetHolder holder, TpAllFacet facet) {
    super(holder, facet);
    this.holder = holder;
    this.tpAll = facet;
  }

  @Command(
      aliases = "tpall",
      desc = "TP's to through each player in the match every x number of seconds",
      perms = UHCPermissions.TP_ALL)
  public void tpAll(@Sender Player player, Optional<Duration> optionalDuration) {
    // If player is tping and no duration, stop tpall
    if (!optionalDuration.isPresent() && tpAll.isTping(player)) {
      tpAll.stopTp(player);
      player.sendMessage(UHCMessages.TPALL_STOP.with(TpAll.TP_ALL));
      return;
    }

    // Otherwise, change tpall duration or start
    Duration duration = optionalDuration.orElse(Duration.ofSeconds(8));
    if (tpAll.isTping(player)) {
      tpAll.stopTp(player);
    }

    tpAll.startTp(player, duration);
    player.sendMessage(UHCMessages.TPALL_START.with(TpAll.TP_ALL));
  }
}

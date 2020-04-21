package network.walrus.games.uhc.facets.revive;

import app.ashcon.intake.Command;
import app.ashcon.intake.CommandException;
import app.ashcon.intake.bukkit.parametric.annotation.Sender;
import app.ashcon.intake.parametric.annotation.Switch;
import java.util.Optional;
import java.util.logging.Level;
import network.walrus.games.uhc.UHCManager;
import network.walrus.games.uhc.UHCMessages;
import network.walrus.games.uhc.UHCPermissions;
import network.walrus.utils.bukkit.logging.TranslatableLogRecord;
import network.walrus.utils.bukkit.translation.text.player.PersonalizedBukkitPlayer;
import network.walrus.utils.core.color.NetworkColorConstants.Games.UHC.Revive;
import network.walrus.utils.core.command.exception.TranslatableCommandErrorException;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import network.walrus.utils.parsing.facet.parse.configurator.command.FacetCommandContainer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReviveCommands extends FacetCommandContainer<ReviveFacet> {

  private final FacetHolder holder;
  private final ReviveFacet facet;

  public ReviveCommands(FacetHolder holder, ReviveFacet facet) {
    super(holder, facet);
    this.holder = holder;
    this.facet = facet;
  }

  @Command(
      aliases = {"revive", "rv"},
      desc = "Return a player to the match and restore their inventory",
      perms = UHCPermissions.REVIVE)
  public void revive(@Sender CommandSender sender, Player player, @Switch('t') boolean teleport)
      throws CommandException {
    boolean result;
    if (teleport && sender instanceof Player) {
      result = facet.revivePlayer(player, Optional.of(((Player) sender).getLocation()));
    } else {
      result = facet.revivePlayer(player);
    }

    if (!result) {
      throw new TranslatableCommandErrorException(UHCMessages.REVIVE_ERROR);
    } else {
      Bukkit.broadcast(
          UHCMessages.REVIVE_BROADCAST.with(
              Revive.BROADCAST, new PersonalizedBukkitPlayer(player)));
      UHCManager.instance
          .hostLogger()
          .log(
              new TranslatableLogRecord(
                  Level.INFO,
                  UHCMessages.REVIVE_HOST_ALERT.with(
                      Revive.HOST_ALERT,
                      new PersonalizedBukkitPlayer(player),
                      new PersonalizedBukkitPlayer(sender))));
    }
  }
}

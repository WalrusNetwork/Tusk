package network.walrus.games.uhc.commands;

import app.ashcon.intake.Command;
import app.ashcon.intake.bukkit.parametric.annotation.Sender;
import java.util.Optional;
import network.walrus.games.uhc.UHCManager;
import network.walrus.games.uhc.UHCMessages;
import network.walrus.utils.bukkit.translation.text.player.PersonalizedBukkitPlayer;
import network.walrus.utils.core.color.NetworkColorConstants;
import network.walrus.utils.core.color.NetworkColorConstants.Games.UHC.PlayerInfo;
import network.walrus.utils.core.text.LocalizedNumber;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Commands used to view player attributes.
 *
 * @author Austin Mayes
 */
public class PlayerInformationCommands {

  /** View other players' heath */
  @Command(
      aliases = {"health", "viewhealth", "vh", "showhealth", "sh", "hearts", "playerhealth", "ph"},
      desc = "View other players' heath")
  public void health(@Sender CommandSender sender, Optional<Player> target) {
    if (target.isPresent()) {
      Player player = target.get();
      sender.sendMessage(
          UHCMessages.PLAYER_HEALTH.with(
              PlayerInfo.HEALTH_TEXT,
              new PersonalizedBukkitPlayer(player),
              new LocalizedNumber(
                  player.getHealth() / 2,
                  NetworkColorConstants.Games.UHC.PlayerInfo.HEALTH_NUMBER)));
    } else {
      for (Player player : UHCManager.instance.getUHC().players()) {
        sender.sendMessage(
            UHCMessages.PLAYER_HEALTH.with(
                PlayerInfo.HEALTH_TEXT,
                new PersonalizedBukkitPlayer(player),
                new LocalizedNumber(player.getHealth() / 2, PlayerInfo.HEALTH_NUMBER)));
      }
    }
  }
}

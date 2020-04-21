package network.walrus.ubiquitous.bukkit.countdown;

import app.ashcon.intake.Command;
import app.ashcon.intake.bukkit.parametric.annotation.Sender;
import network.walrus.ubiquitous.bukkit.UbiquitousBukkitPlugin;
import network.walrus.ubiquitous.bukkit.UbiquitousPermissions;
import network.walrus.utils.core.color.NetworkColorConstants.Countdowns;
import org.bukkit.command.CommandSender;

/**
 * Commands for managing countdowns.
 *
 * @author Austin Mayes
 */
public class CountdownCommands {

  /** Cancel all countdowns. */
  @Command(
      aliases = {"cancel", "cancelall", "stopall", "c", "ca", "sa"},
      desc = "Cancel all running countdowns.",
      perms = UbiquitousPermissions.CANCEL_COUNTDOWNS)
  public void cancel(@Sender CommandSender sender) {
    UbiquitousBukkitPlugin.getInstance().getCountdownManager().cancelAll();
    sender.sendMessage(Countdowns.CANCELED.apply("All countdowns canceled!"));
  }
}

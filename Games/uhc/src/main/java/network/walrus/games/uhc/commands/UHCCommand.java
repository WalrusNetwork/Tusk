package network.walrus.games.uhc.commands;

import app.ashcon.intake.Command;
import app.ashcon.intake.bukkit.parametric.annotation.Sender;
import org.bukkit.command.CommandSender;

/**
 * Command alias for viewing the config.
 *
 * @author Rafi Baum
 */
public class UHCCommand {

  private final ConfigCommands commands;

  public UHCCommand(ConfigCommands commands) {
    this.commands = commands;
  }

  /** View the config */
  @Command(aliases = "uhc", desc = "View the config")
  public void view(@Sender CommandSender sender) {
    commands.view(sender);
  }
}

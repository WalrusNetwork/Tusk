package network.walrus.ubiquitous.bukkit.command.exception;

import network.walrus.ubiquitous.bukkit.UbiquitousMessages;
import network.walrus.utils.core.command.exception.TranslatableCommandErrorException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * An exception which is thrown when the console tries to execute a command that can only be ran by
 * players.
 *
 * @author Austin Mayes
 */
public final class MustBePlayerCommandException extends TranslatableCommandErrorException {

  private MustBePlayerCommandException() {
    super(UbiquitousMessages.ERROR_MUST_BE_PLAYER);
  }

  /**
   * Test if the specified command source is a {@link Player}.
   *
   * @param source the command source
   * @return the player
   * @throws MustBePlayerCommandException if the command source is not a {@link Player}
   */
  public static Player ensurePlayer(CommandSender source) throws MustBePlayerCommandException {
    if (!(source instanceof Player)) {
      throw new MustBePlayerCommandException();
    }
    return (Player) source;
  }
}

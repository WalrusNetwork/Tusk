package network.walrus.games.uhc.facets.whitelist;

import app.ashcon.intake.Command;
import app.ashcon.intake.bukkit.parametric.annotation.Sender;
import java.time.Duration;
import network.walrus.games.uhc.UHCMessages;
import network.walrus.games.uhc.UHCPermissions;
import network.walrus.utils.bukkit.sound.NetworkSoundConstants.Games.UHC;
import network.walrus.utils.core.color.NetworkColorConstants.Games.UHC.Whitelist;
import network.walrus.utils.core.command.exception.TranslatableCommandErrorException;
import network.walrus.utils.core.text.UnlocalizedText;
import network.walrus.utils.core.util.StringUtils;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import network.walrus.utils.parsing.facet.parse.configurator.command.FacetCommandContainer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Commands for managing the whitelist.
 *
 * @author Austin Mayes
 */
public class WhitelistCommands extends FacetCommandContainer<WhitelistAutomationFacet> {

  /**
   * @param holder which this object is inside of
   * @param facet which this object is bound to
   */
  public WhitelistCommands(FacetHolder holder, WhitelistAutomationFacet facet) {
    super(holder, facet);
  }

  /**
   * Whitelist all current online players
   *
   * @param sender who is performing the command
   */
  @Command(
      aliases = {"wla", "whitelistall", "wlall"},
      desc = "Whitelist all current online players",
      perms = UHCPermissions.WHITELIST_ALL_PERM)
  public void whitelistAll(@Sender CommandSender sender) {
    for (Player p : Bukkit.getOnlinePlayers()) {
      p.setWhitelisted(true);
    }
    sender.sendMessage(UHCMessages.WHITELIST_ADD_ALL.with(Whitelist.ADDED_ALL));
  }

  /**
   * Clear the whitelist
   *
   * @param sender who is performing the command
   */
  @Command(
      aliases = {"whitelistclear", "wlclear"},
      desc = "Clear the whitelist",
      perms = UHCPermissions.WHITELIST_CLEAR_PERM)
  public void whitelistClear(@Sender CommandSender sender) {
    for (OfflinePlayer p : Bukkit.getWhitelistedPlayers()) {
      if (!p.isOp()) {
        p.setWhitelisted(false);
      }
    }
    sender.sendMessage(UHCMessages.WHITELIST_CLEAR.with(Whitelist.CLEARED));
    UHC.Whitelist.CLEARED.play(Bukkit.getOnlinePlayers());
  }

  /**
   * Turn off the whitelist for a specified amount of time
   *
   * @param sender who is performing the command
   * @param duration that the whitelist should be off
   */
  @Command(
      aliases = {"wlo", "whitelistoff", "wloff"},
      desc = "Turn off the whitelist for a specified amount of time",
      perms = UHCPermissions.WHITELIST_OFF_PERM)
  public void whitelistOff(@Sender CommandSender sender, Duration duration)
      throws TranslatableCommandErrorException {
    sender.sendMessage(
        UHCMessages.WHITELIST_OFF.with(
            Whitelist.OFF,
            new UnlocalizedText(StringUtils.secondsToClock((int) duration.getSeconds()))));
    UHC.Whitelist.OFF.play(sender);
    getFacet().startCountdown(duration);
  }

  /**
   * Cancel the whitelist countdown
   *
   * @param sender who is performing the command
   */
  @Command(
      aliases = {"wlc", "whitelistcancel", "wlcancel"},
      desc = "Cancel the whitelist countdown",
      perms = UHCPermissions.WHITELIST_CANCEL_PERM)
  public void whitelistCancel(@Sender CommandSender sender)
      throws TranslatableCommandErrorException {
    sender.sendMessage(UHCMessages.WHITELIST_CANCELLED.with(Whitelist.CANCEL));
    UHC.Whitelist.CANCELLED.play(sender);
    getFacet().stopCountdown();
  }
}

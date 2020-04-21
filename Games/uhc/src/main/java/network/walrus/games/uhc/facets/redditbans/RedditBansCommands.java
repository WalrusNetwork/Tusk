package network.walrus.games.uhc.facets.redditbans;

import app.ashcon.intake.Command;
import app.ashcon.intake.bukkit.parametric.annotation.Sender;
import java.util.UUID;
import java.util.logging.Level;
import network.walrus.games.uhc.UHCManager;
import network.walrus.games.uhc.UHCMessages;
import network.walrus.games.uhc.UHCPermissions;
import network.walrus.utils.bukkit.logging.TranslatableLogRecord;
import network.walrus.utils.bukkit.translation.text.player.PersonalizedBukkitPlayer;
import network.walrus.utils.core.color.NetworkColorConstants.Games.UHC.RedditBans;
import network.walrus.utils.core.text.UnlocalizedText;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import network.walrus.utils.parsing.facet.parse.configurator.command.FacetCommandContainer;
import org.bukkit.command.CommandSender;

/**
 * Container for commands related to UBL functionality
 *
 * @author Rafi Baum
 */
public class RedditBansCommands extends FacetCommandContainer<RedditBansFacet> {

  public RedditBansCommands(FacetHolder holder, RedditBansFacet facet) {
    super(holder, facet);
  }

  @Command(
      aliases = "check",
      desc = "Checks Reddit ban list for bans.",
      perms = UHCPermissions.UBL_CHECK)
  public void checkUUID(@Sender CommandSender sender, String sUuid) {
    if (getFacet().isBanned(UUID.fromString(sUuid))) {
      sender.sendMessage(UHCMessages.REDDIT_CHECK_BAN.with(RedditBans.UUID_CHECK));
    } else {
      sender.sendMessage(UHCMessages.REDDIT_CHECK_NOT_BANNED.with(RedditBans.UUID_CHECK));
    }
  }

  @Command(
      aliases = "exempt",
      desc = "Exempts/unexempts a player from the UBL",
      perms = UHCPermissions.UBL_EXEMPT_MANAGE)
  public void exemptUUID(@Sender CommandSender sender, String sUuid) {
    UUID uuid = UUID.fromString(sUuid);
    if (getFacet().isExempt(uuid)) {
      getFacet().removeExemptedPlayer(uuid);
      sender.sendMessage(UHCMessages.REDDIT_UNEXEMPTED.with(RedditBans.EXEMPT_ALERT));
      UHCManager.instance
          .hostLogger()
          .log(
              new TranslatableLogRecord(
                  Level.INFO,
                  UHCMessages.REDDIT_UNEXEMPTED_HOSTS.with(
                      RedditBans.EXEMPT_HOST_ALERT,
                      new PersonalizedBukkitPlayer(sender),
                      new UnlocalizedText(sUuid))));
    } else {
      getFacet().addExemptedPlayer(uuid);
      sender.sendMessage(UHCMessages.REDDIT_EXEMPTED.with(RedditBans.EXEMPT_ALERT));
      UHCManager.instance
          .hostLogger()
          .log(
              new TranslatableLogRecord(
                  Level.INFO,
                  UHCMessages.REDDIT_EXEMPTED_HOSTS.with(
                      RedditBans.EXEMPT_HOST_ALERT,
                      new PersonalizedBukkitPlayer(sender),
                      new UnlocalizedText(sUuid))));
    }
  }

  @Override
  public String[] rootAlias() {
    return new String[] {"ubl"};
  }
}

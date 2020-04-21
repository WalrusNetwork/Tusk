package network.walrus.games.uhc.commands;

import app.ashcon.intake.Command;
import app.ashcon.intake.bukkit.parametric.Type;
import app.ashcon.intake.bukkit.parametric.annotation.Fallback;
import app.ashcon.intake.bukkit.parametric.annotation.Sender;
import app.ashcon.intake.parametric.annotation.Text;
import java.util.logging.Level;
import network.walrus.games.core.facets.group.Competitor;
import network.walrus.games.core.facets.group.Group;
import network.walrus.games.uhc.UHCManager;
import network.walrus.games.uhc.UHCMessages;
import network.walrus.games.uhc.UHCPermissions;
import network.walrus.games.uhc.facets.groups.TeamsManager;
import network.walrus.games.uhc.facets.groups.UHCGroupsManager;
import network.walrus.utils.bukkit.PlayerUtils;
import network.walrus.utils.bukkit.logging.TranslatableLogRecord;
import network.walrus.utils.bukkit.sound.NetworkSoundConstants.Games.UHC.Moderation.Disqualify;
import network.walrus.utils.bukkit.sound.NetworkSoundConstants.Games.UHC.Moderation.HelpOp;
import network.walrus.utils.bukkit.translation.text.player.PersonalizedBukkitPlayer;
import network.walrus.utils.core.color.NetworkColorConstants;
import network.walrus.utils.core.color.NetworkColorConstants.Games.UHC.Hosts.Disqualified;
import network.walrus.utils.core.color.NetworkColorConstants.Games.UHC.Hosts.ForceScatter;
import network.walrus.utils.core.command.exception.TranslatableCommandErrorException;
import network.walrus.utils.core.text.UnlocalizedText;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Commands which aid in UHC moderation.
 *
 * @author Austin Mayes
 */
public class ModerationCommands {

  /** Clear the chat */
  @Command(
      aliases = {"clearchat", "cc"},
      desc = "Clear the chat",
      perms = UHCPermissions.CHAT_CLEAR_PERM)
  public void clearChat(@Sender CommandSender sender) {
    for (int i = 0; i < 50; i++) {
      Bukkit.broadcastMessage("");
    }
  }

  /** Send a message to online staff */
  @Command(
      aliases = {
        "helpop", "ho", "pmmods", "pmstaff", "pmhosts", "pms", "pmm", "pmh", "helpme", "911", "999",
        "helpme"
      },
      desc = "Send a message to online staff")
  public void helpOp(@Sender Player player, @Text String message) {
    UHCManager.instance
        .hostLogger()
        .log(
            new TranslatableLogRecord(
                Level.INFO,
                UHCMessages.HELPOP_ALERT.with(
                    NetworkColorConstants.Games.UHC.Hosts.HelpOp.NOTIFICATION,
                    new PersonalizedBukkitPlayer(player),
                    new UnlocalizedText(message)),
                HelpOp.ALERT));
    HelpOp.SENT.play(player);
    player.sendMessage(
        UHCMessages.HELPOP_SENT.with(NetworkColorConstants.Games.UHC.Hosts.HelpOp.SENT));
  }

  /** Disqualify a player */
  @Command(
      aliases = {"disqualify", "dq"},
      desc = "Disqualify a player",
      perms = UHCPermissions.DISQUALIFY_PERM)
  public void disqualify(@Sender CommandSender sender, Player player) {
    player.setHealth(0.0);
    player.sendMessage(
        UHCMessages.prefix(UHCMessages.PLAYER_DISQUALIFIED.with(Disqualified.NOTIFICATION)));
    Disqualify.PLAYER.play(player);
    Bukkit.broadcast(
        UHCMessages.DISQUALIFIED_ALERT.with(
            Disqualified.ALERT, new PersonalizedBukkitPlayer(player)));

    UHCManager.instance
        .hostLogger()
        .log(
            new TranslatableLogRecord(
                Level.INFO,
                UHCMessages.HOST_DISQUALIFIED_ALERT.with(
                    Disqualified.HOST_ALERT,
                    new PersonalizedBukkitPlayer(sender),
                    new PersonalizedBukkitPlayer(player)),
                Disqualify.ALERT));
  }

  /** Force a player to join the match and be scattered. */
  @Command(
      aliases = {"forcescatter", "fs"},
      desc = "Forces a player to join the match and be scattered",
      perms = UHCPermissions.FORCE_SCATTER)
  public void forceScatter(
      @Sender CommandSender sender, Player player, @Fallback(Type.NULL) Player teamMate)
      throws TranslatableCommandErrorException {
    if (UHCManager.instance.getUHC() == null
        || !UHCManager.instance.getUHC().getState().playing()) {
      throw new TranslatableCommandErrorException(UHCMessages.SPAWN_BEFORE_SCATTER_ERROR);
    }

    UHCGroupsManager groupsManager =
        UHCManager.instance.getUHC().getFacetRequired(UHCGroupsManager.class);

    if (groupsManager.isSpectator(player)) {
      if (teamMate != null && groupsManager instanceof TeamsManager) {
        Group toJoin = groupsManager.getGroup(teamMate);
        groupsManager.changeGroup(player, toJoin, false, false);
      } else {
        groupsManager.addPlayer(player);
      }
    }

    Competitor competitor = groupsManager.getCompetitorOf(player).get();
    UHCManager.instance.getSpawnManager().forceSpawn(competitor);
    PlayerUtils.reset(player);
    ItemStack starter =
        UHCManager.instance.getConfig().starterFood.get().getValue() > 0
            ? new ItemStack(
                UHCManager.instance.getConfig().starterFood.get().getKey(),
                UHCManager.instance.getConfig().starterFood.get().getValue())
            : null;
    if (starter != null) {
      player.getInventory().addItem(starter);
    }

    UHCManager.instance
        .hostLogger()
        .log(
            new TranslatableLogRecord(
                Level.INFO,
                UHCMessages.HOST_SCATTERED_ALERT.with(
                    ForceScatter.HOST_ALERT,
                    new PersonalizedBukkitPlayer(sender),
                    new PersonalizedBukkitPlayer(player))));
  }
}

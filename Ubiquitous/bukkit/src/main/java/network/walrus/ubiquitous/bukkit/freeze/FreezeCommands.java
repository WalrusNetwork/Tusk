package network.walrus.ubiquitous.bukkit.freeze;

import app.ashcon.intake.Command;
import app.ashcon.intake.CommandException;
import app.ashcon.intake.bukkit.parametric.annotation.Sender;
import java.util.logging.Level;
import network.walrus.ubiquitous.bukkit.UbiquitousBukkitPlugin;
import network.walrus.ubiquitous.bukkit.UbiquitousMessages;
import network.walrus.ubiquitous.bukkit.UbiquitousPermissions;
import network.walrus.ubiquitous.bukkit.compat.CompatTitleScreen;
import network.walrus.utils.bukkit.logging.TranslatableLogRecord;
import network.walrus.utils.bukkit.sound.NetworkSoundConstants.Freeze.Frozen;
import network.walrus.utils.bukkit.sound.NetworkSoundConstants.Freeze.Thawed;
import network.walrus.utils.bukkit.translation.text.player.PersonalizedBukkitPlayer;
import network.walrus.utils.core.color.NetworkColorConstants.FREEZE;
import network.walrus.utils.core.command.exception.TranslatableCommandErrorException;
import network.walrus.utils.core.translation.Localizable;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.github.paperspigot.Title;

/**
 * Commands used to freeze/thaw players.
 *
 * @author Austin Mayes
 */
public class FreezeCommands {

  private final FreezeManager freezeManager;

  /** @param freezeManager used to freeze players */
  public FreezeCommands(FreezeManager freezeManager) {
    this.freezeManager = freezeManager;
  }

  /**
   * Freeze a player
   *
   * @throws CommandException if the player cannot be frozen.
   */
  @Command(
      aliases = {"freeze", "fr", "f", "freezeplayer", "fp"},
      desc = "Freeze a player",
      perms = UbiquitousPermissions.FREEZE)
  public void freeze(@Sender CommandSender sender, Player target) throws CommandException {
    if (sender instanceof Player && ((Player) sender).getUniqueId().equals(target.getUniqueId())) {
      throw new TranslatableCommandErrorException(UbiquitousMessages.ERROR_FREEZE_SELF);
    }

    if (target.hasPermission(UbiquitousPermissions.FREEZE_EXEMPT)
        && !sender.hasPermission(UbiquitousPermissions.FREEZE_OVERRIDE)) {
      throw new TranslatableCommandErrorException(UbiquitousMessages.ERROR_FREEZE_EXEMPT);
    }

    boolean freeze = !freezeManager.isFrozen(target);
    CompatTitleScreen titleManager =
        UbiquitousBukkitPlugin.getInstance().getCompatManager().getCompatTitleScreen();
    if (freeze) {
      freezeManager.freeze(target);
      Frozen.VICTIM.play(target);
      Localizable titleText = UbiquitousMessages.FREEZE_ALERT_TITLE.with(FREEZE.TITLE);
      Localizable subtitleText = UbiquitousMessages.FREEZE_ALERT_SUBTITLE.with(FREEZE.SUBTITLE);
      if (!titleManager.isLegacy(target)) {
        Title freezeTitle =
            Title.builder()
                .fadeIn(3)
                .fadeOut(3)
                .stay(400000)
                .title(titleText.render(target))
                .subtitle(subtitleText.render(target))
                .build();
        titleManager.sendTitle(target, freezeTitle);
      } else {
        target.sendMessage(titleText);
        target.sendMessage(subtitleText);
      }

      UbiquitousBukkitPlugin.getInstance()
          .moderationLogger()
          .log(
              new TranslatableLogRecord(
                  Level.INFO,
                  UbiquitousMessages.FREEZE_NOTIFICATIONS_FREEZE.with(
                      FREEZE.FREEZE_ALERT,
                      new PersonalizedBukkitPlayer(sender),
                      new PersonalizedBukkitPlayer(target)),
                  Frozen.ALERT));
    } else {
      freezeManager.thaw(target);
      if (!titleManager.isLegacy(target)) {
        titleManager.hideTitle(target);
      }
      Thawed.VICTIM.play(target);
      UbiquitousBukkitPlugin.getInstance()
          .moderationLogger()
          .log(
              new TranslatableLogRecord(
                  Level.INFO,
                  UbiquitousMessages.FREEZE_NOTIFICATIONS_THAW.with(
                      FREEZE.THAW_ALERT,
                      new PersonalizedBukkitPlayer(sender),
                      new PersonalizedBukkitPlayer(target)),
                  Thawed.ALERT));
    }
  }

  /**
   * Internal alias for /freeze
   *
   * @throws CommandException if the player isn't frozen
   */
  @Command(
      aliases = {"unfreeze", "thaw", "uf", "th"},
      desc = "Unfreeze a player",
      perms = UbiquitousPermissions.FREEZE)
  public void unFreeze(@Sender CommandSender sender, Player target) throws CommandException {
    if (!freezeManager.isFrozen(target)) {
      throw new TranslatableCommandErrorException(UbiquitousMessages.ERROR_NOT_FROZEN);
    }

    freeze(sender, target);
  }
}

package network.walrus.games.uhc.commands;

import app.ashcon.intake.Command;
import app.ashcon.intake.bukkit.parametric.annotation.Sender;
import app.ashcon.intake.parametric.annotation.Switch;
import javax.annotation.Nullable;
import network.walrus.games.uhc.UHCManager;
import network.walrus.games.uhc.UHCMessages;
import network.walrus.games.uhc.UHCPermissions;
import network.walrus.utils.bukkit.sound.NetworkSoundConstants.Games.UHC.PlayerMod;
import network.walrus.utils.bukkit.sound.NetworkSoundConstants.Games.UHC.PlayerMod.Health;
import network.walrus.utils.bukkit.translation.text.player.PersonalizedBukkitPlayer;
import network.walrus.utils.core.color.NetworkColorConstants.Games.UHC.PlayerMod.Feed;
import network.walrus.utils.core.color.NetworkColorConstants.Games.UHC.PlayerMod.Fullbright;
import network.walrus.utils.core.color.NetworkColorConstants.Games.UHC.PlayerMod.Heal;
import network.walrus.utils.core.color.NetworkColorConstants.Games.UHC.PlayerMod.Targets;
import network.walrus.utils.core.command.exception.TranslatableCommandErrorException;
import network.walrus.utils.core.translation.Localizable;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Commands used to modify player attributes.
 *
 * @author Austin Mayes
 */
public class PlayerModificationCommands {

  /** @param player to heal */
  public static void heal(Player player) {
    player.setHealth(player.getMaxHealth());
    player.sendMessage(UHCMessages.prefix(UHCMessages.HEAL_HEALED.with(Heal.HEALED)));
    Health.SELF.play(player);
  }

  /** Feed a target */
  @Command(aliases = "feed", desc = "Feed a/all player(s)", perms = UHCPermissions.FEED_PERM)
  public void feed(
      @Sender CommandSender sender, @Nullable Player player, @Switch('a') boolean all) {
    Localizable target;
    if (player != null) {
      feed(player);
      target = new PersonalizedBukkitPlayer(player);
    } else if (all) {
      for (Player player1 : Bukkit.getOnlinePlayers()) {
        feed(player1);
      }
      target = UHCMessages.TARGETS_ALL.with(Targets.ALL);
    } else if (sender instanceof Player) {
      feed((Player) sender);
      target = UHCMessages.TARGETS_SELF.with(Targets.SELF);
    } else {
      sender.sendMessage(UHCMessages.INVALID_TARGET.with(Targets.INVALID));
      return;
    }
    PlayerMod.Feed.EXECUTED.play(sender);
    sender.sendMessage(UHCMessages.prefix(UHCMessages.FEED_SUCCESS.with(Feed.EXECUTED, target)));
  }

  /** Heal a target */
  @Command(aliases = "heal", desc = "Heal a/all player(s)", perms = UHCPermissions.HEAL_PERM)
  public void heal(
      @Sender CommandSender sender, @Nullable Player player, @Switch('a') boolean all) {
    Localizable target;
    if (player != null) {
      heal(player);
      target = new PersonalizedBukkitPlayer(player);
    } else if (all) {
      for (Player player1 : Bukkit.getOnlinePlayers()) {
        heal(player1);
      }
      target = UHCMessages.TARGETS_ALL.with(Targets.ALL);
    } else if (sender instanceof Player) {
      heal((Player) sender);
      target = UHCMessages.TARGETS_SELF.with(Targets.SELF);
    } else {
      sender.sendMessage(UHCMessages.INVALID_TARGET.with(Targets.INVALID));
      return;
    }
    Health.EXECUTED.play(sender);
    sender.sendMessage(UHCMessages.prefix(UHCMessages.HEAL_SUCCESS.with(Heal.EXECUTED, target)));
  }

  @Command(
      aliases = {"fullbright", "fb"},
      desc = "Enable fullbright")
  public void fullbright(@Sender Player player) throws TranslatableCommandErrorException {
    if (UHCManager.instance.getUHC() == null
        || !UHCManager.instance.getUHC().getState().started()) {
      throw new TranslatableCommandErrorException(UHCMessages.FULLBRIGHT_ERROR);
    }

    if (player.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
      player.removePotionEffect(PotionEffectType.NIGHT_VISION);
      player.sendMessage(UHCMessages.FULLBRIGHT_DISABLED.with(Fullbright.DISABLED));
      return;
    }

    player.addPotionEffect(
        new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0, true, false));
    player.sendMessage(UHCMessages.FULLBRIGHT_ENABLED.with(Fullbright.ENABLED));
  }

  private void feed(Player player) {
    player.setFoodLevel(20);
    player.sendMessage(UHCMessages.prefix(UHCMessages.FEED_FED.with(Feed.FED)));
    PlayerMod.Feed.SELF.play(player);
  }
}

package network.walrus.ubiquitous.bukkit.chat;

import app.ashcon.intake.Command;
import app.ashcon.intake.bukkit.parametric.annotation.Sender;
import java.util.Optional;
import java.util.logging.Level;
import network.walrus.ubiquitous.bukkit.UbiquitousBukkitPlugin;
import network.walrus.ubiquitous.bukkit.UbiquitousMessages;
import network.walrus.ubiquitous.bukkit.UbiquitousPermissions;
import network.walrus.utils.bukkit.logging.TranslatableLogRecord;
import network.walrus.utils.bukkit.translation.text.player.PersonalizedBukkitPlayer;
import network.walrus.utils.core.color.NetworkColorConstants.Chat;
import org.bukkit.command.CommandSender;

/**
 * Commands for managing chat.
 *
 * @author Rafi
 */
public class ChatCommands {

  private final ChatManager manager;

  /** @param chatManager used to detect chat status */
  public ChatCommands(ChatManager chatManager) {
    this.manager = chatManager;
  }

  /** Globally mute the chat. */
  @Command(
      aliases = "muteall",
      desc = "Used to enable or disable the global chat mute",
      perms = UbiquitousPermissions.MUTE_GLOBAL)
  public void mute(@Sender CommandSender sender, Optional<Boolean> enableOptional) {
    boolean enable = enableOptional.orElse(!manager.isChatMuted());

    if (enable) {
      manager.enableGlobalMute();
      UbiquitousBukkitPlugin.getInstance()
          .moderationLogger()
          .log(
              new TranslatableLogRecord(
                  Level.INFO,
                  UbiquitousMessages.CHAT_MUTED_ALERT.with(
                      Chat.CHAT_MUTED_ALERT, new PersonalizedBukkitPlayer(sender))));
    } else {
      manager.disableGlobalMute();
      UbiquitousBukkitPlugin.getInstance()
          .moderationLogger()
          .log(
              new TranslatableLogRecord(
                  Level.INFO,
                  UbiquitousMessages.CHAT_UNMUTED_ALERT.with(
                      Chat.CHAT_MUTED_ALERT, new PersonalizedBukkitPlayer(sender))));
    }
  }
}

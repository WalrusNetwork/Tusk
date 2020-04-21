package network.walrus.ubiquitous.bukkit.chat;

import network.walrus.ubiquitous.bukkit.UbiquitousBukkitPlugin;
import network.walrus.ubiquitous.bukkit.UbiquitousMessages;
import network.walrus.ubiquitous.bukkit.chat.filter.ChatFilter;
import network.walrus.utils.core.color.NetworkColorConstants.Chat;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;

/**
 * Manager responsible for handling per-serve global chat states.
 *
 * <p>Individual chat handling (such as mutes) is handled at the bungee level.
 *
 * @author Rafi
 */
public class ChatManager {

  private boolean globalMute = false;
  private LocalizedChatRenderer listener;

  /**
   * @param mandatoryFilters which are applied to every message
   * @param optInFilters which are applied to messages sent to players with the {@link
   *     network.walrus.ubiquitous.bukkit.chat.LocalizedChatRenderer.ChatType#CLEAN} setting enabled
   */
  public ChatManager(ChatFilter[] mandatoryFilters, ChatFilter[] optInFilters) {
    this.listener = new LocalizedChatRenderer(this, mandatoryFilters, optInFilters);
  }

  /** Enable the manager. */
  public void enable() {
    globalMute = false;
    Bukkit.getPluginManager().registerEvents(listener, UbiquitousBukkitPlugin.getInstance());
  }

  /** Disable the manager. */
  public void disable() {
    HandlerList.unregisterAll(listener);
  }

  /** Enable the global chat mute for this server. */
  public void enableGlobalMute() {
    if (globalMute) {
      return;
    }
    globalMute = true;
    Bukkit.broadcast(UbiquitousMessages.CHAT_MUTED.with(Chat.CHAT_MUTED));
  }

  /** Disable the global chat mute for this server. */
  public void disableGlobalMute() {
    if (!globalMute) {
      return;
    }
    globalMute = false;
    Bukkit.broadcast(UbiquitousMessages.CHAT_UNMUTED.with(Chat.CHAT_UNMUTED));
  }

  /** @return if the server chat is disabled */
  public boolean isChatMuted() {
    return globalMute;
  }
}

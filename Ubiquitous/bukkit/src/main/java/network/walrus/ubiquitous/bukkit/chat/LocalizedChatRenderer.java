package network.walrus.ubiquitous.bukkit.chat;

import network.walrus.ubiquitous.bukkit.UbiquitousMessages;
import network.walrus.ubiquitous.bukkit.UbiquitousPermissions;
import network.walrus.ubiquitous.bukkit.chat.filter.ChatFilter;
import network.walrus.ubiquitous.bukkit.settings.PlayerSettings;
import network.walrus.ubiquitous.bukkit.settings.Setting;
import network.walrus.ubiquitous.bukkit.settings.types.SettingTypes;
import network.walrus.utils.bukkit.translation.text.player.PersonalizedBukkitPlayer;
import network.walrus.utils.core.color.NetworkColorConstants.Chat;
import network.walrus.utils.core.text.UnlocalizedFormat;
import network.walrus.utils.core.text.UnlocalizedText;
import network.walrus.utils.core.translation.Localizable;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 * Listener which intercepts chat events and localizes them correctly.
 *
 * @author Austin Mayes
 */
public class LocalizedChatRenderer implements Listener {

  private static final Setting<ChatType> SETTING =
      new Setting<ChatType>(
          "ubiquitous.bukkit.chat-setting",
          SettingTypes.enumOf(ChatType.class),
          ChatType.CLEAN,
          UbiquitousMessages.SETTING_CHAT_NAME.with(),
          UbiquitousMessages.SETTING_CHAT_DESCRIPTION.with());
  private static final UnlocalizedFormat CHAT_FORMAT = new UnlocalizedFormat("<{0}>: {1}");
  private final ChatFilter[] mandatoryFilters;

  private final ChatManager manager;
  private final ChatFilter[] optInFilters;

  /**
   * @param manager used to detect chat status
   * @param mandatoryFilters which are applied to every message
   * @param optInFilters which are applied for senders with more filtering enabled
   */
  public LocalizedChatRenderer(
      ChatManager manager, ChatFilter[] mandatoryFilters, ChatFilter[] optInFilters) {
    this.manager = manager;
    this.mandatoryFilters = mandatoryFilters;
    this.optInFilters = optInFilters;
    PlayerSettings.register(SETTING);
  }

  /** handle all chat */
  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onChat(AsyncPlayerChatEvent event) {
    String message = event.getMessage();
    if (event.getPlayer().hasPermission(UbiquitousPermissions.COLORED_CHAT)) {
      message = ChatColor.translateAlternateColorCodes('&', message);
    } else message = ChatColor.stripColor(message);

    for (ChatFilter filter : mandatoryFilters) {
      if (!filter.canBypass(event.getPlayer())) {
        message = filter.filter(message);
      }
    }

    String extraFiltered = message;
    for (ChatFilter filter : optInFilters) {
      extraFiltered = filter.filter(extraFiltered);
    }

    Localizable chat =
        CHAT_FORMAT.with(
            new PersonalizedBukkitPlayer(event.getPlayer()), new UnlocalizedText(message));
    Localizable chatWithMoreFilters =
        CHAT_FORMAT.with(
            new PersonalizedBukkitPlayer(event.getPlayer()), new UnlocalizedText(extraFiltered));

    for (Player player : event.getRecipients()) {
      switch (PlayerSettings.get(event.getPlayer(), SETTING)) {
        case ALL:
          player.sendMessage(chat);
          break;
        case CLEAN:
          player.sendMessage(chatWithMoreFilters);
          break;
        case NONE:
          break;
      }
    }
    Bukkit.getConsoleSender().sendMessage(chat);

    event.getRecipients().clear();
    event.setCancelled(true);
  }

  /** handle if chat is muted */
  @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
  public void muteCheck(AsyncPlayerChatEvent event) {
    if (!manager.isChatMuted()
        || event.getPlayer().hasPermission(UbiquitousPermissions.MUTE_EXEMPT)) {
      return;
    }

    event.setCancelled(true);
    event.getPlayer().sendMessage(UbiquitousMessages.CHAT_MUTED_ERROR.with(Chat.CHAT_MUTED_ERROR));
  }

  enum ChatType {
    ALL,
    CLEAN,
    NONE
  }
}

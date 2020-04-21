package network.walrus.games.core.facets.chat;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import net.md_5.bungee.api.ChatColor;
import network.walrus.games.core.GamesCoreMessages;
import network.walrus.games.core.GamesPlugin;
import network.walrus.games.core.events.round.RoundStateChangeEvent;
import network.walrus.games.core.facets.group.Competitor;
import network.walrus.games.core.facets.group.CompetitorRule;
import network.walrus.games.core.facets.group.Group;
import network.walrus.games.core.facets.group.GroupsManager;
import network.walrus.utils.bukkit.translation.text.player.PersonalizedBukkitPlayer;
import network.walrus.utils.core.color.NetworkColorConstants.Games;
import network.walrus.utils.core.text.LocalizedFormat;
import network.walrus.utils.core.text.UnlocalizedFormat;
import network.walrus.utils.core.text.UnlocalizedText;
import network.walrus.utils.core.translation.Localizable;
import network.walrus.utils.parsing.facet.Facet;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * This facet is responsible for handling team and global chat channels.
 *
 * @author Austin Mayes
 */
public class ChatFacet extends Facet implements Listener {

  /** Map of what channel a player is talking in. */
  private final Map<Player, ChatChannel> chatChannel;
  /** Map of players to the quick chat mode they are in. */
  private final Map<Player, QuickChatMode> quickChatModes;
  /** holder that this facet exists in. */
  private final FacetHolder holder;
  /** If team chat is allowed while this holder is active. */
  private final boolean allowTeamChat;
  /** If global chat is allowed while this holder is active. */
  private final boolean allowGlobalChat;
  /** Map of identifier->message for each possible quick chat replacement. */
  private final Map<String, String> quickChatMatchers = new HashMap<>();
  /** Channel players should talk in by default */
  private ChatChannel defaultChannel;

  /**
   * Constructor.
   *
   * <p>
   *
   * <p>Note: Either team chat or global chat must be enabled.
   *
   * @param holder holder that this facet exists in
   * @param allowTeamChat if team chat is allowed while this holder is active
   * @param allowGlobalChat if global chat is allowed while this holder is active
   */
  public ChatFacet(FacetHolder holder, boolean allowTeamChat, boolean allowGlobalChat) {
    Preconditions.checkArgument(
        allowTeamChat || allowGlobalChat, "Team chat or global chat must be enabled");
    this.holder = holder;
    this.allowTeamChat = allowTeamChat;
    this.allowGlobalChat = allowGlobalChat;
    this.chatChannel = Maps.newHashMap();
    this.quickChatModes = Maps.newHashMap();
    this.defaultChannel = ChatChannel.GLOBAL;
    parseMatchers();
  }

  private void parseMatchers() {
    Map<String, Object> matchers =
        GamesPlugin.instance
            .getConfig()
            .getConfigurationSection("chat.quick-chat.matchers")
            .getValues(false);
    if (matchers == null || matchers.isEmpty()) {
      return;
    }

    for (Entry<String, Object> entry : matchers.entrySet()) {
      String m = entry.getKey();
      Object r = entry.getValue();
      quickChatMatchers.put(m, (String) r);
    }
  }

  private ChatChannel calculateDefaultChannel() {
    Optional<GroupsManager> groupManager = holder.getFacet(GroupsManager.class);
    boolean ffa =
        groupManager.isPresent()
            && groupManager.get().getCompetitorRule() == CompetitorRule.INDIVIDUAL;
    if (allowTeamChat && !ffa) {
      return ChatChannel.TEAM;
    } else {
      return ChatChannel.GLOBAL;
    }
  }

  /** Reset global chat status when players log out. */
  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent event) {
    this.chatChannel.remove(event.getPlayer());
    this.quickChatModes.remove(event.getPlayer());
  }

  /**
   * Check if team chat is allowed in the holder.
   *
   * @return if team chat is allowed in the holder
   */
  boolean isTeamChatAllowed() {
    return this.allowTeamChat;
  }

  /**
   * Check if global chat is enabled in the holder.
   *
   * @return if global chat is enabled in the holder
   */
  boolean isGlobalChatAllowed() {
    return this.allowGlobalChat;
  }

  /**
   * Set the player's chat channel.
   *
   * @param player player to check
   * @param chatChannel of the player
   */
  public void setChatChannel(Player player, ChatChannel chatChannel) {
    this.chatChannel.put(player, chatChannel);
  }

  /**
   * Check if a player is currently talking in global chat.
   *
   * @param player player to check
   * @return if the player is currently talking in global chat
   */
  public ChatChannel getChatChannel(Player player) {
    return chatChannel.getOrDefault(player, defaultChannel);
  }

  /**
   * Set a player's current chat mode.
   *
   * @param player player to check
   * @param mode that the player is now using
   */
  public void setChatMode(Player player, QuickChatMode mode) {
    this.quickChatModes.put(player, mode);
  }

  /**
   * Get the quick chat mode a player is currently using.
   *
   * @param player player to check
   * @return mode currently active for the given player
   */
  public QuickChatMode getChatMode(Player player) {
    return this.quickChatModes.getOrDefault(player, QuickChatMode.OFF);
  }

  /**
   * Determine if the player is currently using some form of quick chat.
   *
   * @param player player to check
   * @return if the player currently has quick chat enabled
   */
  public boolean isUsingQuickChat(Player player) {
    return getChatMode(player) != QuickChatMode.OFF;
  }

  /**
   * Determine if the player is allowed to toggle off quick chat.
   *
   * @param player player to check
   * @return if the player can change their quick chat mode
   */
  public boolean canToggleQuickChat(Player player) {
    return getChatMode(player) != QuickChatMode.FORCED;
  }

  private String replaceQuickChatMatchers(Player player, String message) {
    boolean replaced = false;
    for (Entry<String, String> entry : quickChatMatchers.entrySet()) {
      if (message.toLowerCase().contains(entry.getKey().toLowerCase())) {
        replaced = true;
        message = message.replace(entry.getKey(), entry.getValue());
      }
    }
    if (!replaced && getChatMode(player) == QuickChatMode.FORCED) {
      return null;
    } else {
      return message;
    }
  }

  /**
   * Send possible quick chat options to a player.
   *
   * @param player to send options to
   */
  void broadcastOptions(Player player) {
    for (Entry<String, String> entry : quickChatMatchers.entrySet()) {
      player.sendMessage(
          new UnlocalizedFormat("{0}: {1}")
              .with(
                  Games.Chats.QUICKCHAT_DELIMITER,
                  new UnlocalizedText(entry.getKey(), Games.Chats.QUICKCHAT_MATCHER),
                  new UnlocalizedText(entry.getValue(), Games.Chats.QUICKCHAT_REPLACE)));
    }
  }

  private Optional<String> processQuickChat(Player player, String message) {
    if (isUsingQuickChat(player)) {
      message = replaceQuickChatMatchers(player, message);
      if (message == null) {
        LocalizedFormat format = GamesCoreMessages.QUICKCHAT_BAD_FORMAT;
        player.sendMessage(format.with(Games.Chats.QUICKCHAT_ERROR));
        broadcastOptions(player);
        if (getChatMode(player) == QuickChatMode.ON) {
          player.sendMessage(
              GamesCoreMessages.QUICKCHAT_SWITCH.with(Games.Chats.QUICKCHAT_SWITCHED));
        }
        return Optional.empty();
      }
    }

    return Optional.of(message);
  }

  /**
   * Send a message to the player's current chat channel.
   *
   * @param player who is sending the message
   * @param text to send
   */
  public void chatNaturally(Player player, String text) {
    if (getChatChannel(player) == ChatChannel.TEAM) {
      chatToTeam(player, text);
    } else {
      chatToGlobal(player, text);
    }
  }

  /**
   * Process quick chat matchers in the text and send the message to all players in the holder.
   *
   * @param player who is sending the message
   * @param text to process
   */
  public void chatToGlobal(Player player, String text) {
    Optional<String> processed = processQuickChat(player, text);
    processed.ifPresent(s -> chatToGlobal(player, new UnlocalizedText(s)));
  }

  /**
   * Send a message to all players in the holder. Use {@link ChatFacet#chatToGlobal(Player, String)}
   * to handle quick chat in the string.
   *
   * @param player who is sending the message
   * @param message to send
   */
  public void chatToGlobal(Player player, Localizable message) {
    UnlocalizedFormat format = new UnlocalizedFormat("<{0}>: {1}");
    Localizable chat = format.with(new PersonalizedBukkitPlayer(player), message);
    for (Player p : Bukkit.getOnlinePlayers()) {
      p.sendMessage(chat);
    }
    Bukkit.getConsoleSender().sendMessage(chat);
  }

  /**
   * Process quick chat matchers in the text and send the message to all players on the player's
   * team.
   *
   * @param player who is sending the message
   * @param text to process
   */
  public void chatToTeam(Player player, String text) {
    Optional<String> processed = processQuickChat(player, text);
    processed.ifPresent(s -> chatToTeam(player, new UnlocalizedText(s)));
  }

  /**
   * Send a message to all members on the player's team. Use {@link ChatFacet#chatToTeam(Player,
   * String)} * to handle quick chat in the string.
   *
   * @param player who is sending the message
   * @param message to send
   */
  public void chatToTeam(Player player, Localizable message) {
    GroupsManager groups = this.holder.getFacetRequired(GroupsManager.class);
    String color;
    Competitor competitor = groups.getCompetitorOf(player).orElse(null);
    if (competitor == null) {
      color = groups.getGroup(player).getColor().getPrefix();
    } else {
      color = competitor.getColor().getPrefix();
    }

    UnlocalizedFormat format =
        new UnlocalizedFormat(color + "[Team]" + ChatColor.RESET + " {0}: {1}");
    Group group = groups.getGroup(player);

    Set<Player> recipients = competitor == null ? group.getPlayers() : competitor.getPlayers();
    Localizable chat = format.with(new PersonalizedBukkitPlayer(player), message);
    for (Player p : recipients) {
      p.sendMessage(chat);
    }
    Bukkit.getConsoleSender().sendMessage(chat);
  }

  /** Format messages and send them to the correct place. */
  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
    event.setCancelled(true);
    if (event.getMessage().startsWith("!")) {
      chatToGlobal(event.getPlayer(), event.getMessage().substring(1));
      return;
    }
    chatNaturally(event.getPlayer(), event.getMessage());
  }

  /** Use global channel when a round is not in session */
  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onRoundStateChange(RoundStateChangeEvent event) {
    if (event.isChangeToPlaying()) {
      this.defaultChannel = calculateDefaultChannel();
    } else if (event.isChangeToNotPlaying()) {
      this.chatChannel.clear();
      this.defaultChannel = ChatChannel.GLOBAL;
    }
  }

  enum QuickChatMode {
    OFF,
    ON,
    FORCED;
  }
}

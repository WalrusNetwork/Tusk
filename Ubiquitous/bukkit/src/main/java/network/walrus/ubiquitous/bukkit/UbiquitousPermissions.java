package network.walrus.ubiquitous.bukkit;

/**
 * Permissions for stuff in the ubiquitous plugin.
 *
 * @author Austin Mayes
 */
public class UbiquitousPermissions {

  /** Permission needed for a player to freeze another player. */
  public static final String FREEZE = "walrus.freeze.freeze";
  /** Permission needed for a player to be exempt from being frozen. */
  public static final String FREEZE_EXEMPT = "walrus.freeze.exempt";
  /** Permission needed for a player to freeze a player regardless of exemption. */
  public static final String FREEZE_OVERRIDE = "walrus.freeze.override";
  /** Permission needed for a player to receive local moderation alerts. */
  public static final String LOCAL_MODERATION_ALERTS = "walrus.alerts.moderation.local";

  public static final String COLORED_CHAT = "walrus.chat.colors";
  /** Permission to bypass global chat mute. */
  public static final String MUTE_EXEMPT = "walrus.chat.mute.exempt";
  /** Permission to enable/disable global chat mute. */
  public static final String MUTE_GLOBAL = "walrus.chat.mute.global";
  /** Permission to bypass all filters. */
  public static final String FILTER_BYPASS_ALL = "walrus.chat.filters.bypass.all";
  /** Permission to bypass the IP filter. */
  public static final String IP_FILTER_BYPASS = "walrus.chat.filters.bypass.ip";
  /** Permission to bypass the bad words filter. */
  public static final String WORDS_FILTER_BYPASS = "walrus.chat.filters.bypass.words";

  public static final String CANCEL_COUNTDOWNS = "walrus.cancel";
}

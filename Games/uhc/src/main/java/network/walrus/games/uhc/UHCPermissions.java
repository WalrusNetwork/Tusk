package network.walrus.games.uhc;

/**
 * A constants class containing all of the permissions players can have that allow them to perform
 * functions in the UHC component
 *
 * <p>Permissions should do *one* and *only one* thing per node, and no "group" permission nodes
 * (such as {@code walrus.staff}) should be used. This is so we can fine-tune permissions later at
 * any time, without having to refactor a lot of code.
 *
 * @author Austin Mayes
 */
public class UHCPermissions {

  /** Permission which allows players to see host notifications. */
  public static final String HOST_ALERTS = "walrus.uhc.host.alerts";
  /** Permission which allows players to manage hosts. */
  public static final String HOST_MANAGE = "walrus.uhc.host.manage";
  /** Permission which allows players to manage spectators. */
  public static final String SPEC_MANAGE = "walrus.uhc.spec.manage";
  /** Permission which allows players to heal players. */
  public static final String HEAL_PERM = "walrus.uhc.heal";
  /** Permission which allows players to feed players. */
  public static final String FEED_PERM = "walrus.uhc.feed";
  /** Permission which allows players to whitelist all players. */
  public static final String WHITELIST_ALL_PERM = "walrus.uhc.whitelist.all";
  /** Permission which allows players to clear the whitelist. */
  public static final String WHITELIST_CLEAR_PERM = "walrus.uhc.whitelist.clear";
  /** Permission which allows players to turn off the whitelist. */
  public static final String WHITELIST_OFF_PERM = "walrus.uhc.whitelist.off";
  /** Permission which allows players to cancel the whitelist countdown. */
  public static final String WHITELIST_CANCEL_PERM = "walrus.uhc.whitelist.cancel";
  /** Permission which allows players to manage scenarios. */
  public static final String SCENARIO_MANAGE_PERM = "walrus.uhc.scenario.manage";
  /** Permission which allows players to modify the UHC config. */
  public static final String UPDATE_CONFIG = "walrus.uhc.config.manage";
  /** Permission which allows players to scatter players. */
  public static final String SCATTER_START_PERM = "walrus.uhc.scatter.start";
  /** Permission which allows players to clear chat. */
  public static final String CHAT_CLEAR_PERM = "walrus.uhc.clearchat";
  /** Permission which allows players to check a UUID on the UBL. */
  public static final String UBL_CHECK = "walrus.uhc.ubl.check";
  /** Permission which allows players to login even if they're on the UBL */
  public static final String UBL_EXEMPT = "walrus.uhc.ubl.exempt.login";
  /** Permission which allows players to manage who is exempt from the UBL */
  public static final String UBL_EXEMPT_MANAGE = "walrus.uhc.ubl.exempt.manage";
  /** Permission which always allows players to bypass the player cap */
  public static final String PLAYER_COUNT_EXEMPT = "walrus.uhc.count.exempt";
  /** Permission which allows players to bypass the whitelist */
  public static final String PLAYER_WHITELIST_BYPASS = "walrus.uhc.whitelist.bypass";
  /** Permission which allows players to disqualify other players. */
  public static final String DISQUALIFY_PERM = "walrus.uhc.disqualify";
  /** Permission which allows players to force scatter a player or team */
  public static final String FORCE_SCATTER = "walrus.uhc.scatter.force";
  /** Permission which allows players to run /tpall */
  public static final String TP_ALL = "walrus.uhc.tpall";
  /** Permission which allows players to revive competitors */
  public static final String REVIVE = "walrus.uhc.revive";
  /** Permission which allows players to view team inventories of other teams */
  public static final String BACKPACK_VIEW = "walrus.uhc.scenarios.backpack.view";
}

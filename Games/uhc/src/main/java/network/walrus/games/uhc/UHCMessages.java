package network.walrus.games.uhc;

import java.util.Arrays;
import network.walrus.utils.core.text.LegacyText;
import network.walrus.utils.core.text.LocalizedFormat;
import network.walrus.utils.core.translation.Localizable;
import network.walrus.utils.core.translation.MessageReferenceHolder;

/**
 * Messages specifically for the UHC component.
 *
 * @author Austin Mayes
 */
public class UHCMessages extends MessageReferenceHolder {

  public static final LocalizedFormat PREFIX = get("ui.uhc-prefix");
  public static final LocalizedFormat TARGETS_SELF = get("ui.targets.self");
  public static final LocalizedFormat TARGETS_ALL = get("ui.targets.all");
  public static final LocalizedFormat INVALID_TARGET = get("errors.invalid-target");
  public static final LocalizedFormat HEAL_SUCCESS = get("ui.heal.success");
  public static final LocalizedFormat HEAL_HEALED = get("ui.heal.healed");
  public static final LocalizedFormat FEED_SUCCESS = get("ui.feed.success");
  public static final LocalizedFormat FEED_FED = get("ui.feed.fed");
  public static final LocalizedFormat WHITELIST_ADD_ALL = get("ui.whitelist.add-all");
  public static final LocalizedFormat WHITELIST_CLEAR = get("ui.whitelist.clear");
  public static final LocalizedFormat WHITELIST_OFF = get("ui.whitelist.off");
  public static final LocalizedFormat WHITELIST_CANCELLED = get("ui.whitelist.cancel");
  public static final LocalizedFormat WHITELIST_ALREADY_ON = get("errors.whitelist.already-on");
  public static final LocalizedFormat WHITELIST_NONE_EXISTS = get("errors.whitelist.none-exists");
  public static final LocalizedFormat UI_WHITELIST_ON_COUNTDOWN =
      get("ui.whitelist.countdown.remaining");
  public static final LocalizedFormat UI_WHITELIST_ON = get("ui.whitelist.countdown.on");
  public static final LocalizedFormat UI_SCENARIOS = get("ui.scenarios");
  public static final LocalizedFormat SCENARIO_NOT_FOUND = get("error.scenario.not-found");
  public static final LocalizedFormat SCENARIO_ACTIVATED = get("ui.scenario.activated");
  public static final LocalizedFormat SCENARIO_DEACTIVATED = get("ui.scenario.deactivated");
  public static final LocalizedFormat UI_ACTIVE_SCENARIOS = get("ui.active-scenarios");
  public static final LocalizedFormat SCATTERING = get("ui.scatter.countdown");
  public static final LocalizedFormat TEAMS_DISABLED = get("errors.teams.disabled");
  public static final LocalizedFormat ALREADY_ON_TEAM = get("errors.teams.already-on");
  public static final LocalizedFormat NO_TEAM_CREATED = get("errors.teams.not-created");
  public static final LocalizedFormat CREATE_NOT_LONER = get("errors.teams.create-not-loner");
  public static final LocalizedFormat NO_TEAMS_LEFT = get("errors.teams.none-left");
  public static final LocalizedFormat TEAM_CLAIMED = get("ui.teams.claimed");
  public static final LocalizedFormat TEAM_INVITED = get("ui.teams.invited");
  public static final LocalizedFormat TEAM_JOINED = get("ui.teams.joined");
  public static final LocalizedFormat INVITE_ACCEPTED = get("ui.teams.invite-accepted");
  public static final LocalizedFormat CANNOT_MANAGE_INVITES =
      get("errors.teams.cannot-manage.owner");
  public static final LocalizedFormat CANNOT_INVITE = get("errors.teams.cannot-invite");
  public static final LocalizedFormat INVITED = get("ui.teams.been-invited");
  public static final LocalizedFormat INVITE_ACCEPT = get("ui.teams.invite-accept");
  public static final LocalizedFormat INVITE_ACCEPT_HOVER = get("ui.teams.invite-hover");
  public static final LocalizedFormat UI_TEAMS = get("ui.teams.list");
  public static final LocalizedFormat ALREADY_LONER = get("errors.teams.already-loner");
  public static final LocalizedFormat CANNOT_MANAGE_STARTED =
      get("errors.teams.cannot-manage.started");
  public static final LocalizedFormat JOIN_LONER = get("ui.teams.join.loner");
  public static final LocalizedFormat NOT_INVITED = get("errors.teams.not-invite");
  public static final LocalizedFormat TEAM_FULL = get("errors.teams.full");
  public static final LocalizedFormat CONFIG_UPDATE = get("ui.config.update");
  public static final LocalizedFormat CANNOT_CONFIGURE = get("errors.config.cannot");
  public static final LocalizedFormat CONFIG_APPLIED = get("ui.config.applied");
  public static final LocalizedFormat SCATTER_STARTED = get("ui.scatter.started");
  public static final LocalizedFormat ALREADY_SCATTERED = get("errors.already-scattered");
  public static final LocalizedFormat SCENARIOS_SCOREBOARD = get("ui.scenarios.scoreboard");
  public static final LocalizedFormat END_DISABLED = get("errors.disabled.end");
  public static final LocalizedFormat NETHER_DISABLED = get("errors.disabled.nether");
  public static final LocalizedFormat HELPOP_ALERT = get("helpop.alert");
  public static final LocalizedFormat XRAY_NOTIFICATION = get("xray.notification");
  public static final LocalizedFormat CONFIG_APPLY_NOTIFICATION =
      get("ui.config.notification.apply");
  public static final LocalizedFormat CONFIG_UPDATE_NOTIFICATION =
      get("ui.config.notification.update");
  public static final LocalizedFormat HELPOP_SENT = get("helpop.sent");
  public static final LocalizedFormat NOT_ON_TEAM = get("errors.not-on-team");
  public static final LocalizedFormat NO_ORES = get("errors.no-ores");
  public static final LocalizedFormat SCATTER_GEN_STARTED = get("ui.scatter.gen.started");
  public static final LocalizedFormat SCATTER_GEN_FINISHED = get("ui.scatter.gen.finished");
  public static final LocalizedFormat ERROR_POTIONS_DISABLED_ALL =
      get("errors.potions.disabled.all");
  public static final LocalizedFormat ERROR_POTIONS_DISABLED_STRENGTH =
      get("errors.potions.disabled.strength");
  public static final LocalizedFormat PLAYER_HEALTH = get("ui.player-health");
  public static final LocalizedFormat RULES_HEADER = get("ui.rules-header");
  public static final LocalizedFormat GOLDEN_HEAD_ITEM = get("uhc.goldenhead.name");
  public static final LocalizedFormat BORDER_REMOVE_SUCCESS = get("uhc.border.remove.success");
  public static final LocalizedFormat BORDER_REMOVE_FAIL = get("uhc.border.remove.fail");
  public static final LocalizedFormat BORDER_ADDED = get("uhc.border.added");
  public static final LocalizedFormat BORDER_RECALCULATED = get("uhc.border.recalculated");
  public static final LocalizedFormat BORDER_HEADER = get("uhc.border.header");
  public static final LocalizedFormat BORDER_DESC = get("uhc.border.desc");
  public static final LocalizedFormat BORDER_SHRINK = get("uhc.border.shrink");
  public static final LocalizedFormat RELEASE = get("uhc.scatter.release");
  public static final LocalizedFormat PROJECTILE_HIT = get("uhc.projectile.hit");
  public static final LocalizedFormat PROJECTILE_SETTING_NAME = get("uhc.projectile.setting.name");
  public static final LocalizedFormat PROJECTILE_SETTING_DESC = get("uhc.projectile.setting.desc");
  public static final LocalizedFormat PVP_COUNTDOWN = get("uhc.pvp.countdown");
  public static final LocalizedFormat PVP_ENABLED = get("uhc.pvp.enabled");
  public static final LocalizedFormat SCOREBOARD_TIME = get("uhc.scoreboard.time");
  public static final LocalizedFormat SCOREBOARD_BORDER = get("uhc.scoreboard.border");
  public static final LocalizedFormat REDDIT_BANNED = get("uhc.ubl.banned");
  public static final LocalizedFormat REDDIT_CHECK_BAN = get("uhc.ubl.check.banned");
  public static final LocalizedFormat REDDIT_CHECK_NOT_BANNED = get("uhc.ubl.check.notbanned");
  public static final LocalizedFormat REDDIT_EXEMPTED = get("uhc.ubl.exempted");
  public static final LocalizedFormat REDDIT_UNEXEMPTED = get("uhc.ubl.unexempted");
  public static final LocalizedFormat REDDIT_EXEMPTED_HOSTS = get("uhc.ubl.hosts.exempted");
  public static final LocalizedFormat REDDIT_UNEXEMPTED_HOSTS = get("uhc.ubl.hosts.unexempted");
  public static final LocalizedFormat SERVER_FULL = get("uhc.full");
  public static final LocalizedFormat SCOREBOARD_PLAYERS_NO_TOTAL =
      get("uhc.scoreboard.players.no-total");
  public static final LocalizedFormat SCOREBOARD_PLAYERS_TOTAL =
      get("uhc.scoreboard.players.total");
  public static final LocalizedFormat SCOREBOARD_PLAYER_KILLS = get("uhc.scoreboard.kills");
  public static final LocalizedFormat SCOREBOARD_TEAM_KILLS = get("uhc.scoreboard.teamkills");
  public static final LocalizedFormat PLAYER_DISQUALIFIED = get("uhc.disqualified");
  public static final LocalizedFormat DISQUALIFIED_ALERT = get("uhc.disqualified.alert");
  public static final LocalizedFormat HOST_DISQUALIFIED_ALERT = get("uhc.disqualified.host-alert");
  public static final LocalizedFormat SCOREBOARD_ENVIRONMENT_DEATH =
      get("uhc.scoreboard.env-death");
  public static final LocalizedFormat HOST_SCATTERED_ALERT = get("uhc.force-scatter.host-alert");
  public static final LocalizedFormat SPAWN_BEFORE_SCATTER_ERROR =
      get("uhc.force-scatter.before-scatter");
  public static final LocalizedFormat ERROR_JOIN_AFTER_SCATTER = get("uhc.join.after-scatter");
  public static final LocalizedFormat TPALL_START = get("uhc.tpall.start");
  public static final LocalizedFormat TPALL_STOP = get("uhc.tpall.stop");
  public static final LocalizedFormat FULLBRIGHT_ENABLED = get("uhc.fullbright.on");
  public static final LocalizedFormat FULLBRIGHT_DISABLED = get("uhc.fullbright.off");
  public static final LocalizedFormat FULLBRIGHT_ERROR = get("uhc.fullbright.error");
  public static final LocalizedFormat SCOREBOARD_NEXT_BORDER = get("uhc.scoreboard.next-border");
  public static final LocalizedFormat REVIVE_BROADCAST = get("uhc.revive.broadcast");
  public static final LocalizedFormat REVIVE_HOST_ALERT = get("uhc.revive.host");
  public static final LocalizedFormat REVIVE_ERROR = get("uhc.revive.error");
  public static final LocalizedFormat NO_RODS = get("uhc.rodless.no-rods");
  public static final LocalizedFormat KILL_TOP_NONE = get("uhc.kill-top.no-kills");
  public static final LocalizedFormat KILL_TOP_HEADER = get("uhc.kill-top.header");
  public static final LegacyText[] TEAM_INFO_BOOK_CONTENT = getAllLegacy("uhc.teams.book.content");
  public static final LocalizedFormat TEAM_INFO_BOOK_TITLE = get("uhc.teams.book.title");
  public static final LocalizedFormat CONFIG_END = get("uhc.config.end");
  public static final LocalizedFormat CONFIG_NETHER = get("uhc.config.nether");
  public static final LocalizedFormat CONFIG_POTIONS = get("uhc.config.potions");
  public static final LocalizedFormat CONFIG_STRENGTH_TWO = get("uhc.config.strength-two");
  public static final LocalizedFormat CONFIG_GOLDEN_HEAD = get("uhc.config.golden-head");
  public static final LocalizedFormat CONFIG_INITIAL_BORDER = get("uhc.config.initial-border");
  public static final LocalizedFormat CONFIG_TEAM_SIZE = get("uhc.config.team-size");
  public static final LocalizedFormat CONFIG_PLAYER_COUNT = get("uhc.config.player-count");
  public static final LocalizedFormat CONFIG_ENDER_PEARL_DAMAGE = get("uhc.config.pearl-damage");
  public static final LocalizedFormat CONFIG_GOD_APPLES = get("uhc.config.god-apples");
  public static final LocalizedFormat CONFIG_ABSORPTION = get("uhc.config.absorption");
  public static final LocalizedFormat CONFIG_STARTER_FOOD = get("uhc.config.starter-food");
  public static final LocalizedFormat CONFIG_TIMEOUT_DELAY = get("uhc.config.timeout-delay");
  public static final LocalizedFormat CONFIG_PERMA_DAY = get("uhc.config.perma-day");
  public static final LocalizedFormat CONFIG_HEAL_DELAY = get("uhc.config.heal-delay");
  public static final LocalizedFormat CONFIG_PVP_DELAY = get("uhc.config.pvp-delay");
  public static final LocalizedFormat CONFIG_APPLE_CHANCE = get("uhc.config.apple-chance");
  public static final LocalizedFormat CONFIG_FLINT_CHANCE = get("uhc.config.flint-chance");
  public static final LocalizedFormat CONFIG_REDDIT_BANS = get("uhc.config.reddit-bans");
  public static final LocalizedFormat CONFIG_DEATH_LIGHTNING = get("uhc.config.death-lightning");
  public static final LocalizedFormat CONFIG_HORSES = get("uhc.config.horses");
  public static final LocalizedFormat CONFIG_UI_TITLE = get("uhc.config.title");
  public static final LocalizedFormat CONFIG_FIRE_ENCHANTS = get("uhc.config.fire-enchants");
  public static final LocalizedFormat CONFIG_GROUP_WORLDS = get("uhc.config.group.worlds");
  public static final LocalizedFormat CONFIG_GROUP_POTIONS = get("uhc.config.group.potions");
  public static final LocalizedFormat CONFIG_GROUP_PLAYER = get("uhc.config.group.player");
  public static final LocalizedFormat CONFIG_GROUP_APPLE = get("uhc.config.group.apple");
  public static final LocalizedFormat CONFIG_GROUP_REDDIT = get("uhc.config.group.reddit");
  public static final LocalizedFormat CONFIG_GROUP_TIMERS = get("uhc.config.group.timers");
  public static final LocalizedFormat CONFIG_GROUP_MISC = get("uhc.config.group.misc");
  public static final LocalizedFormat[] SCEN_DESC_BLEEDING =
      getAll("uhc.scenario.desc.bleeding-sweets");
  public static final LocalizedFormat[] SCEN_DESC_CUT_CLEAN = getAll("uhc.scenario.desc.cut-clean");
  public static final LocalizedFormat[] SCEN_DESC_HASTEY_BOYS =
      getAll("uhc.scenario.desc.hastey-boys");
  public static final LocalizedFormat[] SCEN_DESC_RODLESS = getAll("uhc.scenario.desc.rodless");
  public static final LocalizedFormat[] SCEN_DESC_TIMBER = getAll("uhc.scenario.desc.timber");
  public static final LocalizedFormat[] SCEN_DESC_TIME_BOMB = getAll("uhc.scenario.desc.time-bomb");
  public static final LocalizedFormat[] SCEN_DESC_NO_CLEAN = getAll("uhc.scenario.desc.noclean");
  public static final LocalizedFormat[] SCEN_DESC_GOLDEN_RETRIEVER =
      getAll("uhc.scenario.desc.golden-retriever");
  public static final LocalizedFormat[] SCEN_DESC_BLOOD_DIAMOND =
      getAll("uhc.scenario.desc.blood-diamond");
  public static final LocalizedFormat FIRE_ENCHANTS_DISABLED = get("uhc.fire-enchants-disabled");
  public static final LocalizedFormat HORSES_DISABLED = get("uhc.horses-disabled");
  public static final LocalizedFormat NOCLEAN_EXPIRED = get("uhc.noclean.expired");
  public static final LocalizedFormat NOCLEAN_REMAINING = get("uhc.noclean.remaining");
  public static final LocalizedFormat NOCLEAN_GIVEN = get("uhc.noclean.given");
  public static final LocalizedFormat NOCLEAN_LOST = get("uhc.noclean.lost");
  public static final LocalizedFormat[] SCEN_DESC_BACKPACK = getAll("uhc.scenario.desc.backpack");
  public static final LocalizedFormat BACKPACK_NO_INV = get("uhc.backpack.none");
  public static final LocalizedFormat BACKPACK_NOT_STARTED = get("uhc.backpack.not-started");
  public static final LocalizedFormat BACKPACK_NOT_ENABLED = get("uhc.backpack.not-enabled");
  public static final LocalizedFormat BACKPACK_NOT_FOUND = get("uhc.backpack.not-found");
  public static final LocalizedFormat[] SCEN_DESC_DIAMONDLESS =
      getAll("uhc.scenario.desc.diamondless");
  public static final LocalizedFormat[] SCEN_DESC_WEAKEST_LINK =
      getAll("uhc.scenario.desc.weakest-link");
  public static final LocalizedFormat WEAKEST_LINK_NONE = get("uhc.weakest-link.none");
  public static final LocalizedFormat WEAKEST_LINK_SELF = get("uhc.weakest-link.self");
  public static final LocalizedFormat WEAKEST_LINK_OTHER = get("uhc.weakest-link.other");
  public static final LocalizedFormat[] SCEN_DESC_BATS = getAll("uhc.scenario.desc.bats");
  public static final LocalizedFormat[] SCEN_DESC_INFINITE_ENCHANTER =
      getAll("uhc.scenario.desc.infinite-enchanter");

  static {
    USED_MESSAGES.addAll(
        Arrays.asList(
            new MessageInformation(PREFIX, "uhc prefix"),
            new MessageInformation(TARGETS_SELF, "describes a self target"),
            new MessageInformation(TARGETS_ALL, "describes a target encompassing all players"),
            new MessageInformation(
                INVALID_TARGET, "error shown when a player targets an invalid person"),
            new MessageInformation(HEAL_SUCCESS, "message shown to the player who initiates a heal")
                .argument("target"),
            new MessageInformation(HEAL_HEALED, "message shown to players who are healed"),
            new MessageInformation(FEED_SUCCESS, "message shown to the player who initiates a feed")
                .argument("target"),
            new MessageInformation(FEED_FED, "message shown to players who are fed"),
            new MessageInformation(
                WHITELIST_ADD_ALL,
                "message shown to a player when they add all players to the whitelist"),
            new MessageInformation(
                WHITELIST_CLEAR, "message shown to a player when they clear the whitelist"),
            new MessageInformation(
                    WHITELIST_OFF, "message shown to a player when they turn off the whitelist")
                .argument("time in clock format"),
            new MessageInformation(
                WHITELIST_CANCELLED, "message shown when the whitelist countdown is cancelled"),
            new MessageInformation(
                WHITELIST_ALREADY_ON,
                "message shown when a player tries to turn on a whitelist countdown but one already exists"),
            new MessageInformation(
                WHITELIST_NONE_EXISTS,
                "message shown when a player tries to cancel a whitelist countdown but none exists"),
            new MessageInformation(
                    UI_WHITELIST_ON_COUNTDOWN, "boss bar text for the whitelist on countdown")
                .argument("time in clock format"),
            new MessageInformation(
                UI_WHITELIST_ON, "message shown when the whitelist is turned back on"),
            new MessageInformation(UI_SCENARIOS, "scenarios header")
                .argument("current page")
                .argument("max pages"),
            new MessageInformation(
                SCENARIO_NOT_FOUND,
                "error shown when a player searches for a scenario that isn't registered"),
            new MessageInformation(SCENARIO_ACTIVATED, "message shown when a scenario is enabled")
                .argument("scenario name"),
            new MessageInformation(
                    SCENARIO_DEACTIVATED, "message shown when a scenario is disabled")
                .argument("scenario name"),
            new MessageInformation(UI_ACTIVE_SCENARIOS, "active scenarios header")
                .argument("current page")
                .argument("max pages"),
            new MessageInformation(SCATTERING, "scatter countdown boss bar text")
                .argument("completion percentage"),
            new MessageInformation(
                TEAMS_DISABLED,
                "error shown when a player runs a team command during a non-teams match"),
            new MessageInformation(
                ALREADY_ON_TEAM,
                "error shown when a player tries to invite someone who is already on a team"),
            new MessageInformation(
                CREATE_NOT_LONER,
                "error shown when a player tries to create a team when they're already on one"),
            new MessageInformation(
                NO_TEAMS_LEFT,
                "error shown when a player tries to claim a team when none are left"),
            new MessageInformation(
                TEAM_CLAIMED, "message shown when a player creates their own team"),
            new MessageInformation(
                    TEAM_INVITED, "message shown when a player invites someone to a team")
                .argument("invitee name"),
            new MessageInformation(
                CANNOT_MANAGE_INVITES, "error shown when a player cannot invite others to a team"),
            new MessageInformation(
                CANNOT_INVITE, "error shown when a player cannot be invited to a team"),
            new MessageInformation(
                    INVITED, "message shown to a player when they are invited to a team")
                .argument("inviter name")
                .argument("accept button"),
            new MessageInformation(UI_TEAMS, "teams list header")
                .argument("current page")
                .argument("max pages"),
            new MessageInformation(
                ALREADY_LONER,
                "error shown to a player when they try to join the loner team when they are already on it"),
            new MessageInformation(
                CANNOT_MANAGE_STARTED,
                "error shown to a player who tries to change team attributes after the round has started"),
            new MessageInformation(
                JOIN_LONER, "message shown to a player when they join the loners team"),
            new MessageInformation(
                NOT_INVITED,
                "error shown to a player when they try to join a team they aren't invited to"),
            new MessageInformation(
                    CONFIG_UPDATE, "message shown to a player when they update the UHC config")
                .argument("configuration attribute")
                .argument("old value")
                .argument("new value"),
            new MessageInformation(
                CANNOT_CONFIGURE, "error shown to a player when they can't edit the config"),
            new MessageInformation(
                CONFIG_APPLIED,
                "message shown to a player when they apply the config and create the UHC"),
            new MessageInformation(
                SCATTER_STARTED, "message shown to a player when they start the scatter countdown"),
            new MessageInformation(
                ALREADY_SCATTERED,
                "error shown to a player when they try to scatter players after they have already been scattered"),
            new MessageInformation(SCENARIOS_SCOREBOARD, "scenarios scoreboard header"),
            new MessageInformation(
                END_DISABLED,
                "error shown to players who try to enter the end when it is disabled"),
            new MessageInformation(
                NETHER_DISABLED,
                "error shown to players who try to enter the nether when it is disabled"),
            new MessageInformation(HELPOP_ALERT, "message sent to hosts when /helpop is used")
                .argument("player name")
                .argument("player message"),
            new MessageInformation(
                    XRAY_NOTIFICATION,
                    "message sent to host when a player mines too much of a certain material")
                .argument("player name")
                .argument("mined count")
                .argument("material")
                .argument("duration that the materials were mined in"),
            new MessageInformation(
                    CONFIG_APPLY_NOTIFICATION,
                    "message sent to other hosts when the config is applied")
                .argument("name of the player who applied the config"),
            new MessageInformation(
                HELPOP_SENT, "message sent to players when they send a helpop command"),
            new MessageInformation(
                NOT_ON_TEAM,
                "error sent to players who are not on a team when they run a command that requires them to be on a team"),
            new MessageInformation(
                NO_ORES,
                "error sent to players who try to share their ore counts when they have no ores"),
            new MessageInformation(
                SCATTER_GEN_STARTED,
                "message shown when world generation begins before scattering"),
            new MessageInformation(
                SCATTER_GEN_FINISHED,
                "message shown when world generation finished and scattering begins"),
            new MessageInformation(
                ERROR_POTIONS_DISABLED_ALL,
                "error shown to players when they are given potion effects when potions are disabled"),
            new MessageInformation(
                ERROR_POTIONS_DISABLED_STRENGTH,
                "error shown to players when they are given a potion effect of strength above 1 when strength is disabled"),
            new MessageInformation(
                    PLAYER_HEALTH, "message shown displaying a player's health in chat")
                .argument("player name")
                .argument("player health"),
            new MessageInformation(RULES_HEADER, "Header of the rules list in chat"),
            new MessageInformation(GOLDEN_HEAD_ITEM, "Name of every golden head item"),
            new MessageInformation(
                BORDER_REMOVE_SUCCESS, "Message shown when a border is successfully removed"),
            new MessageInformation(
                BORDER_REMOVE_FAIL,
                "Message shown when an IDIOT tries to remove a border that doesn't exist"),
            new MessageInformation(BORDER_ADDED, "message shown to a user when they add a border"),
            new MessageInformation(
                BORDER_RECALCULATED, "message shown to a user when they recalculate borders"),
            new MessageInformation(BORDER_HEADER, "Header of the border list"),
            new MessageInformation(BORDER_DESC, "description of each border")
                .argument("time")
                .argument("border size"),
            new MessageInformation(BORDER_SHRINK, "Border shrink countdown text")
                .argument("border size")
                .argument("time"),
            new MessageInformation(
                    PROJECTILE_HIT, "message sent to players when they hit someone with an arrow")
                .argument("player name")
                .argument("new health"),
            new MessageInformation(PROJECTILE_SETTING_NAME, "name of the projectile setting"),
            new MessageInformation(
                PROJECTILE_SETTING_DESC, "Description of the projectile setting"),
            new MessageInformation(PVP_COUNTDOWN, "text of the PVP delay countdown")
                .argument("time in clock format"),
            new MessageInformation(PVP_ENABLED, "Message shown when PVP has been enabled"),
            new MessageInformation(SCOREBOARD_TIME, "Time on scoreboard")
                .argument("time in clock format"),
            new MessageInformation(SCOREBOARD_BORDER, "Border on scoreboard")
                .argument("current border diameter"),
            new MessageInformation(REDDIT_BANNED, "Message shown when a user is on the UBL"),
            new MessageInformation(
                REDDIT_CHECK_BAN, "Message shown when a UBL check shows the UUID is banned"),
            new MessageInformation(
                REDDIT_CHECK_NOT_BANNED,
                "Message shown when a UBL check shows the UUID is not banned"),
            new MessageInformation(
                REDDIT_EXEMPTED, "Shows when a player exempts a player from the UBL"),
            new MessageInformation(
                REDDIT_UNEXEMPTED, "Shows when a player unexempts a player from the UBL"),
            new MessageInformation(
                SERVER_FULL,
                "Message shown when a user was kicked because the player count was exceeded"),
            new MessageInformation(SCOREBOARD_PLAYERS_NO_TOTAL, "Player count on scoreboard")
                .argument("current player count"),
            new MessageInformation(
                    SCOREBOARD_PLAYERS_TOTAL, "Player count on scoreboard with total players")
                .argument("current player count")
                .argument("total players"),
            new MessageInformation(SCOREBOARD_PLAYER_KILLS, "Player's kill count on scoreboard")
                .argument("kills"),
            new MessageInformation(SCOREBOARD_TEAM_KILLS, "Team's kill count on scoreboard")
                .argument("team-kills"),
            new MessageInformation(
                PLAYER_DISQUALIFIED, "Message sent to a player when they are disqualified"),
            new MessageInformation(
                    HOST_DISQUALIFIED_ALERT, "Message sent to hosts when a player is disqualified")
                .argument("host name")
                .argument("player name"),
            new MessageInformation(
                    DISQUALIFIED_ALERT, "Message sent to players when a player is disqualified")
                .argument("player name"),
            new MessageInformation(
                SCOREBOARD_ENVIRONMENT_DEATH,
                "Shown as the environment kills entry on the top kills scoreboard"),
            new MessageInformation(
                    HOST_SCATTERED_ALERT,
                    "Message sent to host when a player/group is forcefully scattered")
                .argument("host name")
                .argument("player/group name"),
            new MessageInformation(
                SPAWN_BEFORE_SCATTER_ERROR,
                "Message sent when a player tries to force a scatter before the round has started"),
            new MessageInformation(
                ERROR_JOIN_AFTER_SCATTER,
                "Message sent when a player tries to join the match after scatter"),
            new MessageInformation(
                TPALL_START, "Message sent to a player when they start tpall'ing"),
            new MessageInformation(TPALL_STOP, "Message sent to a player when they stop tpall'ing"),
            new MessageInformation(
                    REDDIT_EXEMPTED_HOSTS, "Shown to hosts when a player is exempted from the UBL")
                .argument("host name")
                .argument("exempted uuid"),
            new MessageInformation(
                    REDDIT_UNEXEMPTED_HOSTS,
                    "Shown to hosts when a player is unexempted from the UBL")
                .argument("host name")
                .argument("unexempted uuid"),
            new MessageInformation(FULLBRIGHT_ENABLED, "Message sent when fullbright is enabled"),
            new MessageInformation(FULLBRIGHT_DISABLED, "Message sent when fullbright is disabled"),
            new MessageInformation(
                FULLBRIGHT_ERROR,
                "Message sent when a player attempts to enable fullbright before the match starts"),
            new MessageInformation(SCOREBOARD_NEXT_BORDER, "Next border information on scoreboard")
                .argument("border diameter")
                .argument("time to next border"),
            new MessageInformation(
                    REVIVE_BROADCAST, "Message sent to players when a player has been revived")
                .argument("player name"),
            new MessageInformation(
                    REVIVE_HOST_ALERT, "Message sent to hosts when a player has been revived")
                .argument("player name")
                .argument("host name"),
            new MessageInformation(
                REVIVE_ERROR,
                "Message sent when someone tries to revive a player whose state has not been saved"),
            new MessageInformation(
                NO_RODS,
                "Message sent to a player when they try to use a rod/snowball while Rodless is enabled"),
            new MessageInformation(
                KILL_TOP_NONE,
                "Message sent when a player tries to view the top kills but there are none to show"),
            new MessageInformation(KILL_TOP_HEADER, "Header shown on top of the top kills"),
            new MessageInformation(CONFIG_ABSORPTION, "Shown in config UI for absorption"),
            new MessageInformation(CONFIG_APPLE_CHANCE, "Shown in config UI for apple chance"),
            new MessageInformation(
                CONFIG_DEATH_LIGHTNING, "Shown in config UI for death lightning"),
            new MessageInformation(
                CONFIG_FIRE_ENCHANTS, "Shown in config UI for fire enchantments"),
            new MessageInformation(CONFIG_TIMEOUT_DELAY, "Shown in config UI for timeout delay"),
            new MessageInformation(CONFIG_TEAM_SIZE, "Shown in config UI for team size"),
            new MessageInformation(CONFIG_STRENGTH_TWO, "Shown in config UI for strength two"),
            new MessageInformation(CONFIG_STARTER_FOOD, "Shown in config UI for starter food"),
            new MessageInformation(CONFIG_REDDIT_BANS, "Shown in config UI for reddit bans"),
            new MessageInformation(CONFIG_PVP_DELAY, "Shown in config UI for pvp delay"),
            new MessageInformation(CONFIG_POTIONS, "Shown in config UI for potions"),
            new MessageInformation(CONFIG_PLAYER_COUNT, "Shown in config UI for player count"),
            new MessageInformation(CONFIG_PERMA_DAY, "Shown in config UI for perma day"),
            new MessageInformation(CONFIG_NETHER, "Shown in config UI for nether"),
            new MessageInformation(CONFIG_INITIAL_BORDER, "Shown in config UI for initial border"),
            new MessageInformation(CONFIG_HEAL_DELAY, "Shown in config UI for heal delay"),
            new MessageInformation(CONFIG_GOLDEN_HEAD, "Shown in config UI for golden head"),
            new MessageInformation(CONFIG_GOD_APPLES, "Shown in config UI for god apples"),
            new MessageInformation(CONFIG_FLINT_CHANCE, "Shown in config UI for flint chance"),
            new MessageInformation(
                CONFIG_ENDER_PEARL_DAMAGE, "Shown in config UI for ender pearl damage"),
            new MessageInformation(CONFIG_END, "Shown in config UI for end"),
            new MessageInformation(CONFIG_UI_TITLE, "Title of the config UI"),
            new MessageInformation(
                CONFIG_GROUP_WORLDS, "Config group for which worlds are enabled"),
            new MessageInformation(CONFIG_GROUP_POTIONS, "Config group for potion settings"),
            new MessageInformation(CONFIG_GROUP_TIMERS, "Config group for timer settings"),
            new MessageInformation(CONFIG_GROUP_MISC, "Config group for misc settings"),
            new MessageInformation(CONFIG_GROUP_PLAYER, "Config group for player/team composition"),
            new MessageInformation(CONFIG_GROUP_APPLE, "Config group for golden apple settings"),
            new MessageInformation(CONFIG_GROUP_REDDIT, "Config group for Reddit UHC settings"),
            new GroupFormatInformation(
                "uhc.scenario.desc.bleeding-sweets",
                SCEN_DESC_BLEEDING,
                "Description of the bleeding sweets scenario"),
            new GroupFormatInformation(
                "uhc.scenario.desc.cut-clean",
                SCEN_DESC_CUT_CLEAN,
                "Description of the cut clean scenario"),
            new GroupFormatInformation(
                "uhc.scenario.desc.hastey-boys",
                SCEN_DESC_HASTEY_BOYS,
                "Description of the hastey boys scenario"),
            new GroupFormatInformation(
                "uhc.scenario.desc.rodless",
                SCEN_DESC_RODLESS,
                "Description of the rodless scenario"),
            new GroupFormatInformation(
                "uhc.scenario.desc.timber", SCEN_DESC_TIMBER, "Description of the timber scenario"),
            new GroupFormatInformation(
                "uhc.scenario.desc.time-bomb",
                SCEN_DESC_TIME_BOMB,
                "Description of the time bomb scenario"),
            new MessageInformation(TEAM_FULL, "Error shown when invited to a full team")
                .argument("maximum team size"),
            new MessageInformation(INVITE_ACCEPT, "Accept button shown as part of a team invite"),
            new MessageInformation(
                    INVITE_ACCEPT_HOVER, "Shown when a player hovers over the accept button")
                .argument("team owner name"),
            new MessageInformation(TEAM_JOINED, "Shown when you've accepted a team invite")
                .argument("team owner name"),
            new MessageInformation(INVITE_ACCEPTED, "Shown when a player has accepted your invite")
                .argument("player name"),
            new LegacyGroupInformation(
                "uhc.teams.book.content",
                TEAM_INFO_BOOK_CONTENT,
                "Book given to a player when teams are enabled"),
            new MessageInformation(
                TEAM_INFO_BOOK_TITLE, "Title of book given to a player when teams are enabled"),
            new MessageInformation(
                NO_TEAM_CREATED,
                "Shown when a player invites someone to a team when they haven't created one"),
            new MessageInformation(
                FIRE_ENCHANTS_DISABLED,
                "Shown when a player tries to enchant an item with a fire enchant when they are disabled"),
            new MessageInformation(CONFIG_HORSES, "The config item for whether horses are enabled"),
            new MessageInformation(
                HORSES_DISABLED,
                "Shown when a player tries to mount horses when they are disabled"),
            new GroupFormatInformation(
                "uhc.scenario.desc.noclean",
                SCEN_DESC_NO_CLEAN,
                "Description of the no clean scenario"),
            new MessageInformation(
                    NOCLEAN_GIVEN, "Sent when a player gains invulnerability from noclean")
                .argument("seconds to expiry"),
            new MessageInformation(
                    NOCLEAN_REMAINING,
                    "Sent when a player has x time left of noclean invulnerability")
                .argument("seconds to expiry"),
            new MessageInformation(
                NOCLEAN_EXPIRED, "Sent when a player's noclean invulnerability has expired"),
            new MessageInformation(
                NOCLEAN_LOST, "Sent when a player loses noclean invulnerability"),
            new GroupFormatInformation(
                "uhc.scenario.desc.blood-diamond",
                SCEN_DESC_BLOOD_DIAMOND,
                "Description of the blood diamond scenario"),
            new GroupFormatInformation(
                "uhc.scenario.desc.golden-retriever",
                SCEN_DESC_GOLDEN_RETRIEVER,
                "Description of the golden retriever scenario"),
            new GroupFormatInformation(
                "uhc.scenario.desc.backpack",
                SCEN_DESC_BACKPACK,
                "Description of the backpack scenario"),
            new MessageInformation(
                    BACKPACK_NO_INV, "Error shown when a group doesn't have a backpack")
                .argument("group name"),
            new MessageInformation(
                BACKPACK_NOT_ENABLED,
                "Error shown when a player tries to use backpacks when they aren't enabled"),
            new MessageInformation(
                BACKPACK_NOT_STARTED,
                "Error sent when a player tries to use backpacks before the game has started"),
            new MessageInformation(
                BACKPACK_NOT_FOUND,
                "Error sent when a player tries to open a backpack for a team that doesn't exist"),
            new GroupFormatInformation(
                "uhc.scenario.desc.diamondless",
                SCEN_DESC_DIAMONDLESS,
                "Description of the diamondless scenario"),
            new GroupFormatInformation(
                "uhc.scenario.desc.weakest-link",
                SCEN_DESC_WEAKEST_LINK,
                "Description of the weakest link scenario"),
            new MessageInformation(
                WEAKEST_LINK_NONE, "Message shown when a no one can be eliminated by weakest link"),
            new MessageInformation(
                WEAKEST_LINK_SELF, "Message shown to players eliminated by weakest link"),
            new MessageInformation(
                    WEAKEST_LINK_OTHER,
                    "Message sent to everyone when a player is eliminated by weakest link")
                .argument("player name"),
            new GroupFormatInformation(
                "uhc.scenario.desc.bats", SCEN_DESC_BATS, "Description of the bats scenario"),
            new GroupFormatInformation(
                "uhc.scenario.desc.infinite-enchanter",
                SCEN_DESC_INFINITE_ENCHANTER,
                "Description of the infinite enchanter scenario")));
  }

  /**
   * Add the global prefix to a message.
   *
   * @param message to prefix
   * @return the message with the global UHC prefix
   */
  public static Localizable prefix(Localizable message) {
    return PREFIX.with(message);
  }
}

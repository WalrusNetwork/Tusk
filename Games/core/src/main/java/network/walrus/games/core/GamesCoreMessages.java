package network.walrus.games.core;

import java.util.Arrays;
import network.walrus.utils.core.text.LocalizedFormat;
import network.walrus.utils.core.translation.MessageReferenceHolder;

/**
 * Translations for messages shown by the core plugin.
 *
 * @author Austin Mayes
 */
public class GamesCoreMessages extends MessageReferenceHolder {

  public static final LocalizedFormat ERROR_NO_LIBRARY = get("error.no-library");
  public static final LocalizedFormat ERROR_ALREADY_STARTED = get("error.already-started");
  public static final LocalizedFormat UI_SPECTATORS = get("ui.spectators");
  public static final LocalizedFormat UI_TELEPORT_DEVICE_NAME = get("ui.compass.name");
  public static final LocalizedFormat UI_TELEPORT_DEVICE_DESCRIPTION =
      get("ui.compass.description");
  public static final LocalizedFormat GENERIC_DEATH = get("generic.death");
  public static final LocalizedFormat GENERIC_DEATH_FALLBACK = get("generic.death-fallback");
  public static final LocalizedFormat GENERIC_RESPAWN_PUNCH = get("generic.respawn.punch");
  public static final LocalizedFormat GENERIC_RESPAWN_AUTO = get("generic.respawn.auto");
  public static final LocalizedFormat GENERIC_RESPAWN_MANUAL = get("generic.respawn.manual");
  public static final LocalizedFormat UI_ROUND_STARTED = get("ui.round.started");
  public static final LocalizedFormat UI_PLAY = get("ui.play");
  public static final LocalizedFormat UI_ROUND_STARTING = get("ui.round.starting");
  public static final LocalizedFormat UI_ROUND_STARTING_PLURAL = get("ui.round.starting-plural");
  public static final LocalizedFormat GENERIC_AUTOSTART_NEED = get("generic.auto-start.need");
  public static final LocalizedFormat GENERIC_AUTOSTART_BALANCE = get("generic.auto-start.balance");
  public static final LocalizedFormat UI_BY = get("ui.by");
  public static final LocalizedFormat UI_WINS = get("ui.results.wins");
  public static final LocalizedFormat DEATH_DIED = get("death.died");
  public static final LocalizedFormat DEATH_BY_PROJECTILE = get("death.projectile");
  public static final LocalizedFormat DEATH_BY_VOID = get("death.void");
  public static final LocalizedFormat DEATH_BY_LAVA = get("death.lava");
  public static final LocalizedFormat DEATH_BY_FALL = get("death.fall");
  public static final LocalizedFormat DEATH_BY_EXPLOSIVE = get("death.explosive");
  public static final LocalizedFormat DEATH_BY_BLOCK = get("death.block");
  public static final LocalizedFormat DEATH_BY_ANVIL = get("death.anvil");
  public static final LocalizedFormat DEATH_BY_PLAYER_VOID = get("death.player.void");
  public static final LocalizedFormat DEATH_BY_PLAYER_PROJECTILE = get("death.player.projectile");
  public static final LocalizedFormat DEATH_BY_MELEE = get("death.melee");
  public static final LocalizedFormat DEATH_BY_MELEE_FISTS = get("death.melee-fists");
  public static final LocalizedFormat DEATH_SPLEEF_BY_PLAYER = get("death.spleef.player");
  public static final LocalizedFormat DEATH_SPLEEF_FLOOR_FALL = get("death.spleef.fall");
  public static final LocalizedFormat DEATH_SPLEEF_FLOOR_VOID = get("death.spleef.void");
  public static final LocalizedFormat DEATH_BLOCKS = get("death.blocks");
  public static final LocalizedFormat DEATH_SHOT_WATER_FALL = get("death.shot.water.fall");
  public static final LocalizedFormat DEATH_SHOT_WATER_VOID = get("death.shot.water.void");
  public static final LocalizedFormat DEATH_SHOT_LADDER_FALL = get("death.shot.ladder.fall");
  public static final LocalizedFormat DEATH_SHOT_LADDER_VOID = get("death.shot.ladder.void");
  public static final LocalizedFormat DEATH_SHOT_FLOOR_FALL = get("death.shot.floor.fall");
  public static final LocalizedFormat DEATH_SHOT_FLOOR_VOID = get("death.shot.floor.void");
  public static final LocalizedFormat DEATH_HIT_WATER_FALL = get("death.hit.water.fall");
  public static final LocalizedFormat DEATH_HIT_WATER_VOID = get("death.hit.water.void");
  public static final LocalizedFormat DEATH_HIT_LADDER_FALL = get("death.hit.ladder.fall");
  public static final LocalizedFormat DEATH_HIT_LADDER_VOID = get("death.hit.ladder.void");
  public static final LocalizedFormat DEATH_HIT_FLOOR_FALL = get("death.hit.floor.fall");
  public static final LocalizedFormat DEATH_HIT_FLOOR_VOID = get("death.hit.floor.void");
  public static final LocalizedFormat DEATH_BY_PLAYER_TNT = get("death.player.tnt");
  public static final LocalizedFormat DEATH_BY_PLAYER_ANVIL = get("death.player.anvil");
  public static final LocalizedFormat DEATH_BY_PLAYER_MOB = get("death.player.mob");
  public static final LocalizedFormat DEATH_BY_MOB = get("death.mob");
  public static final LocalizedFormat DEATH_BY_MOB_EXPLODE = get("death.mob.explode");
  public static final LocalizedFormat DEATH_BY_MOB_PROJECTILE = get("death.mob.projectile");
  public static final LocalizedFormat SETTING_DEATHMESSAGE_NAME = get("settings.dms.name");
  public static final LocalizedFormat SETTING_DEATHMESSAGE_DESCRIPTION = get("settings.dms.desc");
  public static final LocalizedFormat CANNOT_MODIFY_PORTALS = get("error.modify.portals");
  public static final LocalizedFormat ERROR_CHAT_GLOBAL_DISABLED = get("channels.disabled.global");
  public static final LocalizedFormat GENERIC_CHAT_GLOBAL_ENABLED = get("channels.enabled.global");
  public static final LocalizedFormat ERROR_CHAT_TEAM_DISABLED = get("channels.disabled.team");
  public static final LocalizedFormat GENERIC_CHAT_TEAM_ENABLED = get("channels.enabled.team");
  public static final LocalizedFormat QUICKCHAT_BAD_FORMAT = get("channels.quickchat.bad-format");
  public static final LocalizedFormat QUICKCHAT_SWITCH = get("channels.quickchat.switch");
  public static final LocalizedFormat QUICKCHAT_CANNOT_SWITCH =
      get("channels.quickchat.cannot-switch");
  public static final LocalizedFormat QUICKCHAT_DISABLED = get("channels.disabled.quickchat");
  public static final LocalizedFormat QUICKCHAT_ENABLED = get("channels.enabled.quickchat");
  public static final LocalizedFormat ERROR_CANNOT_JOIN_FINISHED =
      get("groups.join-error.finished");
  public static final LocalizedFormat ERROR_CANNOT_JOIN_FULL = get("groups.join-error.full");
  public static final LocalizedFormat ERROR_CANNOT_JOIN_OVERFILL =
      get("groups.join-error.overfill");
  public static final LocalizedFormat GENERIC_JOINED = get("groups.joined");
  public static final LocalizedFormat TIME_REMAINING = get("ui.time-remaining");
  public static final LocalizedFormat UI_WINNERS = get("ui.results.winners");
  public static final LocalizedFormat UI_SPEC_JOIN_NEXT = get("ui.results.join-next");
  public static final LocalizedFormat UI_TEAM_LOST = get("ui.results.lost");
  public static final LocalizedFormat UI_TEAM_WON = get("ui.results.won");
  public static final LocalizedFormat UI_TIE = get("ui.results.tie");
  public static final LocalizedFormat ERROR_MAX_BUILD_HEIGHT = get("error.max-build-height");
  public static final LocalizedFormat UI_HEALTH = get("ui.health");
  public static final LocalizedFormat UI_FOOD_LEVEL = get("ui.food-level");
  public static final LocalizedFormat ERROR_ONLY_ROUNDS = get("error.only-rounds");
  public static final LocalizedFormat UI_ALERT = get("ui.map.alert");
  public static final LocalizedFormat UI_TIP = get("ui.map.tip");
  public static final LocalizedFormat INFO_OBJECTIVE = get("ui.info.objective");
  public static final LocalizedFormat INFO_RULES = get("ui.info.rules");
  public static final LocalizedFormat SCOREBOARD_TOGGLE = get("ui.scoreboard.toggle");
  public static final LocalizedFormat SCOREBOARD_LIST = get("ui.scoreboard.list");
  public static final LocalizedFormat SCOREBOARD_ALTERNATING_TOGGLED =
      get("ui.scoreboard.alternate-toggle");
  public static final LocalizedFormat OBSERVER_SETTING_NAME = get("settings.see-observers.name");
  public static final LocalizedFormat OBSERVER_SETTING_DESC = get("settings.see-observers.desc");

  // Stats
  public static final LocalizedFormat KILLS = get("stats.kills");
  public static final LocalizedFormat DEATHS = get("stats.deaths");
  public static final LocalizedFormat WOOLS = get("stats.wools");
  public static final LocalizedFormat FLAGS = get("stats.flags");
  public static final LocalizedFormat KDR = get("stats.kdr");
  public static final LocalizedFormat STATS_HEADER = get("stats.header");

  static {
    USED_MESSAGES.addAll(
        Arrays.asList(
            new MessageInformation(
                ERROR_NO_LIBRARY,
                "error shown when a map library is defined for a directory that does not exist"),
            new MessageInformation(
                ERROR_ALREADY_STARTED,
                "error shown when a player tries to start a round that has already started"),
            new MessageInformation(UI_SPECTATORS, "spectators team name"),
            new MessageInformation(
                UI_TELEPORT_DEVICE_NAME, "name of the observer teleport compass"),
            new MessageInformation(
                UI_TELEPORT_DEVICE_DESCRIPTION, "lore of the observer teleport compass"),
            new MessageInformation(GENERIC_DEATH, "title of the death screen"),
            new MessageInformation(
                    GENERIC_DEATH_FALLBACK,
                    "death message sent to players without support for title screens")
                .argument("seconds to respawn"),
            new MessageInformation(
                GENERIC_RESPAWN_PUNCH,
                "subtitle of the respawn screen when a player must punch to respawn"),
            new MessageInformation(
                    GENERIC_RESPAWN_AUTO,
                    "subtitle of the respawn screen when a player will be automatically re-spawned")
                .argument("seconds before respawn is possible"),
            new MessageInformation(
                    GENERIC_RESPAWN_MANUAL,
                    "subtitle of the respawn screen when a player will need to punch ti respawn when the time is up")
                .argument("seconds before respawn is possible"),
            new MessageInformation(UI_ROUND_STARTED, "message shown after the round starts"),
            new MessageInformation(UI_PLAY, "title shown after the round start countdown"),
            new MessageInformation(
                    UI_ROUND_STARTING,
                    "boss bar text of the starting countdown with a singular remaining time")
                .argument("time in seconds"),
            new MessageInformation(
                    UI_ROUND_STARTING_PLURAL,
                    "boss bar text of the starting countdown with a plural remaining time")
                .argument("time in seconds"),
            new MessageInformation(
                    GENERIC_AUTOSTART_NEED,
                    "boss bar text of the auto-start countdown when it is being stopped by a lack of players")
                .argument("needed teams"),
            new MessageInformation(
                GENERIC_AUTOSTART_BALANCE,
                "boss bar text of the auto-start countdown when it is being stopped by a team imbalance"),
            new MessageInformation(UI_BY, "map author information message")
                .argument("map name")
                .argument("author names"),
            new MessageInformation(UI_WINS, "title shown when a single player/group wins")
                .argument("winner name"),
            new MessageInformation(
                    DEATH_DIED, "death message for when a player is killed by unknown causes")
                .argument("player name"),
            new MessageInformation(
                    DEATH_BY_PROJECTILE, "death message for when a player is killed by being shot")
                .argument("player name"),
            new MessageInformation(
                    DEATH_BY_VOID,
                    "death message for when a player is killed by falling in the void")
                .argument("player name"),
            new MessageInformation(
                    DEATH_BY_LAVA,
                    "death message for when a player is killed by being burned in lava")
                .argument("player name"),
            new MessageInformation(
                    DEATH_BY_FALL, "death message for when a player is killed by falling")
                .argument("player name")
                .argument("distance"),
            new MessageInformation(
                    DEATH_BY_EXPLOSIVE,
                    "death message for when a player is killed by being blown up")
                .argument("player name"),
            new MessageInformation(
                    DEATH_BY_BLOCK,
                    "death message for when a player is killed by being squashed by a block")
                .argument("player name"),
            new MessageInformation(
                    DEATH_BY_ANVIL,
                    "death message for when a player is killed by being squashed by an anvil")
                .argument("player name"),
            new MessageInformation(
                    DEATH_BY_PLAYER_VOID,
                    "death message for when a player is killed by being hit into the void")
                .argument("player name")
                .argument("attacker name"),
            new MessageInformation(
                    DEATH_BY_PLAYER_PROJECTILE,
                    "death message for when a player is killed by being shot")
                .argument("player name")
                .argument("attacker name")
                .argument("projectile type")
                .argument("distance"),
            new MessageInformation(
                    DEATH_BY_MELEE,
                    "death message for when a player is killed by a person's hand-held weapon")
                .argument("player name")
                .argument("attacker name")
                .argument("weapon type"),
            new MessageInformation(
                    DEATH_BY_MELEE_FISTS,
                    "death message for when a player is killed by a person's fists")
                .argument("player name")
                .argument("attacker name"),
            new MessageInformation(
                    DEATH_SPLEEF_BY_PLAYER,
                    "death message for when a player is killed by being spleefed by another player")
                .argument("player name")
                .argument("attacker name"),
            new MessageInformation(
                    DEATH_SPLEEF_FLOOR_FALL,
                    "death message for when a player is killed by being spleefed off a block and fell to their death")
                .argument("player name")
                .argument("attacker name"),
            new MessageInformation(
                    DEATH_SPLEEF_FLOOR_VOID,
                    "death message for when a player is killed by being spleefed off a block and into the void")
                .argument("player name")
                .argument("attacker name"),
            new MessageInformation(
                    DEATH_BLOCKS,
                    "blocks amendment for projectile shoot message where distance is known")
                .argument("number of blocks"),
            new MessageInformation(
                    DEATH_SHOT_WATER_FALL,
                    "death message for when a player is killed by being shot out of water and fell to their death")
                .argument("player name")
                .argument("attacker name"),
            new MessageInformation(
                    DEATH_SHOT_WATER_VOID,
                    "death message for when a player is killed by being shot out of water and into the void")
                .argument("player name")
                .argument("attacker name"),
            new MessageInformation(
                    DEATH_SHOT_LADDER_FALL,
                    "death message for when a player is killed by being shot off of a ladder and fell to their death")
                .argument("player name")
                .argument("attacker name"),
            new MessageInformation(
                    DEATH_SHOT_LADDER_VOID,
                    "death message for when a player is killed by being shot off of a ladder and into the void")
                .argument("player name")
                .argument("attacker name"),
            new MessageInformation(
                    DEATH_SHOT_FLOOR_FALL,
                    "death message for when a player is killed by being shot off of the ground and fell to their death")
                .argument("player name")
                .argument("attacker name"),
            new MessageInformation(
                    DEATH_SHOT_FLOOR_VOID,
                    "death message for when a player is killed by being shot off of the ground and into the void")
                .argument("player name")
                .argument("attacker name"),
            new MessageInformation(
                    DEATH_HIT_WATER_FALL,
                    "death message for when a player is killed by being hit out of water and fell to their death")
                .argument("player name")
                .argument("attacker name"),
            new MessageInformation(
                    DEATH_HIT_WATER_VOID,
                    "death message for when a player is killed by being hit out of water and into the void")
                .argument("player name")
                .argument("attacker name"),
            new MessageInformation(
                    DEATH_HIT_LADDER_FALL,
                    "death message for when a player is killed by being hit off of a ladder and fell to their death")
                .argument("player name")
                .argument("attacker name"),
            new MessageInformation(
                    DEATH_HIT_LADDER_VOID,
                    "death message for when a player is killed by being hit off of a ladder and into the void")
                .argument("player name")
                .argument("attacker name"),
            new MessageInformation(
                    DEATH_HIT_FLOOR_FALL,
                    "death message for when a player is killed by being hit off of the ground and fell to their death")
                .argument("player name")
                .argument("attacker name"),
            new MessageInformation(
                    DEATH_HIT_FLOOR_VOID,
                    "death message for when a player is killed by being hit off of the ground and into the void")
                .argument("player name")
                .argument("attacker name"),
            new MessageInformation(
                    DEATH_BY_PLAYER_TNT,
                    "death message for when a player is killed by an owned TNT")
                .argument("player name")
                .argument("owner name"),
            new MessageInformation(
                    DEATH_BY_PLAYER_ANVIL,
                    "death message for when a player is killed by an owned anvil")
                .argument("player name")
                .argument("owner name"),
            new MessageInformation(
                    DEATH_BY_PLAYER_MOB,
                    "death message for when a player is killed by an owned mob")
                .argument("player name")
                .argument("owner name")
                .argument("mob name"),
            new MessageInformation(
                    DEATH_BY_MOB, "death message for when a player is killed by a mob")
                .argument("player name")
                .argument("mob name"),
            new MessageInformation(
                    DEATH_BY_MOB_EXPLODE,
                    "death message for when a player is killed by an exploding mob")
                .argument("player name")
                .argument("mob name"),
            new MessageInformation(
                    DEATH_BY_MOB_PROJECTILE,
                    "death message for when a player is killed by a mob with a projectile")
                .argument("player name")
                .argument("mob name"),
            new MessageInformation(SETTING_DEATHMESSAGE_NAME, "death message setting name"),
            new MessageInformation(
                SETTING_DEATHMESSAGE_DESCRIPTION, "death message setting description"),
            new MessageInformation(
                CANNOT_MODIFY_PORTALS,
                "error shown to players when they try to modify portal entrance/exit areas"),
            new MessageInformation(
                ERROR_CHAT_GLOBAL_DISABLED, "error shown when a player can't use global chat"),
            new MessageInformation(
                GENERIC_CHAT_GLOBAL_ENABLED, "message shown when a player switches to global chat"),
            new MessageInformation(
                ERROR_CHAT_TEAM_DISABLED, "error shown when a player can't use team chat"),
            new MessageInformation(
                GENERIC_CHAT_TEAM_ENABLED, "message shown when a player switches to team chat"),
            new MessageInformation(
                QUICKCHAT_BAD_FORMAT,
                "error shown to players when they enter an invalid quick chat format"),
            new MessageInformation(
                QUICKCHAT_SWITCH,
                "information message shown to players telling them how to switch out of quick chat mode"),
            new MessageInformation(
                QUICKCHAT_CANNOT_SWITCH,
                "error shown to players when they try to disable quick chat when they can't"),
            new MessageInformation(
                QUICKCHAT_DISABLED, "message shown to players when they disable quick chat"),
            new MessageInformation(
                QUICKCHAT_ENABLED, "message shown to players when they enable quick chat"),
            new MessageInformation(
                ERROR_CANNOT_JOIN_FINISHED,
                "error shown to a player when they try to join a team after the round ends"),
            new MessageInformation(
                ERROR_CANNOT_JOIN_FULL,
                "error shown to a player when they can't join over the max team size"),
            new MessageInformation(
                ERROR_CANNOT_JOIN_OVERFILL,
                "error shown to a player when they can't join over the max overfill limit"),
            new MessageInformation(GENERIC_JOINED, "message shown when a player joins a team")
                .argument("team name"),
            new MessageInformation(TIME_REMAINING, "time remaining boss bar message")
                .argument("time left in clock format"),
            new MessageInformation(UI_WINNERS, "winners message header"),
            new MessageInformation(
                UI_SPEC_JOIN_NEXT,
                "message shown to players who were spectating when the round ends"),
            new MessageInformation(UI_TEAM_LOST, "message shown to players on the loosing team"),
            new MessageInformation(UI_TEAM_WON, "message shown to players on the winning team"),
            new MessageInformation(UI_TIE, "message shown when the match ties"),
            new MessageInformation(
                ERROR_MAX_BUILD_HEIGHT,
                "error shown to players who build above the max build height"),
            new MessageInformation(UI_HEALTH, "health level item name")
                .argument("current health")
                .argument("max health"),
            new MessageInformation(UI_FOOD_LEVEL, "food level item name")
                .argument("current food")
                .argument("max food"),
            new MessageInformation(
                ERROR_ONLY_ROUNDS,
                "error shown to players when they execute a command which requires the presence of a game round"),
            new MessageInformation(UI_ALERT, "alert shown to users when specified by map devs")
                .argument("message"),
            new MessageInformation(UI_TIP, "tip shown to users when specified by map devs")
                .argument("message"),
            new MessageInformation(
                    INFO_OBJECTIVE, "translation of 'objective' shown when user requests map info")
                .argument("objective"),
            new MessageInformation(
                    INFO_RULES, "translation of 'rules' shown when user requests map info")
                .argument("rules"),
            new MessageInformation(
                    SCOREBOARD_TOGGLE,
                    "message shown to players when they toggle which scoreboard they see")
                .argument("scoreboard name"),
            new MessageInformation(
                    SCOREBOARD_LIST, "message displaying a list of possible scoreboards")
                .argument("comma separated list of names"),
            new MessageInformation(
                    SCOREBOARD_ALTERNATING_TOGGLED,
                    "message shown to a user when they turn off/on scoreboard alternating")
                .argument("new value"),
            new MessageInformation(
                OBSERVER_SETTING_NAME, "Name of the see other observers setting"),
            new MessageInformation(
                OBSERVER_SETTING_DESC, "Description of the see observers setting"),
            new MessageInformation(KILLS, "shown in stats command output for kills"),
            new MessageInformation(DEATHS, "shown in stats command output for deaths"),
            new MessageInformation(WOOLS, "shown in stats command output for wools"),
            new MessageInformation(FLAGS, "shown in stats command output for flags"),
            new MessageInformation(KDR, "shown in stats command output for KDR"),
            new MessageInformation(STATS_HEADER, "shown as stats command header")
                .argument("player name")));
  }
}

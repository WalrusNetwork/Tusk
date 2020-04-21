package network.walrus.games.octc;

import java.util.Arrays;
import network.walrus.utils.core.text.LocalizedFormat;
import network.walrus.utils.core.translation.MessageReferenceHolder;
import org.bukkit.DyeColor;

/**
 * Messages specifically for the OCN component.
 *
 * @author Austin Mayes
 */
public class OCNMessages extends MessageReferenceHolder {

  // CTF
  public static final LocalizedFormat FLAG_RECOVER_SINGULAR =
      get("objectives.flag.recover.singular");
  public static final LocalizedFormat FLAG_RECOVER_PLURAL = get("objectives.flag.recover.plural");
  public static final LocalizedFormat FLAG_RESPAWN_SINGULAR =
      get("objectives.flag.respawn.singular");
  public static final LocalizedFormat FLAG_RESPAWN_PLURAL = get("objectives.flag.respawn.plural");
  public static final LocalizedFormat FLAG_CANT_BREAK_POST = get("objectives.flag.cant.break-post");
  public static final LocalizedFormat FLAG_CANT_CAPTURE_NET =
      get("objectives.flag.cant.capture-net");
  public static final LocalizedFormat FLAG_PICKUP = get("objectives.flag.pickup");
  public static final LocalizedFormat FLAG_CAPTURE = get("objectives.flag.capture");
  public static final LocalizedFormat FLAG_DROP = get("objectives.flag.drop");
  public static final LocalizedFormat FLAG_RESPAWN_END = get("objectives.flag.respawn.end");
  public static final LocalizedFormat FLAG_CAPTURE_ACTIONBAR =
      get("objectives.flag.capture.actionbar");

  // CTW
  public static final LocalizedFormat WOOL_PLACED = get("objectives.wool.placed");
  public static final LocalizedFormat WOOL_PICKUP = get("objectives.wool.pickup");
  public static final LocalizedFormat WOOL_WRONG_WOOL = get("objectives.wool.wrong-wool");
  public static final LocalizedFormat WOOL_BAD_WOOL = get("objectives.wool.bad-wool");
  public static final LocalizedFormat WOOL_ALREADY_PLACED = get("objectives.wool.already-placed");
  public static final LocalizedFormat WOOL_CANNOT_CRAFT = get("objectives.wool.cannot-craft");
  public static final LocalizedFormat WOOL_CHEST_ILLEGAL_ITEMS =
      get("objectives.wool.chest-illegal-items");

  // join gui
  public static final LocalizedFormat JOIN_SWORD_NAME = get("groups.join-gui.sword-name");
  public static final LocalizedFormat PICKER_HELMET_NAME = get("groups.join-gui.helmet-name");
  public static final LocalizedFormat PICKER_GUI_TITLE = get("groups.join-gui.picker-title");
  public static final LocalizedFormat JOIN_HINT = get("groups.join-hint");

  // join errors
  public static final LocalizedFormat ERROR_CANNOT_PICK_TEAM = get("groups.cannot-pick");
  public static final LocalizedFormat ERROR_TEAM_NOT_FOUND = get("groups.not-found");
  public static final LocalizedFormat ERROR_ALREADY_SPECTATOR = get("groups.already.spectator");
  public static final LocalizedFormat ERROR_ALREADY_TEAM = get("groups.already.team");
  public static final LocalizedFormat ERROR_ALREADY_PLAYING = get("groups.already.playing");
  public static final LocalizedFormat ERROR_CANNOT_JOIN_IMBALANCE =
      get("groups.join-error.imbalance");

  // team size errors
  public static final LocalizedFormat ERROR_NEGATIVE_TEAMSIZE = get("groups.teamsize.negative");

  public static final LocalizedFormat UI_POINTS = get("ui.points");
  public static final LocalizedFormat SCOREBOX_POINT = get("objectives.scorebox.scored");

  // hills
  public static final LocalizedFormat CAPTURE_HILL = get("objectives.hill.captured");
  public static final LocalizedFormat UNCAPTURE_HILL = get("objectives.hill.uncaptured");
  public static final LocalizedFormat DOMINATION_OVERTIME_BOSSBAR =
      get("objectives.domination.overtime.bossbar");
  public static final LocalizedFormat DOMINATION_OVERTIME_BROADCAST =
      get("objectives.domination.overtime.broadcast");

  public static final LocalizedFormat KOTH_OVERTIME_BOSSBAR =
      get("objectives.koth.overtime.bossbar");
  public static final LocalizedFormat KOTH_OVERTIME_BROADCAST =
      get("objectives.koth.overtime.broadcast");

  // TDM
  public static final LocalizedFormat TDM_OVERTIME_BOSSBAR = get("objectives.tdm.overtime.bossbar");
  public static final LocalizedFormat TDM_OVERTIME_BROADCAST =
      get("objectives.tdm.overtime.broadcast");
  public static final LocalizedFormat TDM_OVERTIME_DEATH = get("objectives.tdm.overtime.death");
  public static final LocalizedFormat TDM_OVERTIME_ACTIVE = get("objectives.tdm.overtime.active");

  // dt(c)(m)
  public static final LocalizedFormat UI_CORE = get("ui.core");
  public static final LocalizedFormat CORE_LEAKED = get("objectives.core.leaked");
  public static final LocalizedFormat CORE_TOUCHED = get("objectives.core.touched");
  public static final LocalizedFormat MONUMENT_TOUCHED = get("objectives.monument.touched");
  public static final LocalizedFormat MONUMENT_BROKEN = get("objectives.monument.broken");
  public static final LocalizedFormat ERROR_OBJECTIVE_REPAIR_ENEMY =
      get("objectives.destroyable.repair.enemy");
  public static final LocalizedFormat ERROR_OBJECTIVE_CANNOT_REPAIR =
      get("objectives.destroyable.repair.own");
  public static final LocalizedFormat ERROR_OBJECTIVE_DAMAGE =
      get("objectives.destroyable.damage.other");
  public static final LocalizedFormat ERROR_OBJECTIVE_DAMAGE_OWN =
      get("objectives.destroyable.damage.own");
  public static final LocalizedFormat GENERIC_OBJECTIVE_REPAIRED =
      get("objectives.destroyable.repair.success");
  public static final LocalizedFormat MODE_CHANGE_SUCCESS = get("modes.change.success");
  public static final LocalizedFormat MODE_CHANGE_FAIL = get("modes.change.fail");
  public static final LocalizedFormat MODE_CHANGE_COUNTDOWN = get("modes.change.countdown");

  // Rotation map change messages
  public static final LocalizedFormat MAP_CHANGE_COUNTDOWN = get("maps.countdown");
  public static final LocalizedFormat MAP_VOTE_ANNOUNCEMENT = get("maps.vote.announcement");
  public static final LocalizedFormat MAP_VOTE_NO_MAP = get("maps.vote.no-map-found");
  public static final LocalizedFormat MAP_VOTE_SUCCESS = get("maps.vote.success");
  public static final LocalizedFormat MAP_VOTE_CHANGED = get("maps.vote.changed");
  public static final LocalizedFormat MAP_VOTE_NONE = get("maps.vote.no-vote");
  public static final LocalizedFormat MAP_VOTE_UNCHANGED = get("maps.vote.already-voted");
  public static final LocalizedFormat MAP_VOTE_TALLY = get("maps.vote.tally");
  public static final LocalizedFormat MAP_VOTE_TALLY_FINAL = get("maps.vote.tally-final");
  public static final LocalizedFormat MAP_VOTE_COUNTDOWN = get("maps.vote.countdown");
  public static final LocalizedFormat MAP_VOTE_UI_TITLE = get("maps.vote.title");
  public static final LocalizedFormat MAP_VOTE_SHOW_SETTING = get("maps.vote.show-setting.name");
  public static final LocalizedFormat MAP_VOTE_SHOW_SETTING_DESC =
      get("maps.vote.show-setting.desc");

  static {
    USED_MESSAGES.addAll(
        Arrays.asList(
            new MessageInformation(WOOL_PLACED, "message when player places a wool")
                .argument("wool name")
                .argument("placer name"),
            new MessageInformation(WOOL_PICKUP, "message when player picks up a wool")
                .argument("player name")
                .argument("wool name"),
            new MessageInformation(
                WOOL_WRONG_WOOL, "error shown when a player places a wool they can't complete"),
            new MessageInformation(WOOL_BAD_WOOL, "error shown when player places the wrong wool")
                .argument("wool name"),
            new MessageInformation(
                WOOL_ALREADY_PLACED,
                "error shown when a wool is interacted with that is already placed"),
            new MessageInformation(WOOL_CANNOT_CRAFT, "error shown when a wool can't be crafted")
                .argument("wool name"),
            new MessageInformation(
                ERROR_CANNOT_PICK_TEAM, "error shown when a player can't pick their team"),
            new MessageInformation(
                ERROR_TEAM_NOT_FOUND,
                "error shown when a player searches for a team that doesn't exist"),
            new MessageInformation(
                ERROR_ALREADY_SPECTATOR,
                "error shown when a player is a spectator and tries to leave"),
            new MessageInformation(
                    ERROR_ALREADY_TEAM,
                    "error shown when a player tries to join a team they are already on")
                .argument("team name"),
            new MessageInformation(
                ERROR_ALREADY_PLAYING, "error shown when a player tries to join while playing"),
            new MessageInformation(
                ERROR_CANNOT_JOIN_IMBALANCE,
                "error shown when a player can't join a team because an imbalance will be created"),
            new MessageInformation(
                ERROR_NEGATIVE_TEAMSIZE,
                "error shown when trying to give a team a negative team size"),
            new MessageInformation(UI_POINTS, "name of a score objective"),
            new MessageInformation(CAPTURE_HILL, "message shown when a hill is captured")
                .argument("hill name")
                .argument("hill owner name"),
            new MessageInformation(UNCAPTURE_HILL, "message shown when a hill is uncaptured")
                .argument("old owner name")
                .argument("hill name"),
            new MessageInformation(
                DOMINATION_OVERTIME_BOSSBAR, "message in the bossbar during domination overtime"),
            new MessageInformation(
                DOMINATION_OVERTIME_BROADCAST,
                "message broadcasted in chat at start of domination overtime"),
            new MessageInformation(
                KOTH_OVERTIME_BOSSBAR, "message in the bossbar during koth overtime"),
            new MessageInformation(
                KOTH_OVERTIME_BROADCAST, "message broadcasted in chat at start of koth overtime"),
            new MessageInformation(
                TDM_OVERTIME_BROADCAST, "message broadcasted in chat at start of tdm overtime"),
            new MessageInformation(
                    TDM_OVERTIME_BOSSBAR, "message in the bossbar during tdm overtime")
                .argument("border time"),
            new MessageInformation(
                TDM_OVERTIME_DEATH, "message displayed when the player dies during blitz overtime"),
            new MessageInformation(
                TDM_OVERTIME_ACTIVE,
                "message sent to the player when he tries to join and overtime is active"),
            new MessageInformation(UI_CORE, "name of a core objective"),
            new MessageInformation(CORE_LEAKED, "message shown when a core is leaked")
                .argument("core name")
                .argument("leaker name"),
            new MessageInformation(CORE_TOUCHED, "message shown when a core is touched")
                .argument("core name")
                .argument("toucher name"),
            new MessageInformation(MONUMENT_TOUCHED, "message shown when a monument is touched")
                .argument("monument name")
                .argument("toucher name"),
            new MessageInformation(MONUMENT_BROKEN, "message shown when a monument is broken")
                .argument("monument name")
                .argument("breaker name"),
            new MessageInformation(
                ERROR_OBJECTIVE_REPAIR_ENEMY,
                "error shown when a player tries to repair an enemy objective"),
            new MessageInformation(
                ERROR_OBJECTIVE_CANNOT_REPAIR,
                "error shown when a player tries to repair an objective that has repair disabled"),
            new MessageInformation(
                ERROR_OBJECTIVE_DAMAGE,
                "error shown when a player tries to destroy an objective they cannot complete"),
            new MessageInformation(
                ERROR_OBJECTIVE_DAMAGE_OWN,
                "error shown when a player tries to destroy an objective they own"),
            new MessageInformation(
                    GENERIC_OBJECTIVE_REPAIRED, "message shown when a player repairs an objective")
                .argument("monument name")
                .argument("player name"),
            new MessageInformation(
                    MODE_CHANGE_SUCCESS, "message shown when a destroyable mode is applied")
                .argument("mode name"),
            new MessageInformation(
                    MODE_CHANGE_FAIL, "message shown when a destroyable mode fails to apply")
                .argument("mode name"),
            new MessageInformation(
                    MODE_CHANGE_COUNTDOWN, "boss bar text for mode change countdowns")
                .argument("mode name")
                .argument("time in clock format"),
            new MessageInformation(
                    SCOREBOX_POINT, "broadcasted when a player scores by entering a scorebox")
                .argument("player name")
                .argument("points scored")
                .argument("team name"),
            new MessageInformation(
                WOOL_CHEST_ILLEGAL_ITEMS,
                "sent when a player places illegal items in their wool chest which have been returned to their inventory or dropped on the floor"),
            new MessageInformation(
                FLAG_CANT_CAPTURE_NET,
                "sent when a player tries to capture at a net they're unable to"),
            new MessageInformation(FLAG_PICKUP, "sent when a player picks up a flag")
                .argument("flag name")
                .argument("player"),
            new MessageInformation(FLAG_CAPTURE, "sent when a player captures a flag")
                .argument("flag name")
                .argument("player name"),
            new MessageInformation(FLAG_DROP, "sent when a player drops a flag")
                .argument("flag name"),
            new MessageInformation(FLAG_RESPAWN_END, "sent when a flag respawns")
                .argument("flag name"),
            new MessageInformation(
                FLAG_CAPTURE_ACTIONBAR, "sent to a player's action bar when they capture a flag"),
            new MessageInformation(
                    MAP_CHANGE_COUNTDOWN, "title of countdown bossbar when switching to a new map")
                .argument("map name")
                .argument("time until switch clock"),
            new MessageInformation(
                MAP_VOTE_ANNOUNCEMENT,
                "sent when announcing in chat the maps available for voting on"),
            new MessageInformation(
                MAP_VOTE_NO_MAP,
                "sent when a user tries voting for a map but one with the specified name can't be found"),
            new MessageInformation(
                    MAP_VOTE_SUCCESS, "sent when a user successfully votes for a map")
                .argument("map name"),
            new MessageInformation(
                    MAP_VOTE_CHANGED,
                    "sent when a user successfully votes for a map, having voted for a different one previously")
                .argument("old map")
                .argument("new map"),
            new MessageInformation(
                MAP_VOTE_NONE, "sent when a user tries to vote but there is no active vote"),
            new MessageInformation(
                MAP_VOTE_TALLY, "sent when broadcasting current map vote tallies"),
            new MessageInformation(
                    MAP_VOTE_TALLY_FINAL, "sent when broadcasting the final map vote tallies")
                .argument("winning map"),
            new MessageInformation(
                    MAP_VOTE_COUNTDOWN, "countdown bossbar when players are voting on maps")
                .argument("time left"),
            new MessageInformation(MAP_VOTE_UI_TITLE, "title of map voting UI"),
            new MessageInformation(
                MAP_VOTE_SHOW_SETTING,
                "name of setting which defines whether the vote GUI is shown or not by default"),
            new MessageInformation(
                MAP_VOTE_SHOW_SETTING_DESC, "description of map vote GUI setting"),
            new MessageInformation(
                MAP_VOTE_UNCHANGED,
                "error message shown when the player votes for the map they've already voted for"),
            new MessageInformation(JOIN_SWORD_NAME, "name of the item used to join the game"),
            new MessageInformation(
                PICKER_HELMET_NAME, "name of the item used to pick your own team"),
            new MessageInformation(PICKER_GUI_TITLE, "title of the team picker GUI"),
            new MessageInformation(
                JOIN_HINT, "message shown to players to tell them how to join")));
  }

  /**
   * Get a wool color translation for a specific {@link DyeColor}.
   *
   * @param color to get a translation for
   * @return translation for the specified color
   */
  public static LocalizedFormat forWoolColor(DyeColor color) {
    return get("colors.wool." + color.name().toLowerCase().replace("_", "-"));
  }

  /**
   * Get a flag color translation for a specific {@link DyeColor}.
   *
   * @param color to get a translation for
   * @return translation for the specified color
   */
  public static LocalizedFormat forFlagColor(DyeColor color) {
    return get("colors.flag." + color.name().toLowerCase().replace("_", "-"));
  }
}

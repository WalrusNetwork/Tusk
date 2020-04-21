package network.walrus.ubiquitous.bukkit;

import java.util.Arrays;
import net.md_5.bungee.api.ChatColor;
import network.walrus.utils.core.text.LocalizedFormat;
import network.walrus.utils.core.text.LocalizedText;
import network.walrus.utils.core.translation.MessageReferenceHolder;

/**
 * Translations for messages shown by the ubiquitous plugin.
 *
 * @author Austin Mayes
 */
public class UbiquitousMessages extends MessageReferenceHolder {

  public static final LocalizedFormat DEFUSER_NAME = get("defuser.name");
  public static final LocalizedFormat[] DEFUSER_LORE = getAll("defuser.lore");
  public static final LocalizedFormat EXTINGUISHER_NAME = get("extinguisher.name");
  public static final LocalizedFormat[] EXTINGUISHER_LORE = getAll("extinguisher.lore");
  public static final LocalizedFormat ERRORS_INVALID_PAGE = get("error.invalid-page");
  public static final LocalizedFormat ERROR_SETTING_NOT_FOUND = get("error.settings.not-found");
  public static final LocalizedFormat ERROR_SETTINGS_INVALID_VALUE =
      get("error.settings.invalid-value");
  public static final LocalizedFormat SETTINGS_SET = get("ui.settings.set");
  public static final LocalizedFormat ERROR_SETTINGS_NOT_TOGGLE = get("error.settings.not-toggle");
  public static final LocalizedFormat CLICK_ME = get("ui.chat.hover");
  public static final LocalizedFormat SETTINGS_HEADER = get("ui.settings.title");
  public static final LocalizedFormat SETTINGS_TOGGLE = get("ui.settings.toggle");
  public static final LocalizedFormat SETTiNGS_DEFAULT = get("ui.settings.default");
  public static final LocalizedFormat SETTiNGS_CURRENT = get("ui.settings.current");
  public static final LocalizedFormat SETTINGS_DESCRIPTION = get("ui.settings.description");
  public static final LocalizedFormat SETTINGS_SUMMARY = get("ui.settings.summary");
  public static final LocalizedFormat ERROR_MUST_BE_PLAYER = get("error.must-be-player");
  public static final LocalizedFormat ERROR_NOT_FROZEN = get("error.freeze.not");
  public static final LocalizedFormat ERROR_FREEZE_SELF = get("error.freeze.self");
  public static final LocalizedFormat ERROR_FREEZE_EXEMPT = get("error.freeze.exempt");
  public static final LocalizedFormat FREEZE_ALERT_TITLE = get("freeze.alert.title");
  public static final LocalizedFormat FREEZE_ALERT_SUBTITLE = get("freeze.alert.subtitle");
  public static final LocalizedFormat FREEZE_NOTIFICATIONS_FREEZE =
      get("freeze.notifications.freeze");
  public static final LocalizedFormat FREEZE_NOTIFICATIONS_THAW = get("freeze.notifications.thaw");
  public static final LocalizedFormat DAMAGE_BY_PROJECTILE = get("damage.projectile");
  public static final LocalizedFormat DAMAGE_BY_VOID = get("damage.void");
  public static final LocalizedFormat DAMAGE_BY_LAVA = get("damage.lava");
  public static final LocalizedFormat DAMAGE_BY_FALL = get("damage.fall");
  public static final LocalizedFormat DAMAGE_BY_EXPLOSIVE = get("damage.explosive");
  public static final LocalizedFormat DAMAGE_BY_BLOCK = get("damage.block");
  public static final LocalizedFormat DAMAGE_BY_ANVIL = get("damage.anvil");
  public static final LocalizedFormat DAMAGE_BY_PLAYER_VOID = get("damage.player.void");
  public static final LocalizedFormat DAMAGE_BY_PLAYER_PROJECTILE = get("damage.player.projectile");
  public static final LocalizedFormat DAMAGE_BY_MELEE = get("damage.melee");
  public static final LocalizedFormat DAMAGE_BY_MELEE_FISTS = get("damage.melee-fists");
  public static final LocalizedFormat DAMAGE_SPLEEF_BY_PLAYER = get("damage.spleef.player");
  public static final LocalizedFormat DAMAGE_SPLEEF_FLOOR_FALL = get("damage.spleef.fall");
  public static final LocalizedFormat DAMAGE_SHOT_WATER_FALL = get("damage.shot.water.fall");
  public static final LocalizedFormat DAMAGE_SHOT_LADDER_FALL = get("damage.shot.ladder.fall");
  public static final LocalizedFormat DAMAGE_SHOT_FLOOR_FALL = get("damage.shot.floor.fall");
  public static final LocalizedFormat DAMAGE_HIT_WATER_FALL = get("damage.hit.water.fall");
  public static final LocalizedFormat DAMAGE_HIT_LADDER_FALL = get("damage.hit.ladder.fall");
  public static final LocalizedFormat DAMAGE_HIT_FLOOR_FALL = get("damage.hit.floor.fall");
  public static final LocalizedFormat DAMAGE_BY_PLAYER_TNT = get("damage.player.tnt");
  public static final LocalizedFormat DAMAGE_BY_PLAYER_ANVIL = get("damage.player.anvil");
  public static final LocalizedFormat DAMAGE_BY_PLAYER_MOB = get("damage.player.mob");
  public static final LocalizedFormat DAMAGE_BY_MOB = get("damage.mob");
  public static final LocalizedFormat DAMAGE_HEADER = get("damage.header");
  public static final LocalizedFormat UH_OH = get("error.unknown");
  public static final LocalizedFormat TRUE = get("boolean.true");
  public static final LocalizedFormat FALSE = get("boolean.false");
  public static final LocalizedFormat CHAT_MUTED = get("chat.global.muted");
  public static final LocalizedFormat CHAT_UNMUTED = get("chat.global.unmuted");
  public static final LocalizedFormat CHAT_MUTED_ERROR = get("chat.global.mute-error");
  public static final LocalizedFormat CHAT_MUTED_ALERT = get("chat.alert.global.muted");
  public static final LocalizedFormat CHAT_UNMUTED_ALERT = get("chat.alert.global.unmuted");
  public static final LocalizedFormat LEGACY_ENCHANT = get("legacy.enchant");
  // inventory stuff
  public static final LocalizedFormat INVENTORY_BACK_TITLE = get("inventory.back.title");
  public static final LocalizedFormat[] INVENTORY_BACK_LORE = getAll("inventory.back.lore");
  public static final LocalizedFormat INVENTORY_NEXT_TITLE = get("inventory.paginated.next.title");
  public static final LocalizedFormat[] INVENTORY_NEXT_LORE =
      getAll("inventory.paginated.next.lore");
  public static final LocalizedFormat INVENTORY_PREVIOUS_TITLE =
      get("inventory.paginated.previous.title");
  public static final LocalizedFormat[] INVENTORY_PREVIOUS_LORE =
      getAll("inventory.paginated.previous.lore");
  public static final LocalizedFormat JUMP_PAD_USE_ERROR = get("lobby.pads.use-error");
  public static final LocalizedFormat[] GIZMO_DESC_TNT = getAll("gizmo.device.tnt.description");
  public static final LocalizedFormat[] GIZMO_DESC_BODY_SLAM =
      getAll("gizmo.device.body-slam.description");
  public static final LocalizedFormat GIZMO_NAME_PET = get("gizmo.pet.name");
  public static final LocalizedFormat GIZMO_NAME_BODY_SLAM = get("gizmo.device.body-slam.name");
  public static final LocalizedFormat GIZMO_NAME_TNT = get("gizmo.device.tnt.name");
  public static final LocalizedFormat SETTING_CHAT_NAME = get("settings.chat.name");
  public static final LocalizedFormat SETTING_CHAT_DESCRIPTION = get("settings.chat.description");

  static {
    USED_MESSAGES.addAll(
        Arrays.asList(
            new MessageInformation(DEFUSER_NAME, "defuser item name"),
            new GroupFormatInformation("defuser.lore", DEFUSER_LORE, "defuser lore"),
            new MessageInformation(EXTINGUISHER_NAME, "extinguisher item name"),
            new GroupFormatInformation("extinguisher.lore", EXTINGUISHER_LORE, "extinguisher lore"),
            new MessageInformation(ERRORS_INVALID_PAGE, "error when user supplies an invalid page")
                .argument("maximum allowed pages"),
            new MessageInformation(
                ERROR_SETTING_NOT_FOUND,
                "error shown when a player searches for an unknown setting"),
            new MessageInformation(
                ERROR_SETTINGS_INVALID_VALUE,
                "error shown when a player tries to set a setting to an invalid value"),
            new MessageInformation(SETTINGS_SET, "message shown when a player changes a setting")
                .argument("setting name")
                .argument("value"),
            new MessageInformation(
                ERROR_SETTINGS_NOT_TOGGLE,
                "error shown when a player tries to toggle a non-toggleable setting"),
            new MessageInformation(
                CLICK_ME, "text shown when chat elements which are clickable are hovered over"),
            new MessageInformation(SETTINGS_HEADER, "title of the settings list"),
            new MessageInformation(
                SETTINGS_TOGGLE,
                "message shown informing players they can click to toggle a setting"),
            new MessageInformation(
                    SETTiNGS_DEFAULT, "message informing users of the default value of a setting")
                .argument("value"),
            new MessageInformation(
                    SETTiNGS_CURRENT, "message informing users of the current value of a setting")
                .argument("value"),
            new MessageInformation(
                    SETTINGS_DESCRIPTION, "message informing users of the description of a setting")
                .argument("description"),
            new MessageInformation(
                    SETTINGS_SUMMARY, "message informing users of the summary of a setting")
                .argument("summary"),
            new MessageInformation(
                ERROR_MUST_BE_PLAYER,
                "error shown to console when it tries to execute a player only command"),
            new MessageInformation(
                ERROR_NOT_FROZEN,
                "error shown when a player tries to unfreeze a non-frozen player"),
            new MessageInformation(
                ERROR_FREEZE_SELF, "error shown when a player tries to freeze themself"),
            new MessageInformation(
                ERROR_FREEZE_EXEMPT,
                "error shown when a player tries to freeze an exempted player"),
            new MessageInformation(
                FREEZE_ALERT_TITLE, "title sent to a player when they are frozen"),
            new MessageInformation(
                FREEZE_ALERT_SUBTITLE, "subtitle sent to a player when they are frozen"),
            new MessageInformation(
                    FREEZE_NOTIFICATIONS_FREEZE,
                    "notification sent to other staff when a player is frozen")
                .argument("freezer name")
                .argument("victim name"),
            new MessageInformation(
                    FREEZE_NOTIFICATIONS_THAW,
                    "notification sent to other staff when a player is unfrozen")
                .argument("staff name")
                .argument("victim name"),
            new MessageInformation(
                DAMAGE_BY_PROJECTILE, "description of being damaged by a projectile"),
            new MessageInformation(DAMAGE_BY_VOID, "description of being damaged by the void"),
            new MessageInformation(DAMAGE_BY_LAVA, "description of being damaged by lava"),
            new MessageInformation(DAMAGE_BY_FALL, "description of fall damage")
                .argument("distance"),
            new MessageInformation(
                DAMAGE_BY_EXPLOSIVE, "description of being damaged by an explosive"),
            new MessageInformation(DAMAGE_BY_BLOCK, "description of being damaged by suffocation"),
            new MessageInformation(DAMAGE_BY_ANVIL, "description of being damaged by an anvil"),
            new MessageInformation(
                    DAMAGE_BY_PLAYER_VOID,
                    "description of being damaged by being hit into the void by someone")
                .argument("attacker name"),
            new MessageInformation(
                    DAMAGE_BY_PLAYER_PROJECTILE,
                    "description of being hit by a player's projectile")
                .argument("attacker name")
                .argument("projectile type")
                .argument("distance"),
            new MessageInformation(
                    DAMAGE_BY_MELEE, "description of being damaged by a person's weapon")
                .argument("attacker name")
                .argument("weapon type"),
            new MessageInformation(
                    DAMAGE_BY_MELEE_FISTS, "description of being damaged by a person's fists")
                .argument("attacker name"),
            new MessageInformation(
                    DAMAGE_SPLEEF_BY_PLAYER, "description of being damaged by being spleefed")
                .argument("attacker name"),
            new MessageInformation(
                    DAMAGE_SPLEEF_FLOOR_FALL,
                    "description of taking fall damage from being spleefed")
                .argument("attacker name"),
            new MessageInformation(
                    DAMAGE_SHOT_WATER_FALL,
                    "description of being shot out of the water and falling")
                .argument("attacker name"),
            new MessageInformation(
                    DAMAGE_SHOT_LADDER_FALL,
                    "description of being shot off of a ladder and falling")
                .argument("attacker name"),
            new MessageInformation(DAMAGE_SHOT_FLOOR_FALL, "description of being shot and falling")
                .argument("attacker name"),
            new MessageInformation(
                    DAMAGE_HIT_WATER_FALL, "description of being hit out of the water and falling")
                .argument("attacker name"),
            new MessageInformation(
                    DAMAGE_HIT_LADDER_FALL, "description of being hit off of a ladder and falling")
                .argument("attacker name"),
            new MessageInformation(DAMAGE_HIT_FLOOR_FALL, "description of being hit and falling")
                .argument("attacker name"),
            new MessageInformation(
                    DAMAGE_BY_PLAYER_TNT, "description of being damaged by a player's TNT")
                .argument("owner name"),
            new MessageInformation(
                    DAMAGE_BY_PLAYER_ANVIL, "description of being damaged by a player's anvil")
                .argument("owner name"),
            new MessageInformation(
                    DAMAGE_BY_PLAYER_MOB, "description of being damaged by a player's mob")
                .argument("owner name")
                .argument("mob name"),
            new MessageInformation(DAMAGE_BY_MOB, "description of being damaged by a mob")
                .argument("mob name"),
            new MessageInformation(DAMAGE_HEADER, "header of the damage list")
                .argument("player name")
                .argument("current page")
                .argument("total pages"),
            new MessageInformation(UH_OH, "message shown to a user when an unknown error occurs"),
            new MessageInformation(CHAT_MUTED, "message broadcast when the chat is muted"),
            new MessageInformation(CHAT_UNMUTED, "message broadcast when the chat is unmuted"),
            new MessageInformation(
                CHAT_MUTED_ERROR,
                "message shown when a user tries to chat but a global mute is in place"),
            new MessageInformation(CHAT_MUTED_ALERT, "shown to moderators when the chat is muted")
                .argument("mod who muted name"),
            new MessageInformation(
                    CHAT_UNMUTED_ALERT, "shown to moderators when the chat is unmuted")
                .argument("mod who unmuted name"),
            new MessageInformation(LEGACY_ENCHANT, "shown when a legacy player view enchantments")
                .argument("enchantment name")
                .argument("enchantment level")
                .argument("exp cost")
                .argument("lapis cost"),
            new MessageInformation(
                INVENTORY_BACK_TITLE, "title of the back button in a player's inventory"),
            new GroupFormatInformation(
                "inventory.back.lore",
                INVENTORY_BACK_LORE,
                "lore for the back button in an inventory"),
            new MessageInformation(
                INVENTORY_NEXT_TITLE, "title for the next button in a paginated inventory"),
            new GroupFormatInformation(
                "inventory.paginated.next.lore",
                INVENTORY_NEXT_LORE,
                "lore for the next button in a paginated inventory"),
            new MessageInformation(
                INVENTORY_BACK_TITLE, "title for the back button in a paginated inventory"),
            new GroupFormatInformation(
                "inventory.paginated.back.lore",
                INVENTORY_BACK_LORE,
                "lore for the back button in a paginated inventory"),
            new MessageInformation(
                    JUMP_PAD_USE_ERROR,
                    "Error shown when users are not allowed to use a jump pad for some reason")
                .argument("reason"),
            new GroupFormatInformation(
                "gizmo.device.tnt.description", GIZMO_DESC_TNT, "Description for the TNT device"),
            new GroupFormatInformation(
                "gizmo.device.body-slam.description",
                GIZMO_DESC_BODY_SLAM,
                "Description for the body slam device"),
            new MessageInformation(GIZMO_NAME_PET, "Name of the pet gizmo").argument("pet name"),
            new MessageInformation(GIZMO_NAME_BODY_SLAM, "Name of the body slam gizmo"),
            new MessageInformation(GIZMO_NAME_TNT, "Name of the tnt launcher gizmo"),
            new MessageInformation(SETTING_CHAT_NAME, "Name of the chat setting"),
            new MessageInformation(SETTING_CHAT_DESCRIPTION, "Description of the chat setting")));
  }

  /**
   * Get a colored localized text for a supplied boolean.
   *
   * @param value of the boolean
   * @return translation for the boolean
   */
  public static LocalizedText bool(final boolean value) {
    return value
        ? get("boolean.true").with(ChatColor.GREEN)
        : get("boolean.false").with(ChatColor.RED);
  }
}

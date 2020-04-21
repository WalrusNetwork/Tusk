package network.walrus.utils.core.color;

import java.time.Duration;
import net.md_5.bungee.api.ChatColor;
import network.walrus.utils.core.translation.TextStyle;

/**
 * Network-wide constants for every sound which is played by any plugin on any server.
 *
 * @author Austin Mayes
 */
public final class NetworkColorConstants {

  public static final class Branding {

    public static final TextStyle NAME = StyleInjector.$NULL$;
    public static final TextStyle IP = StyleInjector.$NULL$;
  }

  public static class Games {

    public static final class Maps {

      public static final TextStyle PREFIX = StyleInjector.$NULL$;
      public static final TextStyle NAME = StyleInjector.$NULL$;
      public static final TextStyle AUTHOR = StyleInjector.$NULL$;
      public static final TextStyle BY = StyleInjector.$NULL$;
      public static final TextStyle ERROR = StyleInjector.$NULL$;
      public static final TextStyle LOADED = StyleInjector.$NULL$;
      public static final TextStyle RELOADED = StyleInjector.$NULL$;

      public static final class Random {
        public static final TextStyle TITLE_COUNTDOWN_TEXT = StyleInjector.$NULL$;
        public static final TextStyle TITLE_COUNTDOWN_MAP = StyleInjector.$NULL$;
        public static final TextStyle TITLE_COUNTDOWN_TIME = StyleInjector.$NULL$;
      }

      public static final class Vote {
        public static final TextStyle MAP_VOTE_ANNOUNCEMENT = StyleInjector.$NULL$;
        public static final TextStyle MAP_VOTE_ITEM_TEXT = StyleInjector.$NULL$;
        public static final TextStyle MAP_VOTE_ITEM_NUMBER = StyleInjector.$NULL$;
        public static final TextStyle MAP_VOTE_ITEM_NAME = StyleInjector.$NULL$;
        public static final TextStyle MAP_VOTE_ITEM_GAMEMODE = StyleInjector.$NULL$;
        public static final TextStyle MAP_VOTES = StyleInjector.$NULL$;
        public static final TextStyle MAP_VOTE_NONE = StyleInjector.$NULL$;
        public static final TextStyle MAP_VOTE_NO_MAP = StyleInjector.$NULL$;
        public static final TextStyle MAP_VOTE_SUCCESS = StyleInjector.$NULL$;
        public static final TextStyle MAP_VOTE_CHANGED = StyleInjector.$NULL$;
        public static final TextStyle MAP_VOTE_UNCHANGED = StyleInjector.$NULL$;
        public static final TextStyle MAP_VOTE_TALLY = StyleInjector.$NULL$;
        public static final TextStyle MAP_VOTE_COUNTDOWN_TEXT = StyleInjector.$NULL$;
        public static final TextStyle MAP_VOTE_COUNTDOWN_CLOCK = StyleInjector.$NULL$;
      }
    }

    public static final class Commands {

      public static final TextStyle MAP_NAME = StyleInjector.$NULL$;
      public static final TextStyle MAP_VERSION = StyleInjector.$NULL$;
      public static final TextStyle MAP_OBJECTIVE = StyleInjector.$NULL$;

      public static final class Rules {
        public static final TextStyle HEADER = StyleInjector.$NULL$;
        public static final TextStyle PREFIX = StyleInjector.$NULL$;
        public static final TextStyle TEXT = StyleInjector.$NULL$;
      }
    }

    public static final class OCN {

      public static final class CTW {

        public static final TextStyle SCOREBOARD_HEADER = StyleInjector.$NULL$;

        public static class Errors {

          public static final TextStyle ALREADY_PLACED = StyleInjector.$NULL$;
          public static final TextStyle WRONG_WOOL = StyleInjector.$NULL$;
          public static final TextStyle BAD_WOOL = StyleInjector.$NULL$;
          public static final TextStyle CANNOT_CRAFT = StyleInjector.$NULL$;
          public static final TextStyle CHEST_ILLEGAL_ITEMS = StyleInjector.$NULL$;
        }
      }

      public static final class DTC {

        public static final TextStyle SCOREBOARD_HEADER = StyleInjector.$NULL$;
        public static final TextStyle CORE_LEAKED = StyleInjector.$NULL$;
      }

      public static final class DTM {

        public static final TextStyle SCOREBOARD_HEADER = StyleInjector.$NULL$;
        public static final TextStyle MONUMENT_BROKEN = StyleInjector.$NULL$;
      }

      public static final class DTCM {

        public static final TextStyle SCOREBOARD_HEADER = StyleInjector.$NULL$;
        public static final TextStyle DESTROYABLE_TOUCHED = StyleInjector.$NULL$;

        public static class Errors {

          public static final TextStyle DAMAGE_OWN = StyleInjector.$NULL$;
          public static final TextStyle DAMAGE_OTHER = StyleInjector.$NULL$;
          public static final TextStyle REPAIR_ENEMY = StyleInjector.$NULL$;
          public static final TextStyle REPAIR_DISABLED = StyleInjector.$NULL$;
          public static final TextStyle BAD_REPAIR = StyleInjector.$NULL$;
        }
      }

      public static final class CP {

        public static final TextStyle SCOREBOARD_HEADER = StyleInjector.$NULL$;
        public static final TextStyle COMPLETION_SYMBOL = StyleInjector.$NULL$;
        public static final TextStyle OVERTIME_BOSSBAR = StyleInjector.$NULL$;
        public static final TextStyle OVERTIME_BROADCAST = StyleInjector.$NULL$;
      }

      public static final class TDM {

        public static final TextStyle SCOREBOARD_HEADER = StyleInjector.$NULL$;

        public static final class Overtime {

          public static final TextStyle BOSSBAR = StyleInjector.$NULL$;
          public static final TextStyle BROADCAST = StyleInjector.$NULL$;
          public static final TextStyle DEATH = StyleInjector.$NULL$;
          public static final TextStyle ACTIVE = StyleInjector.$NULL$;
        }
      }

      public static final class CTF {

        public static final TextStyle CANT_CAPTURE = StyleInjector.$NULL$;
        public static final TextStyle CANT_BREAK_POST = StyleInjector.$NULL$;
        public static final TextStyle PICKUP = StyleInjector.$NULL$;
        public static final TextStyle CAPTURE = StyleInjector.$NULL$;
        public static final TextStyle DROP = StyleInjector.$NULL$;
        public static final TextStyle RESPAWN = StyleInjector.$NULL$;
        public static final TextStyle SIDEBAR_COUNTDOWN = StyleInjector.$NULL$;
        public static final TextStyle SCORE = StyleInjector.$NULL$;
        public static final TextStyle SCORE_LIMIT = StyleInjector.$NULL$;
      }

      public static final class KOTH {
        public static final TextStyle SCORE = StyleInjector.$NULL$;
        public static final TextStyle SCORE_LIMIT = StyleInjector.$NULL$;
      }

      public static final class Groups {

        public static final TextStyle JOIN_HINT = StyleInjector.$NULL$;

        public static class Errors {

          public static final TextStyle CANNOT_PICK = StyleInjector.$NULL$;
          public static final TextStyle NOT_FOUND = StyleInjector.$NULL$;
          public static final TextStyle SAME_TEAM_TYPE = StyleInjector.$NULL$;
          public static final TextStyle ALREADY_PLAYING = StyleInjector.$NULL$;
          public static final TextStyle CANNOT_JOIN_FINISHED = StyleInjector.$NULL$;
          public static final TextStyle CANNOT_JOIN_FULL = StyleInjector.$NULL$;
          public static final TextStyle CANNOT_JOIN_OVERFILL = StyleInjector.$NULL$;
          public static final TextStyle CANNOT_JOIN_IMBALANCE = StyleInjector.$NULL$;
          public static final TextStyle NEGATIVE_TEAM_SIZE = StyleInjector.$NULL$;
        }

        public static class GUI {

          public static final TextStyle JOIN_SWORD_NAME = StyleInjector.$NULL$;
          public static final TextStyle HELMET_PICKER_NAME = StyleInjector.$NULL$;
          public static final TextStyle PICKER_TITLE = StyleInjector.$NULL$;
        }
      }

      public static final class TabList {

        public static final TextStyle NOT_ALIVE = TextStyle.create().strike();
        public static final TextStyle NEUTRAL = StyleInjector.$NULL$;

        public static class Size {

          public static final TextStyle CURRENT = StyleInjector.$NULL$;
          public static final TextStyle DELIMITER = StyleInjector.$NULL$;
          public static final TextStyle MAX = StyleInjector.$NULL$;
        }
      }

      public static final class Respawn {

        public static final TextStyle TITLE = StyleInjector.$NULL$;
        public static final TextStyle FALLBACK = StyleInjector.$NULL$;

        public static class Subtitle {

          public static final TextStyle TEXT = StyleInjector.$NULL$;
          public static final TextStyle TIME = StyleInjector.$NULL$;
        }
      }

      public static final class Objectives {

        public static final TextStyle COMPLETED = StyleInjector.$NULL$;
        public static final TextStyle TOUCHED = StyleInjector.$NULL$;
        public static final TextStyle UNTOUCHED = StyleInjector.$NULL$;
        public static final TextStyle HOLDING = StyleInjector.$NULL$;
      }

      public static final class Stats {

        public static final TextStyle HEADER = StyleInjector.$NULL$;
        public static final TextStyle ITEM_NAME = StyleInjector.$NULL$;
        public static final TextStyle ITEM = StyleInjector.$NULL$;
        public static final TextStyle ITEM_VALUE = StyleInjector.$NULL$;
      }

      public static final class Starting {

        public static final TextStyle PLAY = StyleInjector.$NULL$;
        public static final TextStyle STARTED = StyleInjector.$NULL$;
        public static final TextStyle TITLE = StyleInjector.$NULL$;
      }

      public static final class Countdowns {

        public static final TextStyle TIME_REMAINING = StyleInjector.$NULL$;
        public static final TextStyle NEEDED_BALANCE = StyleInjector.$NULL$;
        public static final TextStyle STARTING_TIME = StyleInjector.$NULL$;
        public static final TextStyle STARTING_TEXT = StyleInjector.$NULL$;

        /**
         * Colorizes text based on time remaining.
         *
         * @param elapsed time elapsed so far
         * @return colorized text
         */
        public static ChatColor determineTimeColor(Duration elapsed, Duration total) {
          double percent = 100 - 100 * (double) elapsed.getSeconds() / total.getSeconds();
          ChatColor color = ChatColor.GREEN;
          if (percent <= 33) {
            color = ChatColor.RED;
          } else if (percent < 66) {
            color = ChatColor.YELLOW;
          }

          return color;
        }
      }
    }

    public static final class UHC {

      public static final TextStyle SERVER_FULL = StyleInjector.$NULL$;

      public static final class PVP {

        public static final TextStyle COUNTDOWN_COLOR = StyleInjector.$NULL$;
        public static final TextStyle ENABLED = StyleInjector.$NULL$;
      }

      public static final class Rules {
        public static final TextStyle HEADER_TEXT = StyleInjector.$NULL$;
        public static final TextStyle HEADER_LINE = StyleInjector.$NULL$;
        public static final TextStyle RULE = StyleInjector.$NULL$;
      }

      public static final class KillTop {

        public static final TextStyle ENTRY = StyleInjector.$NULL$;
        public static final TextStyle POSITION = StyleInjector.$NULL$;
        public static final TextStyle KILL_COUNT = StyleInjector.$NULL$;
        public static final TextStyle NO_KILLS = StyleInjector.$NULL$;
      }

      public static final class Hosts {

        public static final TextStyle PREFIX = StyleInjector.$NULL$;

        public static class HelpOp {

          public static final TextStyle SENT = StyleInjector.$NULL$;
          public static final TextStyle NOTIFICATION = StyleInjector.$NULL$;
        }

        public static class Disqualified {

          public static final TextStyle NOTIFICATION = StyleInjector.$NULL$;
          public static final TextStyle HOST_ALERT = StyleInjector.$NULL$;
          public static final TextStyle ALERT = StyleInjector.$NULL$;
        }

        public static class ForceScatter {

          public static final TextStyle HOST_ALERT = StyleInjector.$NULL$;
        }

        public static final class Xray {

          public static final TextStyle NOTIFICATION = StyleInjector.$NULL$;

          /**
           * Determine the severity of ores mined for the XRay alert.
           *
           * @param count of ores mined
           * @param base number of ores for the alert to be sent
           * @return severity of ores mined
           */
          public static ChatColor determineSeverity(int count, int base) {
            switch (Math.floorDiv(count, base)) {
              case 1:
                return ChatColor.GREEN;
              case 2:
                return ChatColor.YELLOW;
              case 3:
                return ChatColor.GOLD;
              case 4:
                return ChatColor.RED;
              default:
                return ChatColor.DARK_RED;
            }
          }
        }
      }

      public static final class PlayerMod {

        public static class Targets {

          public static final TextStyle ALL = StyleInjector.$NULL$;
          public static final TextStyle SELF = StyleInjector.$NULL$;
          public static final TextStyle INVALID = StyleInjector.$NULL$;
        }

        public static class Feed {

          public static final TextStyle FED = StyleInjector.$NULL$;
          public static final TextStyle EXECUTED = StyleInjector.$NULL$;
        }

        public static class Heal {

          public static final TextStyle HEALED = StyleInjector.$NULL$;
          public static final TextStyle EXECUTED = StyleInjector.$NULL$;
        }

        public static class Fullbright {

          public static final TextStyle ENABLED = StyleInjector.$NULL$;
          public static final TextStyle DISABLED = StyleInjector.$NULL$;
        }
      }

      public static final class Scenarios {

        public static final TextStyle ENABLED = StyleInjector.$NULL$;
        public static final TextStyle DISABLED = StyleInjector.$NULL$;

        public static final class List {

          public static final TextStyle CURRENT_PAGE_COLOR = StyleInjector.$NULL$;
          public static final TextStyle TOTAL_PAGES_COLOR = StyleInjector.$NULL$;
          public static final TextStyle HEADER_COLOR = StyleInjector.$NULL$;
          public static final TextStyle SCENARIO_NAME_COLOR = StyleInjector.$NULL$;
          public static final TextStyle SCENARIO_DESCRIPTION_COLOR = StyleInjector.$NULL$;
          public static final TextStyle SCENARIO_ACTIVE_COLOR = StyleInjector.$NULL$;
          public static final TextStyle DELIMITER_COLOR = StyleInjector.$NULL$;
        }

        public static final class Rodless {
          public static final TextStyle NO_RODS = StyleInjector.$NULL$;
        }

        public static final class NoClean {
          public static final TextStyle GAINED = StyleInjector.$NULL$;
          public static final TextStyle REMAINING = StyleInjector.$NULL$;
          public static final TextStyle LOST = StyleInjector.$NULL$;
          public static final TextStyle EXPIRED = StyleInjector.$NULL$;
          public static final TextStyle TIME_LEFT = StyleInjector.$NULL$;
        }

        public static final class WeakestLink {
          public static final TextStyle NONE = StyleInjector.$NULL$;
          public static final TextStyle SELF = StyleInjector.$NULL$;
          public static final TextStyle OTHER = StyleInjector.$NULL$;
        }
      }

      public static final class Scoreboard {

        public static final TextStyle TITLE = StyleInjector.$NULL$;
        public static final TextStyle SCENARIO_HEADER = StyleInjector.$NULL$;
        public static final TextStyle SCENARIO_NAME = StyleInjector.$NULL$;
        public static final TextStyle TIME_TEXT = StyleInjector.$NULL$;
        public static final TextStyle TIME_CLOCK = StyleInjector.$NULL$;
        public static final TextStyle BORDER_SIZE = StyleInjector.$NULL$;
        public static final TextStyle BORDER_TEXT = StyleInjector.$NULL$;
        public static final TextStyle PLAYERS_TEXT = StyleInjector.$NULL$;
        public static final TextStyle PLAYERS_COUNT = StyleInjector.$NULL$;
        public static final TextStyle PLAYERS_TOTAL = StyleInjector.$NULL$;
        public static final TextStyle PLAYER_KILL_TEXT = StyleInjector.$NULL$;
        public static final TextStyle TEAM_KILL_TEXT = StyleInjector.$NULL$;
        public static final TextStyle PLAYER_KILL_COUNT = StyleInjector.$NULL$;
        public static final TextStyle TEAM_KILL_COUNT = StyleInjector.$NULL$;
        public static final TextStyle KILLS_BOARD_TEXT = StyleInjector.$NULL$;
        public static final TextStyle KILLS_BOARD_COUNT = StyleInjector.$NULL$;
        public static final TextStyle ENV_KILLS = StyleInjector.$NULL$;
        public static final TextStyle NEXT_BORDER_SIZE = StyleInjector.$NULL$;
        public static final TextStyle NEXT_BORDER_TEXT = StyleInjector.$NULL$;
        public static final TextStyle NEXT_BORDER_TIME = StyleInjector.$NULL$;
        public static final TextStyle KILLS_BOARD_HEADER = StyleInjector.$NULL$;
      }

      public static final class Whitelist {

        public static final TextStyle ADDED_ALL = StyleInjector.$NULL$;
        public static final TextStyle CLEARED = StyleInjector.$NULL$;
        public static final TextStyle OFF = StyleInjector.$NULL$;
        public static final TextStyle CANCEL = StyleInjector.$NULL$;

        public static final class Countdown {

          public static final TextStyle TIME = StyleInjector.$NULL$;
          public static final TextStyle TEXT = StyleInjector.$NULL$;
          public static final TextStyle ON = StyleInjector.$NULL$;
        }
      }

      public static final class Revive {

        public static final TextStyle BROADCAST = StyleInjector.$NULL$;
        public static final TextStyle HOST_ALERT = StyleInjector.$NULL$;
      }

      public static final class Scatter {

        public static final TextStyle STARTED = StyleInjector.$NULL$;
        public static final TextStyle COUNTDOWN_TEXT = StyleInjector.$NULL$;
        public static final TextStyle COUNTDOWN_PERCENT = StyleInjector.$NULL$;
        public static final TextStyle GEN_STARTED = StyleInjector.$NULL$;
        public static final TextStyle GEN_FINISHED = StyleInjector.$NULL$;
        public static final TextStyle RELEASE_TEXT = StyleInjector.$NULL$;
        public static final TextStyle RELEASE_NUMBER = StyleInjector.$NULL$;
      }

      public static final class Teams {

        public static final TextStyle CREATED = StyleInjector.$NULL$;
        public static final TextStyle INVITE_SENT = StyleInjector.$NULL$;
        public static final TextStyle INVITE_RECEIVED = StyleInjector.$NULL$;
        public static final TextStyle JOINED = StyleInjector.$NULL$;
        public static final TextStyle ACCEPT_INVITE = StyleInjector.$NULL$;
        public static final TextStyle INVITE_ACCEPTED = StyleInjector.$NULL$;
        public static final TextStyle INVITE_HOVER = StyleInjector.$NULL$;
        public static final TextStyle BECAME_LONER = StyleInjector.$NULL$;
        public static final TextStyle BOOK_TITLE = StyleInjector.$NULL$;

        public static final class List {

          public static final TextStyle CURRENT_PAGE = StyleInjector.$NULL$;
          public static final TextStyle TOTAL_PAGES = StyleInjector.$NULL$;
          public static final TextStyle HEADER = StyleInjector.$NULL$;
          public static final TextStyle DELIMITER = StyleInjector.$NULL$;
          public static final TextStyle INDEX = StyleInjector.$NULL$;
        }
      }

      public static final class TeamChat {

        public static final TextStyle CORD = StyleInjector.$NULL$;
        public static final TextStyle WORLD_NAME = StyleInjector.$NULL$;
        public static final TextStyle ORES_MESSAGE = StyleInjector.$NULL$;
        public static final TextStyle ORES_NUMBER = StyleInjector.$NULL$;
      }

      public static final class Portals {

        public static final TextStyle NETHER_DISABLED = StyleInjector.$NULL$;
        public static final TextStyle END_DISABLED = StyleInjector.$NULL$;
      }

      public static final class Config {

        public static final TextStyle OPTION = StyleInjector.$NULL$;
        public static final TextStyle VALUE = StyleInjector.$NULL$;
        public static final TextStyle UPDATE = StyleInjector.$NULL$;
        public static final TextStyle UPDATE_NOTIFICATION = StyleInjector.$NULL$;
        public static final TextStyle APPLIED = StyleInjector.$NULL$;
        public static final TextStyle APPLIED_NOTIFICATION = StyleInjector.$NULL$;
        public static final TextStyle TITLE = StyleInjector.$NULL$;
        public static final TextStyle GROUP = StyleInjector.$NULL$;
      }

      public static class Potions {

        public static final TextStyle DISABLED = StyleInjector.$NULL$;
      }

      public static class PlayerInfo {

        public static final TextStyle HEALTH_TEXT = StyleInjector.$NULL$;
        public static final TextStyle HEALTH_NUMBER = StyleInjector.$NULL$;
      }

      public static class GoldenHead {

        public static final TextStyle NAME = StyleInjector.$NULL$;
      }

      public static class Borders {

        public static final TextStyle REMOVED = StyleInjector.$NULL$;
        public static final TextStyle RECALCULATED = StyleInjector.$NULL$;
        public static final TextStyle HEADER_TEXT = StyleInjector.$NULL$;
        public static final TextStyle HEADER_LINE = StyleInjector.$NULL$;
        public static final TextStyle DESC_TEXT = StyleInjector.$NULL$;
        public static final TextStyle DESC_TIME = StyleInjector.$NULL$;
        public static final TextStyle DESC_SIZE = StyleInjector.$NULL$;
        public static final TextStyle ADDED = StyleInjector.$NULL$;
        public static final TextStyle SHRINK_TEXT = StyleInjector.$NULL$;
        public static final TextStyle SHRINK_TIME = StyleInjector.$NULL$;
        public static final TextStyle SHRINK_SIZE = StyleInjector.$NULL$;
      }

      public static class Projectile {

        public static final TextStyle HIT_TEXT = StyleInjector.$NULL$;
      }

      public static class RedditBans {

        public static final TextStyle BANNED = StyleInjector.$NULL$;
        public static final TextStyle UUID_CHECK = StyleInjector.$NULL$;
        public static final TextStyle EXEMPT_ALERT = StyleInjector.$NULL$;
        public static final TextStyle EXEMPT_HOST_ALERT = StyleInjector.$NULL$;
      }

      public static class TpAll {

        public static final TextStyle TP_ALL = StyleInjector.$NULL$;
      }
    }

    public static final class States {

      public static final TextStyle IDLE = StyleInjector.$NULL$;
      public static final TextStyle STARTING = StyleInjector.$NULL$;
      public static final TextStyle PLAYING = StyleInjector.$NULL$;
      public static final TextStyle FINISHED = StyleInjector.$NULL$;
    }

    public static final class Results {

      public static final TextStyle JOIN_NEXT = StyleInjector.$NULL$;
      public static final TextStyle TEAM_WON = StyleInjector.$NULL$;
      public static final TextStyle TEAM_LOST = StyleInjector.$NULL$;
      public static final TextStyle WINNERS_LINE = StyleInjector.$NULL$;
      public static final TextStyle WINNER_TEXT = StyleInjector.$NULL$;
      public static final TextStyle TIE = StyleInjector.$NULL$;
    }

    public static final class Applicators {

      public static final TextStyle EVENT_DISALLOWED = StyleInjector.$NULL$;
    }

    public static final class Items {

      public static final TextStyle COMPASS_NAME = StyleInjector.$NULL$;
      public static final TextStyle COMPASS_LORE = StyleInjector.$NULL$;
      public static final TextStyle HEALTH_NAME = StyleInjector.$NULL$;
      public static final TextStyle HUNGER_NAME = StyleInjector.$NULL$;
    }

    public static final class Chats {

      public static final TextStyle CHAT_DISABLED = StyleInjector.$NULL$;
      public static final TextStyle CHAT_SWITCHED = StyleInjector.$NULL$;
      public static final TextStyle QUICKCHAT_MATCHER = StyleInjector.$NULL$;
      public static final TextStyle QUICKCHAT_DELIMITER = StyleInjector.$NULL$;
      public static final TextStyle QUICKCHAT_REPLACE = StyleInjector.$NULL$;
      public static final TextStyle QUICKCHAT_SWITCHED = StyleInjector.$NULL$;
      public static final TextStyle QUICKCHAT_ERROR = StyleInjector.$NULL$;
    }

    public static final class Deaths {

      public static final TextStyle MESSAGE = StyleInjector.$NULL$;

      /**
       * Determine the color which should be applied to a bow shot to determine how far the shot was
       * away.
       *
       * @param distance of the shot
       * @return color of the shot distance
       */
      public static ChatColor bowDistanceColor(double distance) {
        ChatColor color = MESSAGE.color();
        if (distance >= 80.0) {
          color = ChatColor.DARK_RED;
        } else if (distance >= 50.0) {
          color = ChatColor.RED;
        } else if (distance >= 35.0) {
          color = ChatColor.GOLD;
        } else if (distance >= 25.0) {
          color = ChatColor.YELLOW;
        } else if (distance >= 10.0) {
          color = ChatColor.GREEN;
        }
        return color;
      }
    }

    public static class Scoreboard {

      public static final TextStyle TOGGLED = StyleInjector.$NULL$;
      public static final TextStyle NAME = StyleInjector.$NULL$;
      public static final TextStyle LIST = StyleInjector.$NULL$;
    }
  }

  public static final class Lobby {

    public static final TextStyle ERROR_LOGGER_PREFIX = StyleInjector.$NULL$;
  }

  public static final class Inventory {

    public static final TextStyle BACK_BUTTON_TITLE = StyleInjector.$NULL$;
    public static final TextStyle BACK_BUTTON_LORE = StyleInjector.$NULL$;

    public static final class Paginated {

      public static final TextStyle NEXT_BUTTON_TITLE = StyleInjector.$NULL$;
      public static final TextStyle NEXT_BUTTON_LORE = StyleInjector.$NULL$;
      public static final TextStyle PREVIOUS_BUTTON_TITLE = StyleInjector.$NULL$;
      public static final TextStyle PREVIOUS_BUTTON_LORE = StyleInjector.$NULL$;
    }
  }

  public static final class Network {

    public static final TextStyle LOCAL_SERVER = StyleInjector.$NULL$;
    public static final TextStyle REMOTE_SERVER = StyleInjector.$NULL$;

    public static final class Servers {
      public static final class List {
        public static final TextStyle CURRENT_PAGE = StyleInjector.$NULL$;
        public static final TextStyle TOTAL_PAGES = StyleInjector.$NULL$;
        public static final TextStyle HEADER = StyleInjector.$NULL$;
        public static final TextStyle NAME = StyleInjector.$NULL$;
        public static final TextStyle PLAYERS = StyleInjector.$NULL$;
        public static final TextStyle MOTD = StyleInjector.$NULL$;
      }

      public static final class Request {
        public static final TextStyle REQUEST_CREATED = StyleInjector.$NULL$;
        public static final TextStyle SERVER_CREATED = StyleInjector.$NULL$;
        public static final TextStyle ALREADY_EXISTS = StyleInjector.$NULL$;
      }

      public static final class Current {
        public static final TextStyle TEXT = StyleInjector.$NULL$;
        public static final TextStyle NAME = StyleInjector.$NULL$;
      }
    }
  }

  public static final class Countdowns {

    public static final TextStyle CANCELED = StyleInjector.$NULL$;
  }

  public static final class Settings {

    public static final TextStyle SET = StyleInjector.$NULL$;

    public static final class Info {

      public static final TextStyle HEADER_LINE = StyleInjector.$NULL$;
      public static final TextStyle HEADER_TEXT = StyleInjector.$NULL$;
      public static final TextStyle SUMMARY_TEXT = StyleInjector.$NULL$;
      public static final TextStyle SUMMARY_IDENTIFIER = StyleInjector.$NULL$;
      public static final TextStyle DESCRIPTION_TEXT = StyleInjector.$NULL$;
      public static final TextStyle DESCRIPTION_IDENTIFIER = StyleInjector.$NULL$;
      public static final TextStyle CURRENT_VALUE_TEXT = StyleInjector.$NULL$;
      public static final TextStyle CURRENT_VALUE_IDENTIFIER = StyleInjector.$NULL$;
      public static final TextStyle DEFAULT_VALUE_TEXT = StyleInjector.$NULL$;
      public static final TextStyle DEFAULT_VALUE_IDENTIFIER = StyleInjector.$NULL$;
      public static final TextStyle TOGGLE = StyleInjector.$NULL$;
    }

    public static final class List {

      public static final TextStyle HEADER_LINE = StyleInjector.$NULL$;
      public static final TextStyle HEADER_TEXT = StyleInjector.$NULL$;
      public static final TextStyle CURRENT_PAGE = StyleInjector.$NULL$;
      public static final TextStyle TOTAL_PAGES = StyleInjector.$NULL$;
      public static final TextStyle NAME = StyleInjector.$NULL$;
      public static final TextStyle SUMMARY = StyleInjector.$NULL$;
      public static final TextStyle HOVER = StyleInjector.$NULL$;
    }
  }

  public static final class FREEZE {

    public static final TextStyle FREEZE_ALERT = StyleInjector.$NULL$;
    public static final TextStyle THAW_ALERT = StyleInjector.$NULL$;
    public static final TextStyle TITLE = StyleInjector.$NULL$;
    public static final TextStyle SUBTITLE = StyleInjector.$NULL$;
  }

  public static final class Moderation {

    public static final TextStyle PREFIX = StyleInjector.$NULL$;
  }

  public static final class Commands {

    public static final TextStyle ERROR = StyleInjector.$NULL$;
    public static final TextStyle WARNING = StyleInjector.$NULL$;
  }

  public static final class Punishments {

    public static final TextStyle REASON = StyleInjector.$NULL$;
  }

  public static class Damage {

    public static final TextStyle TIME = StyleInjector.$NULL$;
    public static final TextStyle DAMAGE = StyleInjector.$NULL$;
    public static final TextStyle LOCATION = StyleInjector.$NULL$;
    public static final TextStyle DESC = StyleInjector.$NULL$;

    public static class List {

      public static final TextStyle CURRENT_PAGE_COLOR = StyleInjector.$NULL$;
      public static final TextStyle TOTAL_PAGES_COLOR = StyleInjector.$NULL$;
      public static final TextStyle HEADER_COLOR = StyleInjector.$NULL$;
    }
  }

  public static class Chat {

    public static final TextStyle CHAT_MUTED = StyleInjector.$NULL$;
    public static final TextStyle CHAT_UNMUTED = StyleInjector.$NULL$;
    public static final TextStyle CHAT_MUTED_ERROR = StyleInjector.$NULL$;
    public static final TextStyle CHAT_MUTED_ALERT = StyleInjector.$NULL$;
  }

  public static class Boolean {

    public static final TextStyle TRUE = StyleInjector.$NULL$;
    public static final TextStyle FALSE = StyleInjector.$NULL$;
  }

  public static final class Config {

    public static final TextStyle OPTION = StyleInjector.$NULL$;
  }

  public static class Compat {

    public static class Enchanting {

      public static final TextStyle ENCHANTMENT = StyleInjector.$NULL$;
      public static final TextStyle ENCHANTMENT_DISABLED = StyleInjector.$NULL$;
      public static final TextStyle ENCHANTMENT_NAME = StyleInjector.$NULL$;
      public static final TextStyle ENCHANTMENT_LEVEL = StyleInjector.$NULL$;
      public static final TextStyle ENCHANTMENT_EXP_COST = StyleInjector.$NULL$;
      public static final TextStyle ENCHANTMENT_LAPIS_COST = StyleInjector.$NULL$;
    }
  }

  public static class Whisper {

    public static final TextStyle SELF = StyleInjector.$NULL$;
    public static final TextStyle ARROW = StyleInjector.$NULL$;
    public static final TextStyle OTHER = StyleInjector.$NULL$;
    public static final TextStyle MESSAGE = StyleInjector.$NULL$;
  }

  public static class Gizmo {
    public static class Pet {
      public static final TextStyle NAME = StyleInjector.$NULL$;
    }

    public static class Device {
      public static class TNT {
        public static final TextStyle NAME = StyleInjector.$NULL$;
      }

      public static class BodySlam {
        public static final TextStyle NAME = StyleInjector.$NULL$;
      }
    }
  }

  public static class Restart {

    public static class Now {

      public static final TextStyle SUCCESS = StyleInjector.$NULL$;
      public static final TextStyle FAILED_MESSAGE = StyleInjector.$NULL$;
      public static final TextStyle FAILED_REASON = StyleInjector.$NULL$;
    }

    public static class Happening {

      public static final TextStyle WARNING = StyleInjector.$NULL$;
      public static final TextStyle KICK = StyleInjector.$NULL$;
    }

    public static class Queue {

      public static final TextStyle SUCCESS = StyleInjector.$NULL$;
      public static final TextStyle ALREADY_MESSAGE = StyleInjector.$NULL$;
      public static final TextStyle ALREADY_COMMAND = StyleInjector.$NULL$;
    }

    public static class Cancel {

      public static final TextStyle SUCCESS = StyleInjector.$NULL$;
      public static final TextStyle NONE_MESSAGE = StyleInjector.$NULL$;
      public static final TextStyle NONE_COMMAND = StyleInjector.$NULL$;
    }
  }
}

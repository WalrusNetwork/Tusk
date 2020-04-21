package network.walrus.utils.bukkit.sound;

import static network.walrus.utils.bukkit.sound.SoundInjector.$NULL$;

/**
 * Network-wide constants for every sound which is played by any plugin on any server.
 *
 * @author Austin Mayes
 */
public class NetworkSoundConstants {

  public static class Games {

    public static class Results {

      public static final ConfiguredSound TIE = $NULL$;
      public static final ConfiguredSound END_COUNTDOWN_TICK = $NULL$;
    }

    public static class Kits {

      public static final ConfiguredSound DOUBLE_JUMP = $NULL$;
    }

    public static class Applicators {

      public static final ConfiguredSound EVENT_CANCELED = $NULL$;
    }

    public static class Chat {

      public static final ConfiguredSound QUICKCHAT_SWITCHED = $NULL$;
      public static final ConfiguredSound QUICKCHAT_CANNOT_DISABLE = $NULL$;
    }

    public static class Spawns {

      public static final ConfiguredSound SELF = $NULL$;
    }

    public static class Deaths {

      public static final ConfiguredSound SELF = $NULL$;
      public static final ConfiguredSound KILLER = $NULL$;
      public static final ConfiguredSound TEAM = $NULL$;
      public static final ConfiguredSound ENEMY = $NULL$;
      public static final ConfiguredSound SPECTATOR = $NULL$;
      public static final ConfiguredSound REWARD_RECEIVE = $NULL$;
    }

    public static class Portals {

      public static final ConfiguredSound SELF = $NULL$;
      public static final ConfiguredSound OTHER = $NULL$;
    }

    public static class TNT {

      public static final ConfiguredSound INSTANT_IGNITE = $NULL$;
    }

    public static class Countdowns {

      public static class Start {

        public static final ConfiguredSound MESSAGE_BROADCAST = $NULL$;
        public static final ConfiguredSound TITLE_TICK = $NULL$;
        public static final ConfiguredSound STARTED = $NULL$;
      }
    }

    public static class OCN {

      public static class CTF {

        public static class Errors {

          public static final ConfiguredSound WRONG_FLAG = $NULL$;
          public static final ConfiguredSound CANT_CAPTURE = $NULL$;
        }

        public static class PickUp {

          public static final ConfiguredSound SELF = $NULL$;
          public static final ConfiguredSound TEAM = $NULL$;
          public static final ConfiguredSound ENEMY = $NULL$;
          public static final ConfiguredSound SPECTATOR = $NULL$;
        }

        public static class Capture {

          public static final ConfiguredSound SELF = $NULL$;
          public static final ConfiguredSound TEAM = $NULL$;
          public static final ConfiguredSound ENEMY = $NULL$;
          public static final ConfiguredSound SPECTATOR = $NULL$;
        }

        public static class Respawn {

          public static final ConfiguredSound SELF = $NULL$;
          public static final ConfiguredSound TEAM = $NULL$;
          public static final ConfiguredSound ENEMY = $NULL$;
          public static final ConfiguredSound SPECTATOR = $NULL$;
        }

        public static class Drop {

          public static final ConfiguredSound SELF = $NULL$;
          public static final ConfiguredSound TEAM = $NULL$;
          public static final ConfiguredSound ENEMY = $NULL$;
          public static final ConfiguredSound SPECTATOR = $NULL$;
        }
      }

      public static class CTW {

        public static class Errors {

          public static final ConfiguredSound BAD_WOOL = $NULL$;
          public static final ConfiguredSound WRONG_WOOL = $NULL$;
        }

        public static class PickUp {

          public static final ConfiguredSound SELF = $NULL$;
          public static final ConfiguredSound TEAM = $NULL$;
          public static final ConfiguredSound ENEMY = $NULL$;
          public static final ConfiguredSound SPECTATOR = $NULL$;
        }

        public static class Place {

          public static final ConfiguredSound SELF = $NULL$;
          public static final ConfiguredSound TEAM = $NULL$;
          public static final ConfiguredSound ENEMY = $NULL$;
          public static final ConfiguredSound SPECTATOR = $NULL$;
        }
      }

      public static class DTCM {

        public static class Errors {

          public static final ConfiguredSound NOT_REPAIRABLE = $NULL$;
          public static final ConfiguredSound ENEMY_REPAIR = $NULL$;
          public static final ConfiguredSound BREAK_OWN = $NULL$;
          public static final ConfiguredSound BREAK_OTHER = $NULL$;
        }

        public static class Touch {

          public static final ConfiguredSound SELF = $NULL$;
          public static final ConfiguredSound TEAM = $NULL$;
          public static final ConfiguredSound SPECTATOR = $NULL$;
        }

        public static class Repair {

          public static final ConfiguredSound SELF = $NULL$;
          public static final ConfiguredSound TEAM = $NULL$;
          public static final ConfiguredSound ENEMY = $NULL$;
          public static final ConfiguredSound SPECTATOR = $NULL$;
        }

        public static class Modes {

          public static final ConfiguredSound CHANGE_SUCCESS = $NULL$;
          public static final ConfiguredSound CHANGE_FAIL = $NULL$;
          public static final ConfiguredSound CHANGE_COUNTDOWN = $NULL$;
        }
      }

      public static class DTC {

        public static class Leak {

          public static final ConfiguredSound SELF = $NULL$;
          public static final ConfiguredSound TEAM = $NULL$;
          public static final ConfiguredSound ENEMY = $NULL$;
          public static final ConfiguredSound SPECTATOR = $NULL$;
        }
      }

      public static class DTM {

        public static class Destroy {

          public static final ConfiguredSound SELF = $NULL$;
          public static final ConfiguredSound TEAM = $NULL$;
          public static final ConfiguredSound ENEMY = $NULL$;
          public static final ConfiguredSound SPECTATOR = $NULL$;
        }
      }

      public static class Results {

        public static class Win {

          public static final ConfiguredSound WIN = $NULL$;
          public static final ConfiguredSound LOST = $NULL$;
        }
      }

      public static class Respawn {

        public static final ConfiguredSound ALLOWED = $NULL$;
      }

      public static class TDM {

        public static class Score {

          public static final ConfiguredSound SELF = $NULL$;
          public static final ConfiguredSound TEAM = $NULL$;
          public static final ConfiguredSound ENEMY = $NULL$;
          public static final ConfiguredSound SPECTATOR = $NULL$;
        }

        public static class Overtime {

          public static final ConfiguredSound STARTED = $NULL$;
        }
      }

      public static class Hill {

        public static class Capture {

          public static final ConfiguredSound SELF = $NULL$;
          public static final ConfiguredSound TEAM = $NULL$;
          public static final ConfiguredSound ENEMY = $NULL$;
          public static final ConfiguredSound SPECTATOR = $NULL$;
        }

        public static class UnCapture {

          public static final ConfiguredSound SELF = $NULL$;
          public static final ConfiguredSound TEAM = $NULL$;
          public static final ConfiguredSound ENEMY = $NULL$;
          public static final ConfiguredSound SPECTATOR = $NULL$;
        }

        public static class Overtime {

          public static final ConfiguredSound STARTED = $NULL$;
        }
      }

      public static class Maps {
        public static class Vote {
          public static final ConfiguredSound VOTED = $NULL$;
          public static final ConfiguredSound VOTE_ENDING = $NULL$;
          public static final ConfiguredSound VOTE_ENDED = $NULL$;
        }
      }
    }

    public static class UHC {

      public static final class PVP {

        public static final ConfiguredSound ENABLED = $NULL$;

        public static class Countdown {

          public static final ConfiguredSound EVERY_30 = $NULL$;
          public static final ConfiguredSound MINUTE_WARNING = $NULL$;
          public static final ConfiguredSound FINAL_30 = $NULL$;
        }
      }

      public static class Config {

        public static final ConfiguredSound APPLIED = $NULL$;
      }

      public static class Moderation {

        public static class HelpOp {

          public static final ConfiguredSound SENT = $NULL$;
          public static final ConfiguredSound ALERT = $NULL$;
        }

        public static class Disqualify {

          public static final ConfiguredSound PLAYER = $NULL$;
          public static final ConfiguredSound ALERT = $NULL$;
        }

        public static class Xray {

          public static final ConfiguredSound ALERT = $NULL$;
        }
      }

      public static class PlayerMod {

        public static class Health {

          public static final ConfiguredSound SELF = $NULL$;
          public static final ConfiguredSound EXECUTED = $NULL$;
        }

        public static class Feed {

          public static final ConfiguredSound SELF = $NULL$;
          public static final ConfiguredSound EXECUTED = $NULL$;
        }
      }

      public static class Scenarios {

        public static class Commands {

          public static final ConfiguredSound ENABLED = $NULL$;
          public static final ConfiguredSound DISABLED = $NULL$;
        }

        public static final class WeakestLink {
          public static final ConfiguredSound NONE = $NULL$;
          public static final ConfiguredSound SELF = $NULL$;
          public static final ConfiguredSound OTHER = $NULL$;
        }
      }

      public static class Border {

        public static final ConfiguredSound SHRINK = $NULL$;
        public static final ConfiguredSound TELEPORT = $NULL$;
      }

      public static class EndGame {

        public static final ConfiguredSound END = $NULL$;
      }

      public static class Portals {

        public static final ConfiguredSound NETHER_DISABLED = $NULL$;
        public static final ConfiguredSound END_DISABLED = $NULL$;
      }

      public static class Teams {

        public static final ConfiguredSound CREATED = $NULL$;
        public static final ConfiguredSound INVITE_SENT = $NULL$;
        public static final ConfiguredSound INVITE_RECEIVED = $NULL$;
        public static final ConfiguredSound BECOME_LONER = $NULL$;

        public static class Join {

          public static final ConfiguredSound SELF = $NULL$;
          public static final ConfiguredSound TEAMMATE = $NULL$;
        }
      }

      public static class Scatter {

        public static final ConfiguredSound STARTED = $NULL$;
        public static final ConfiguredSound COMPLETE = $NULL$;
        public static final ConfiguredSound TELEPORTED = $NULL$;
        public static final ConfiguredSound UNFROZEN = $NULL$;
        public static final ConfiguredSound RELEASE_TICK = $NULL$;
      }

      public static class Whitelist {

        public static final ConfiguredSound OFF = $NULL$;
        public static final ConfiguredSound COUNTDOWN = $NULL$;
        public static final ConfiguredSound ON = $NULL$;
        public static final ConfiguredSound CLEARED = $NULL$;
        public static final ConfiguredSound CANCELLED = $NULL$;
      }
    }
  }

  public static class Freeze {

    public static class Frozen {

      public static final ConfiguredSound VICTIM = $NULL$;
      public static final ConfiguredSound ALERT = $NULL$;
    }

    public static class Thawed {

      public static final ConfiguredSound VICTIM = $NULL$;
      public static final ConfiguredSound ALERT = $NULL$;
    }
  }

  public static class Defuse {

    public static final ConfiguredSound SELF = $NULL$;
    public static final ConfiguredSound OTHER = $NULL$;
  }

  public static class Extinguish {

    public static final ConfiguredSound SELF = $NULL$;
    public static final ConfiguredSound OTHER = $NULL$;
  }

  public static class Lobby {
    public static class JumpPads {
      public static final ConfiguredSound ERROR = $NULL$;
      public static final ConfiguredSound USE = $NULL$;
    }
  }
}

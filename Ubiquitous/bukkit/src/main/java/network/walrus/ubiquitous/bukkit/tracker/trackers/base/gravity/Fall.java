package network.walrus.ubiquitous.bukkit.tracker.trackers.base.gravity;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

/**
 * Data class to represent a fall from a location.
 *
 * @author Overcast Network
 */
public class Fall {

  // The player who will get credit for any damage caused by the fall
  public final LivingEntity attacker;
  // The kind of attack that initiated the fall
  public final Cause cause;
  // The type of place that the player fell from
  public final From from;
  // The time of the attack or block break that initiated the fall
  final long attackTime;
  // THe location the player was last on the ground
  public Location whereOnGround;
  // The falling player
  LivingEntity victim;
  // If the player is on the ground when attacked, this is initially set false and later set true
  // when they leave
  // the ground within the allowed time window. If the player is already in the air when attacked,
  // this is set true.
  // This is used to distinguish the initial knockback/spleef from ground touches that occur during
  // the fall.
  boolean isFalling;
  // Time the player last transitioned from off-ground to on-ground
  long onGroundTime;
  // The player's most recent swimming state and the time it was last set true
  boolean isSwimming;
  long swimmingTime;
  // The player's most recent climbing state and the time it was last set true
  boolean isClimbing;
  long climbingTime;
  // The player's most recent in-lava state and the time it was last set true
  boolean isInLava;
  long inLavaTime;
  // The number of times the player has touched the ground during since isFalling was set true
  int groundTouchCount;

  Fall(LivingEntity attacker, Cause cause, LivingEntity victim, From from, long attackTime) {
    this.attacker = attacker;
    this.cause = cause;
    this.victim = victim;
    this.from = from;
    this.attackTime = this.swimmingTime = this.climbingTime = this.onGroundTime = attackTime;
    this.groundTouchCount = 0;
  }

  /** Action which caused the fall to occur. */
  public enum Cause {
    HIT,
    SHOOT,
    SPLEEF
  }

  /** General location type where the fall started. */
  public enum From {
    FLOOR,
    LADDER,
    WATER
  }
}

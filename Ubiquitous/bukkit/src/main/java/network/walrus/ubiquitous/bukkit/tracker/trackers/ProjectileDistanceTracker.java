package network.walrus.ubiquitous.bukkit.tracker.trackers;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bukkit.Location;
import org.bukkit.entity.Projectile;

/**
 * Tracks all interactions with projectiles.
 *
 * @author Overcast Network
 */
public interface ProjectileDistanceTracker extends Tracker {

  /**
   * Determine if a projectile has a launch location.
   *
   * @param entity to get launch
   * @return if the projectile has a launch location
   */
  boolean hasLaunchLocation(@Nonnull Projectile entity);

  /**
   * Get the known location that a projectile was launched from.
   *
   * @param projectile to get location from
   * @return location that the projectile launched from
   */
  @Nullable
  Location getLaunchLocation(@Nonnull Projectile projectile);

  /**
   * Set the known location that a projectile was launched from.
   *
   * @param projectile to set location for
   * @param location that the projectile launched from
   * @return previous location that the projectile launched from
   */
  @Nullable
  Location setLaunchLocation(@Nonnull Projectile projectile, @Nullable Location location);
}

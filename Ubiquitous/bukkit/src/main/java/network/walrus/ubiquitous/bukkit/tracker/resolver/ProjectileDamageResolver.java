package network.walrus.ubiquitous.bukkit.tracker.resolver;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import network.walrus.ubiquitous.bukkit.tracker.info.DamageInfo;
import network.walrus.ubiquitous.bukkit.tracker.info.ProjectileDamageInfo;
import network.walrus.ubiquitous.bukkit.tracker.lifetime.Lifetime;
import network.walrus.ubiquitous.bukkit.tracker.trackers.ProjectileDistanceTracker;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * Resolver which resolves the information about the cause of a damage caused by a projectile.
 *
 * @author Overcast Network
 */
public class ProjectileDamageResolver implements DamageResolver {

  private final ProjectileDistanceTracker projectileDistanceTracker;

  /**
   * Constructor.
   *
   * @param projectileDistanceTracker used to track projectile distance
   */
  public ProjectileDamageResolver(ProjectileDistanceTracker projectileDistanceTracker) {
    this.projectileDistanceTracker = projectileDistanceTracker;
  }

  public @Nullable DamageInfo resolve(
      @Nonnull LivingEntity entity,
      @Nonnull Lifetime lifetime,
      @Nonnull EntityDamageEvent damageEvent) {
    if (damageEvent instanceof EntityDamageByEntityEvent) {
      EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) damageEvent;

      if (event.getDamager() instanceof Projectile) {
        Projectile projectile = (Projectile) event.getDamager();
        Location launchLocation = this.projectileDistanceTracker.getLaunchLocation(projectile);
        Double projectileDistance = null;

        if (launchLocation != null) {
          projectileDistance = event.getEntity().getLocation().distance(launchLocation);
        }

        if (projectile.getShooter() instanceof LivingEntity) {
          return new ProjectileDamageInfo(
              projectile, (LivingEntity) projectile.getShooter(), projectileDistance);
        }
      }
    }
    return null;
  }
}

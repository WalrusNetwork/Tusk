package network.walrus.ubiquitous.bukkit.tracker.resolver;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import network.walrus.ubiquitous.bukkit.tracker.info.DamageInfo;
import network.walrus.ubiquitous.bukkit.tracker.info.DispensedProjectileDamageInfo;
import network.walrus.ubiquitous.bukkit.tracker.lifetime.Lifetime;
import network.walrus.ubiquitous.bukkit.tracker.trackers.DispenserTracker;
import network.walrus.ubiquitous.bukkit.tracker.trackers.ProjectileDistanceTracker;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.projectiles.BlockProjectileSource;

/**
 * Resolver which resolves the information about the cause of a damage caused by a projectile shot
 * from a dispenser.
 *
 * @author Overcast Network
 */
public class DispensedProjectileDamageResolver implements DamageResolver {

  private final ProjectileDistanceTracker projectileDistanceTracker;
  private final DispenserTracker dispenserTracker;

  /**
   * Constructor.
   *
   * @param projectileDistanceTracker used to get projectile distance
   * @param dispenserTracker used to get dispenser information
   */
  public DispensedProjectileDamageResolver(
      ProjectileDistanceTracker projectileDistanceTracker, DispenserTracker dispenserTracker) {
    this.projectileDistanceTracker = projectileDistanceTracker;
    this.dispenserTracker = dispenserTracker;
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
        OfflinePlayer dispenserOwner = dispenserTracker.getOwner(projectile);

        if (launchLocation != null) {
          projectileDistance = event.getEntity().getLocation().distance(launchLocation);
        }

        if (projectile.getShooter() instanceof BlockProjectileSource) {
          Block block = ((BlockProjectileSource) projectile.getShooter()).getBlock();
          OfflinePlayer placer = this.dispenserTracker.getPlacer(block);

          if (placer != null) {
            if (placer.isOnline()) {
              return new DispensedProjectileDamageInfo(
                  projectile, (Player) placer, projectileDistance, placer);
            } else {
              return new DispensedProjectileDamageInfo(
                  projectile, null, projectileDistance, placer);
            }
          }
        }
        if (projectile.getShooter() instanceof LivingEntity) {
          return new DispensedProjectileDamageInfo(
              projectile,
              (LivingEntity) projectile.getShooter(),
              projectileDistance,
              dispenserOwner);
        }

        return new DispensedProjectileDamageInfo(projectile, null, projectileDistance, null);
      }
    }
    return null;
  }
}

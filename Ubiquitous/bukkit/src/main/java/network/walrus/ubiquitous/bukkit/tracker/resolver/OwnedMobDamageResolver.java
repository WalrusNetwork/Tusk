package network.walrus.ubiquitous.bukkit.tracker.resolver;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import network.walrus.ubiquitous.bukkit.tracker.info.DamageInfo;
import network.walrus.ubiquitous.bukkit.tracker.info.OwnedMobDamageInfo;
import network.walrus.ubiquitous.bukkit.tracker.lifetime.Lifetime;
import network.walrus.ubiquitous.bukkit.tracker.trackers.OwnedMobTracker;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * Resolver which resolves the information about the cause of a damage caused by a mob.
 *
 * @author Overcast Network
 */
public class OwnedMobDamageResolver implements DamageResolver {

  private final OwnedMobTracker ownedMobTracker;

  /**
   * Constructor.
   *
   * @param ownedMobTracker tracker used to get mob information from
   */
  public OwnedMobDamageResolver(OwnedMobTracker ownedMobTracker) {
    this.ownedMobTracker = ownedMobTracker;
  }

  public @Nullable DamageInfo resolve(
      @Nonnull LivingEntity entity,
      @Nonnull Lifetime lifetime,
      @Nonnull EntityDamageEvent damageEvent) {
    if (damageEvent instanceof EntityDamageByEntityEvent) {
      EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) damageEvent;

      if (event.getDamager() instanceof Projectile) {
        if (((Projectile) event.getDamager()).getShooter() == null) {
          return null;
        }
        if (!(((Projectile) event.getDamager()).getShooter() instanceof Player)
            && ((Projectile) event.getDamager()).getShooter() instanceof LivingEntity) {
          LivingEntity mob = (LivingEntity) ((Projectile) event.getDamager()).getShooter();
          OfflinePlayer mobOwner = this.ownedMobTracker.getOwner(mob);

          if (mobOwner != null) {
            return new OwnedMobDamageInfo(mobOwner, mob, (Projectile) event.getDamager());
          }
        }
      } else if (!(event.getDamager() instanceof Player)
          && event.getDamager() instanceof LivingEntity) {
        LivingEntity mob = (LivingEntity) event.getDamager();
        OfflinePlayer mobOwner = this.ownedMobTracker.getOwner(mob);

        if (mobOwner != null) {
          return new OwnedMobDamageInfo(mobOwner, mob, null);
        }
      }
    }
    return null;
  }
}

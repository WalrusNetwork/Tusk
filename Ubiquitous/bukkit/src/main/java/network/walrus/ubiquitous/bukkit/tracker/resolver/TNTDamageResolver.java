package network.walrus.ubiquitous.bukkit.tracker.resolver;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import network.walrus.ubiquitous.bukkit.tracker.info.DamageInfo;
import network.walrus.ubiquitous.bukkit.tracker.info.TNTDamageInfo;
import network.walrus.ubiquitous.bukkit.tracker.lifetime.Lifetime;
import network.walrus.ubiquitous.bukkit.tracker.trackers.DispenserTracker;
import network.walrus.ubiquitous.bukkit.tracker.trackers.ExplosiveTracker;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * Resolver which resolves the information about the cause of a damage caused by a tnt explosion.
 *
 * @author Overcast Network
 */
public class TNTDamageResolver implements DamageResolver {

  private final ExplosiveTracker explosiveTracker;
  private final DispenserTracker dispenserTracker;

  /**
   * Constructor.
   *
   * @param explosiveTracker used to track base explosion damage
   * @param dispenserTracker used to track explosions caused by entities shot from dispensers
   */
  public TNTDamageResolver(ExplosiveTracker explosiveTracker, DispenserTracker dispenserTracker) {
    this.explosiveTracker = explosiveTracker;
    this.dispenserTracker = dispenserTracker;
  }

  public @Nullable DamageInfo resolve(
      @Nonnull LivingEntity entity,
      @Nonnull Lifetime lifetime,
      @Nonnull EntityDamageEvent damageEvent) {
    if (damageEvent instanceof EntityDamageByEntityEvent) {
      EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) damageEvent;

      if (event.getDamager() instanceof TNTPrimed) {
        TNTPrimed tnt = (TNTPrimed) event.getDamager();
        OfflinePlayer player = null;
        if (this.explosiveTracker.hasOwner(tnt)) {
          player = this.explosiveTracker.getOwner(tnt);
        } else if (this.dispenserTracker.hasOwner(tnt)) {
          player = this.dispenserTracker.getOwner(tnt);
        }

        LivingEntity owner = null;
        if (player != null && player.isOnline()) {
          owner = (LivingEntity) player;
        }

        return new TNTDamageInfo(tnt, owner);
      }
    }

    return null;
  }
}

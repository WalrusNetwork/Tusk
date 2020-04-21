package network.walrus.ubiquitous.bukkit.tracker.resolver;

import network.walrus.ubiquitous.bukkit.tracker.info.AnvilDamageInfo;
import network.walrus.ubiquitous.bukkit.tracker.info.DamageInfo;
import network.walrus.ubiquitous.bukkit.tracker.lifetime.Lifetime;
import network.walrus.ubiquitous.bukkit.tracker.trackers.AnvilTracker;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * Class responsible for resolving who caused an anvil to deal damage.
 *
 * @author Overcast Network
 */
public class AnvilDamageResolver implements DamageResolver {

  private final AnvilTracker anvilTracker;

  /**
   * Constructor.
   *
   * @param anvilTracker used to track interactions with anvils
   */
  public AnvilDamageResolver(AnvilTracker anvilTracker) {
    this.anvilTracker = anvilTracker;
  }

  public DamageInfo resolve(LivingEntity entity, Lifetime lifetime, EntityDamageEvent damageEvent) {
    if (damageEvent instanceof EntityDamageByEntityEvent) {
      EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) damageEvent;

      if (event.getDamager() instanceof FallingBlock) {
        FallingBlock anvil = (FallingBlock) event.getDamager();
        OfflinePlayer offlineOwner = this.anvilTracker.getOwner(anvil);
        Player onlineOwner = null;

        if (offlineOwner != null) {
          onlineOwner = offlineOwner.getPlayer();
        }

        return new AnvilDamageInfo(anvil, onlineOwner, offlineOwner);
      }
    }

    return null;
  }
}

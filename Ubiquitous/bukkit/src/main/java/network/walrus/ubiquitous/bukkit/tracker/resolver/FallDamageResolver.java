package network.walrus.ubiquitous.bukkit.tracker.resolver;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import network.walrus.ubiquitous.bukkit.tracker.info.DamageInfo;
import network.walrus.ubiquitous.bukkit.tracker.info.base.SimpleFallDamageInfo;
import network.walrus.ubiquitous.bukkit.tracker.lifetime.Lifetime;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

/**
 * Resolver which resolves the information about the cause of a damage caused by falling.
 *
 * @author Overcast Network
 */
public class FallDamageResolver implements DamageResolver {

  public @Nullable DamageInfo resolve(
      @Nonnull LivingEntity entity,
      @Nonnull Lifetime lifetime,
      @Nonnull EntityDamageEvent damageEvent) {
    if (damageEvent.getCause() == DamageCause.FALL) {
      float fallDistance = Math.max(0, entity.getFallDistance());

      return new SimpleFallDamageInfo(null, fallDistance);
    }

    return null;
  }
}

package network.walrus.ubiquitous.bukkit.tracker.resolver;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import network.walrus.ubiquitous.bukkit.tracker.info.DamageInfo;
import network.walrus.ubiquitous.bukkit.tracker.info.base.SimpleVoidDamageInfo;
import network.walrus.ubiquitous.bukkit.tracker.lifetime.Lifetime;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

/**
 * Resolver which resolves the information about the cause of a damage caused by a falling in the
 * void.
 *
 * @author Overcast Network
 */
public class VoidDamageResolver implements DamageResolver {

  public @Nullable DamageInfo resolve(
      @Nonnull LivingEntity entity,
      @Nonnull Lifetime lifetime,
      @Nonnull EntityDamageEvent damageEvent) {
    if (damageEvent.getCause() == DamageCause.VOID) {
      return new SimpleVoidDamageInfo(null);
    }

    return null;
  }
}

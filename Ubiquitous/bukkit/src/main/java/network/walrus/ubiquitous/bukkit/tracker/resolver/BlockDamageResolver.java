package network.walrus.ubiquitous.bukkit.tracker.resolver;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import network.walrus.ubiquitous.bukkit.tracker.info.DamageInfo;
import network.walrus.ubiquitous.bukkit.tracker.info.base.SimpleBlockDamageInfo;
import network.walrus.ubiquitous.bukkit.tracker.lifetime.Lifetime;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

/**
 * Resolver responsible for resolving the block which caused a {@link EntityDamageByBlockEvent}.
 *
 * @author Overcast Network
 */
public class BlockDamageResolver implements DamageResolver {

  public @Nullable DamageInfo resolve(
      @Nonnull LivingEntity entity,
      @Nonnull Lifetime lifetime,
      @Nonnull EntityDamageEvent damageEvent) {
    if (damageEvent instanceof EntityDamageByBlockEvent
        && damageEvent.getCause() == DamageCause.CONTACT) {
      EntityDamageByBlockEvent blockEvent = (EntityDamageByBlockEvent) damageEvent;

      return new SimpleBlockDamageInfo(blockEvent.getDamager().getState());
    }

    return null;
  }
}

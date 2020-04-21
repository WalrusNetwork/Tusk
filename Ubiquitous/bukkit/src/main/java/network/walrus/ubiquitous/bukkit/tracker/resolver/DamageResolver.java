package network.walrus.ubiquitous.bukkit.tracker.resolver;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import network.walrus.ubiquitous.bukkit.tracker.info.DamageInfo;
import network.walrus.ubiquitous.bukkit.tracker.lifetime.Lifetime;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * Resolves a {@link DamageInfo} using various info about and surrounding a {@link
 * EntityDamageEvent}.
 *
 * @author Overcast Network
 */
public interface DamageResolver {

  /**
   * Generate a {@link DamageInfo} using information about the {@link EntityDamageEvent} and the
   * entity which the event is being called for.
   *
   * @param entity who is being damaged
   * @param lifetime of the entity
   * @param damageEvent thrown by Bukkit when the damage occurred
   * @return info describing more information about the damage situation
   */
  @Nullable
  DamageInfo resolve(
      @Nonnull LivingEntity entity,
      @Nonnull Lifetime lifetime,
      @Nonnull EntityDamageEvent damageEvent);
}

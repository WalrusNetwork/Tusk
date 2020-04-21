package network.walrus.ubiquitous.bukkit.tracker.api;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import network.walrus.ubiquitous.bukkit.tracker.Damage;
import network.walrus.ubiquitous.bukkit.tracker.info.DamageInfo;
import network.walrus.ubiquitous.bukkit.tracker.lifetime.Lifetime;
import network.walrus.ubiquitous.bukkit.tracker.resolver.DamageResolver;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * Resolves the damage stored in the {@link SimpleDamageAPI}.
 *
 * <p>When a plugin uses the API to inflict damage on an entity, it specifies its own {@link Damage}
 * object to use. However, plugins listening on the Bukkit event will use the regular channels to
 * fetch the object for the event. Therefore, this resolver is necessary to feed the proper object
 * to those event listeners.
 *
 * @author Overcast Network
 */
public class DamageAPIResolver implements DamageResolver {

  private final DamageAPIHelper helper;

  /**
   * Constructor.
   *
   * @param helper used to resolve damage info
   */
  public DamageAPIResolver(DamageAPIHelper helper) {
    this.helper = helper;
  }

  /** @see DamageAPIHelper#getEventDamageInfo(EntityDamageEvent) */
  public @Nullable DamageInfo resolve(
      @Nonnull LivingEntity entity,
      @Nonnull Lifetime lifetime,
      @Nonnull EntityDamageEvent damageEvent) {
    return helper.getEventDamageInfo(damageEvent);
  }
}

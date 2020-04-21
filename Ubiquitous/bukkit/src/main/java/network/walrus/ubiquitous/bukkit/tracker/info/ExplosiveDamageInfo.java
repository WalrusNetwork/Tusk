package network.walrus.ubiquitous.bukkit.tracker.info;

import com.google.common.base.Preconditions;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import network.walrus.ubiquitous.bukkit.tracker.base.AbstractDamageInfo;
import org.bukkit.entity.Explosive;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

/**
 * Information about damage caused by an {@link Explosive}.
 *
 * @author Overcast Network
 */
public class ExplosiveDamageInfo extends AbstractDamageInfo {

  Explosive explosive;

  /**
   * Constructor.
   *
   * @param explosive which caused the damage
   * @param resolvedDamager which caused the explosion
   */
  public ExplosiveDamageInfo(@Nonnull Explosive explosive, @Nullable LivingEntity resolvedDamager) {
    super(resolvedDamager);
    this.explosive = Preconditions.checkNotNull(explosive);
  }

  public @Nonnull Explosive getExplosive() {
    return this.explosive;
  }

  @Override
  public @Nonnull String toString() {
    return "ExplosiveDamageInfo{explosive="
        + this.explosive
        + ",damager="
        + this.resolvedDamager
        + "}";
  }

  @Override
  public @Nonnull DamageCause getDamageCause() {
    return DamageCause.ENTITY_ATTACK;
  }
}

package network.walrus.ubiquitous.bukkit.tracker.info;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

/**
 * Represents damage inflicted by TNT explosions.
 *
 * @author Overcast Network
 */
public class TNTDamageInfo extends ExplosiveDamageInfo {

  /**
   * Constructor.
   *
   * @param explosive which caused the damage
   * @param resolvedDamager which caused the explosion
   */
  public TNTDamageInfo(@Nonnull TNTPrimed explosive, @Nullable LivingEntity resolvedDamager) {
    super(explosive, resolvedDamager);
  }

  @Override
  public @Nonnull TNTPrimed getExplosive() {
    return (TNTPrimed) this.explosive;
  }

  @Override
  public @Nonnull String toString() {
    return "TNTDamageInfo{explosive=" + this.explosive + ",damager=" + this.resolvedDamager + "}";
  }

  @Override
  public @Nonnull DamageCause getDamageCause() {
    return DamageCause.BLOCK_EXPLOSION;
  }
}

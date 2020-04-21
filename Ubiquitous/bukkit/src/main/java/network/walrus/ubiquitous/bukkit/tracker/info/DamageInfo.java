package network.walrus.ubiquitous.bukkit.tracker.info;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

/**
 * Provides more detailed information about a damage instance.
 *
 * <p>Subclasses should be completely immutable.
 *
 * @author Overcast Network
 */
public interface DamageInfo {

  /**
   * Gets the Bukkit damage cause associated with this info.
   *
   * @return The damage cause.
   */
  @Nonnull
  DamageCause getDamageCause();

  /**
   * Gets the living entity most responsible for this damage.
   *
   * @return Resolved damager or null if none exists
   */
  @Nullable
  LivingEntity getResolvedDamager();
}

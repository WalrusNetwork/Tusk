package network.walrus.ubiquitous.bukkit.tracker.base;

import javax.annotation.Nullable;
import network.walrus.ubiquitous.bukkit.tracker.info.DamageInfo;
import org.bukkit.entity.LivingEntity;

/**
 * Class which is used to provide more detailed information about a damage instance.
 *
 * @author Overcast Network
 */
public abstract class AbstractDamageInfo implements DamageInfo {

  protected final @Nullable LivingEntity resolvedDamager;

  /**
   * Constructor.
   *
   * @param resolvedDamager entity which caused the damage
   */
  protected AbstractDamageInfo(@Nullable LivingEntity resolvedDamager) {
    this.resolvedDamager = resolvedDamager;
  }

  public @Nullable LivingEntity getResolvedDamager() {
    return this.resolvedDamager;
  }
}

package network.walrus.ubiquitous.bukkit.tracker.info.base;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import network.walrus.ubiquitous.bukkit.tracker.base.AbstractDamageInfo;
import network.walrus.ubiquitous.bukkit.tracker.info.LavaDamageInfo;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

/**
 * @author Overcast Network
 * @see LavaDamageInfo
 */
public class SimpleLavaDamageInfo extends AbstractDamageInfo implements LavaDamageInfo {

  /**
   * Constructor.
   *
   * @param resolvedDamager entity which caused the damage
   */
  public SimpleLavaDamageInfo(@Nullable LivingEntity resolvedDamager) {
    super(resolvedDamager);
  }

  @Override
  public @Nonnull DamageCause getDamageCause() {
    return DamageCause.LAVA;
  }
}

package network.walrus.ubiquitous.bukkit.tracker.info.base;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import network.walrus.ubiquitous.bukkit.tracker.base.AbstractDamageInfo;
import network.walrus.ubiquitous.bukkit.tracker.info.VoidDamageInfo;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

/**
 * @author Overcast Network
 * @see VoidDamageInfo
 */
public class SimpleVoidDamageInfo extends AbstractDamageInfo implements VoidDamageInfo {

  /**
   * Constructor.
   *
   * @param resolvedDamager entity which caused the player to fall in the void
   */
  public SimpleVoidDamageInfo(@Nullable LivingEntity resolvedDamager) {
    super(resolvedDamager);
  }

  @Override
  public @Nonnull DamageCause getDamageCause() {
    return DamageCause.VOID;
  }
}

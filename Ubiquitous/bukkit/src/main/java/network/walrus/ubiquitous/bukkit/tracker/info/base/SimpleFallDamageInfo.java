package network.walrus.ubiquitous.bukkit.tracker.info.base;

import com.google.common.base.Preconditions;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import network.walrus.ubiquitous.bukkit.tracker.base.AbstractDamageInfo;
import network.walrus.ubiquitous.bukkit.tracker.info.FallDamageInfo;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

/**
 * @author Overcast Network
 * @see FallDamageInfo
 */
public class SimpleFallDamageInfo extends AbstractDamageInfo implements FallDamageInfo {

  private final float fallDistance;

  /**
   * Constructor.
   *
   * @param resolvedDamager entity which caused the damage
   * @param fallDistance distance the player fell
   */
  public SimpleFallDamageInfo(@Nullable LivingEntity resolvedDamager, float fallDistance) {
    super(resolvedDamager);

    Preconditions.checkArgument(fallDistance >= 0, "fall distance must be >= 0");

    this.fallDistance = fallDistance;
  }

  public float getFallDistance() {
    return this.fallDistance;
  }

  @Override
  public @Nonnull DamageCause getDamageCause() {
    return DamageCause.FALL;
  }
}

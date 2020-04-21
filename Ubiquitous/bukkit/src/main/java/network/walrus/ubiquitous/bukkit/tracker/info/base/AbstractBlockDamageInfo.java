package network.walrus.ubiquitous.bukkit.tracker.info.base;

import com.google.common.base.Preconditions;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import network.walrus.ubiquitous.bukkit.tracker.base.AbstractDamageInfo;
import network.walrus.ubiquitous.bukkit.tracker.info.BlockDamageInfo;
import org.bukkit.block.BlockState;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

/**
 * Class to represent a damage caused by a specific block in the world.
 *
 * @author Overcast Network
 */
public class AbstractBlockDamageInfo extends AbstractDamageInfo implements BlockDamageInfo {

  private final @Nonnull BlockState blockDamager;

  /**
   * Constructor.
   *
   * @param resolvedDamager who caused the damage, if there was one
   * @param blockDamager responsible for the damage
   */
  AbstractBlockDamageInfo(
      @Nullable LivingEntity resolvedDamager, @Nonnull BlockState blockDamager) {
    super(resolvedDamager);

    Preconditions.checkNotNull(blockDamager, "block damager");

    this.blockDamager = blockDamager;
  }

  public @Nonnull BlockState getBlockDamager() {
    return this.blockDamager;
  }

  @Override
  public @Nonnull DamageCause getDamageCause() {
    return DamageCause.CONTACT;
  }
}

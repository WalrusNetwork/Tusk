package network.walrus.ubiquitous.bukkit.tracker.info;

import javax.annotation.Nonnull;
import org.bukkit.block.BlockState;

/**
 * Represents a damage caused by a specific block in the world.
 *
 * @author Overcast Network
 */
public interface BlockDamageInfo extends DamageInfo {

  /**
   * Gets the world block responsible for this damage.
   *
   * @return Snapshot of the damaging block
   */
  @Nonnull
  BlockState getBlockDamager();
}

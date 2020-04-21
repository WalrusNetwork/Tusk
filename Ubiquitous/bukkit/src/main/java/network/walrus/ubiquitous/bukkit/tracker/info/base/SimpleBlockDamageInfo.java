package network.walrus.ubiquitous.bukkit.tracker.info.base;

import javax.annotation.Nonnull;
import org.bukkit.block.BlockState;

/**
 * Class to represent a damage caused by a specific block in the world.
 *
 * @author Overcast Network
 */
public class SimpleBlockDamageInfo extends AbstractBlockDamageInfo {

  /**
   * Constructor.
   *
   * @param blockDamager responsible for the damage
   */
  public SimpleBlockDamageInfo(@Nonnull BlockState blockDamager) {
    super(null, blockDamager);
  }
}

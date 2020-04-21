package network.walrus.ubiquitous.bukkit.tracker.trackers.base.gravity;

import java.util.HashMap;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

/**
 * A block is has been broken by a player.
 *
 * @author Overcast Network
 */
public class BrokenBlock {

  private static final float PLAYER_WIDTH = 0.6f;
  private static final float PLAYER_RADIUS = PLAYER_WIDTH / 2.0f;
  public final Block block;
  public final long time;
  final Player breaker;

  /**
   * Constructor.
   *
   * @param block which was broken
   * @param breaker who broke the block
   * @param time the block was broken
   */
  BrokenBlock(Block block, Player breaker, long time) {
    this.block = block;
    this.breaker = breaker;
    this.time = time;
  }

  /**
   * Determine the {@link BrokenBlock} which the player was last standing on.
   *
   * @param player to get the blocks for
   * @param blocks to get the latest from
   * @return the block which was broken last under the player
   */
  public static BrokenBlock lastBlockBrokenUnderPlayer(
      LivingEntity player, HashMap<Location, BrokenBlock> blocks) {
    Location location = player.getLocation();

    int y = (int) Math.floor(location.getY() - 0.1);

    int x1 = (int) Math.floor(location.getX() - PLAYER_RADIUS);
    int z1 = (int) Math.floor(location.getZ() - PLAYER_RADIUS);

    int x2 = (int) Math.floor(location.getX() + PLAYER_RADIUS);
    int z2 = (int) Math.floor(location.getZ() + PLAYER_RADIUS);

    BrokenBlock lastBrokenBlock = null;

    for (int x = x1; x <= x2; ++x) {
      for (int z = z1; z <= z2; ++z) {
        Location bl = new Location(location.getWorld(), x, y, z);

        if (blocks.containsKey(bl)) {
          BrokenBlock brokenBlock = blocks.get(bl);
          if (lastBrokenBlock == null || brokenBlock.time > lastBrokenBlock.time) {
            lastBrokenBlock = brokenBlock;
          }
        }
      }
    }

    return lastBrokenBlock;
  }
}

package network.walrus.games.octc.ctf.flags;

import network.walrus.utils.bukkit.region.shapes.BlockRegion;
import org.bukkit.util.Vector;

/**
 * A place where a {@link FlagObjective} spawns.
 *
 * @author Austin Mayes
 */
public class Post {

  private final BlockRegion region;
  private final float yaw;

  /**
   * @param region where this post is
   * @param yaw that flags will spawned at
   */
  public Post(BlockRegion region, float yaw) {
    this.region = region;
    this.yaw = yaw;
  }

  public BlockRegion getRegion() {
    return region;
  }

  public Vector getVector() {
    return region.getCenter();
  }

  /**
   * Spawn a flag at this post.
   *
   * @param objective to spawn here
   */
  public void spawn(FlagObjective objective) {
    objective.spawn(this.region.getCenter(), this.yaw);
  }
}

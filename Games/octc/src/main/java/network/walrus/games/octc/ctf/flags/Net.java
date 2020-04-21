package network.walrus.games.octc.ctf.flags;

import java.util.Optional;
import network.walrus.games.octc.global.groups.teams.Team;
import network.walrus.utils.bukkit.region.BoundedRegion;
import org.bukkit.entity.Player;

/**
 * A region where a {@link FlagObjective} can be captured.
 *
 * @author Austin Mayes
 */
public class Net {
  private final BoundedRegion region;
  private final Optional<Team> owner;
  private final int reward;

  /**
   * @param region making up the net
   * @param owner of the net, if empty anyone can capture at this net
   * @param reward to give to players who capture flags at this net
   */
  public Net(BoundedRegion region, Optional<Team> owner, int reward) {
    this.region = region;
    this.owner = owner;
    this.reward = reward;
  }

  /** @return if this net has a capture reward */
  public boolean hasReward() {
    return this.reward > 0;
  }

  public BoundedRegion getRegion() {
    return region;
  }

  public int getReward() {
    return reward;
  }

  /**
   * Determine if a player is allowed to capture flags at this net.
   *
   * @param player to check
   * @return if the player can capture a flag at this net
   */
  public boolean canUse(Player player) {
    return this.owner.map(team -> team.hasPlayer(player.getUniqueId())).orElse(true);
  }
}

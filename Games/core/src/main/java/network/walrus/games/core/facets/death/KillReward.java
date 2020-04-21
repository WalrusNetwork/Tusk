package network.walrus.games.core.facets.death;

import java.util.Optional;
import java.util.UUID;
import network.walrus.games.core.facets.filters.Filter;
import network.walrus.games.core.facets.filters.context.FilterContext;
import network.walrus.games.core.facets.filters.variable.AttackerVariable;
import network.walrus.games.core.facets.filters.variable.LocationVariable;
import network.walrus.games.core.facets.filters.variable.PlayerVariable;
import network.walrus.games.core.facets.filters.variable.VictimVariable;
import network.walrus.games.core.facets.kits.Kit;
import network.walrus.games.core.facets.stats.trackers.KillTracker;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import org.bukkit.entity.Player;

/**
 * A wrapper for a {@link Kit} that is rewarded to a player when they kill someone if an optional
 * {@link Filter} passes.
 *
 * @author Austin Mayes
 */
public class KillReward {

  private final FacetHolder holder;
  private final Kit kit;
  private final Optional<Filter> filter;
  private final int afterKills;
  private final int everyKills;

  /**
   * @param kit to be rewarded
   * @param filter to determine if this reward should be given
   */
  KillReward(
      FacetHolder holder,
      Kit kit,
      Optional<Filter> filter,
      Optional<Integer> after,
      Optional<Integer> every) {
    this.holder = holder;
    this.kit = kit;
    this.filter = filter;
    this.afterKills = after.orElse(0);
    this.everyKills = every.orElse(1);
  }

  /**
   * Give this reward to a player.
   *
   * @param player to give the reward to
   */
  public void give(Player player) {
    this.kit.apply(player);
  }

  /**
   * Determine if this specific reward should be given to a player based on who they killed.
   *
   * @param killer who is getting the reward
   * @param target who the player killed
   * @return if this reward should be given
   */
  public boolean passes(Player killer, Player target) {
    if (this.filter.isPresent()) {
      FilterContext context = new FilterContext();

      AttackerVariable attacker = new AttackerVariable();
      attacker.add(new PlayerVariable(killer));

      VictimVariable victim = new VictimVariable();
      victim.add(new PlayerVariable(target));

      context.add(attacker);
      context.add(victim);
      context.add(new LocationVariable(target.getLocation()));

      return this.filter.get().test(context).passes();
    } else {
      return true;
    }
  }

  /**
   * Determine if this reward should be given to a player based on their kill streak.
   *
   * @param uuid of the player to check
   * @return if the reward is applicable with this number of kills
   */
  public boolean isStreakValid(KillTracker killTracker, UUID uuid) {
    int kills = killTracker.getStreak(uuid);

    kills -= afterKills;
    if (kills < 0) {
      return false;
    } else {
      return kills % everyKills == 0;
    }
  }
}

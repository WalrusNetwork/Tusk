package network.walrus.games.octc.tdm;

import network.walrus.games.core.facets.filters.Filter;
import network.walrus.games.core.facets.filters.FilterCache;
import network.walrus.games.core.facets.filters.context.FilterContext;
import network.walrus.games.core.facets.filters.variable.PlayerVariable;
import network.walrus.utils.bukkit.region.BoundedRegion;
import network.walrus.utils.core.math.NumberAction;
import org.bukkit.entity.Player;

/**
 * A region a {@link org.bukkit.entity.Player} can enter to gain points.
 *
 * @author Austin Mayes
 */
public class ScoreBox {

  private final Integer points;
  private final NumberAction pointsAction;
  private final BoundedRegion region;
  private final Filter filter;
  private final FilterCache<Player> cache = new FilterCache<>();
  private final boolean heal;

  /**
   * @param points that this box rewards
   * @param pointsAction to use when modifying the points
   * @param region that this box occupies
   * @param filter to determine if a player should get points
   * @param heal if the player should be healed when they are sent back to spawn
   */
  public ScoreBox(
      Integer points,
      NumberAction pointsAction,
      BoundedRegion region,
      Filter filter,
      boolean heal) {
    this.points = points;
    this.pointsAction = pointsAction;
    this.region = region;
    this.filter = filter;
    this.heal = heal;
  }

  Integer getPoints() {
    return points;
  }

  NumberAction getPointsAction() {
    return pointsAction;
  }

  BoundedRegion getRegion() {
    return region;
  }

  boolean canEnter(Player player) {
    return !cache
        .get(
            player,
            (p) -> {
              FilterContext context = new FilterContext();
              context.add(new PlayerVariable(player));
              return filter.test(context);
            })
        .fails();
  }

  public boolean isHeal() {
    return heal;
  }
}

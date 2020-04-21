package network.walrus.games.core.facets.filters.types;

import java.util.Optional;
import network.walrus.games.core.facets.filters.Filter;
import network.walrus.games.core.facets.filters.FilterResult;
import network.walrus.games.core.facets.filters.context.FilterContext;
import network.walrus.games.core.facets.filters.variable.PlayerVariable;
import org.bukkit.entity.Entity;

/**
 * A onGround filter checks the player's onGround status.
 *
 * @author Avicus Network
 */
public class OnGroundFilter implements Filter {

  private final boolean onGround;

  /**
   * Constructor.
   *
   * @param onGround state to check against
   */
  public OnGroundFilter(boolean onGround) {
    this.onGround = onGround;
  }

  @Override
  public FilterResult test(FilterContext context, boolean describe) {
    Optional<PlayerVariable> optional = context.getFirst(PlayerVariable.class);

    if (!optional.isPresent()) {
      return FilterResult.IGNORE;
    }

    Entity player = optional.get().getPlayer();
    return FilterResult.valueOf(this.onGround == player.isOnGround());
  }

  @Override
  public String describe() {
    return "on ground";
  }
}

package network.walrus.games.core.facets.filters.types;

import java.util.Optional;
import network.walrus.games.core.facets.filters.Filter;
import network.walrus.games.core.facets.filters.FilterResult;
import network.walrus.games.core.facets.filters.context.FilterContext;
import network.walrus.games.core.facets.filters.variable.PlayerVariable;
import org.bukkit.entity.Player;

/**
 * A flying filter checks the player's flying status.
 *
 * @author Avicus Network
 */
public class FlyingFilter implements Filter {

  private final boolean flying;

  /**
   * Constructor.
   *
   * @param flying state to check against
   */
  public FlyingFilter(boolean flying) {
    this.flying = flying;
  }

  @Override
  public FilterResult test(FilterContext context, boolean describe) {
    Optional<PlayerVariable> optional = context.getFirst(PlayerVariable.class);

    if (!optional.isPresent()) {
      return FilterResult.IGNORE;
    }

    Player player = optional.get().getPlayer();
    return FilterResult.valueOf(this.flying == player.isFlying());
  }

  @Override
  public String describe() {
    return "flying";
  }
}

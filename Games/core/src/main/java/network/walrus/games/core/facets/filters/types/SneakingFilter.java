package network.walrus.games.core.facets.filters.types;

import java.util.Optional;
import network.walrus.games.core.facets.filters.Filter;
import network.walrus.games.core.facets.filters.FilterResult;
import network.walrus.games.core.facets.filters.context.FilterContext;
import network.walrus.games.core.facets.filters.variable.PlayerVariable;
import org.bukkit.entity.Player;

/**
 * A sneaking filter checks the player's sneaking status.
 *
 * @author Avicus Network
 */
public class SneakingFilter implements Filter {

  private final boolean sneaking;

  /**
   * Constructor.
   *
   * @param sneaking state to check for
   */
  public SneakingFilter(boolean sneaking) {
    this.sneaking = sneaking;
  }

  @Override
  public FilterResult test(FilterContext context, boolean describe) {
    Optional<PlayerVariable> optional = context.getFirst(PlayerVariable.class);

    if (!optional.isPresent()) {
      return FilterResult.IGNORE;
    }

    Player player = optional.get().getPlayer();
    return FilterResult.valueOf(this.sneaking == player.isSneaking());
  }

  @Override
  public String describe() {
    return "sneaking";
  }
}

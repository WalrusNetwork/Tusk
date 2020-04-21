package network.walrus.games.core.facets.filters.types;

import java.util.Optional;
import network.walrus.games.core.facets.filters.Filter;
import network.walrus.games.core.facets.filters.FilterResult;
import network.walrus.games.core.facets.filters.context.FilterContext;
import network.walrus.games.core.facets.filters.variable.PlayerVariable;
import org.bukkit.entity.Player;

/**
 * A sprinting filter checks the player's sprinting status.
 *
 * @author Avicus Network
 */
public class SprintingFilter implements Filter {

  private final boolean sprinting;

  /**
   * Constructor.
   *
   * @param sprinting state to check for
   */
  public SprintingFilter(boolean sprinting) {
    this.sprinting = sprinting;
  }

  @Override
  public FilterResult test(FilterContext context, boolean describe) {
    Optional<PlayerVariable> optional = context.getFirst(PlayerVariable.class);

    if (!optional.isPresent()) {
      return FilterResult.IGNORE;
    }

    Player player = optional.get().getPlayer();
    return FilterResult.valueOf(this.sprinting == player.isSprinting());
  }

  @Override
  public String describe() {
    return "sprinting";
  }
}

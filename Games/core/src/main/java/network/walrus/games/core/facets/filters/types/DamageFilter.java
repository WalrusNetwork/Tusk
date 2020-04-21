package network.walrus.games.core.facets.filters.types;

import java.util.Optional;
import network.walrus.games.core.facets.filters.Filter;
import network.walrus.games.core.facets.filters.FilterResult;
import network.walrus.games.core.facets.filters.context.FilterContext;
import network.walrus.games.core.facets.filters.variable.DamageVariable;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

/**
 * A damage filter checks the type of damage a player is receiving.
 *
 * @author Avicus Network
 */
public class DamageFilter implements Filter {

  private final DamageCause cause;

  /**
   * Constructor.
   *
   * @param cause of the damage which should be compared against
   */
  public DamageFilter(DamageCause cause) {
    this.cause = cause;
  }

  @Override
  public FilterResult test(FilterContext context, boolean describe) {
    Optional<DamageVariable> damage = context.getFirst(DamageVariable.class);
    if (damage.isPresent()) {
      return FilterResult.valueOf(damage.get().getCause() == this.cause);
    }
    return FilterResult.IGNORE;
  }

  @Override
  public String describe() {
    return "damage cause is " + cause.name();
  }
}

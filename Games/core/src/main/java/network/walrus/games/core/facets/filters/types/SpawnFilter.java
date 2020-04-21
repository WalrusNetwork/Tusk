package network.walrus.games.core.facets.filters.types;

import java.util.Optional;
import network.walrus.games.core.facets.filters.Filter;
import network.walrus.games.core.facets.filters.FilterResult;
import network.walrus.games.core.facets.filters.context.FilterContext;
import network.walrus.games.core.facets.filters.variable.SpawnReasonVariable;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

/**
 * A spawn filter checks the reason an entity spawned.
 *
 * @author Avicus Network
 */
public class SpawnFilter implements Filter {

  private final SpawnReason reason;

  /**
   * Constructor.
   *
   * @param reason which should compared with {@link CreatureSpawnEvent#getSpawnReason()}.
   */
  public SpawnFilter(SpawnReason reason) {
    this.reason = reason;
  }

  @Override
  public FilterResult test(FilterContext context, boolean describe) {
    Optional<SpawnReasonVariable> var = context.getFirst(SpawnReasonVariable.class);
    if (var.isPresent()) {
      return FilterResult.valueOf(this.reason == var.get().getReason());
    }
    return FilterResult.IGNORE;
  }

  @Override
  public String describe() {
    return "spawn reason is " + reason.name();
  }
}

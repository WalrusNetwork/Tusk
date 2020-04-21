package network.walrus.games.core.facets.filters.types;

import java.util.Optional;
import network.walrus.games.core.facets.filters.Filter;
import network.walrus.games.core.facets.filters.FilterResult;
import network.walrus.games.core.facets.filters.context.FilterContext;
import network.walrus.games.core.facets.filters.variable.LocationVariable;
import network.walrus.utils.bukkit.region.Region;
import network.walrus.utils.core.registry.WeakReference;

/**
 * An inside filter checks if a player is in a certain region.
 *
 * @author Avicus Network
 */
public class InsideFilter implements Filter {

  private final Optional<WeakReference<Region>> regionReference;

  /**
   * Constructor.
   *
   * @param regionReference to check against
   */
  public InsideFilter(Optional<WeakReference<Region>> regionReference) {
    this.regionReference = regionReference;
  }

  @Override
  public FilterResult test(FilterContext context, boolean describe) {
    Optional<Region> region = Optional.empty();

    if (this.regionReference.isPresent()) {
      region = this.regionReference.get().getObject();
    }

    if (!region.isPresent()) {
      return FilterResult.IGNORE;
    }

    Optional<LocationVariable> var = context.getFirst(LocationVariable.class);

    if (var.isPresent()) {
      return FilterResult.valueOf(region.get().contains(var.get().getLocation()));
    }

    return FilterResult.IGNORE;
  }

  @Override
  public String describe() {
    return "inside " + regionReference.get().getObject().get().toString();
  }
}

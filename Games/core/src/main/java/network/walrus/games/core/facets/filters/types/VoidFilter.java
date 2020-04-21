package network.walrus.games.core.facets.filters.types;

import java.util.Optional;
import network.walrus.games.core.facets.block36.Block36Facet;
import network.walrus.games.core.facets.filters.Filter;
import network.walrus.games.core.facets.filters.FilterResult;
import network.walrus.games.core.facets.filters.context.FilterContext;
import network.walrus.games.core.facets.filters.variable.LocationVariable;
import network.walrus.utils.bukkit.inventory.MultiMaterialMatcher;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

/**
 * A void filter checks if the location supplied is above void.
 *
 * @author Avicus Network
 */
public class VoidFilter implements Filter {

  private final int min;
  private final int max;
  private final Optional<MultiMaterialMatcher> ignoredBlocks;

  /**
   * @param min height to check for air at
   * @param max height to check for air at
   * @param ignoredBlocks blocks which should be counted as air
   */
  public VoidFilter(int min, int max, Optional<MultiMaterialMatcher> ignoredBlocks) {
    this.min = min;
    this.max = max;
    this.ignoredBlocks = ignoredBlocks;
  }

  @Override
  public FilterResult test(FilterContext context, boolean describe) {
    Optional<LocationVariable> optional = context.getFirst(LocationVariable.class);

    if (!optional.isPresent()) {
      return FilterResult.IGNORE;
    }

    Vector vector = optional.get().getLocation().toVector();
    World world = optional.get().getLocation().getWorld();
    // Filter is at bottom count as void.
    if (vector.getY() == this.min) {
      return FilterResult.ALLOW;
    }

    for (int i = this.min; i <= this.max; i++) {
      Block block = world.getBlockAt(vector.getBlockX(), i, vector.getBlockZ());
      // Ignore if same block
      if (block.getLocation().toVector().equals(vector)) {
        continue;
      }

      if (this.ignoredBlocks.map(m -> m.matches(block.getState())).orElse(false)) {
        continue;
      }

      if (block.getType() != Material.AIR
          || Block36Facet.getInstance().is36Here(vector.getBlockX(), i, vector.getBlockZ())) {
        return FilterResult.DENY;
      }
    }
    return FilterResult.ALLOW;
  }

  @Override
  public String describe() {
    return "blocks above " + min + " and blocks under " + max + " are air";
  }
}

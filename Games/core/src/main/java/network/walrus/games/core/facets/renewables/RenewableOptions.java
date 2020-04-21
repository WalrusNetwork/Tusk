package network.walrus.games.core.facets.renewables;

import network.walrus.games.core.facets.filters.Filter;
import network.walrus.utils.bukkit.region.BoundedRegion;

/**
 * User-defined options for {@link Renewable}s.
 *
 * @author Austin Mayes
 */
class RenewableOptions {

  final BoundedRegion region;
  final Filter renewableBlocks;
  final Filter replaceableBlocks;
  final float renewalsPerSecond; // Blocks per second
  final boolean rateScaled; // Renewal rate is per-volume
  final boolean growAdjacent;
  final boolean natural;
  final double avoidPlayersRange;

  /**
   * @param region that the renewable should act upon
   * @param renewableBlocks to determine which blocks are eligible for renewal
   * @param replaceableBlocks to determine which blocks can be replaced by renewing blocks
   * @param renewalsPerSecond number of renew operations to run per second
   * @param rateScaled if the renew rate is based on volume
   * @param growAdjacent if renewing blocks should only put back in locations with neighboring
   *     originals
   * @param natural if block place effects should be played
   * @param avoidPlayersRange minimum distance away from players renewal operations can execute
   */
  public RenewableOptions(
      BoundedRegion region,
      Filter renewableBlocks,
      Filter replaceableBlocks,
      float renewalsPerSecond,
      boolean rateScaled,
      boolean growAdjacent,
      boolean natural,
      double avoidPlayersRange) {
    this.region = region;
    this.renewableBlocks = renewableBlocks;
    this.replaceableBlocks = replaceableBlocks;
    this.renewalsPerSecond = renewalsPerSecond;
    this.rateScaled = rateScaled;
    this.growAdjacent = growAdjacent;
    this.natural = natural;
    this.avoidPlayersRange = avoidPlayersRange;
  }
}

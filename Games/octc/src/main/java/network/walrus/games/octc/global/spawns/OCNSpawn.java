package network.walrus.games.octc.global.spawns;

import co.aikar.timings.Timing;
import co.aikar.timings.Timings;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import network.walrus.games.core.GamesPlugin;
import network.walrus.games.core.api.spawns.Spawn;
import network.walrus.games.core.api.spawns.SpawnLocationUnavailableException;
import network.walrus.games.core.api.spawns.SpawnRegion;
import network.walrus.games.core.facets.filters.Filter;
import network.walrus.games.core.facets.group.Competitor;
import network.walrus.games.core.facets.group.Group;
import network.walrus.games.core.facets.kits.Kit;
import network.walrus.utils.bukkit.points.AngleProvider;
import network.walrus.utils.bukkit.region.PositionUnavailableException;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 * Competitive implementation of the {@link Spawn}s API.
 *
 * <p>Adds the following features: - Spread from enemies/anyone - Check if 10 block above air -
 * Filter if this spawn object should even be used
 *
 * @author Austin Mayes
 */
public class OCNSpawn implements Spawn {

  private static final Random random = new Random();
  private final SpawnOptions options;
  private final FacetHolder holder;
  private final Timing airTimer = Timings.of(GamesPlugin.instance, "Spawn safety: air");
  private final Timing safeTimer = Timings.of(GamesPlugin.instance, "Spawn safety: safe");

  /**
   * Constructor.
   *
   * @param options to be used when spawning players at locations
   * @param holder which holds this spawn
   */
  OCNSpawn(SpawnOptions options, FacetHolder holder) {
    this.options = options;
    this.holder = holder;
  }

  private Pair<SpawnRegion, Vector> selectPosition(
      Player player, List<SpawnRegion> regions, SelectionMode mode)
      throws SpawnLocationUnavailableException {
    Competitor competitor = null;

    // If no regions left, throw exeption
    if (regions.isEmpty()) {
      throw new SpawnLocationUnavailableException("No regions");
    }

    SpawnRegion selected;

    // TODO: Get set of comps and players
    if (mode == SelectionMode.SEQUENTIAL) { // sequential - first region
      selected = regions.get(0);
    } else if (mode == SelectionMode.RANDOM) { // random - random region
      selected = regions.get(random.nextInt(regions.size()));
    } else if (mode == SelectionMode.SAFE && competitor != null) { // safe - use safety algorithm
      selected = selectSafe(player, Sets.newHashSet());
    } else { // spread - use spread algorithm
      selected = selectSpread(player, Sets.newHashSet());
    }

    // No region selected from above
    if (selected == null) {
      if (mode != SelectionMode.RANDOM) {
        // Try again with random selection
        return selectPosition(player, regions, SelectionMode.RANDOM);
      }
      // Already tried random, throw error
      throw new SpawnLocationUnavailableException("out of regions");
    }

    // Count total possible locations in selected region
    int regionSize = 0;
    for (Vector v : selected.getRegion()) {
      regionSize++;
    }

    // Honor spawn safety options
    Vector position = safetyCheck(selected, 0, regionSize);
    // Selected region didn't return any safe positions
    if (position == null) {
      // Remove unsafe region - top of method handles no remaining regions
      regions.remove(selected);
      // Try to get a new selected region
      return selectPosition(
          player, regions, options.sequential ? SelectionMode.SEQUENTIAL : SelectionMode.RANDOM);
    }

    // Safety check passed - return position w/ selected region parent
    return Pair.of(selected, position);
  }

  private Vector safetyCheck(SpawnRegion selected, int attempts, int regionSize) {
    // Tried every block in region, nowhere is safe - abort to parent method
    if (attempts > regionSize) {
      return null;
    }

    Vector position;
    // Get a random position
    try {
      position = selected.randomPosition(random);
    } catch (PositionUnavailableException e) {
      // This region can't even be used for spawns
      e.printStackTrace();
      return null;
    }

    World world = holder.getContainer().mainWorld();
    // Make sure player is 10 blocks above solid ground
    if (options.checkAir) {
      boolean air = true;
      try (Timing timing = airTimer.startClosable()) {
        Location loc = position.toLocation(world);
        // Check down 10 blocks
        for (int y = loc.getBlockY(); y >= loc.getBlockY() - 10; y--) {
          if (world.getBlockAt(new Location(world, loc.getX(), y, loc.getZ())).getType()
              != Material.AIR) {
            // Somewhere 10 blocks under is solid
            air = false;
            break;
          }
        }
      }

      // Only air, try again
      if (air) {
        return safetyCheck(selected, ++attempts, regionSize);
      }
    }

    // Make sure the player won't get stuck if they spawn here
    if (options.safe) {
      try (Timing timing = safeTimer.startClosable()) {
        // +1 for ground
        Location loc = position.toLocation(world).add(0, 1, 0);
        Block block = loc.getBlock();
        Block above = block.getRelative(BlockFace.UP);
        Block below = block.getRelative(BlockFace.DOWN);

        // Block at feet height is not clear, and,
        // block at head height is not clear, and,
        // Block under feet is not solid
        if (!(block.isEmpty() && above.isEmpty() && below.getType().isSolid())) {
          return safetyCheck(selected, ++attempts, regionSize);
        }
      }
    }

    // All safety checks passed
    return position;
  }

  /**
   * Select a region that contains the most distance of any players from the center.
   *
   * @param player to compare against
   * @param players to get distance for
   * @return region that contains the most distance of any players from the center
   */
  private SpawnRegion selectSpread(Player player, Set<Player> players) {
    Map<SpawnRegion, Double> minimumDistances = new HashMap<>();

    for (Player test : players) {
      if (test.equals(player)) {
        continue;
      }
      calculateMinimumDistance(test, minimumDistances);
    }

    return selectMostIsolated(minimumDistances);
  }

  /**
   * Select a region that contains the most distance of enemy players from the center.
   *
   * @param player to to get enemies of
   * @param competitors enemies of the player
   * @return region that contains the most distance of enemy players from the center
   */
  private SpawnRegion selectSafe(Player player, Set<Competitor> competitors) {
    Map<SpawnRegion, Double> minimumDistances = new HashMap<>();

    for (Competitor otherCompetitor : competitors) {
      // If player's group and group does not have friendly fire, skip
      if (otherCompetitor.hasPlayer(player)
          && !otherCompetitor.getGroup().isFriendlyFireEnabled()) {
        continue;
      }
      for (Player test : otherCompetitor.getPlayers()) {
        if (test.equals(player)) {
          continue;
        }
        calculateMinimumDistance(test, minimumDistances);
      }
    }

    return selectMostIsolated(minimumDistances);
  }

  /**
   * Select the spawn region with the highest distance from players.
   *
   * @param minimumDistances mapping of spawn region -> closest player distance
   * @return spawn region with the highest distance from players
   */
  private SpawnRegion selectMostIsolated(Map<SpawnRegion, Double> minimumDistances) {
    SpawnRegion selected = null;
    double mostIsolatedDistance = Double.MIN_VALUE;

    for (Entry<SpawnRegion, Double> entry : minimumDistances.entrySet()) {
      if (entry.getValue() > mostIsolatedDistance) {
        selected = entry.getKey();
        mostIsolatedDistance = entry.getValue();
      }
    }

    return selected;
  }

  /**
   * Calculate the distance a player is to the center of each spawn region, and add the data to a
   * map.
   *
   * @param test to use as a base
   * @param minimumDistances mapping of spawn region -> closest player distance
   */
  private void calculateMinimumDistance(Player test, Map<SpawnRegion, Double> minimumDistances) {
    for (SpawnRegion region : options.regions) {
      double distance = region.getCenter().distanceSquared(test.getLocation().toVector());
      double minimumDistance = minimumDistances.getOrDefault(region, Double.MAX_VALUE);
      if (distance < minimumDistance) {
        minimumDistances.put(region, distance);
      }
    }
  }

  @Override
  public Location selectLocation(FacetHolder round, Player player)
      throws SpawnLocationUnavailableException {
    Pair<SpawnRegion, Vector> position =
        selectPosition(player, new ArrayList<>(options.regions), options.selectionMode);
    AngleProvider yaw = position.getKey().getYaw().orElse(options.yaw);
    AngleProvider pitch = position.getKey().getPitch().orElse(options.pitch);

    return new Location(
        holder.getContainer().mainWorld(),
        position.getValue().getX(),
        position.getValue().getY(),
        position.getValue().getZ(),
        yaw.getAngle(position.getValue()),
        pitch.getAngle(position.getValue()));
  }

  public Group getGroup() {
    return options.group;
  }

  public Optional<Filter> getCheck() {
    return options.filter;
  }

  @Override
  public Optional<Kit> kit() {
    return this.options.kit;
  }
}

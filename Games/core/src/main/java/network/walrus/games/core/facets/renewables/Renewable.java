package network.walrus.games.core.facets.renewables;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Random;
import network.walrus.games.core.GamesPlugin;
import network.walrus.games.core.facets.filters.FilterResult;
import network.walrus.games.core.facets.filters.context.FilterContext;
import network.walrus.games.core.facets.filters.variable.MaterialVariable;
import network.walrus.games.core.round.GameRound;
import network.walrus.ubiquitous.bukkit.events.world.BlockChangeEvent;
import network.walrus.utils.bukkit.block.BlockFaceUtils;
import network.walrus.utils.bukkit.block.BlockUtils;
import network.walrus.utils.bukkit.block.BlockVectorSet;
import network.walrus.utils.bukkit.region.BoundedRegion;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

/**
 * Object which resets blocks inside of a {@link BoundedRegion} back to the state that they were in
 * when {@link #saveOriginal(BlockChangeEvent)} was called based on user-defined filters and the
 * position of nearby players.
 *
 * @author Overcast Network
 */
public class Renewable {

  private static final int MAX_FAILED_ITERATIONS = 100;
  final RenewableOptions options;
  private final Random RANDOM = new Random();
  // Set of blocks that are immediately renewable, dynamically updated from block events.
  // Maintaining this set avoids nearly all trial and error logic in the renewal tick.
  private final BlockVectorSet renewablePool = new BlockVectorSet();
  private final GameRound round;
  private final Map<Long, FilterResult> renewableCache = Maps.newHashMap();
  private final Map<Long, BlockState> originals = Maps.newHashMap();
  // Number of blocks that currently must to be renewed to keep up with the configured rate.
  private long lastTick;

  /**
   * @param round that the renewable is running in
   * @param options defining how this renewable should operate
   */
  Renewable(GameRound round, RenewableOptions options) {
    this.round = round;
    this.options = options;

    updateLastTick();
  }

  /** Save the original state of a block so it can be renewed later. */
  void saveOriginal(BlockChangeEvent event) {
    Long vec = BlockUtils.encodePos(event.getBlock().getLocation().toVector().toBlockVector());
    originals.putIfAbsent(vec, event.getOldState());
  }

  void execute() {
    renewableCache.clear();

    float interval = updateLastTick(); // should always be 1
    float count = interval * options.renewalsPerSecond / 20f; // calculate renewals per tick
    if (options.rateScaled) {
      count *= renewablePool.size();
    }

    for (; count > 0 && !renewablePool.isEmpty(); count--) {
      if (RANDOM.nextFloat() < count) {
        for (int i = 0; i < MAX_FAILED_ITERATIONS; i++) {
          if (renew(renewablePool.chooseRandom(RANDOM))) {
            break;
          }
        }
      }
    }
  }

  /**
   * Determine if the block that was originally at the specified location is eligible to be reset
   * back to the state it was in at load time.
   *
   * @param pos to check
   * @return if the block that originally at this location should be put back
   */
  private boolean isOriginalRenewable(BlockVector pos) {
    if (!options.region.contains(pos)) {
      return false;
    }
    if (!originals.containsKey(BlockUtils.encodePos(pos))) {
      return false;
    }
    FilterResult response = renewableCache.get(BlockUtils.encodePos(pos));
    if (response == null) {
      FilterContext context = new FilterContext();
      context.add(new MaterialVariable(originals.get(BlockUtils.encodePos(pos)).getMaterialData()));
      response = options.renewableBlocks.test(context);
      renewableCache.put(BlockUtils.encodePos(pos), response);
    }
    return response.passes();
  }

  private long updateLastTick() {
    long delta = GamesPlugin.instance.timer().getTicks() - lastTick;
    lastTick = GamesPlugin.instance.timer().getTicks();
    return delta;
  }

  void updateRenewablePool(BlockState block) {
    if (canRenew(block)) {
      renewablePool.add(BlockUtils.position(block));
    } else {
      renewablePool.remove(BlockUtils.position(block));
    }
  }

  /**
   * Determine if the block provided by the supplied state is exactly the same as the block that was
   * originally at the state's location.
   *
   * @param currentState to check
   * @return if the block has changed from the original state
   */
  private boolean isNew(BlockState currentState) {
    // If original block does not match renewable rule, block is new
    BlockVector pos = BlockUtils.position(currentState);
    if (!isOriginalRenewable(pos)) {
      return true;
    }

    MaterialData currentMaterial = currentState.getMaterialData();
    // If current material matches original, block is new
    return currentMaterial.equals(originals.get(BlockUtils.encodePos(pos)).getMaterialData());
  }

  /**
   * Run {@link #isNew(BlockState)} on all neighbors of a block.
   *
   * @param block to check neighbors for
   * @return if the block has a new neighbor
   */
  private boolean hasNewNeighbor(BlockState block) {
    for (BlockFace face : BlockFaceUtils.NEIGHBORS) {
      if (isNew(BlockFaceUtils.getRelative(block, face))) {
        return true;
      }
    }
    return false;
  }

  /**
   * Determine if the block should and can be replaced with the original block that was at it's
   * location.
   *
   * @param currentState of the block
   * @return if the block can be replaced
   */
  private boolean canRenew(BlockState currentState) {
    // Must not already be new
    if (isNew(currentState)) {
      return false;
    }

    // Must grow from an adjacent block that is renewed
    if (options.growAdjacent && !hasNewNeighbor(currentState)) {
      return false;
    }

    // Current block must be replaceable
    FilterContext context = new FilterContext();
    context.add(new MaterialVariable(currentState.getMaterialData()));
    return options.replaceableBlocks.test(context).passes();
  }

  /**
   * Determine if the specified vector is far enough from players to place blocks at.
   *
   * @param pos to check
   * @return if the location is safe
   */
  private boolean isSafeDistance(Vector pos) {
    if (options.avoidPlayersRange > 0d) {
      double rangeSquared = options.avoidPlayersRange * options.avoidPlayersRange;
      for (Player player : round.players()) {
        Location location = player.getLocation().add(0, 1, 0);
        if (location.toVector().distanceSquared(pos) < rangeSquared) {
          return false;
        }
      }
    }
    return true;
  }

  /**
   * Place the original block that was at the specified location back, if the player distance check
   * succeeds.
   *
   * @param pos to place at
   * @return if the block was placed
   */
  private boolean renew(BlockVector pos) {
    MaterialData material = originals.get(BlockUtils.encodePos(pos)).getMaterialData();

    if (material != null) {
      if (!isSafeDistance(pos)) {
        return false;
      }

      Location location = pos.toLocation(round.getContainer().mainWorld());
      location.getWorld().fastBlockChange(location.toVector(), material);

      if (options.natural) {
        location
            .getWorld()
            .spigot()
            .playEffect(
                location,
                Effect.STEP_SOUND,
                material.getItemTypeId() + (material.getData() << 12),
                0,
                0,
                0,
                0,
                1,
                1,
                45);
      }
      renewablePool.remove(pos);

      return true;
    }

    return false;
  }
}

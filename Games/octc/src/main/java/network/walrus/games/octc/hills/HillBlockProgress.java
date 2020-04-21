package network.walrus.games.octc.hills;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Optional;
import network.walrus.games.core.facets.group.Competitor;
import network.walrus.games.core.round.GameRound;
import network.walrus.utils.bukkit.block.BlockUtils;
import network.walrus.utils.bukkit.region.BoundedRegion;
import network.walrus.utils.bukkit.region.special.SectorRegion;
import org.bukkit.DyeColor;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

/**
 * Manages changing the blocks on the hill
 *
 * @author Matthew Arnold
 */
public class HillBlockProgress {

  // private static final DyeColor NEUTRAL = DyeColor.GRAY;

  private final GameRound gameRound;
  private final HillProperties properties;
  private final HillObjective objective;

  private final Map<Vector, DyeColor>
      neutralStates; // maps the neutral states of the hill, byte is the block data

  /**
   * Creates a new hill block change listener
   *
   * @param gameRound the game round the listener is operating in
   */
  public HillBlockProgress(HillObjective objective, GameRound gameRound) {
    this.gameRound = gameRound;
    this.properties = objective.options();
    this.objective = objective;
    this.neutralStates = Maps.newHashMap();
  }

  /**
   * Resets a hill to be owned by a specific person
   *
   * @param competitor the competitor who should be in control of the region
   */
  public void reset(Optional<Competitor> competitor) {
    Optional<DyeColor> color = color(competitor);
    for (Vector vector : properties.progressRegion) {
      setBlock(vector, color);
    }

    if (properties.controlRegion.isPresent()) {
      for (Vector vector : properties.controlRegion.get()) {
        setBlock(vector, color);
      }
    }
  }

  private void setBlock(Vector vector, Optional<DyeColor> dyeColor) {
    World world = gameRound.getContainer().mainWorld();
    Block block = BlockUtils.blockAt(world, vector);
    MaterialData data = block.getState().getMaterialData();

    if (properties.blockFilter.matches(data.getItemType(), data.getData())) {
      addBlock(vector, block.getData()); // adds block to the map of neutral states
      block.setData(dyeColor.orElseGet(() -> neutralStates.get(vector)).getWoolData());
    }
  }

  private Optional<DyeColor> color(Optional<Competitor> competitor) {
    return competitor.map(x -> x.getColor().getDyeColor());
  }

  public void updateHillProgress(int oldPercentage) {
    BoundedRegion progressRegion = properties.progressRegion;

    double oldAngle = ((double) oldPercentage / 100) * 360;
    SectorRegion negativeRegion =
        new SectorRegion(
            progressRegion.getCenter().getX(), progressRegion.getCenter().getZ(), 0, oldAngle);

    double endAngle = ((double) objective.completionPercentage() / 100) * 360;
    SectorRegion sectorRegion =
        new SectorRegion(
            progressRegion.getCenter().getX(), progressRegion.getCenter().getZ(), 0, endAngle);

    if (oldAngle > endAngle) {
      SectorRegion swapRegion = negativeRegion;
      negativeRegion = sectorRegion;
      sectorRegion = swapRegion;
    }

    Optional<DyeColor> color = color(objective.dominator());
    if (HillUtils.isRevertingNeutral(objective)) {
      color = color(Optional.empty());
    } else if (!objective.dominator().isPresent()) {
      color = color(objective.owner());
    }

    for (Vector vector : progressRegion) {
      if (!sectorRegion.contains(vector) || negativeRegion.contains(vector)) {
        continue;
      }
      setBlock(vector, color);
    }
  }

  /**
   * The reason this works is because all hills are built (in the map) in a neutral state. This
   * means that the first time the blocks are being changed they're being changed from neutral to a
   * non neutral state.
   *
   * <p>Therefore, we check to see if the cache of neutral blocks already contains a block for the
   * position. If it does we don't update it. If no block is in the stash we can safely store the
   * current block state because it's definitely a block for the neutral state
   *
   * @param vector the vector of the block
   * @param data the current data of the block
   */
  private void addBlock(Vector vector, byte data) {
    if (!neutralStates.containsKey(vector)) {
      // only puts it in the neutral state
      neutralStates.put(vector, DyeColor.getByWoolData(data));
    }
  }
}

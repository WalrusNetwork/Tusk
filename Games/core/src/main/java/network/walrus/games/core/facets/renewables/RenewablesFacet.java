package network.walrus.games.core.facets.renewables;

import java.util.List;
import network.walrus.games.core.util.GameTask;
import network.walrus.ubiquitous.bukkit.events.world.BlockChangeByPlayerEvent;
import network.walrus.ubiquitous.bukkit.events.world.BlockChangeEvent;
import network.walrus.utils.bukkit.block.BlockFaceUtils;
import network.walrus.utils.parsing.facet.Facet;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 * Object responsible for loading and sending updates to {@link Renewable}s.
 *
 * @author Austin Mayes
 */
public class RenewablesFacet extends Facet implements Listener {

  private final FacetHolder holder;
  private final List<Renewable> renewables;
  private final GameTask runner;

  /**
   * @param holder which the reneables are operating inside of
   * @param renewables which should be active during the round
   */
  RenewablesFacet(FacetHolder holder, List<Renewable> renewables) {
    this.holder = holder;
    this.renewables = renewables;
    this.runner =
        GameTask.of(
            "renewer",
            () -> {
              for (Renewable renewable : renewables) {
                renewable.execute();
              }
            });
  }

  /** @see #onBlockChange(BlockChangeEvent) */
  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onPlayerBlockChange(BlockChangeByPlayerEvent event) {
    onBlockChange(event);
  }

  /** @see #saveOriginals(BlockChangeEvent) */
  @EventHandler(priority = EventPriority.LOWEST)
  public void saveOriginals(BlockChangeByPlayerEvent event) {
    saveOriginals((BlockChangeEvent) event);
  }

  /** Save original block states so renewables can use them later. */
  @EventHandler(priority = EventPriority.LOWEST)
  public void saveOriginals(BlockChangeEvent event) {
    for (Renewable renewable : renewables) {
      if (renewable.options.region.contains(event.getBlock())) {
        renewable.saveOriginal(event);
      }
    }
  }

  /** Inform all renewables of relevant block changes. */
  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onBlockChange(BlockChangeEvent event) {
    for (Renewable renewable : renewables) {
      if (!renewable.options.region.contains(event.getBlock())) {
        continue;
      }

      renewable.updateRenewablePool(event.getNewState());

      if (renewable.options.growAdjacent) {
        for (BlockFace face : BlockFaceUtils.NEIGHBORS) {
          BlockState state = BlockFaceUtils.getRelative(event.getNewState(), face);
          if (renewable.options.region.contains(state.getBlock())) {
            renewable.updateRenewablePool(state);
          }
        }
      }
    }
  }

  @Override
  public void enable() {
    runner.repeat(1, 1);
  }

  @Override
  public void disable() {
    runner.reset();
  }
}

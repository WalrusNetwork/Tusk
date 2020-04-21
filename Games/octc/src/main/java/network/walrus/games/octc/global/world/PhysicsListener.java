package network.walrus.games.octc.global.world;

import network.walrus.games.core.round.GameRound;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import network.walrus.utils.parsing.facet.parse.configurator.listener.FacetListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockDispenseEntityEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.FurnaceBurnEvent;

/**
 * Listener which stops physics events when the round is not in progress.
 *
 * @author Austin Mayes
 */
@SuppressWarnings("JavaDoc")
public class PhysicsListener extends FacetListener<WorldFacet> {

  private final GameRound round;
  private final WorldFacet facet;

  /**
   * @param round which this object is inside of
   * @param facet which this object is bound to
   */
  public PhysicsListener(FacetHolder round, WorldFacet facet) {
    super(round, facet);
    this.round = (GameRound) round;
    this.facet = facet;
  }

  private boolean shouldDenyPhysics() {
    return !round.getState().playing();
  }

  @EventHandler
  public void onLiquidFlow(BlockFromToEvent event) {
    event.setCancelled(shouldDenyPhysics());
  }

  @EventHandler
  public void onBlockBurn(BlockBurnEvent event) {
    event.setCancelled(shouldDenyPhysics());
  }

  @EventHandler
  public void onBlockDispense(BlockDispenseEvent event) {
    event.setCancelled(shouldDenyPhysics());
  }

  @EventHandler
  public void onBlockDispense(BlockDispenseEntityEvent event) {
    event.setCancelled(shouldDenyPhysics());
  }

  @EventHandler
  public void onBlockFade(BlockFadeEvent event) {
    event.setCancelled(shouldDenyPhysics());
  }

  @EventHandler
  public void onBlockForm(BlockFormEvent event) {
    event.setCancelled(shouldDenyPhysics());
  }

  @EventHandler
  public void onBlockGrow(BlockGrowEvent event) {
    event.setCancelled(shouldDenyPhysics());
  }

  @EventHandler
  public void onBlockIgnite(BlockIgniteEvent event) {
    event.setCancelled(shouldDenyPhysics());
  }

  @EventHandler
  public void onBlockRedstone(BlockRedstoneEvent event) {
    if (shouldDenyPhysics()) {
      event.setNewCurrent(event.getOldCurrent());
    }
  }

  @EventHandler
  public void onBlockPhysics(BlockPhysicsEvent event) {
    event.setCancelled(shouldDenyPhysics());
  }

  @EventHandler
  public void onFurnaceBurn(FurnaceBurnEvent event) {
    if (shouldDenyPhysics()) {
      event.setBurning(true);
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onEntityExplode(EntityExplodeEvent event) {
    event.setCancelled(shouldDenyPhysics());
  }
}

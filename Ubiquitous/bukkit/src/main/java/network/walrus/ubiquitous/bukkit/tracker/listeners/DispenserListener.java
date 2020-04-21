package network.walrus.ubiquitous.bukkit.tracker.listeners;

import network.walrus.ubiquitous.bukkit.tracker.event.tag.NPCBecomePlayerEvent;
import network.walrus.ubiquitous.bukkit.tracker.event.tag.PlayerBecomeNPCEvent;
import network.walrus.ubiquitous.bukkit.tracker.trackers.DispenserTracker;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDispenseEntityEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

/** Listener which passes information on to a {@link DispenserTracker}. */
@SuppressWarnings("JavaDoc")
public class DispenserListener implements Listener {

  private final DispenserTracker tracker;

  /**
   * Constructor.
   *
   * @param tracker used to track dispenser information
   */
  public DispenserListener(DispenserTracker tracker) {
    this.tracker = tracker;
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onBlockPlace(BlockPlaceEvent event) {
    if (event.getBlock().getType() != Material.DISPENSER) {
      return;
    }

    if (this.tracker.isEnabled(event.getBlock().getWorld())) {
      this.tracker.setPlacer(event.getBlock(), event.getPlayer());
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onBlockBreak(BlockBreakEvent event) {
    if (!this.tracker.isEnabled(event.getBlock().getWorld())) {
      return;
    }

    this.tracker.clearPlacer(event.getBlock());
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onBlockExplode(EntityExplodeEvent event) {
    if (!this.tracker.isEnabled(event.getLocation().getWorld())) {
      return;
    }

    // Remove all blocks that are destroyed from explosion
    for (Block block : event.blockList()) {
      this.tracker.clearPlacer(block);
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onDispense(BlockDispenseEntityEvent event) {
    if (!this.tracker.isEnabled(event.getEntity().getWorld())) {
      return;
    }

    Block block = event.getBlock();
    OfflinePlayer placer = this.tracker.getPlacer(block);
    if (placer != null) {
      this.tracker.setOwner(event.getEntity(), placer.getPlayer());
    }
  }

  @EventHandler
  public void onChange(PlayerBecomeNPCEvent event) {
    tracker.transferOwnership(event.getPlayer(), event.getState());
  }

  @EventHandler
  public void onChange(NPCBecomePlayerEvent event) {
    tracker.transferOwnership(event.getState(), event.getPlayer());
  }
}

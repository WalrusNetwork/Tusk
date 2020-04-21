package network.walrus.ubiquitous.bukkit.tracker.listeners;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import network.walrus.ubiquitous.bukkit.tracker.event.tag.NPCBecomePlayerEvent;
import network.walrus.ubiquitous.bukkit.tracker.event.tag.PlayerBecomeNPCEvent;
import network.walrus.ubiquitous.bukkit.tracker.trackers.DispenserTracker;
import network.walrus.ubiquitous.bukkit.tracker.trackers.ExplosiveTracker;
import network.walrus.ubiquitous.bukkit.tracker.trackers.OwnedMobTracker;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDispenseEntityEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.ExplosionPrimeByEntityEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;

/** Listener which passes information to the {@link ExplosiveTracker}. */
@SuppressWarnings("JavaDoc")
public class ExplosiveListener implements Listener {

  private final ExplosiveTracker tracker;
  private final OwnedMobTracker ownedMobTracker;
  private final DispenserTracker dispenserTracker;

  /**
   * Constructor.
   *
   * @param tracker to send explosive information to
   * @param ownedMobTracker to track explosions from mobs
   * @param dispenserTracker to track explosions from entities shot from dispensers
   */
  public ExplosiveListener(
      ExplosiveTracker tracker,
      OwnedMobTracker ownedMobTracker,
      DispenserTracker dispenserTracker) {
    this.tracker = tracker;
    this.ownedMobTracker = ownedMobTracker;
    this.dispenserTracker = dispenserTracker;
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onBlockPlace(BlockPlaceEvent event) {
    if (!this.tracker.isEnabled(event.getBlock().getWorld())) {
      return;
    }

    if (event.getBlock().getType() == Material.TNT) {
      this.tracker.setPlacer(event.getBlock(), event.getPlayer());
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onBlockBreak(BlockBreakEvent event) {
    if (!this.tracker.isEnabled(event.getBlock().getWorld())) {
      return;
    }

    this.tracker.setPlacer(event.getBlock(), null);
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onBlockPistonExtend(BlockPistonExtendEvent event) {
    if (!this.tracker.isEnabled(event.getBlock().getWorld())) {
      return;
    }

    Map<Block, OfflinePlayer> updated = Maps.newHashMap();
    List<Block> toremove = Lists.newLinkedList();

    for (Block block : event.getBlocks()) {
      OfflinePlayer placer = this.tracker.getPlacer(block);
      if (placer != null) {
        toremove.add(block);
        updated.put(block.getRelative(event.getDirection()), placer);
      }
    }

    for (Block block : toremove) {
      OfflinePlayer newPlacer = updated.remove(block);
      this.tracker.setPlacer(block, newPlacer);
    }

    for (Map.Entry<Block, OfflinePlayer> entry : updated.entrySet()) {
      this.tracker.setPlacer(entry.getKey(), entry.getValue());
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onBlockPistonRetract(BlockPistonRetractEvent event) {
    if (!this.tracker.isEnabled(event.getBlock().getWorld())) {
      return;
    }

    if (event.isSticky()) {
      Block newBlock = event.getBlock().getRelative(event.getDirection());
      Block oldBlock = newBlock.getRelative(event.getDirection());
      OfflinePlayer player = this.tracker.getPlacer(oldBlock);
      if (player != null) {
        this.tracker.setPlacer(oldBlock, null);
        this.tracker.setPlacer(newBlock, player);
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onTNTIgnite(ExplosionPrimeEvent event) {
    if (!this.tracker.isEnabled(event.getEntity().getWorld())) {
      return;
    }

    if (event.getEntity() instanceof TNTPrimed) {
      TNTPrimed tnt = (TNTPrimed) event.getEntity();
      OfflinePlayer owner = null;
      if (event instanceof ExplosionPrimeByEntityEvent) {
        Entity primer = ((ExplosionPrimeByEntityEvent) event).getPrimer();
        if (primer instanceof TNTPrimed) {
          owner = this.tracker.getOwner((TNTPrimed) primer);
        } else {
          if (!primer.isDead()) {
            owner = ownedMobTracker.getOwner((LivingEntity) primer);
          }
        }
      }

      if (owner == null) {
        OfflinePlayer placer = this.tracker.getPlacer(tnt.getLocation().getBlock());
        if (placer != null) {
          owner = placer;
        }
      }

      if (owner != null) {
        this.tracker.setOwner(tnt, owner);
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onDispense(BlockDispenseEntityEvent event) {
    if (event.getEntity() instanceof TNTPrimed) {
      OfflinePlayer placer = dispenserTracker.getPlacer(event.getBlock());
      if (placer != null && placer.isOnline()) {
        this.tracker.setOwner((TNTPrimed) event.getEntity(), placer.getPlayer());
      }
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

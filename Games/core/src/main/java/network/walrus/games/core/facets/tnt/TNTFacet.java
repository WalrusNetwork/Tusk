package network.walrus.games.core.facets.tnt;

import java.time.Duration;
import java.util.Random;
import network.walrus.ubiquitous.bukkit.UbiquitousBukkitPlugin;
import network.walrus.ubiquitous.bukkit.events.world.BlockChangeByPlayerEvent;
import network.walrus.ubiquitous.bukkit.events.world.BlockChangeEvent;
import network.walrus.ubiquitous.bukkit.tracker.trackers.ExplosiveTracker;
import network.walrus.utils.bukkit.block.BlockUtils;
import network.walrus.utils.bukkit.sound.NetworkSoundConstants.Games.TNT;
import network.walrus.utils.parsing.facet.Facet;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.Dispenser;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

/**
 * Facet which modifies how TNT behaves in the round.
 *
 * @author Austin Mayes
 */
public class TNTFacet extends Facet implements Listener {

  private final Random RANDOM = new Random();
  private final FacetHolder round;
  private final boolean instant;
  private final boolean damage;
  private final Float yield;
  private final Float power;
  private final Duration fuse;
  private final Integer dispenserLimit;
  private final Float dispenserMultiplier;

  /**
   * @param round that the facet is operating in
   * @param instant if TNT should instant ignite
   * @param damage if TNT should damage blocks
   * @param yield that explosions have
   * @param power that explosions have
   * @param fuse time of the TNT
   * @param dispenserLimit limit of TNT blocks spawned by exploding dispensers
   * @param dispenserMultiplier amount to multiply TNT counts in dispensers by when spawning TNT
   */
  TNTFacet(
      FacetHolder round,
      boolean instant,
      boolean damage,
      Float yield,
      Float power,
      Duration fuse,
      Integer dispenserLimit,
      Float dispenserMultiplier) {
    this.round = round;
    this.instant = instant;
    this.damage = damage;
    this.yield = yield;
    this.power = power;
    this.fuse = fuse;
    this.dispenserLimit = dispenserLimit;
    this.dispenserMultiplier = dispenserMultiplier;
  }

  public int getFuseTicks() {
    assert this.fuse != null;
    return (int) (this.fuse.toMillis() / 50.0);
  }

  /** Set yield */
  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void yieldSet(EntityExplodeEvent event) {
    if (event.getEntity() instanceof TNTPrimed) {
      if (!this.damage) {
        event.setCancelled(true);
      }

      if (this.yield != null) {
        event.setYield(this.yield);
      }
    }
  }

  /** Instantly ignite TNT and set fuse time/power. */
  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void handleInstantActivation(BlockPlaceEvent event) {
    if (this.instant && event.getBlock().getType() == Material.TNT) {
      World world = event.getBlock().getWorld();
      TNTPrimed tnt = world.spawn(BlockUtils.base(event.getBlock().getLocation()), TNTPrimed.class);

      if (this.fuse != null) {
        tnt.setFuseTicks(this.getFuseTicks());
      }

      if (this.power != null) {
        tnt.setYield(this.power);
      }

      event.setCancelled(true);
      TNT.INSTANT_IGNITE.play(round.players(), event.getBlock().getLocation());

      ExplosiveTracker tracker =
          UbiquitousBukkitPlugin.getInstance()
              .getTrackerSupervisor()
              .getManager()
              .getTracker(ExplosiveTracker.class);

      tracker.setOwner(tnt, event.getPlayer());

      ItemStack inHand = event.getItemInHand();
      if (inHand.getAmount() == 1) {
        inHand = null;
      } else {
        inHand.setAmount(inHand.getAmount() - 1);
      }
      event
          .getPlayer()
          .getInventory()
          .setItem(event.getPlayer().getInventory().getHeldItemSlot(), inHand);
    }
  }

  /** Set fuse and power. */
  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void setCustomProperties(ExplosionPrimeEvent event) {
    if (event.getEntity() instanceof TNTPrimed) {
      TNTPrimed tnt = (TNTPrimed) event.getEntity();

      if (this.fuse != null) {
        tnt.setFuseTicks(this.getFuseTicks());
      }

      if (this.power != null) {
        tnt.setYield(this.power);
      }
    }
  }

  /** @see #handleDispense(BlockChangeEvent) */
  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void dispenserNukes(BlockChangeByPlayerEvent event) {
    handleDispense(event);
  }

  /** Spawn TNT from exploding dispensers. */
  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void handleDispense(BlockChangeEvent event) {
    BlockState oldState = event.getOldState();
    if (oldState instanceof Dispenser
        && this.dispenserLimit > 0
        && this.dispenserMultiplier > 0
        && event.getCause() instanceof EntityExplodeEvent) {

      Dispenser dispenser = (Dispenser) oldState;
      int tntLimit = Math.round(this.dispenserLimit / this.dispenserMultiplier);
      int tntCount = 0;

      ExplosiveTracker tracker =
          UbiquitousBukkitPlugin.getInstance()
              .getTrackerSupervisor()
              .getManager()
              .getTracker(ExplosiveTracker.class);

      boolean trackPlayer = event instanceof BlockChangeByPlayerEvent;

      for (ItemStack stack : dispenser.getInventory().getContents()) {
        if (stack != null && stack.getType() == Material.TNT) {
          int transfer = Math.min(stack.getAmount(), tntLimit - tntCount);
          if (transfer > 0) {
            stack.setAmount(stack.getAmount() - transfer);
            tntCount += transfer;
          }
        }
      }

      tntCount = (int) Math.ceil(tntCount * this.dispenserMultiplier);

      for (int i = 0; i < tntCount; i++) {
        TNTPrimed tnt =
            this.round.getContainer().mainWorld().spawn(dispenser.getLocation(), TNTPrimed.class);

        tnt.setFuseTicks(
            10
                + this.RANDOM.nextInt(
                    10)); // between 0.5 and 1.0 seconds, same as vanilla TNT chaining

        Vector velocity =
            new Vector(
                RANDOM.nextGaussian(),
                RANDOM.nextGaussian(),
                RANDOM.nextGaussian()); // uniform random direction
        velocity.normalize().multiply(0.5 + 0.5 * RANDOM.nextDouble());
        tnt.setVelocity(velocity);

        if (trackPlayer) {
          tracker.setOwner(tnt, ((BlockChangeByPlayerEvent) event).getPlayer());
        }
      }
    }
  }
}

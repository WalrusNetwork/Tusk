package network.walrus.ubiquitous.bukkit.freeze;

import network.walrus.ubiquitous.bukkit.events.world.BlockChangeByPlayerEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.util.Vector;

/**
 * Listener which blocks frozen players from doing things.
 *
 * @author Austin Mayes
 */
public class FreezeListener implements Listener {

  private final FreezeManager manager;

  FreezeListener(FreezeManager manager) {
    this.manager = manager;
  }

  /** Prevent players from changing blocks */
  @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
  public void preventInteract(BlockChangeByPlayerEvent event) {
    if (manager.isFrozen(event.getPlayer())) {
      event.setCancelled(true);
    }
  }

  /** Keep players frozen */
  @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
  public void onChangeWorld(PlayerChangedWorldEvent event) {
    if (manager.isFrozen(event.getPlayer())) {
      manager.thaw(event.getPlayer());
      event.yield();
      manager.freeze(event.getPlayer());
    }
  }

  /** Prevent players from interacting */
  @EventHandler(priority = EventPriority.LOW) // ignoreCancelled doesn't seem to work well here
  public void onPlayerInteract(PlayerInteractEvent event) {
    if (manager.isFrozen(event.getPlayer())) {
      event.setCancelled(true);
    }
  }

  /** Block inventory manipulation */
  @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
  public void onInventoryClick(InventoryClickEvent event) {
    if (event.getWhoClicked() instanceof Player) {
      if (manager.isFrozen((Player) event.getWhoClicked())) {
        event.setCancelled(true);
      }
    }
  }

  /** Block vehicles */
  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onVehicleMove(VehicleMoveEvent event) {
    if (!event.getVehicle().isEmpty()
        && event.getVehicle().getPassenger() instanceof Player
        && manager.isFrozen((Player) event.getVehicle().getPassenger())) {
      event.getVehicle().setVelocity(new Vector(0, 0, 0));
    }
  }

  /** Block vehicles */
  @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
  public void onVehicleEnter(VehicleEnterEvent event) {
    if (event.getActor() instanceof Player && manager.isFrozen((Player) event.getActor())) {
      event.setCancelled(true);
    }
  }

  /** Block vehicles */
  @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
  public void onVehicleExit(VehicleExitEvent event) {
    if (event.getActor() instanceof Player && manager.isFrozen((Player) event.getActor())) {
      event.setCancelled(true);
    }
  }

  /** Prevent drops */
  @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
  public void onPlayerDropItem(PlayerDropItemEvent event) {
    if (manager.isFrozen(event.getPlayer())) {
      event.setCancelled(true);
    }
  }

  /** Prevent damage */
  @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
  public void onEntityDamage(EntityDamageByEntityEvent event) {
    if (event.getDamager() instanceof Player && manager.isFrozen((Player) event.getDamager())) {
      event.setCancelled(true);
    }
  }

  /** Block vehicle damage */
  @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
  public void onVehicleDamage(VehicleDamageEvent event) {
    if (event.getAttacker() instanceof Player && manager.isFrozen((Player) event.getAttacker())) {
      event.setCancelled(true);
    }
  }
}

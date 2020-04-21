package network.walrus.ubiquitous.bukkit.lobby.facets.sterile;

import java.util.Random;
import network.walrus.ubiquitous.bukkit.events.world.BlockChangeByPlayerEvent;
import network.walrus.ubiquitous.bukkit.events.world.EntityChangeEvent;
import network.walrus.ubiquitous.bukkit.tracker.event.player.PlayerCoarseMoveEvent;
import network.walrus.utils.bukkit.region.PositionUnavailableException;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import network.walrus.utils.parsing.facet.parse.configurator.listener.FacetListener;
import network.walrus.utils.parsing.lobby.facets.spawns.LobbySpawnManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.entity.EntityCombustByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;

/**
 * Listener which blocks players from interacting with the lobby.
 *
 * @author Austin Mayes
 */
public class PlayerInteractionListener extends FacetListener {

  /**
   * @param holder which this object is inside of
   * @param facet which this object is bound to
   */
  public PlayerInteractionListener(FacetHolder holder, LobbySpawnManager facet) {
    super(holder, facet);
  }

  private boolean isInLobby(Location location) {
    return location.getWorld().equals(getHolder().getContainer().mainWorld());
  }

  // -----------------
  // -- Interaction --
  // -----------------

  /** Block all player interaction. */
  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onPlayerInteract(final PlayerInteractEvent event) {
    if (isInLobby(event.getPlayer().getLocation())) {
      event.setCancelled(true);
      event.setUseItemInHand(Event.Result.DENY);
      event.setUseInteractedBlock(Event.Result.DENY);
      // Right clicking armor
      event.getPlayer().updateInventory();
    }
  }

  /** Block armor stand manipulation. */
  @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
  public void onPlayerInteract(final PlayerArmorStandManipulateEvent event) {
    if (isInLobby(event.getPlayer().getLocation())) {
      event.setCancelled(true);
    }
  }

  /** Block entity interaction. */
  @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
  public void onPlayerInteract(final PlayerInteractEntityEvent event) {
    if (isInLobby(event.getPlayer().getLocation())) {
      event.setCancelled(true);
    }
  }

  /** Block entity interaction. */
  @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
  public void onPlayerInteract(final PlayerInteractAtEntityEvent event) {
    if (isInLobby(event.getPlayer().getLocation())) {
      event.setCancelled(true);
    }
  }

  /** Block entity interaction. */
  @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
  public void onPlayerInteract(final HangingBreakByEntityEvent event) {
    if (isInLobby(event.getEntity().getLocation())) {
      event.setCancelled(true);
    }
  }

  // ---------------
  // -- Inventory --
  // ---------------

  /** Block players from modifying inventories that aren't there own. */
  @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
  public void onInventoryClick(InventoryClickEvent event) {
    if (event.getWhoClicked() instanceof Player && isInLobby(event.getWhoClicked().getLocation())) {
      if (event.getInventory().getType() != InventoryType.PLAYER) {
        event.setCancelled(true);
      }
    }
  }

  /** Block item pickup. */
  @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
  public void onPlayerPickupItem(PlayerPickupItemEvent event) {
    if (isInLobby(event.getPlayer().getLocation())) {
      event.setCancelled(true);
    }
  }

  /** Block players dropping items. */
  @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
  public void onPlayerDropItem(PlayerDropItemEvent event) {
    if (isInLobby(event.getPlayer().getLocation())) {
      event.getItemDrop().remove();
    }
  }

  // ------------
  // -- Damage --
  // ------------

  /** Disable damage. */
  @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
  public void onPlayerDamage(EntityDamageEvent event) {
    if (event.getEntity() instanceof Player && isInLobby(event.getEntity().getLocation())) {
      event.setDamage(0);
      event.setCancelled(true);
    }
  }

  /** Disable damage. */
  @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
  public void onPlayerDeath(PlayerDeathEvent event) {
    if (isInLobby(event.getEntity().getLocation())) {
      event.getEntity().setHealth(20);
    }
  }

  /** Disable damage. */
  @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
  public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
    if (event.getDamager() instanceof Player && isInLobby(event.getDamager().getLocation())) {
      event.setCancelled(true);
    }
  }

  /** Disable hunger. */
  @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
  public void onHungerLoss(FoodLevelChangeEvent event) {
    if (isInLobby(event.getEntity().getLocation())) {
      event.setCancelled(true);
    }
  }

  // ------------
  // -- Entity --
  // ------------

  /** Disable entity targeting. */
  @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
  public void onEntityTarget(EntityTargetLivingEntityEvent event) {
    if (event.getTarget() instanceof Player && isInLobby(event.getTarget().getLocation())) {
      event.setCancelled(true);
    }
  }

  /** Block players from modifying entities. */
  @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
  public void onEntityChange(EntityChangeEvent event) {
    if (event.getWhoChanged() instanceof Player) {
      if (isInLobby(event.getWhoChanged().getLocation())) {
        event.setCancelled(true);
      }
    }
  }

  /** Disable burning. */
  @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
  public void onEntityCombustEvent(EntityCombustByBlockEvent event) {
    if (event.getEntity() instanceof Player && isInLobby(event.getEntity().getLocation())) {
      event.getEntity().setFireTicks(0);
    }
  }

  // ------------
  // -- Blocks --
  // ------------

  /** Disable players breaking blocks. */
  @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
  public void onBlockChange(BlockChangeByPlayerEvent event) {
    if (isInLobby(event.getPlayer().getLocation())) {
      event.setCancelled(true);
    }
  }

  /** Disable players breaking blocks. */
  @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
  public void onBlockChange(BlockDamageEvent event) {
    if (isInLobby(event.getPlayer().getLocation())) {
      event.setCancelled(true);
    }
  }

  // --------------
  // -- Vehicles --
  // --------------

  /** Disable vehicles. */
  @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
  public void onVehicleDamage(VehicleDamageEvent event) {
    if (event.getAttacker() instanceof Player && isInLobby(event.getAttacker().getLocation())) {
      event.setCancelled(true);
    }
  }

  /** Disable vehicles. */
  @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
  public void onVehicleEnter(VehicleEnterEvent event) {
    if (event.getEntered() instanceof Player && isInLobby(event.getEntered().getLocation())) {
      event.setCancelled(true);
    }
  }

  /** Disable vehicles. */
  @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
  public void onVehicleCollide(VehicleEntityCollisionEvent event) {
    if (event.getActor() instanceof Player && isInLobby(event.getActor().getLocation())) {
      event.setCancelled(true);
    }
  }

  /** Prevent players from falling forever */
  @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
  public void onCoarseMove(PlayerCoarseMoveEvent event) {
    if (isInLobby(event.getPlayer().getLocation())
        && event.getFrom().getY() >= -50
        && event.getTo().getY() < -50) {
      event.setCancelled(true);
      try {
        event
            .getPlayer()
            .teleport(
                getHolder()
                    .getFacetRequired(LobbySpawnManager.class)
                    .getSpawn()
                    .getRandomPosition(new Random())
                    .toLocation(getHolder().getContainer().mainWorld()));
      } catch (PositionUnavailableException e) {
        e.printStackTrace();
      }
    }
  }
}

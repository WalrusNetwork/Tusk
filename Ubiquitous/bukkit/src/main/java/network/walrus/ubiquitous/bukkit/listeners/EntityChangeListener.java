package network.walrus.ubiquitous.bukkit.listeners;

import network.walrus.ubiquitous.bukkit.events.world.EntityChangeEvent;
import network.walrus.ubiquitous.bukkit.events.world.EntityChangeEvent.Action;
import network.walrus.utils.bukkit.listener.EventUtil;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;

/** Listener which wraps multiple events into one {@link EntityChangeEvent} type. */
@SuppressWarnings("JavaDoc")
public class EntityChangeListener implements Listener {

  private boolean callEntityChange(Entity whoChanged, Entity entity, Event cause, Action action) {
    return EventUtil.call(new EntityChangeEvent<>(whoChanged, entity, cause, action)).isCancelled();
  }

  @EventHandler(priority = EventPriority.LOW)
  public void onHangingPlace(HangingPlaceEvent event) {
    boolean cancel = callEntityChange(event.getPlayer(), event.getEntity(), event, Action.PLACE);
    event.setCancelled(cancel);
  }

  @EventHandler(priority = EventPriority.LOW)
  public void onHangingBreak(HangingBreakByEntityEvent event) {
    boolean cancel = callEntityChange(event.getRemover(), event.getEntity(), event, Action.BREAK);
    event.setCancelled(cancel);
  }

  @EventHandler(priority = EventPriority.LOW)
  public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
    // Rotating item frame
    if (event.getRightClicked() instanceof ItemFrame
        || event.getRightClicked() instanceof ArmorStand) {
      boolean cancel =
          callEntityChange(event.getPlayer(), event.getRightClicked(), event, Action.CHANGE);
      event.setCancelled(cancel);
    }
  }

  @EventHandler(priority = EventPriority.LOW)
  public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
    // Get item from item frame
    if (event.getEntity() instanceof ItemFrame) {
      ItemFrame frame = (ItemFrame) event.getEntity();

      boolean cancel = callEntityChange(event.getEntity(), frame, event, Action.BREAK);
      event.setCancelled(cancel);
    }
  }

  @EventHandler(priority = EventPriority.LOW)
  public void onVehicleDestroy(VehicleDestroyEvent event) {
    boolean cancel = callEntityChange(event.getAttacker(), event.getVehicle(), event, Action.BREAK);
    event.setCancelled(cancel);
  }
}

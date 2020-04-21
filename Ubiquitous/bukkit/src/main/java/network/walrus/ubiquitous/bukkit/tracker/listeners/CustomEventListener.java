package network.walrus.ubiquitous.bukkit.tracker.listeners;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import network.walrus.ubiquitous.bukkit.tracker.event.entity.EntityDeathByEntityEvent;
import network.walrus.ubiquitous.bukkit.tracker.event.entity.EntityDeathByPlayerEvent;
import network.walrus.ubiquitous.bukkit.tracker.event.entity.EntityDeathByTaggedPlayerEvent;
import network.walrus.ubiquitous.bukkit.tracker.event.entity.EntityDeathEvent;
import network.walrus.ubiquitous.bukkit.tracker.event.player.PlayerCoarseMoveEvent;
import network.walrus.ubiquitous.bukkit.tracker.event.player.PlayerDeathByEntityEvent;
import network.walrus.ubiquitous.bukkit.tracker.event.player.PlayerDeathByPlayerEvent;
import network.walrus.ubiquitous.bukkit.tracker.event.player.PlayerDeathByTaggedPlayerEvent;
import network.walrus.ubiquitous.bukkit.tracker.event.player.PlayerDeathEvent;
import network.walrus.ubiquitous.bukkit.tracker.event.tag.TaggedPlayerDeathByEntityEvent;
import network.walrus.ubiquitous.bukkit.tracker.event.tag.TaggedPlayerDeathByPlayerEvent;
import network.walrus.ubiquitous.bukkit.tracker.event.tag.TaggedPlayerDeathByTaggedPlayerEvent;
import network.walrus.ubiquitous.bukkit.tracker.event.tag.TaggedPlayerDeathEvent;
import network.walrus.ubiquitous.bukkit.tracker.lifetime.Lifetime;
import network.walrus.ubiquitous.bukkit.tracker.lifetime.LifetimeManager;
import network.walrus.ubiquitous.bukkit.tracker.tag.CombatLoggerState;
import network.walrus.ubiquitous.bukkit.tracker.tag.LoggerNPCManager;
import network.walrus.utils.bukkit.listener.EventUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

/** Listener responsible for wrapping bukkit events in our own custom types. */
@SuppressWarnings("JavaDoc")
public class CustomEventListener implements Listener {

  private final LifetimeManager manager;

  /**
   * Constructor.
   *
   * @param manager to get lifetime information from
   */
  public CustomEventListener(LifetimeManager manager) {
    this.manager = manager;
  }

  @EventHandler
  public void stopNaturalLoggerBurn(EntityCombustEvent event) {
    if (LoggerNPCManager.isNPC(event.getEntity())) event.setCancelled(true);
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onPlayerCoarseMoveCall(PlayerMoveEvent event) {
    Location from = event.getFrom();
    Location to = event.getTo();

    if (from.getBlockX() == to.getBlockX()) {
      if (from.getBlockY() == to.getBlockY()) {
        if (from.getBlockZ() == to.getBlockZ()) {
          return;
        }
      }
    }

    PlayerCoarseMoveEvent call = new PlayerCoarseMoveEvent(event.getPlayer(), from, to);
    call.setCancelled(event.isCancelled());

    for (EventPriority priority : EventPriority.values()) {
      EventUtil.callEvent(call, PlayerCoarseMoveEvent.getHandlerList(), priority);
    }

    event.setCancelled(call.isCancelled());
    event.setFrom(call.getFrom());
    event.setTo(call.getTo());
  }

  @SuppressWarnings("JavaDoc")
  @EventHandler
  public void onEntityDeath(org.bukkit.event.entity.EntityDeathEvent event) {
    LivingEntity entity = event.getEntity();
    Lifetime lifetime = manager.getLifetime(entity);
    Location location = entity.getLocation();
    Instant time = Instant.now();

    EntityDeathEvent call;

    int droppedExp = event.getDroppedExp();
    List<ItemStack> drops = new ArrayList<>(event.getDrops());

    // EntityDeathEvent or EntityDeathBy____Event??
    if (lifetime.getLastDamage() == null
        || lifetime.getLastDamage().getInfo().getResolvedDamager() == null) {
      if (entity instanceof Player) {
        call = new PlayerDeathEvent((Player) entity, location, lifetime, time, drops, droppedExp);
      } else if (LoggerNPCManager.isNPC(entity)) {
        call =
            new TaggedPlayerDeathEvent(
                LoggerNPCManager.getState(entity), location, lifetime, time, drops, droppedExp);
      } else {
        call = new EntityDeathEvent(entity, location, lifetime, time, drops, droppedExp);
      }
    } else {
      LivingEntity cause = lifetime.getLastDamage().getInfo().getResolvedDamager();

      if (entity instanceof Player) {
        if (cause instanceof Player) {
          call =
              new PlayerDeathByPlayerEvent(
                  (Player) entity, location, lifetime, time, drops, droppedExp, (Player) cause);
        } else if (LoggerNPCManager.isNPC(cause)) {
          call =
              new PlayerDeathByTaggedPlayerEvent(
                  (Player) entity,
                  location,
                  lifetime,
                  time,
                  drops,
                  droppedExp,
                  LoggerNPCManager.getState(cause));
        } else {
          call =
              new PlayerDeathByEntityEvent<>(
                  (Player) entity, location, lifetime, time, drops, droppedExp, cause);
        }
      } else if (LoggerNPCManager.isNPC(entity)) {
        CombatLoggerState state = LoggerNPCManager.getState(entity);
        if (cause instanceof Player) {
          call =
              new TaggedPlayerDeathByPlayerEvent(
                  state, location, lifetime, time, drops, droppedExp, (Player) cause);
        } else if (LoggerNPCManager.isNPC(cause)) {
          call =
              new TaggedPlayerDeathByTaggedPlayerEvent(
                  state,
                  location,
                  lifetime,
                  time,
                  drops,
                  droppedExp,
                  LoggerNPCManager.getState(cause));
        } else {
          call =
              new TaggedPlayerDeathByEntityEvent<>(
                  state, location, lifetime, time, drops, droppedExp, cause);
        }
      } else {
        if (cause instanceof Player) {
          call =
              new EntityDeathByPlayerEvent(
                  entity, location, lifetime, time, drops, droppedExp, (Player) cause);
        } else if (LoggerNPCManager.isNPC(cause)) {
          call =
              new EntityDeathByTaggedPlayerEvent(
                  entity,
                  location,
                  lifetime,
                  time,
                  drops,
                  droppedExp,
                  LoggerNPCManager.getState(cause));
        } else {
          call =
              new EntityDeathByEntityEvent<>(
                  entity, location, lifetime, time, drops, droppedExp, cause);
        }
      }
    }

    // Call event!
    Bukkit.getServer().getPluginManager().callEvent(call);

    // Apply changes in drops
    event.getDrops().clear();
    event.setDroppedExp(call.getDroppedExp());
    for (ItemStack itemStack : call.getDrops()) {
      location.getWorld().dropItemNaturally(location, itemStack);
    }
  }
}

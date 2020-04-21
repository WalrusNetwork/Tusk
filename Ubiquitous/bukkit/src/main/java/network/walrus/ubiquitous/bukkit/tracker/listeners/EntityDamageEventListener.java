package network.walrus.ubiquitous.bukkit.tracker.listeners;

import com.google.common.base.Preconditions;
import java.time.Instant;
import javax.annotation.Nonnull;
import network.walrus.ubiquitous.bukkit.tracker.api.DamageAPI;
import network.walrus.ubiquitous.bukkit.tracker.api.DamageAPIHelper;
import network.walrus.ubiquitous.bukkit.tracker.event.entity.EntityDamageEvent;
import network.walrus.ubiquitous.bukkit.tracker.event.player.PlayerDamageEvent;
import network.walrus.ubiquitous.bukkit.tracker.event.tag.TaggedPlayerDamageEvent;
import network.walrus.ubiquitous.bukkit.tracker.info.DamageInfo;
import network.walrus.ubiquitous.bukkit.tracker.lifetime.Lifetime;
import network.walrus.ubiquitous.bukkit.tracker.lifetime.LifetimeManager;
import network.walrus.ubiquitous.bukkit.tracker.resolver.DamageResolver;
import network.walrus.ubiquitous.bukkit.tracker.resolver.DamageResolverManager;
import network.walrus.ubiquitous.bukkit.tracker.tag.LoggerNPCManager;
import network.walrus.utils.bukkit.listener.EventUtil;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

/**
 * Event listener which calls {@link EntityDamageEvent}s in parallel with Bukkit events and keeps
 * the {@link DamageAPI} up to date.
 *
 * @author Overcast Network
 */
public class EntityDamageEventListener implements Listener {

  private final LifetimeManager manager;
  private final DamageResolverManager resolverManager;
  private final DamageAPIHelper helper;

  /**
   * Constructor,
   *
   * @param manager to get lifetime information from
   * @param resolverManager used to resolve damage info
   * @param helper used to interact with the damage API
   */
  public EntityDamageEventListener(
      LifetimeManager manager, DamageResolverManager resolverManager, DamageAPIHelper helper) {
    this.manager = manager;
    this.resolverManager = resolverManager;
    this.helper = helper;
  }

  /**
   * Register the {@link EntityDamageEventRunner} using a certain plugin.
   *
   * @param plugin to register the listener with
   */
  public void register(@Nonnull Plugin plugin) {
    Preconditions.checkNotNull(plugin, "plugin");

    PluginManager pm = plugin.getServer().getPluginManager();

    for (EventPriority priority : EventPriority.values()) {
      pm.registerEvent(
          org.bukkit.event.entity.EntityDamageEvent.class,
          this,
          priority,
          new EntityDamageEventRunner(this, manager, resolverManager, helper, priority),
          plugin,
          false);
    }
  }

  /** Unregister the event runner. */
  public void unregister() {
    HandlerList.unregisterAll(this);
  }

  /**
   * Class which passes {@link EntityDamageEvent}s through the damage API at every event prioriy
   * level and applies information using {@link DamageResolver}s.
   */
  public static class EntityDamageEventRunner implements EventExecutor {

    private final @Nonnull EntityDamageEventListener parent;
    private final @Nonnull LifetimeManager manager;
    private final @Nonnull DamageResolverManager resolverManager;
    private final @Nonnull DamageAPIHelper apiHelper;
    private final @Nonnull EventPriority priority;

    /**
     * Constructor.
     *
     * @param parent listener which registered the runner
     * @param manager used to get lifetime data
     * @param resolverManager used to resolve damage
     * @param apiHelper used to pass information to the damage API
     * @param priority of the event being called
     */
    public EntityDamageEventRunner(
        @Nonnull EntityDamageEventListener parent,
        @Nonnull LifetimeManager manager,
        @Nonnull DamageResolverManager resolverManager,
        @Nonnull DamageAPIHelper apiHelper,
        @Nonnull EventPriority priority) {
      Preconditions.checkNotNull(parent, "parent");
      Preconditions.checkNotNull(priority, "event priority");

      this.parent = parent;
      this.manager = manager;
      this.resolverManager = resolverManager;
      this.apiHelper = apiHelper;
      this.priority = priority;
    }

    public void execute(Listener listener, Event event) throws EventException {
      if (listener != this.parent) {
        return;
      }

      if (!(event instanceof org.bukkit.event.entity.EntityDamageEvent)) {
        return;
      }
      org.bukkit.event.entity.EntityDamageEvent bukkit =
          (org.bukkit.event.entity.EntityDamageEvent) event;

      if (!(bukkit.getEntity() instanceof LivingEntity)) {
        return;
      }
      LivingEntity entity = (LivingEntity) bukkit.getEntity();

      if (entity.isDead()) {
        return;
      }

      Lifetime lifetime = manager.getLifetime(entity);

      // get our version of the event
      EntityDamageEvent our = apiHelper.getOurEvent(bukkit);
      if (our == null) {
        int hearts = (int) bukkit.getDamage();
        Location location = entity.getLocation();
        Instant time = Instant.now();
        DamageInfo info = resolverManager.resolve(entity, lifetime, bukkit);

        if (entity instanceof Player) {
          our = new PlayerDamageEvent((Player) entity, lifetime, hearts, location, time, info);
        } else if (LoggerNPCManager.isNPC(entity)) {
          our =
              new TaggedPlayerDamageEvent(
                  LoggerNPCManager.getState(entity), lifetime, hearts, location, time, info);
        } else {
          our = new EntityDamageEvent(entity, lifetime, hearts, location, time, info);
        }
        apiHelper.setOurEvent(bukkit, our);
      }

      // update mutable information
      our.setCancelled(bukkit.isCancelled());
      our.setDamage((int) bukkit.getDamage());

      // call
      EventUtil.callEvent(our, EntityDamageEvent.getHandlerList(), this.priority);

      // update bukkit event
      bukkit.setCancelled(our.isCancelled());
      bukkit.setDamage(our.getDamage());

      // clean up
      if (this.priority == EventPriority.MONITOR) {
        apiHelper.setOurEvent(bukkit, null);

        if (!bukkit.isCancelled()) {
          lifetime.addDamage(our.toDamageObject());
        }
      }
    }
  }
}

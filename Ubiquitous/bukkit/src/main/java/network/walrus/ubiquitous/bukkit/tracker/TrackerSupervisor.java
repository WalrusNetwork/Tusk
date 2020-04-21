package network.walrus.ubiquitous.bukkit.tracker;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.LinkedHashSet;
import network.walrus.ubiquitous.bukkit.tracker.api.DamageAPIHelper;
import network.walrus.ubiquitous.bukkit.tracker.base.SimpleLifetimeManager;
import network.walrus.ubiquitous.bukkit.tracker.base.SimpleResolverManager;
import network.walrus.ubiquitous.bukkit.tracker.lifetime.LifetimeManager;
import network.walrus.ubiquitous.bukkit.tracker.listeners.AnvilListener;
import network.walrus.ubiquitous.bukkit.tracker.listeners.CustomEventListener;
import network.walrus.ubiquitous.bukkit.tracker.listeners.DispenserListener;
import network.walrus.ubiquitous.bukkit.tracker.listeners.EntityDamageEventListener;
import network.walrus.ubiquitous.bukkit.tracker.listeners.ExplosiveListener;
import network.walrus.ubiquitous.bukkit.tracker.listeners.GravityListener;
import network.walrus.ubiquitous.bukkit.tracker.listeners.LifetimeListener;
import network.walrus.ubiquitous.bukkit.tracker.listeners.OwnedMobListener;
import network.walrus.ubiquitous.bukkit.tracker.listeners.ProjectileDistanceListener;
import network.walrus.ubiquitous.bukkit.tracker.listeners.WorldListener;
import network.walrus.ubiquitous.bukkit.tracker.manager.SimpleTrackerManager;
import network.walrus.ubiquitous.bukkit.tracker.manager.TrackerManager;
import network.walrus.ubiquitous.bukkit.tracker.resolver.AnvilDamageResolver;
import network.walrus.ubiquitous.bukkit.tracker.resolver.BlockDamageResolver;
import network.walrus.ubiquitous.bukkit.tracker.resolver.DamageResolver;
import network.walrus.ubiquitous.bukkit.tracker.resolver.DamageResolverManager;
import network.walrus.ubiquitous.bukkit.tracker.resolver.DispensedProjectileDamageResolver;
import network.walrus.ubiquitous.bukkit.tracker.resolver.FallDamageResolver;
import network.walrus.ubiquitous.bukkit.tracker.resolver.GravityDamageResolver;
import network.walrus.ubiquitous.bukkit.tracker.resolver.LavaDamageResolver;
import network.walrus.ubiquitous.bukkit.tracker.resolver.MeleeDamageResolver;
import network.walrus.ubiquitous.bukkit.tracker.resolver.OwnedMobDamageResolver;
import network.walrus.ubiquitous.bukkit.tracker.resolver.ProjectileDamageResolver;
import network.walrus.ubiquitous.bukkit.tracker.resolver.TNTDamageResolver;
import network.walrus.ubiquitous.bukkit.tracker.resolver.VoidDamageResolver;
import network.walrus.ubiquitous.bukkit.tracker.trackers.AnvilTracker;
import network.walrus.ubiquitous.bukkit.tracker.trackers.DispenserTracker;
import network.walrus.ubiquitous.bukkit.tracker.trackers.ExplosiveTracker;
import network.walrus.ubiquitous.bukkit.tracker.trackers.OwnedMobTracker;
import network.walrus.ubiquitous.bukkit.tracker.trackers.ProjectileDistanceTracker;
import network.walrus.ubiquitous.bukkit.tracker.trackers.Tracker;
import network.walrus.ubiquitous.bukkit.tracker.trackers.base.SimpleAnvilTracker;
import network.walrus.ubiquitous.bukkit.tracker.trackers.base.SimpleDispenserTracker;
import network.walrus.ubiquitous.bukkit.tracker.trackers.base.SimpleExplosiveTracker;
import network.walrus.ubiquitous.bukkit.tracker.trackers.base.SimpleOwnedMobTracker;
import network.walrus.ubiquitous.bukkit.tracker.trackers.base.SimpleProjectileDistanceTracker;
import network.walrus.ubiquitous.bukkit.tracker.trackers.base.gravity.SimpleGravityKillTracker;
import network.walrus.utils.bukkit.WalrusBukkitPlugin;
import org.bukkit.event.Listener;

/**
 * Parent object for the enabling/disabling of all of the tracker components.
 *
 * @author Austin Mayes
 */
public class TrackerSupervisor {

  private final WalrusBukkitPlugin plugin;
  private final TrackerManager manager;
  private final LifetimeManager lifetimeManager;
  private final LinkedHashSet<Tracker> trackers;
  private final LinkedHashSet<DamageResolver> resolvers;
  private final DamageResolverManager resolverManager;
  private final EntityDamageEventListener listener;

  /**
   * Constructor.
   *
   * @param plugin to register events for
   */
  public TrackerSupervisor(WalrusBukkitPlugin plugin) {
    this.plugin = plugin;
    this.manager = new SimpleTrackerManager();
    this.lifetimeManager = new SimpleLifetimeManager();
    this.trackers = Sets.newLinkedHashSet();
    this.resolvers = Sets.newLinkedHashSet();
    this.resolverManager = new SimpleResolverManager();
    this.listener =
        new EntityDamageEventListener(
            this.lifetimeManager, this.resolverManager, new DamageAPIHelper());
    init();
  }

  private void init() {
    // basic operation listeners

    ExplosiveTracker explosiveTracker = new SimpleExplosiveTracker();
    SimpleGravityKillTracker gravityKillTracker =
        new SimpleGravityKillTracker(plugin, plugin.timer());

    explosiveTracker.enable();
    gravityKillTracker.enable();

    this.trackers.add(explosiveTracker);
    this.trackers.add(gravityKillTracker);

    DispenserTracker dispenserTracker = new SimpleDispenserTracker();
    dispenserTracker.enable();

    this.trackers.add(dispenserTracker);

    ProjectileDistanceTracker projectileDistanceTracker = new SimpleProjectileDistanceTracker();
    projectileDistanceTracker.enable();

    this.trackers.add(projectileDistanceTracker);

    OwnedMobTracker ownedMobTracker = new SimpleOwnedMobTracker();
    ownedMobTracker.enable();

    this.trackers.add(ownedMobTracker);

    AnvilTracker anvilTracker = new SimpleAnvilTracker();
    anvilTracker.enable();

    this.trackers.add(anvilTracker);
    resolvers.addAll(
        Lists.newArrayList(
            new BlockDamageResolver(),
            new FallDamageResolver(),
            new LavaDamageResolver(),
            new MeleeDamageResolver(),
            new ProjectileDamageResolver(projectileDistanceTracker),
            new TNTDamageResolver(explosiveTracker, dispenserTracker),
            new VoidDamageResolver(),
            new GravityDamageResolver(gravityKillTracker),
            new DispensedProjectileDamageResolver(projectileDistanceTracker, dispenserTracker),
            new OwnedMobDamageResolver(ownedMobTracker),
            new AnvilDamageResolver(anvilTracker)));

    this.registerEvents(new LifetimeListener(this.lifetimeManager));
    this.registerEvents(new WorldListener(this.manager));
    this.registerEvents(new ExplosiveListener(explosiveTracker, ownedMobTracker, dispenserTracker));
    this.registerEvents(new GravityListener(gravityKillTracker));
    this.registerEvents(new CustomEventListener(this.lifetimeManager));
    this.registerEvents(new DispenserListener(dispenserTracker));
    this.registerEvents(new ProjectileDistanceListener(projectileDistanceTracker));
    this.registerEvents(new OwnedMobListener(ownedMobTracker));
    this.registerEvents(new AnvilListener(anvilTracker));

    // debug
    // this.registerEvents(new DebugListener());
  }

  /**
   * Enable all trackers, register the {@link EntityDamageEventListener}, and register all resolvers
   * with the manager.
   */
  public void enable() {
    listener.register(plugin);
    for (Tracker tracker : trackers) {
      manager.setTracker((Class<Tracker>) tracker.getClass(), tracker);
      tracker.enable();
    }
    for (DamageResolver resolver : resolvers) {
      resolverManager.register(resolver);
    }
  }

  /**
   * Disable all trackers, unregister the {@link EntityDamageEventListener}, and unregister all
   * resolvers with the manager.
   */
  public void disable() {
    listener.unregister();
    for (Tracker t : trackers) {
      t.disable();
      manager.clearTracker(t.getClass());
    }
    for (DamageResolver resolver : resolvers) {
      resolverManager.unregister(resolver);
    }
  }

  private void registerEvents(Listener listener) {
    plugin.getServer().getPluginManager().registerEvents(listener, plugin);
  }

  public TrackerManager getManager() {
    return manager;
  }

  public LifetimeManager getLifetimeManager() {
    return lifetimeManager;
  }
}

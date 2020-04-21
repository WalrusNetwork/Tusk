package network.walrus.utils.parsing.facet.holder;

import app.ashcon.intake.bukkit.BukkitIntake;
import app.ashcon.intake.bukkit.graph.BasicBukkitCommandGraph;
import app.ashcon.intake.parametric.Module;
import com.google.common.base.Objects;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import network.walrus.utils.core.config.ParsingException;
import network.walrus.utils.core.registry.Registry;
import network.walrus.utils.parsing.facet.Facet;
import network.walrus.utils.parsing.facet.FacetLoadException;
import network.walrus.utils.parsing.facet.parse.configurator.ActiveTime;
import network.walrus.utils.parsing.facet.parse.configurator.command.FacetCommandContainer;
import network.walrus.utils.parsing.facet.parse.configurator.command.HolderCommandProperties;
import network.walrus.utils.parsing.facet.parse.configurator.listener.HolderListenerProperties;
import network.walrus.utils.parsing.world.PlayerContainer;
import network.walrus.utils.parsing.world.WorldProvider;
import network.walrus.utils.parsing.world.config.FacetConfigurationSource;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

/**
 * Holds parsed {@link Facet}s which add features to the holder. These facets only live as long as
 * the holder object does.
 *
 * @author Austin Mayes
 */
public abstract class FacetHolder {

  // Listeners
  private static final Set<HolderListenerProperties> listenerProps = Sets.newHashSet();
  // Commands
  private static final Set<HolderCommandProperties> commandProps = Sets.newHashSet();
  public static Module COMMAND_MODULE = null;
  // System
  protected final Plugin plugin;
  protected final Logger logger;
  // Facets
  protected final Set<Facet> facets = Sets.newHashSet();
  // World
  protected final FacetConfigurationSource source;
  // Unique holder id
  private final UUID id;
  private final Registry registry = new Registry();
  private final WorldProvider<? extends PlayerContainer> worldProvider;
  private final Multimap<ActiveTime, Listener> listeners = HashMultimap.create();
  private final Multimap<ActiveTime, Pair<String[], FacetCommandContainer>> commands =
      HashMultimap.create();
  private final BasicBukkitCommandGraph loadedGraph;
  private final BukkitIntake loadedIntake;
  private final BasicBukkitCommandGraph enabledGraph;
  private final BukkitIntake enabledIntake;
  // World
  private PlayerContainer container;

  /**
   * Constructor.
   *
   * @param plugin to register commands and listeners with
   * @param logger to log errors and info to
   * @param source which this holder is for
   * @param worldProvider used to create and load the world
   */
  public FacetHolder(
      Plugin plugin,
      Logger logger,
      FacetConfigurationSource source,
      WorldProvider<? extends PlayerContainer> worldProvider) {
    this.id = UUID.randomUUID();
    this.plugin = plugin;
    this.logger = logger;
    this.source = source;
    this.worldProvider = worldProvider;
    this.loadedGraph =
        COMMAND_MODULE == null
            ? new BasicBukkitCommandGraph()
            : new BasicBukkitCommandGraph(COMMAND_MODULE);
    this.enabledGraph =
        COMMAND_MODULE == null
            ? new BasicBukkitCommandGraph()
            : new BasicBukkitCommandGraph(COMMAND_MODULE);
    this.loadedIntake = new BukkitIntake(plugin, this.loadedGraph);
    this.enabledIntake = new BukkitIntake(plugin, this.enabledGraph);
  }

  /**
   * Register a listener property object that will be used in all holders to register a listener.
   *
   * @param properties to register
   */
  public static void registerListener(HolderListenerProperties properties) {
    listenerProps.add(properties);
  }

  /**
   * Register a command property object that will be used in all holders to register commands.
   *
   * @param properties to register
   */
  public static void registerCommands(HolderCommandProperties properties) {
    commandProps.add(properties);
  }

  /**
   * Load a fresh world from the {@link FacetConfigurationSource#source()} for use in this holder.
   *
   * @return if loading was successful
   */
  public boolean loadWorld() {
    this.container = this.worldProvider.load();
    return !this.worldProvider.loadFailed();
  }

  public FacetConfigurationSource getSource() {
    return source;
  }

  /**
   * Call {@link Facet#load()} on all parsed facets.
   *
   * <p>If the facets are also {@link Listener}s, they will be registered at this point as well.
   *
   * <p>All listeners bound to {@link ActiveTime#LOADED} who's bounding conditions are met will be
   * registered to receive events at this stage.
   */
  public void loadFacets() {
    facets.removeIf(f -> !f.shouldLoad());
    for (Facet facet : facets) {
      try {
        facet.load();
        if (facet instanceof Listener) {
          Bukkit.getPluginManager().registerEvents((Listener) facet, plugin);
        }
      } catch (FacetLoadException e) {
        throw new ParsingException(
            "Failed to enable facet: " + facet.getClass().getSimpleName(), e);
      }
    }
    registerListeners(ActiveTime.LOADED);
    registerCommands(ActiveTime.LOADED);
    List<Player> toTeleport = new ArrayList<>(Bukkit.getOnlinePlayers());
    Collections.shuffle(toTeleport);
    for (Player player : toTeleport) {
      player.teleport(container.mainWorld().getSpawnLocation());
      player.setArrowsStuck(0);
    }
  }

  /**
   * Call {@link Facet#enable()} on all parsed facets.
   *
   * <p>All listeners bound to {@link ActiveTime#ENABLED} who's bounding conditions are met will be
   * registered to receive events at this stage.
   */
  public void enableFacets() {
    for (Facet facet : facets) {
      facet.enable();
    }
    registerListeners(ActiveTime.ENABLED);
    registerCommands(ActiveTime.ENABLED);
  }

  /**
   * Call {@link Facet#disable()} on all parsed facets.
   *
   * <p>All listeners bound to {@link ActiveTime#ENABLED} will be unregistered at this stage.
   */
  public void disableFacets() {
    unregisterListeners(ActiveTime.ENABLED);
    unregisterCommands(ActiveTime.ENABLED);
    for (Facet facet : facets) {
      facet.disable();
    }
  }

  /**
   * Call {@link Facet#unload()} on all parsed facets.
   *
   * <p>If the facets are also {@link Listener}s, they will be unregistered at this point as well.
   *
   * <p>All listeners bound to {@link ActiveTime#LOADED} will be unregistered at this stage.
   */
  public void unloadFacets() {
    unregisterListeners(ActiveTime.LOADED);
    unregisterCommands(ActiveTime.LOADED);
    for (Facet facet : facets) {
      facet.unload();
      if (facet instanceof Listener) {
        HandlerList.unregisterAll((Listener) facet);
      }
    }
  }

  /** Unload all worlds attached to this holder. */
  public void unloadWorld() {
    container.actOnAllWorlds(w -> Bukkit.unloadWorld(w, false));
  }

  private void registerListeners(ActiveTime time) {
    for (HolderListenerProperties properties : listenerProps) {
      if (properties.getActiveTime() != time) {
        continue;
      }

      Facet facet = null;
      if (properties.boundToFacet()) {
        if (!hasFacet(properties.getFacetClass())) {
          continue;
        }

        facet = getFacetRequired(properties.getFacetClass());
      }

      Listener listener;
      try {
        Class<? extends Listener> clazz = properties.getListenerClass();
        if (properties.boundToFacet()) {
          listener =
              clazz
                  .getConstructor(FacetHolder.class, properties.getFacetClass())
                  .newInstance(this, facet);
        } else {
          listener = clazz.getConstructor(FacetHolder.class).newInstance(this);
        }
      } catch (InstantiationException
          | IllegalAccessException
          | NoSuchMethodException
          | InvocationTargetException e) {
        logger.log(
            Level.SEVERE,
            "Failed to create listener instance for "
                + properties.getListenerClass().getName()
                + "!",
            e);
        continue;
      }
      listeners.put(time, listener);
      Bukkit.getPluginManager().registerEvents(listener, plugin);
    }
  }

  private void unregisterListeners(ActiveTime activeTime) {
    for (Listener listener : this.listeners.get(activeTime)) {
      HandlerList.unregisterAll(listener);
    }
  }

  private void registerCommands(ActiveTime time) {
    for (HolderCommandProperties properties : commandProps) {
      if (properties.getActiveTime() != time) {
        continue;
      }

      Facet facet = null;
      if (properties.boundToFacet()) {
        if (!hasFacet(properties.getFacetClass())) {
          continue;
        }

        facet = getFacetRequired(properties.getFacetClass());
      }

      FacetCommandContainer container;
      try {
        Class<? extends FacetCommandContainer> clazz = properties.getCommandClass();
        if (properties.boundToFacet()) {
          container =
              clazz
                  .getConstructor(FacetHolder.class, properties.getFacetClass())
                  .newInstance(this, facet);
        } else {
          container = clazz.getConstructor(FacetHolder.class).newInstance(this);
        }
      } catch (InstantiationException
          | IllegalAccessException
          | NoSuchMethodException
          | InvocationTargetException e) {
        logger.log(
            Level.SEVERE,
            "Failed to create command instance for " + properties.getCommandClass().getName() + "!",
            e);
        continue;
      }
      String[] alias = null;
      try {
        Method aliasMethod = container.getClass().getMethod("rootAlias");
        alias = (String[]) aliasMethod.invoke(container);
      } catch (NoSuchMethodException e) {
        e.printStackTrace(); // Not possible
      } catch (IllegalAccessException | InvocationTargetException e) {
        e.printStackTrace();
      }
      if (alias != null && alias.length == 0) alias = null;
      commands.put(time, Pair.of(alias, container));
    }

    switch (time) {
      case LOADED:
        for (Pair<String[], FacetCommandContainer> facetCommandContainerPair : commands.get(time)) {
          registerCommands(loadedGraph, facetCommandContainerPair);
        }
        loadedIntake.register();
        break;
      case ENABLED:
        for (Pair<String[], FacetCommandContainer> c : commands.get(time)) {
          registerCommands(enabledGraph, c);
        }
        enabledIntake.register();
        break;
      default:
        throw new RuntimeException("Unknown active time " + time);
    }
  }

  private void registerCommands(
      BasicBukkitCommandGraph graph, Pair<String[], FacetCommandContainer> toRegister) {
    String[] root = toRegister.getKey();
    FacetCommandContainer container = toRegister.getRight();
    if (root == null) graph.getRootDispatcherNode().registerCommands(container);
    else graph.getRootDispatcherNode().registerNode(root).registerCommands(container);
  }

  private void unregisterCommands(ActiveTime activeTime) {
    switch (activeTime) {
      case LOADED:
        loadedIntake.unregister();
        break;
      case ENABLED:
        enabledIntake.unregister();
        break;
      default:
        throw new RuntimeException("Unknown active time " + activeTime);
    }
  }

  /**
   * Determine if this holder has a facet loaded which can be assignable from the supplied class
   *
   * @param clazz to search
   * @return if a facet matching the query is present
   */
  public boolean hasFacet(Class<? extends Facet> clazz) {
    for (Facet f : facets) {
      if (clazz.isAssignableFrom(f.getClass())) {
        return true;
      }
    }
    return false;
  }

  /**
   * Find a loaded facet by the class which defines it. If no facet with the class is loaded, {@link
   * Optional#empty()} will be returned instead.
   *
   * @param clazz to search for facets using
   * @param <F> type of facet being searched for
   * @return facet matching the class, or empty if one isn't loaded
   */
  public <F extends Facet> Optional<F> getFacet(Class<F> clazz) {
    for (Facet f : facets) {
      if (clazz.isAssignableFrom(f.getClass())) {
        return (Optional<F>) Optional.of(f);
      }
    }
    return Optional.empty();
  }

  /**
   * {@link #getFacet(Class)} that will throw an exception of {@link Optional#empty()} is returned.
   */
  public <F extends Facet> F getFacetRequired(Class<F> clazz) {
    return getFacet(clazz)
        .orElseThrow(
            () ->
                new RuntimeException("No facet found when searching for " + clazz.getSimpleName()));
  }

  /**
   * Add a parsed facet to this holder.
   *
   * @param facet to add
   */
  public void addFacet(Facet facet) {
    facets.add(facet);
  }

  public PlayerContainer getContainer() {
    return container;
  }

  public Registry getRegistry() {
    return registry;
  }

  /**
   * @return all of the players currently inside of this holder. For round environments, this
   *     includes spectators as well.
   */
  public List<Player> players() {
    return this.container == null ? Collections.emptyList() : this.container.players();
  }

  public WorldProvider<? extends PlayerContainer> getWorldProvider() {
    return worldProvider;
  }

  /**
   * Add a {@link Module} to both command graphs.
   *
   * @param module to add
   */
  public void addCommandModule(Module module) {
    this.loadedGraph.getBuilder().getInjector().install(module);
    this.enabledGraph.getBuilder().getInjector().install(module);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FacetHolder that = (FacetHolder) o;
    return Objects.equal(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }
}

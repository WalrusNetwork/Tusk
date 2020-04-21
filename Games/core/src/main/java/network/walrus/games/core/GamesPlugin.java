package network.walrus.games.core;

import app.ashcon.intake.bukkit.BukkitIntake;
import app.ashcon.intake.bukkit.graph.BasicBukkitCommandGraph;
import co.aikar.timings.Timing;
import co.aikar.timings.Timings;
import java.io.File;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;
import network.walrus.games.core.api.game.GameParser;
import network.walrus.games.core.commands.DevCommands;
import network.walrus.games.core.commands.GamesCoreCommandModule;
import network.walrus.games.core.commands.MapInfoCommands;
import network.walrus.games.core.external.ComponentLoader;
import network.walrus.games.core.external.ComponentLoader.ExternalComponentInfo;
import network.walrus.games.core.external.ExternalComponent;
import network.walrus.games.core.facets.applicators.ApplicatorsConfigurator;
import network.walrus.games.core.facets.broadcasts.BroadcastsConfigurator;
import network.walrus.games.core.facets.crafting.CraftingConfigurator;
import network.walrus.games.core.facets.damage.DamageConfigurator;
import network.walrus.games.core.facets.death.DeathsConfigurator;
import network.walrus.games.core.facets.filters.FilterConfigurator;
import network.walrus.games.core.facets.group.GroupsManager;
import network.walrus.games.core.facets.hunger.HungerFacetConfigurator;
import network.walrus.games.core.facets.items.ItemsConfigurator;
import network.walrus.games.core.facets.kits.KitsConfigurator;
import network.walrus.games.core.facets.modifyprojectile.ModifyProjectileConfigurator;
import network.walrus.games.core.facets.portals.PortalsConfigurator;
import network.walrus.games.core.facets.rage.RageConfigurator;
import network.walrus.games.core.facets.renewables.RenewablesConfigurator;
import network.walrus.games.core.facets.spawners.SpawnerConfigurator;
import network.walrus.games.core.facets.tnt.TNTConfigurator;
import network.walrus.games.core.listeners.DoubleJumpListener;
import network.walrus.games.core.listeners.GeneralListener;
import network.walrus.games.core.util.EventUtil;
import network.walrus.ubiquitous.bukkit.settings.PlayerSettings;
import network.walrus.utils.bukkit.WalrusBukkitPlugin;
import network.walrus.utils.bukkit.logging.ChatLogHandler;
import network.walrus.utils.core.color.NetworkColorConstants.Games;
import network.walrus.utils.parsing.facet.facets.region.RegionsConfigurator;
import network.walrus.utils.parsing.facet.parse.configurator.ConfiguratorManager;
import network.walrus.utils.parsing.facet.parse.configurator.FacetConfigurator;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

/**
 * Main class of the core game management plugin.
 *
 * <p>Without an {@link ExternalComponent}, this plugin really doesn't do anything. All this plugin
 * is responsible for is setting up a base state for a game environment, but will not even be able
 * to load maps without at least one defined {@link GameParser}.
 *
 * @author Austin Mayes
 */
public class GamesPlugin extends WalrusBukkitPlugin {

  public static GamesPlugin instance;
  public final AtomicBoolean loaded = new AtomicBoolean();
  private final ConfiguratorManager configuratorManager = new ConfiguratorManager();
  private Logger mapLogger;
  private ComponentLoader loader;
  private BasicBukkitCommandGraph graph;
  private BukkitIntake intake;

  @Override
  public void load() {
    instance = this;
    registerCoreConfigurators();
    loadExternalComponents();
    loadConfig();
  }

  @Override
  public void enable() {
    registerListeners();
    addConsolePerms();
    this.configuratorManager.actOnAll(FacetConfigurator::configure);
    this.graph = new BasicBukkitCommandGraph(new GamesCoreCommandModule());
    this.intake = new BukkitIntake(this, this.graph);
    enableExternalComponents();
    registerListeners();
    registerCommands();
    intake.register();

    // TODO: Remove when done beta
    Bukkit.getServer()
        .getPluginManager()
        .addPermission(new Permission(GamesCorePermissions.VIEW_ERRORS_MAPS, PermissionDefault.OP));

    // Load messages file
    GamesCoreMessages.UI_TIE.with();
    PlayerSettings.register(GroupsManager.OBS_SETTING);
  }

  /** Attempt to load external components from the components/ directory. */
  private void loadExternalComponents() {
    this.loader = new ComponentLoader(new File(this.getDataFolder(), "components"));
    Bukkit.getLogger().info("Beginning external component loading...");
    this.loader.loadComponents();
    Bukkit.getLogger()
        .info(
            "Finished external component loading! Loaded "
                + this.loader.getLoadedComponents().size()
                + " components!");
  }

  private void registerCoreConfigurators() {
    this.configuratorManager.addConfigurator(new RegionsConfigurator());
    this.configuratorManager.addConfigurator(new FilterConfigurator());
    this.configuratorManager.addConfigurator(new KitsConfigurator());
    this.configuratorManager.addConfigurator(new DeathsConfigurator());
    this.configuratorManager.addConfigurator(new ApplicatorsConfigurator());
    this.configuratorManager.addConfigurator(new ItemsConfigurator());
    this.configuratorManager.addConfigurator(new DamageConfigurator());
    this.configuratorManager.addConfigurator(new PortalsConfigurator());
    this.configuratorManager.addConfigurator(new RenewablesConfigurator());
    this.configuratorManager.addConfigurator(new TNTConfigurator());
    this.configuratorManager.addConfigurator(new RageConfigurator());
    this.configuratorManager.addConfigurator(new BroadcastsConfigurator());
    this.configuratorManager.addConfigurator(new CraftingConfigurator());
    this.configuratorManager.addConfigurator(new HungerFacetConfigurator());
    this.configuratorManager.addConfigurator(new ModifyProjectileConfigurator());
    this.configuratorManager.addConfigurator(new SpawnerConfigurator());
    this.configuratorManager.addConfigurator(
        new FacetConfigurator() {
          @Override
          public void configure() {
            bindConstantCommands(DevCommands.class);
            bindConstantCommands(MapInfoCommands.class);
          }
        });
  }

  /** Call {@link ExternalComponent#onEnable()} on all successfully loaded components. */
  private void enableExternalComponents() {
    for (ExternalComponentInfo m : this.loader.getLoadedComponents()) {
      try (Timing t =
          Timings.ofStart(
              GamesPlugin.instance, "Component enable: " + m.getDescriptionFile().getName())) {
        ExternalComponent component = m.getComponentInstance();
        component.onEnable();
      } catch (Exception e) {
        Bukkit.getLogger().info("Failed to enable component!");
        e.printStackTrace();
      }
      Bukkit.getLogger().info("Enabled Component: " + m.getDescriptionFile().getName());
    }
  }

  private void loadConfig() {
    for (Entry<String, Object> entry :
        this.getConfig().getConfigurationSection("component-configs").getValues(false).entrySet()) {
      String n = entry.getKey();
      Object s = entry.getValue();
      for (ExternalComponentInfo c : this.loader.getLoadedComponents()) {
        if (c.getDescriptionFile().getName().replace(" ", "-").equalsIgnoreCase(n)) {
          Bukkit.getLogger().info("Loading config for " + n);
          c.getComponentInstance().loadConfig((ConfigurationSection) s);
          break;
        }
      }
    }
  }

  @Override
  public void disable() {
    this.loader.disableAll();
  }

  private void addConsolePerms() {
    Bukkit.getConsoleSender().addAttachment(this, GamesCorePermissions.VIEW_ERRORS_MAPS, true);
  }

  /** @return logger which should be used to send configuration errors to */
  public Logger mapLogger() {
    if (mapLogger != null) {
      return mapLogger;
    }

    mapLogger = Logger.getLogger("map-errors");
    mapLogger.setUseParentHandlers(false);
    mapLogger.addHandler(
        new ChatLogHandler(Games.Maps.PREFIX, "Maps", GamesCorePermissions.VIEW_ERRORS_MAPS));
    return mapLogger;
  }

  private void registerListeners() {
    EventUtil.register(new GeneralListener(this));
    EventUtil.register(new DoubleJumpListener());
  }

  private void registerCommands() {}

  public ConfiguratorManager getConfiguratorManager() {
    return configuratorManager;
  }

  public BasicBukkitCommandGraph getGraph() {
    return graph;
  }
}

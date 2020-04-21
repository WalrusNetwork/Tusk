package network.walrus.games.uhc;

import app.ashcon.intake.bukkit.BukkitIntake;
import app.ashcon.intake.bukkit.graph.BasicBukkitCommandGraph;
import com.google.common.collect.Sets;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;
import network.walrus.games.core.GamesPlugin;
import network.walrus.games.core.external.ExternalComponent;
import network.walrus.games.core.facets.group.GroupsManager;
import network.walrus.games.core.facets.group.ScoreboardHandler;
import network.walrus.games.core.util.EventUtil;
import network.walrus.games.uhc.commands.ConfigCommands;
import network.walrus.games.uhc.commands.GameInformationCommands;
import network.walrus.games.uhc.commands.HostManagementCommands;
import network.walrus.games.uhc.commands.ModerationCommands;
import network.walrus.games.uhc.commands.PlayerInformationCommands;
import network.walrus.games.uhc.commands.PlayerModificationCommands;
import network.walrus.games.uhc.commands.ScenarioCommands;
import network.walrus.games.uhc.commands.ScenarioCommands.ManagementCommands;
import network.walrus.games.uhc.commands.UHCCommand;
import network.walrus.games.uhc.commands.UHCCommandModule;
import network.walrus.games.uhc.config.UHCConfig;
import network.walrus.games.uhc.facets.border.BorderConfigurator;
import network.walrus.games.uhc.facets.chat.UHCChatConfigurator;
import network.walrus.games.uhc.facets.combatlog.CombatLogConfigurator;
import network.walrus.games.uhc.facets.crafting.UHCDisableCraftingConfigurator;
import network.walrus.games.uhc.facets.deathlightning.DeathLightningConfigurator;
import network.walrus.games.uhc.facets.delay.DelayConfigurator;
import network.walrus.games.uhc.facets.endgame.EndGameConfigurator;
import network.walrus.games.uhc.facets.enforcers.PortalEnforcer;
import network.walrus.games.uhc.facets.entity.EntititesConfigurator;
import network.walrus.games.uhc.facets.goldenhead.GoldenHeadConfigurator;
import network.walrus.games.uhc.facets.groups.GroupsConfigurator;
import network.walrus.games.uhc.facets.groups.UHCGroupsManager;
import network.walrus.games.uhc.facets.healthindicator.HealthIndicatorConfigurator;
import network.walrus.games.uhc.facets.potions.PotionsConfigurator;
import network.walrus.games.uhc.facets.redditbans.RedditBansConfigurator;
import network.walrus.games.uhc.facets.revive.ReviveConfigurator;
import network.walrus.games.uhc.facets.scatter.ScatterCommands.Configurator;
import network.walrus.games.uhc.facets.tpall.TpAllConfigurator;
import network.walrus.games.uhc.facets.visuals.VisualsConfigurator;
import network.walrus.games.uhc.facets.whitelist.WhitelistConfigurator;
import network.walrus.games.uhc.facets.xray.XRayConfigurator;
import network.walrus.games.uhc.listeners.UHCListener;
import network.walrus.games.uhc.populators.OnlyCavesOrePopulator;
import network.walrus.games.uhc.scenarios.ScenarioManager;
import network.walrus.games.uhc.spawn.SpawnManager;
import network.walrus.ubiquitous.bukkit.listeners.BlockChangeListener;
import network.walrus.ubiquitous.bukkit.lobby.facets.sterile.LobbySterilizationConfigurator;
import network.walrus.utils.bukkit.WalrusBukkitPlugin;
import network.walrus.utils.bukkit.logging.ChatLogHandler;
import network.walrus.utils.bukkit.parse.BukkitParserRegistry;
import network.walrus.utils.core.color.NetworkColorConstants.Games.UHC.Hosts;
import network.walrus.utils.core.stage.Stage;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import network.walrus.utils.parsing.facet.parse.DocumentParser;
import network.walrus.utils.parsing.facet.parse.SimpleHolderParser;
import network.walrus.utils.parsing.facet.parse.configurator.ConfiguratorManager;
import network.walrus.utils.parsing.facet.parse.configurator.FacetConfigurator;
import network.walrus.utils.parsing.lobby.Lobby;
import network.walrus.utils.parsing.lobby.LobbyLoader;
import network.walrus.utils.parsing.lobby.LobbyWorldParser;
import network.walrus.utils.parsing.lobby.facets.spawns.LobbySpawnsConfigurator;
import network.walrus.utils.parsing.world.NullChunkGenerator;
import network.walrus.utils.parsing.world.config.FacetConfigurationSource;
import network.walrus.utils.parsing.world.library.WorldLibrary;
import network.walrus.utils.parsing.world.library.single.SingleLibrary;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 * Main class for all of the UHC game code.
 *
 * @author Austin Mayes
 */
public class UHCManager extends ExternalComponent {

  public static final String GAME_NAME = "Walrus UHC Beta";
  public static final Set<UUID> HOSTS = Sets.newHashSet();
  public static final Set<UUID> SPECS = Sets.newHashSet();

  public static UHCManager instance;
  private final ScenarioManager scenarioManager;
  private final SpawnManager spawnManager = new SpawnManager();
  private UHCConfig config;
  private Logger hostLogger;
  private LobbyLoader lobbyLoader;
  private Lobby lobby;
  private FacetConfigurationSource source;
  private UHCRound uhc;
  private World overWorld;
  private UHCWorld world;

  /** @param plugin which owns the facet */
  public UHCManager(GamesPlugin plugin) {
    super(plugin);
    instance = this;
    this.scenarioManager = new ScenarioManager();
    bindConfigurators();
    BlockChangeListener.TRACK_NATURAL_EVENTS = false;
  }

  private void bindConfigurators() {
    ConfiguratorManager manager = this.getPlugin().getConfiguratorManager();
    // Lobby
    manager.addConfigurator(new LobbySpawnsConfigurator());
    manager.addConfigurator(new LobbySterilizationConfigurator());
    // UHC
    manager.addConfigurator(new GroupsConfigurator());
    manager.addConfigurator(new XRayConfigurator());
    manager.addConfigurator(new BorderConfigurator());
    manager.addConfigurator(new EntititesConfigurator());
    manager.addConfigurator(new WhitelistConfigurator());
    manager.addConfigurator(new CombatLogConfigurator());
    manager.addConfigurator(new EndGameConfigurator());
    manager.addConfigurator(new Configurator());
    manager.addConfigurator(new VisualsConfigurator());
    manager.addConfigurator(new UHCChatConfigurator());
    manager.addConfigurator(new PotionsConfigurator());
    manager.addConfigurator(new DelayConfigurator());
    manager.addConfigurator(new GoldenHeadConfigurator());
    manager.addConfigurator(new HealthIndicatorConfigurator());
    manager.addConfigurator(new RedditBansConfigurator());
    manager.addConfigurator(new DeathLightningConfigurator());
    manager.addConfigurator(new TpAllConfigurator());
    manager.addConfigurator(new ReviveConfigurator());
    manager.addConfigurator(new UHCDisableCraftingConfigurator());
    manager.addConfigurator(
        new FacetConfigurator() {
          @Override
          public void configure() {
            bindFacetListener(UHCListener.class, GroupsManager.class);
            bindConstantCommands(GameInformationCommands.class);
          }
        });
  }

  @Override
  public void onEnable() {
    config = new UHCConfig();
    loadOverworld();
    FacetHolder.COMMAND_MODULE = new UHCCommandModule();
    registerCommands();
    registerListeners();

    loadLobby();

    // Load messages file
    UHCMessages.PREFIX.with();

    /*PersonalizedPlayer.PREFIX_GENERATOR =
    (i) -> {
      if (i.target() instanceof Player) {
        if (HOSTS.contains(((Player) i.target()).getUniqueId())) {
          return new TextComponent(ChatColor.DARK_RED + "[Host] ");
        } else if (SPECS.contains(((Player) i.target()).getUniqueId())) {
          return new TextComponent(ChatColor.RED + "[Spec] ");
        }
      }
      return new TextComponent("");
    };*/
  }

  private void loadLobby() {
    LobbyWorldParser worldParser =
        new LobbyWorldParser(
            getPlugin().mapLogger(),
            WalrusBukkitPlugin.getStage(),
            BukkitParserRegistry.versionParser(),
            BukkitParserRegistry.ofEnum(Stage.class));
    lobbyLoader = new LobbyLoader(worldParser, getPlugin().mapLogger());

    lobby = lobbyLoader.load(getPlugin().mapLogger(), getPlugin());
  }

  /** Create the UHC based on the {@link UHCConfig}. */
  public void createUHC() {
    Optional<World> nether =
        this.config.nether.get() ? Optional.of(Bukkit.getWorld("world_nether")) : Optional.empty();
    Optional<World> end =
        this.config.end.get() ? Optional.of(Bukkit.getWorld("world_the_end")) : Optional.empty();
    world = new UHCWorld(this.overWorld, nether, end);
    world.actOnAllWorlds(w -> w.setGameRuleValue("naturalRegeneration", "false"));
    ScoreboardHandler.SHOW_HEALTH = true;
    SpawnManager.loadSpawn(
        Collections.singleton(new Location(world.mainWorld(), 0, 0, 0)),
        world.mainWorld(),
        10,
        () -> {
          uhc =
              new UHCRound(
                  GamesPlugin.instance, GamesPlugin.instance.mapLogger(), this.source, world);
          DocumentParser<UHCRound> parser =
              new DocumentParser<>(
                  uhc, new SimpleHolderParser<>(), GamesPlugin.instance.mapLogger());
          parser.parse();
          uhc.loadFacets();
          UHCGroupsManager manager = uhc.getFacetRequired(UHCGroupsManager.class);
          for (Player p : Bukkit.getOnlinePlayers()) {
            manager.changeGroup(p, Optional.empty(), manager.getSpectators(), false, false);
          }
          getPlugin().loaded.set(true);
        });
  }

  private void loadOverworld() {
    WorldCreator creator = new WorldCreator("uhc/over-world");
    creator.generator(new NullChunkGenerator());
    this.overWorld = Bukkit.createWorld(creator);
    OnlyCavesOrePopulator populator = new OnlyCavesOrePopulator(overWorld);
    Bukkit.getPluginManager().registerEvents(populator, getPlugin());
    config.initialBorder.set(getWorldSize(this.overWorld));
  }

  private int getWorldSize(World world) {
    int size = 0;
    while (world.isChunkGenerated((size + 500) / 16, (size + 500) / 16)) {
      size += 500;
    }

    return size;
  }

  @Override
  public void onDisable() {
    lobby.disableFacets();
    lobby.unloadFacets();
    try {
      FileUtils.deleteDirectory(new File(Bukkit.getWorldContainer(), "uhc"));
      FileUtils.deleteDirectory(new File(Bukkit.getWorldContainer(), "world_nether"));
      FileUtils.deleteDirectory(new File(Bukkit.getWorldContainer(), "world_the_end"));
      lobbyLoader.clearUserData();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void loadConfig(ConfigurationSection section) {
    File file = new File(section.getString("config-location"));
    new WorldSelector(new File(section.getString("worlds-root")))
        .selectWorld("2500", false, false); // TODO: Pull prefix from API
    WorldLibrary<UHCWorldSource> library = new SingleLibrary<UHCWorldSource>(file, "uhc");
    library.build(
        new UHCWorldFactory(GamesPlugin.instance.mapLogger()), GamesPlugin.instance.mapLogger());
    this.source = library.getSources().get(0);
  }

  public FacetConfigurationSource getSource() {
    return source;
  }

  public UHCRound getUHC() {
    return uhc;
  }

  /** @return logger which should be used to send host alerts to */
  public Logger hostLogger() {
    if (hostLogger != null) {
      return hostLogger;
    }

    hostLogger = Logger.getLogger("hosting-alerts");
    hostLogger.setUseParentHandlers(false);
    hostLogger.addHandler(new ChatLogHandler(Hosts.PREFIX, "Hosts", UHCPermissions.HOST_ALERTS));
    return hostLogger;
  }

  private void registerCommands() {
    BasicBukkitCommandGraph graph = new BasicBukkitCommandGraph(new UHCCommandModule());
    BukkitIntake intake = new BukkitIntake(getPlugin(), graph);
    graph.getRootDispatcherNode().registerCommands(new HostManagementCommands());
    graph.getRootDispatcherNode().registerCommands(new PlayerInformationCommands());
    graph.getRootDispatcherNode().registerCommands(new PlayerModificationCommands());
    graph.getRootDispatcherNode().registerCommands(new ScenarioCommands());
    graph.getRootDispatcherNode().registerCommands(new ModerationCommands());
    graph
        .getRootDispatcherNode()
        .registerNode("sc", "scenario")
        .registerCommands(new ManagementCommands(scenarioManager));
    ConfigCommands configCommands = new ConfigCommands(config);
    graph
        .getRootDispatcherNode()
        .registerNode("config", "uhcconfig")
        .registerCommands(configCommands);
    graph.getRootDispatcherNode().registerCommands(new UHCCommand(configCommands));
    intake.register();
  }

  private void registerListeners() {
    EventUtil.register(new PortalEnforcer());
    EventUtil.register(config);
  }

  public ScenarioManager getScenarioManager() {
    return scenarioManager;
  }

  public UHCConfig getConfig() {
    return config;
  }

  public SpawnManager getSpawnManager() {
    return spawnManager;
  }

  public LobbyLoader getLobbyLoader() {
    return lobbyLoader;
  }
}

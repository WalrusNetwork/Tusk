package network.walrus.games.octc;

import app.ashcon.intake.bukkit.BukkitIntake;
import app.ashcon.intake.bukkit.graph.BasicBukkitCommandGraph;
import co.aikar.timings.Timing;
import co.aikar.timings.Timings;
import com.google.common.collect.Maps;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import network.walrus.games.core.GamesPlugin;
import network.walrus.games.core.api.game.Game;
import network.walrus.games.core.api.round.RoundFactory;
import network.walrus.games.core.external.ExternalComponent;
import network.walrus.games.core.facets.block36.Block36Configurator;
import network.walrus.games.core.facets.chat.ChatConfigurator;
import network.walrus.games.core.facets.crafting.DisableCraftingConfigurator;
import network.walrus.games.core.facets.visual.VisualsConfigurator;
import network.walrus.games.core.map.ConfiguredWorldManagerImpl;
import network.walrus.games.core.map.MapParser;
import network.walrus.games.core.round.RoundManager;
import network.walrus.games.core.round.states.AutoStartingCountdown;
import network.walrus.games.core.round.states.RoundState;
import network.walrus.games.core.util.GameTask;
import network.walrus.games.octc.commands.MapCommands;
import network.walrus.games.octc.commands.OCNCommandModule;
import network.walrus.games.octc.ctf.CTFGame;
import network.walrus.games.octc.ctf.flags.FlagsConfigurator;
import network.walrus.games.octc.ctw.CTWGame;
import network.walrus.games.octc.ctw.wools.WoolsConfigurator;
import network.walrus.games.octc.destroyables.MixedDestroyableGame;
import network.walrus.games.octc.destroyables.dtc.DTCGame;
import network.walrus.games.octc.destroyables.dtm.DTMGame;
import network.walrus.games.octc.destroyables.objectives.DestroyablesConfigurator;
import network.walrus.games.octc.global.groups.OCNGroupsConfigurator;
import network.walrus.games.octc.global.spawns.SpawnConfigurator;
import network.walrus.games.octc.global.stats.OCNStatsConfigurator;
import network.walrus.games.octc.global.world.WorldConfigurator;
import network.walrus.games.octc.hills.domination.DomConfigurator;
import network.walrus.games.octc.hills.domination.DomGame;
import network.walrus.games.octc.hills.koth.KothConfigurator;
import network.walrus.games.octc.hills.koth.KothGame;
import network.walrus.games.octc.rotations.MapSelectionMode;
import network.walrus.games.octc.rotations.MapSelector;
import network.walrus.games.octc.score.ScoreConfigurator;
import network.walrus.games.octc.tdm.TDMConfigurator;
import network.walrus.games.octc.tdm.TDMGame;
import network.walrus.ubiquitous.bukkit.UbiquitousBukkitPlugin;
import network.walrus.utils.core.color.NetworkColorConstants.Games;
import network.walrus.utils.core.config.ParsingException;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import network.walrus.utils.parsing.facet.parse.DocumentParser;
import network.walrus.utils.parsing.facet.parse.SimpleHolderParser;
import network.walrus.utils.parsing.world.config.ConfiguredWorldManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Main class for all of the oc.tc type game code.
 *
 * @author Austin Mayes
 */
public class OCNGameManager extends ExternalComponent {

  public static OCNGameManager instance = null;

  private final ConfiguredWorldManager<OCNMap> mapManager;
  private final MapParser<OCNMap> mapParser;
  private final Map<String, Game> gamesById;
  private ConfigurationSection config;
  private Match current;
  private MapSelector mapSelector;
  private BasicBukkitCommandGraph graph;

  private OCNGameManager(GamesPlugin plugin) {
    super(plugin);
    instance = this;
    RoundFactory<Match> matchFactory = map -> new Match(map, plugin.mapLogger());

    this.gamesById = Maps.newHashMap();
    // Capture
    gamesById.put("ctw", new CTWGame(matchFactory));
    gamesById.put("ctf", new CTFGame(matchFactory));
    // Team Death Match
    gamesById.put("tdm", new TDMGame(matchFactory));
    // Destroy
    gamesById.put("dtc", new DTCGame(matchFactory));
    gamesById.put("dtm", new DTMGame(matchFactory));
    gamesById.put("dtcm", new MixedDestroyableGame(matchFactory));
    // KOTh
    gamesById.put("dom", new DomGame(matchFactory));
    gamesById.put("koth", new KothGame(matchFactory));

    this.mapParser = new OCNParser(GamesPlugin.instance.mapLogger());
    this.mapManager = new ConfiguredWorldManagerImpl<>(mapParser, GamesPlugin.instance.mapLogger());
    registerConfigurators();

    // Load messages file
    OCNMessages.CORE_LEAKED.with();
  }

  @Override
  public void onEnable() {
    this.mapParser.addParser(
        node ->
            Optional.ofNullable(
                gamesById.get(node.childRequired("game").text().asRequiredString().toLowerCase())));
    FacetHolder.COMMAND_MODULE = new OCNCommandModule();
    mapManager.loadLibraries((List<Map>) config.getList("maps.libraries"));

    graph = new BasicBukkitCommandGraph(new OCNCommandModule());
    BukkitIntake intake = new BukkitIntake(getPlugin(), graph);

    MapSelectionMode mode =
        MapSelectionMode.valueOf(
            config.getString("maps.selection-mode").toUpperCase().replace("-", "_"));
    mapSelector = new MapSelector(mode, mapManager);
    mapSelector.selectNextMap();

    graph.getRootDispatcherNode().registerCommands(new MapCommands(this));
    intake.register();
  }

  @Override
  public void onDisable() {}

  @Override
  public void loadConfig(ConfigurationSection section) {
    this.config = section;
  }

  private void registerConfigurators() {
    this.getPlugin().getConfiguratorManager().addConfigurator(new Block36Configurator());
    this.getPlugin().getConfiguratorManager().addConfigurator(new OCNGroupsConfigurator());
    this.getPlugin().getConfiguratorManager().addConfigurator(new SpawnConfigurator());
    this.getPlugin().getConfiguratorManager().addConfigurator(new VisualsConfigurator());
    this.getPlugin().getConfiguratorManager().addConfigurator(new WorldConfigurator());
    this.getPlugin().getConfiguratorManager().addConfigurator(new ChatConfigurator(true, true));
    this.getPlugin().getConfiguratorManager().addConfigurator(new DisableCraftingConfigurator());
    this.getPlugin().getConfiguratorManager().addConfigurator(new OCNStatsConfigurator());

    this.getPlugin().getConfiguratorManager().addConfigurator(new ScoreConfigurator());

    // CTF
    this.getPlugin().getConfiguratorManager().addConfigurator(new FlagsConfigurator());

    // CTW
    this.getPlugin().getConfiguratorManager().addConfigurator(new WoolsConfigurator());

    // DTC/M
    this.getPlugin().getConfiguratorManager().addConfigurator(new DestroyablesConfigurator());

    // TDM
    this.getPlugin().getConfiguratorManager().addConfigurator(new TDMConfigurator());

    // Hills
    this.getPlugin().getConfiguratorManager().addConfigurator(new KothConfigurator());
    this.getPlugin().getConfiguratorManager().addConfigurator(new DomConfigurator());
  }

  public void setMap(OCNMap map) {
    if (map == null) {
      return;
    }

    Match match = (Match) map.game().constructRound(map);
    DocumentParser<Match> parser =
        new DocumentParser<Match>(
            match, new SimpleHolderParser<Match>(), GamesPlugin.instance.mapLogger());
    RoundManager<Match> manager =
        new RoundManager<Match>(match, parser, GamesPlugin.instance.mapLogger());

    boolean loaded = manager.load();

    if (!loaded) {
      return;
    }

    Match old = null;

    if (current != null) {
      UbiquitousBukkitPlugin.getInstance().getCountdownManager().cancelAll();
      current.setState(RoundState.FINISHED);
      try (Timing t =
          Timings.ofStart(GamesPlugin.instance, "Facet disable: " + current.map().name())) {
        current.disableFacets();
      }
      try (Timing t =
          Timings.ofStart(GamesPlugin.instance, "Round unload: " + current.map().name())) {
        current.unloadFacets();
      }
      old = current;
    }

    current = match;
    try (Timing t = Timings.ofStart(GamesPlugin.instance, "Facets load: " + current.map().name())) {
      try {
        match.loadFacets();
      } catch (ParsingException e) {
        String message = e.getMessage();
        if (e.getCause() != null) {
          message = message + ": " + e.getCause().getMessage();
        }
        GamesPlugin.instance.mapLogger().log(Level.SEVERE, message, e);
        return;
      }
    }

    if (old != null) {
      GameTask.of("World unload: " + old.map().name(), old::unloadWorld).later(20 * 10);
    } else {
      GameTask.of("Main world unload", () -> Bukkit.unloadWorld("world", false)).later(20 * 5);
    }

    Bukkit.broadcastMessage(
        Games.Maps.LOADED.apply("Loaded " + map.mapInfo().getName() + "!").toLegacyText());
    UbiquitousBukkitPlugin.getInstance()
        .getCountdownManager()
        .start(new AutoStartingCountdown(Duration.ofSeconds(20), match));
    GamesPlugin.instance.loaded.set(true);
  }

  public Match getCurrentMatch() {
    return current;
  }

  public MapParser<OCNMap> getMapParser() {
    return mapParser;
  }

  public ConfiguredWorldManager getMapManager() {
    return mapManager;
  }

  public ConfigurationSection getConfig() {
    return config;
  }

  public MapSelector getMapSelector() {
    return mapSelector;
  }

  /** Selects the next map using the selected strategy. */
  public void selectNextMap() {
    mapSelector.selectNextMap();
  }

  public void registerCommand(Object object) {
    graph.getRootDispatcherNode().registerCommands(object);
  }
}

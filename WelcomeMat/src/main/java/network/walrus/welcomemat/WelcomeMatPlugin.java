package network.walrus.welcomemat;

import java.io.IOException;
import java.util.logging.Logger;
import network.walrus.ubiquitous.bukkit.lobby.facets.sterile.LobbySterilizationConfigurator;
import network.walrus.utils.bukkit.WalrusBukkitPlugin;
import network.walrus.utils.bukkit.logging.ChatLogHandler;
import network.walrus.utils.bukkit.parse.BukkitParserRegistry;
import network.walrus.utils.core.color.NetworkColorConstants;
import network.walrus.utils.core.stage.Stage;
import network.walrus.utils.parsing.facet.facets.region.RegionsConfigurator;
import network.walrus.utils.parsing.facet.parse.configurator.ConfiguratorManager;
import network.walrus.utils.parsing.facet.parse.configurator.FacetConfigurator;
import network.walrus.utils.parsing.lobby.Lobby;
import network.walrus.utils.parsing.lobby.LobbyLoader;
import network.walrus.utils.parsing.lobby.LobbyWorldParser;
import network.walrus.utils.parsing.lobby.facets.spawns.LobbySpawnsConfigurator;
import network.walrus.welcomemat.facets.spawn.SpawnConfigurator;
import org.bukkit.Bukkit;

/**
 * Lobby plugin main class. .\
 *
 * @author Austin Mayes
 */
public class WelcomeMatPlugin extends WalrusBukkitPlugin {

  private static WelcomeMatPlugin instance;
  private final ConfiguratorManager configuratorManager = new ConfiguratorManager();
  private Logger mapLogger;
  private LobbyLoader lobbyLoader;
  private Lobby lobby;

  public static WelcomeMatPlugin getInstance() {
    return instance;
  }

  @Override
  public void load() {
    instance = this;
    registerConfigurators();
  }

  @Override
  public void enable() {
    this.configuratorManager.actOnAll(FacetConfigurator::configure);
    LobbyWorldParser worldParser =
        new LobbyWorldParser(
            getLogger(),
            getStage(),
            BukkitParserRegistry.versionParser(),
            BukkitParserRegistry.ofEnum(Stage.class));
    lobbyLoader = new LobbyLoader(worldParser, mapLogger());
    addConsolePerms();
    registerConfigurators();
    this.configuratorManager.actOnAll(FacetConfigurator::configure);
    this.lobby = lobbyLoader.load(mapLogger, this);
  }

  @Override
  public void disable() {
    lobby.disableFacets();
    lobby.unloadFacets();
    try {
      lobbyLoader.clearUserData();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void registerConfigurators() {
    this.configuratorManager.addConfigurator(new RegionsConfigurator());
    this.configuratorManager.addConfigurator(new LobbySpawnsConfigurator());
    this.configuratorManager.addConfigurator(new LobbySterilizationConfigurator());
    this.configuratorManager.addConfigurator(new SpawnConfigurator());
  }

  private void addConsolePerms() {
    Bukkit.getConsoleSender().addAttachment(this, WelcomeMatPermissions.VIEW_LOBBY_ERRORS, true);
  }

  /** @return logger which should be used to send configuration errors to */
  public Logger mapLogger() {
    if (mapLogger != null) {
      return mapLogger;
    }

    mapLogger = Logger.getLogger("lobby-errors");
    mapLogger.setUseParentHandlers(false);
    mapLogger.addHandler(
        new ChatLogHandler(
            NetworkColorConstants.Lobby.ERROR_LOGGER_PREFIX,
            "Lobby",
            WelcomeMatPermissions.VIEW_LOBBY_ERRORS));
    return mapLogger;
  }

  /** @return lobby loader for the world */
  public LobbyLoader getLobbyLoader() {
    return lobbyLoader;
  }
}

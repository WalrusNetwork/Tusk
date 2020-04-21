package network.walrus.utils.parsing.lobby;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import network.walrus.utils.core.config.ParsingException;
import network.walrus.utils.parsing.facet.Facet;
import network.walrus.utils.parsing.facet.parse.DocumentParser;
import network.walrus.utils.parsing.facet.parse.GlobalParser;
import network.walrus.utils.parsing.facet.parse.SimpleHolderParser;
import network.walrus.utils.parsing.world.PlayerContainer;
import network.walrus.utils.parsing.world.WorldProvider;
import network.walrus.utils.parsing.world.library.WorldLibrary;
import network.walrus.utils.parsing.world.library.single.SingleLibrary;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

/**
 * Simple class which handles end-to-end loading of {@link LobbyWorld}s and {@link Lobby lobbies}.
 *
 * @author Austin Mayes
 */
public class LobbyLoader {

  private final GlobalParser<LobbyWorld> lobbyWorldParser;
  private final Logger logger;
  private LobbyWorld lobbyWorld;
  private Lobby lobby;

  /**
   * Constructor.
   *
   * @param lobbyWorldParser used to parse lobby worlds
   * @param logger to log configuration errors to
   */
  public LobbyLoader(GlobalParser<LobbyWorld> lobbyWorldParser, Logger logger) {
    this.lobbyWorldParser = lobbyWorldParser;
    this.logger = logger;
  }

  private boolean loadSource() {
    WorldLibrary<LobbyWorld> library =
        new SingleLibrary<>(Bukkit.getWorld("world").getWorldFolder(), "config");
    library.build(lobbyWorldParser, logger);
    lobbyWorld = library.getSources().get(0);
    return lobbyWorld != null;
  }

  private boolean loadLobby(Logger mapLogger, Plugin plugin) {
    lobby =
        new Lobby(
            plugin,
            mapLogger,
            lobbyWorld,
            new WorldProvider<PlayerContainer>() {
              @Override
              public String worldName() {
                return "world";
              }

              @Override
              public void copyWorld(File path) throws IOException {} // Don't copy

              @Override
              public void postLoad(PlayerContainer world) {}

              @Override
              public File source() {
                return lobbyWorld.source().source();
              }

              @Override
              public PlayerContainer load() {
                try {
                  PlayerContainer wrapped = wrap(Bukkit.getWorld("world"));
                  postLoad(wrapped);
                  return wrapped;
                } catch (Exception e) {
                  failed(e);
                  return null;
                }
              }
            });
    return true;
  }

  private void enable(DocumentParser<Lobby> parser) {
    try {
      parser.parse();
    } catch (ParsingException e) {
      this.logger.log(
          Level.SEVERE, "Failed to parse configuration for lobby" + ": " + e.getMessage());
      if (e.getCause() != null) {
        e.getCause().printStackTrace();
      }
      Bukkit.shutdown();
    }
    lobby.loadFacets();
    lobby.enableFacets();
  }

  /** Load the source, then the lobby, and then parse/enable all of the {@link Facet}s. */
  public Lobby load(Logger mapLogger, Plugin plugin) {
    if (loadSource()) {
      if (loadLobby(mapLogger, plugin)) {
        enable(new DocumentParser<>(this.lobby, new SimpleHolderParser<>(), mapLogger));
        return this.lobby;
      }
    }
    return null;
  }

  public Lobby getLobby() {
    return lobby;
  }

  /**
   * Clear all non-essential world data from the lobby source.
   *
   * @throws IOException if a file fails to be deleted
   */
  public void clearUserData() throws IOException {
    for (File file : Bukkit.getWorld("world").getWorldFolder().listFiles()) {
      String name = file.getName();
      if (name.equals("region") || name.equals("level.dat") || name.contains("config")) {
        continue;
      }

      if (file.isDirectory()) {
        FileUtils.deleteDirectory(file);
      } else {
        file.delete();
      }
    }
  }
}

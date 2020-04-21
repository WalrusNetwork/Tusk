package network.walrus.games.core.map;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import network.walrus.games.core.GamesCoreMessages;
import network.walrus.games.core.api.map.GameMap;
import network.walrus.games.core.map.library.LocalWorldLibrary;
import network.walrus.utils.core.color.NetworkColorConstants.Games;
import network.walrus.utils.core.translation.Localizable;
import network.walrus.utils.parsing.world.Sourced;
import network.walrus.utils.parsing.world.config.ConfiguredWorldManager;
import network.walrus.utils.parsing.world.library.WorldLibrary;
import org.bukkit.Bukkit;

/**
 * Manages a collection of maps parsed from multiple {@link WorldLibrary libraries}.
 *
 * @param <M> type of object created when the maps are parsed.
 * @author Austin Mayes
 */
public class ConfiguredWorldManagerImpl<M extends GameMap> implements ConfiguredWorldManager<M> {

  private final List<WorldLibrary<M>> libraries = new ArrayList<>();
  private final MapParser<M> parser;
  private final Logger logger;

  /**
   * Constructor.
   *
   * @param parser used for parsing each map
   * @param logger to log parsing information to
   */
  public ConfiguredWorldManagerImpl(MapParser<M> parser, Logger logger) {
    this.parser = parser;
    this.logger = logger;
  }

  @Override
  public List<WorldLibrary<M>> getLibraries() {
    return this.libraries;
  }

  @Override
  public List<Sourced> getSources() {
    List<Sourced> list = new ArrayList<>();
    for (WorldLibrary<M> l : this.libraries) {
      list.addAll(l.getSources());
    }
    return list;
  }

  @Override
  public Optional<M> search(String name) {
    return WorldLibrary.search(name, this.libraries);
  }

  @Override
  public void loadLibraries(List<Map> libraries) {
    for (Map section : libraries) {
      String path = (String) section.get("path");
      logger.info("Loading maps from " + path + "...");
      List<String> ignored = new ArrayList<>();
      if (section.get("ignored-directories") != null) {
        ignored.addAll((List<String>) section.get("ignored-directories"));
      }

      WorldLibrary<M> library;

      library = new LocalWorldLibrary<M>(new File(path), ignored);

      if (!library.exists()) {
        Localizable message = GamesCoreMessages.ERROR_NO_LIBRARY.with(library.getName());
        message.style().inherit(Games.Maps.ERROR);
        Bukkit.getConsoleSender().sendMessage(message);
        continue;
      }

      library.build(this.parser, this.logger);
      this.libraries.add(library);
    }
  }
}

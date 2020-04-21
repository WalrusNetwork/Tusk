package network.walrus.games.core.map.library;

import co.aikar.timings.Timing;
import co.aikar.timings.Timings;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import network.walrus.games.core.GamesPlugin;
import network.walrus.games.core.api.map.GameMap;
import network.walrus.utils.parsing.facet.parse.GlobalParser;
import network.walrus.utils.parsing.world.library.WorldLibrary;
import network.walrus.utils.parsing.world.library.WorldSource;

/**
 * A library of maps that is stored on disk. We does not need write access to the directory.
 *
 * @param <M> map type which this library holds
 * @author Avicus Network
 */
public class LocalWorldLibrary<M extends GameMap> implements WorldLibrary<M> {

  /** Root directory of the library. */
  private final File root;
  /** Ignored directory names within this library. */
  private final List<String> ignoredDirectories;
  /** Sources parsed from the root folder's contents. */
  private final List<M> maps;

  /**
   * Constructor.
   *
   * @param root root directory of the library
   * @param ignoredDirectories ignored directory names within this library
   */
  public LocalWorldLibrary(File root, List<String> ignoredDirectories) {
    this.root = root;
    this.ignoredDirectories = ignoredDirectories;
    this.maps = new ArrayList<>();
  }

  @Override
  public String getName() {
    return this.root.getAbsolutePath();
  }

  @Override
  public boolean exists() {
    return this.root.exists() && this.root.isDirectory() && this.root.canRead();
  }

  @Override
  public void build(GlobalParser<M> parser, Logger logger, Map<String, Exception> loadErrors) {
    this.maps.clear();

    // recursive method
    try (Timing timing = Timings.ofStart(GamesPlugin.instance, "Library build: " + getName())) {
      build(this.root, parser, loadErrors);
    }
    logger.info(
        "Loaded "
            + getSources().size()
            + " map"
            + (getSources().size() == 1 ? "" : "s")
            + " from "
            + getName());
  }

  @Override
  public InputStream getFileStream(String path) throws FileNotFoundException {
    return new FileInputStream(getFile(path));
  }

  @Override
  public File getFile(String path) throws FileNotFoundException {
    return new File(this.root, path);
  }

  /**
   * Recursively parse sources in a directory.
   *
   * @param directory directory that contains the items for parsing
   */
  private void build(File directory, GlobalParser<M> parser, Map<String, Exception> loadErrors) {
    File[] list = directory.listFiles();
    if (list == null) {
      return;
    }

    for (int i = 0, listLength = list.length; i < listLength; i++) {
      File file = list[i];
      if (file.isDirectory() && !this.ignoredDirectories.contains(file.getName())) {
        build(file, parser, loadErrors);
      } else {
        if (file.getName().equals("map.xml")) {
          try {
            final WorldSource source = new LocalWorldSource(this, file.getParentFile(), file);
            final M map = parser.parse(source);
            if (!map.usable()) {
              continue;
            }
            this.maps.add(map);
          } catch (Exception e) {
            loadErrors.put(file.getPath(), e);
          }
        }
      }
    }
  }

  public File getRoot() {
    return root;
  }

  public List<String> getIgnoredDirectories() {
    return ignoredDirectories;
  }

  @Override
  public List<M> getSources() {
    return maps;
  }
}

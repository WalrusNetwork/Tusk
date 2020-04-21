package network.walrus.utils.parsing.world.library;

import com.google.common.collect.Maps;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.logging.Logger;
import network.walrus.utils.core.config.ParsingException;
import network.walrus.utils.parsing.facet.parse.GlobalParser;
import network.walrus.utils.parsing.world.config.ConfigurationParseException;
import network.walrus.utils.parsing.world.config.FacetConfigurationSource;

/**
 * A world library is a collection of {@link WorldSource}s within a root location. The plugin can
 * pull from multiple libraries at once, but identical configuration sources from different sources
 * are not allowed.
 *
 * @param <S> type of source that this library contains
 * @author Avicus Network
 */
public interface WorldLibrary<S extends FacetConfigurationSource> {

  /**
   * Search for a world source by name in a list of libraries.
   *
   * @param name name of the source to search for
   * @param libraries libraries to search inside of
   * @return a world source matching the name
   */
  static <M extends FacetConfigurationSource> Optional<M> search(
      String name, List<WorldLibrary<M>> libraries) {
    for (WorldLibrary library : libraries) {
      Optional<M> result = library.search(name);
      if (result.isPresent()) {
        return result;
      }
    }
    return Optional.empty();
  }

  /**
   * Name of the library.
   *
   * @return name of the library
   */
  String getName();

  /**
   * World sources that are inside of this library.
   *
   * @return sources that are inside of this library
   */
  List<S> getSources();

  /**
   * Get a file from inside of the library.
   *
   * @param path path of the file to be retrieved
   * @return a file from inside of the library
   * @throws FileNotFoundException if the file cannot be found
   */
  InputStream getFileStream(String path) throws FileNotFoundException;

  /**
   * Get a file from inside of the library.
   *
   * @param path path of the file to be retrieved
   * @return a file from inside of the library
   * @throws FileNotFoundException if the file cannot be found
   */
  File getFile(String path) throws FileNotFoundException;

  /**
   * If the library's root location exists and can be accessed.
   *
   * @return if the library's root location exists and can be accessed
   */
  boolean exists();

  default void build(GlobalParser<S> parser, Logger logger) {
    Map<String, Exception> errors = Maps.newHashMap();
    build(parser, logger, errors);
    for (Entry<String, Exception> entry : errors.entrySet()) {
      Exception e = entry.getValue();
      if (e instanceof ConfigurationParseException || e instanceof ParsingException) {
        if (e.getCause() != null) {
          parser
              .logger()
              .warning(
                  "Failed to read map config: " + entry.getKey() + " " + e.getCause().getMessage());
        } else {
          parser
              .logger()
              .warning("Failed to read map config: " + entry.getKey() + " " + e.getMessage());
        }
        e.printStackTrace();
      } else {
        e.printStackTrace();
      }
    }
  }

  /** Load sources into the library and perform any needed setup. */
  void build(GlobalParser<S> parser, Logger logger, Map<String, Exception> loadErrors);

  /**
   * Search for a world source by name.
   *
   * @param name name of the source to search for
   * @return a source matching the name
   */
  default Optional<S> search(String name) {
    name = name.toLowerCase();
    S closest = null;
    for (S source : this.getSources()) {
      String check = source.name().toLowerCase();
      if (check.equals(name)) {
        return Optional.of(source);
      } else if (check.startsWith(name)) {
        closest = source;
      }
    }
    if (closest == null) {
      for (S source : this.getSources()) {
        String check = source.source().getName().toLowerCase();
        if (check.equals(name)) {
          return Optional.of(source);
        } else if (check.startsWith(name)) {
          closest = source;
        }
      }
    }
    return Optional.ofNullable(closest);
  }
}

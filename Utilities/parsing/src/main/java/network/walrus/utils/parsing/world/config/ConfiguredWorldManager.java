package network.walrus.utils.parsing.world.config;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import network.walrus.utils.parsing.world.Sourced;
import network.walrus.utils.parsing.world.library.WorldLibrary;

/**
 * Manages a collection of facet sources parsed from multiple {@link WorldLibrary libraries}.
 *
 * @param <S> type of object created when the sources are parsed.
 * @author Austin Mayes
 */
public interface ConfiguredWorldManager<S extends FacetConfigurationSource> {

  /** @return all of the libraries the manager can load data from */
  List<WorldLibrary<S>> getLibraries();

  /** @return all of the loaded sources */
  List<Sourced> getSources();

  /** @see WorldLibrary#search(String, List). */
  Optional<S> search(String name);

  /**
   * Load configurations from a list of libraries.
   *
   * @param libraries to load data from
   */
  void loadLibraries(List<Map> libraries);
}

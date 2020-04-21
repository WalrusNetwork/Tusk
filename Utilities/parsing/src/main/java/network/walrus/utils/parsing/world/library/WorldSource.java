package network.walrus.utils.parsing.world.library;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import network.walrus.utils.parsing.world.config.FacetConfigurationSource;

/**
 * A world source is the location that a {@link FacetConfigurationSource}'s world files and config
 * are stored.
 *
 * @author Avicus Network
 */
public interface WorldSource {

  /**
   * Library that this source is a part of.
   *
   * @return library that this source is a part of
   */
  WorldLibrary getLibrary();

  /**
   * Name of this source.
   *
   * @return name of this source
   */
  String getName();

  /**
   * Get the config that should be used for parsing with this source's world files.
   *
   * @return the config that should be used for parsing with this source's world files
   * @throws Exception if the file cannot be found
   */
  InputStream getConfig() throws Exception;

  /**
   * Retrieve a file from this source.
   *
   * @param path path of the file
   * @return a file from this source
   * @throws FileNotFoundException if the file cannot be found
   */
  InputStream getFile(String path) throws FileNotFoundException;

  /** @return file representation of this source */
  File source();
}

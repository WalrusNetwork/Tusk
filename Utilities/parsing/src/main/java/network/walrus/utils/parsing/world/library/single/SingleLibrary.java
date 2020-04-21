package network.walrus.utils.parsing.world.library.single;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import network.walrus.utils.parsing.facet.parse.GlobalParser;
import network.walrus.utils.parsing.world.config.ConfigurationParseException;
import network.walrus.utils.parsing.world.config.FacetConfigurationSource;
import network.walrus.utils.parsing.world.library.WorldLibrary;
import network.walrus.utils.parsing.world.library.WorldSource;
import org.bukkit.Bukkit;

/**
 * A {@link WorldLibrary} which loads data from one source.
 *
 * @param <S> type of source that this library contains
 * @author Austin Mayes
 */
public class SingleLibrary<S extends FacetConfigurationSource> implements WorldLibrary<S> {

  private final File location;
  private final String configName;
  private S world;

  /**
   * @param location where the world and configuration are located
   * @param configName name of the config file
   */
  public SingleLibrary(File location, String configName) {
    this.location = location;
    this.configName = configName;
  }

  @Override
  public String getName() {
    return "world";
  }

  @Override
  public List<S> getSources() {
    return Collections.singletonList(world);
  }

  @Override
  public InputStream getFileStream(String path) throws FileNotFoundException {
    return new FileInputStream(getFile(path));
  }

  @Override
  public File getFile(String path) throws FileNotFoundException {
    return new File(this.location, path);
  }

  @Override
  public boolean exists() {
    return this.location.exists() && this.location.canRead();
  }

  @Override
  public void build(GlobalParser<S> parser, Logger logger, Map<String, Exception> loadErrors) {
    try {
      world =
          parser.parse(
              new WorldSource() {
                @Override
                public WorldLibrary getLibrary() {
                  return SingleLibrary.this;
                }

                @Override
                public String getName() {
                  return "single world source";
                }

                @Override
                public InputStream getConfig() throws Exception {
                  return new FileInputStream(xml(location));
                }

                @Override
                public InputStream getFile(String path) throws FileNotFoundException {
                  return SingleLibrary.this.getFileStream(path);
                }

                @Override
                public File source() {
                  return location;
                }
              });
      Bukkit.getLogger().info(world.versionInfo().getProto().toString());
    } catch (ConfigurationParseException e) {
      logger.log(Level.SEVERE, "Failed to parse configuration for world: " + e.getMessage());
      if (e.getCause() != null) {
        e.getCause().printStackTrace();
      }
      Bukkit.shutdown();
    }
  }

  /**
   * Get a {@code map.xml} from a directory
   *
   * @param root folder containing the XML
   * @return the XML
   * @throws Exception if the folder does not contain a {@code map.xml}
   */
  private File xml(File root) throws Exception {
    File[] files = root.listFiles();
    if (files != null) {
      for (File file : files) {
        if (file.getName().equals(this.configName + ".xml")) {
          return file;
        }
      }
    }
    throw new Exception("config not found");
  }
}

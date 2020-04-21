package network.walrus.games.core.map;

import java.util.List;
import java.util.Optional;
import network.walrus.games.core.api.map.GameMap;
import network.walrus.utils.core.stage.Stage;
import network.walrus.utils.core.versioning.Version;
import network.walrus.utils.core.versioning.VersionInfo;

/**
 * Information about a {@link GameMap} which should always be avaliable. This should be kept as
 * minimal as possible to save on memory.
 *
 * @author Austin Mayes
 */
public class MapInfo implements VersionInfo {

  private final String name;
  private final Version proto;
  private final Version version;
  private final Stage stage;
  private final Optional<String> objective;
  private final List<String> rules;

  /**
   * Constructor.
   *
   * @param name of the map
   * @param proto of the map
   * @param version of the map
   * @param stage of the map
   * @param objective of the map
   * @param rules that apply during the map
   */
  public MapInfo(
      String name,
      Version proto,
      Version version,
      Stage stage,
      Optional<String> objective,
      List<String> rules) {
    this.name = name;
    this.proto = proto;
    this.version = version;
    this.stage = stage;
    this.objective = objective;
    this.rules = rules;
  }

  public String getName() {
    return name;
  }

  @Override
  public Version getProto() {
    return proto;
  }

  @Override
  public Version getVersion() {
    return version;
  }

  public Optional<String> getObjective() {
    return objective;
  }

  public List<String> getRules() {
    return rules;
  }

  @Override
  public Stage stage() {
    return this.stage;
  }
}

package network.walrus.utils.parsing.lobby;

import network.walrus.utils.core.config.Node;
import network.walrus.utils.core.versioning.VersionInfo;
import network.walrus.utils.parsing.world.config.FacetConfigurationSource;
import network.walrus.utils.parsing.world.library.WorldSource;

/**
 * Base class created from the base lobby configuration which is used to create {@link Lobby
 * lobbies}. This is a bit redundant in the lobby environment, but it is needed by the upstream
 * parsing code in order for everything to function properly during the parsing chain.
 *
 * @author Austin Mayes
 */
public abstract class LobbyWorld implements FacetConfigurationSource {

  private final WorldSource source;
  private final Node parent;
  private final VersionInfo info;

  /**
   * Constructor.
   *
   * @param source containing files for the lobby
   * @param parent node of the configuration
   * @param info pulled from the lobby configuration
   */
  public LobbyWorld(WorldSource source, Node parent, VersionInfo info) {
    this.source = source;
    this.parent = parent;
    this.info = info;
  }

  @Override
  public Node parent() {
    return this.parent;
  }

  @Override
  public boolean playable() {
    return true;
  }

  @Override
  public VersionInfo versionInfo() {
    return this.info;
  }

  @Override
  public String name() {
    return "Lobby";
  }

  @Override
  public String slugify() {
    return "lobby";
  }

  @Override
  public WorldSource source() {
    return this.source;
  }
}

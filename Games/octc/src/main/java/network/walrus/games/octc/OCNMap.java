package network.walrus.games.octc;

import network.walrus.games.core.api.game.Game;
import network.walrus.games.core.api.map.GameMap;
import network.walrus.games.core.map.MapInfo;
import network.walrus.utils.core.config.Node;
import network.walrus.utils.core.versioning.VersionInfo;
import network.walrus.utils.parsing.world.library.WorldSource;

/**
 * A map for an OCN game.
 *
 * @author Austin Mayes
 */
public class OCNMap implements GameMap {

  private final WorldSource source;
  private final Node parent;
  private final MapInfo info;
  private Game game;

  /**
   * Constructor.
   *
   * @param source of the world and XML
   * @param parent node of the configuration document
   * @param info about the map
   */
  public OCNMap(WorldSource source, Node parent, MapInfo info) {
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
  public String name() {
    return info.getName();
  }

  @Override
  public String slugify() {
    return info.getName().toLowerCase().replace(" ", "-");
  }

  @Override
  public WorldSource source() {
    return this.source;
  }

  @Override
  public MapInfo mapInfo() {
    return this.info;
  }

  @Override
  public VersionInfo versionInfo() {
    return mapInfo();
  }

  @Override
  public void game(Game game) {
    this.game = game;
  }

  @Override
  public Game game() {
    return this.game;
  }
}

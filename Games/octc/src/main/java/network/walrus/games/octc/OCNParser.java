package network.walrus.games.octc;

import java.util.logging.Logger;
import network.walrus.games.core.map.MapParser;
import network.walrus.utils.bukkit.parse.BukkitParserRegistry;
import network.walrus.utils.core.config.Node;
import network.walrus.utils.core.stage.Stage;
import network.walrus.utils.parsing.world.config.ConfigurationParseException;
import network.walrus.utils.parsing.world.library.WorldSource;

/**
 * Parser for {@link OCNMap}s.
 *
 * @author Austin Mayes
 */
public class OCNParser extends MapParser<OCNMap> {

  /**
   * Constructor.
   *
   * @param logger to log parsing errors/info to.
   */
  public OCNParser(Logger logger) {
    super(logger, BukkitParserRegistry.versionParser(), BukkitParserRegistry.ofEnum(Stage.class));
  }

  @Override
  public OCNMap construct(WorldSource source) throws ConfigurationParseException {
    Node parent = createNode(source);
    return new OCNMap(source, parent, parseInfo(parent));
  }
}

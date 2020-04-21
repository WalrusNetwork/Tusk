package network.walrus.utils.parsing.lobby;

import java.util.logging.Logger;
import network.walrus.utils.core.config.Node;
import network.walrus.utils.core.parse.SimpleParser;
import network.walrus.utils.core.stage.Stage;
import network.walrus.utils.core.versioning.Version;
import network.walrus.utils.core.versioning.VersionInfo;
import network.walrus.utils.parsing.facet.parse.GlobalParser;
import network.walrus.utils.parsing.world.config.ConfigurationParseException;
import network.walrus.utils.parsing.world.library.WorldSource;

/**
 * Parser for {@link LobbyWorld}s.
 *
 * @author Austin Mayes
 */
public class LobbyWorldParser extends GlobalParser<LobbyWorld> {

  private final Stage stage;
  private final SimpleParser<Version> versionParser;
  private final SimpleParser<Stage> stageParser;

  /**
   * Constructor.
   *
   * @param logger used to log parsing errors and info to
   * @param versionParser used to parse proto and version
   * @param stageParser used to parse stage
   */
  public LobbyWorldParser(
      Logger logger,
      Stage stage,
      SimpleParser<Version> versionParser,
      SimpleParser<Stage> stageParser) {
    super(logger);
    this.stage = stage;
    this.versionParser = versionParser;
    this.stageParser = stageParser;
  }

  @Override
  public LobbyWorld parseInternal(LobbyWorld source) throws ConfigurationParseException {
    return source;
  }

  @Override
  public LobbyWorld construct(WorldSource source) throws ConfigurationParseException {
    Node node = createNode(source);
    Version proto = this.versionParser.parseRequired(node.attribute("proto"));
    Version version = this.versionParser.parseRequired(node.childRequired("version").text());
    Stage stage = stageParser.parse(node.attribute("stage")).orElse(Stage.PRODUCTION);
    VersionInfo info =
        new VersionInfo() {
          @Override
          public Version getProto() {
            return proto;
          }

          @Override
          public Version getVersion() {
            return version;
          }

          @Override
          public Stage stage() {
            return stage;
          }
        };
    return new LobbyWorld(source, node, info) {
      @Override
      public Stage environment() {
        return LobbyWorldParser.this.stage;
      }
    };
  }
}

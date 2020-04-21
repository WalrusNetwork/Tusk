package network.walrus.games.core.map;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;
import network.walrus.games.core.api.game.Game;
import network.walrus.games.core.api.game.GameParser;
import network.walrus.games.core.api.map.GameMap;
import network.walrus.utils.core.config.Node;
import network.walrus.utils.core.config.ParsingException;
import network.walrus.utils.core.parse.SimpleParser;
import network.walrus.utils.core.parse.simple.EnumParser;
import network.walrus.utils.core.stage.Stage;
import network.walrus.utils.core.versioning.Version;
import network.walrus.utils.parsing.facet.parse.GlobalParser;
import network.walrus.utils.parsing.world.config.ConfigurationParseException;

/**
 * Parser responsible for parsing every loaded {@link GameMap}'s configuration file. This is used
 * for basic pre-round parsing, and the data retained from this process will stay around as long as
 * the game management system is loaded. This is only used to determine base data, and to perform
 * some basic validations.
 *
 * @param <M> type of map this parser is responsible for parsing
 * @author Austin Mayes
 */
public abstract class MapParser<M extends GameMap> extends GlobalParser<M> {

  private final SimpleParser<Version> versionParser;
  private final EnumParser<Stage> stageParser;
  private final Set<GameParser> parsers = Sets.newHashSet();

  /**
   * Constructor.
   *
   * @param logger used to log parsing errors and info to
   * @param versionParser used to parse map versions
   * @param stageParser used to parse the map stage
   */
  public MapParser(
      Logger logger, SimpleParser<Version> versionParser, EnumParser<Stage> stageParser) {
    super(logger);
    this.versionParser = versionParser;
    this.stageParser = stageParser;
  }

  /**
   * Add a {@link GameParser} to the set of available parsers that will be used to determine the
   * game for a map during the initial parsing chain.
   *
   * <p>Note that if one parser in the set returns a non-empty optional, no other parsers in the
   * chain will be called. Because of this, only *one* parser should be defined for each game.
   *
   * @param parser to add
   */
  public void addParser(GameParser parser) {
    parsers.add(parser);
  }

  @Override
  public M parseInternal(M source) throws ConfigurationParseException {
    for (GameParser parser : parsers) {
      Optional<Game<?>> parsed = parser.parse(source.parent());
      if (parsed.isPresent()) {
        source.game(parsed.get());
        return source;
      }
    }
    throw new ConfigurationParseException("Unable to determine game for supplied map!");
  }

  /**
   * Parse a {@link MapInfo} object from the map parent node.
   *
   * @param node to parse info
   * @return parsed info
   * @throws ParsingException if required info is invalid or missing
   */
  public MapInfo parseInfo(Node<?> node) throws ParsingException {
    String name = node.childRequired("name").text().asRequiredString();
    Version proto = this.versionParser.parseRequired(node.attribute("proto"));
    Version version = this.versionParser.parseRequired(node.childRequired("version").text());
    Stage stage = stageParser.parse(node.attribute("stage")).orElse(Stage.PRODUCTION);
    Optional<String> objective = node.child("objective").flatMap(t -> t.text().value());
    List<String> rules = Lists.newArrayList();
    node.child("rules")
        .ifPresent(
            c -> {
              for (Node<?> r : c.children()) {
                rules.add(r.text().asRequiredString());
              }
            });
    return new MapInfo(name, proto, version, stage, objective, rules);
  }
}

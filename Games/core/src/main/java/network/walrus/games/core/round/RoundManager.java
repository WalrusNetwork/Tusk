package network.walrus.games.core.round;

import co.aikar.timings.Timing;
import co.aikar.timings.Timings;
import java.util.logging.Level;
import java.util.logging.Logger;
import network.walrus.games.core.GamesPlugin;
import network.walrus.utils.core.config.ParsingException;
import network.walrus.utils.parsing.facet.parse.DocumentParser;

/**
 * Class responsible for managing rounds.
 *
 * @param <R> type of round this manager is responsible for managing
 * @author Austin Mayes
 */
public class RoundManager<R extends GameRound> {

  private final R round;
  private final DocumentParser<R> parserProvider;
  private final Logger mapErrorLogger;

  /**
   * Constructor.
   *
   * @param round that this manager is responsible for handling
   * @param parserProvider to provide the main document parser
   * @param mapErrorLogger to log map errors to
   */
  public RoundManager(R round, DocumentParser<R> parserProvider, Logger mapErrorLogger) {
    this.round = round;
    this.parserProvider = parserProvider;
    this.mapErrorLogger = mapErrorLogger;
  }

  /** Parse the map. */
  public boolean load() {
    try (Timing timing =
        Timings.ofStart(GamesPlugin.instance, "Map parse: " + round.map().name())) {
      parserProvider.parse();
      return true;
    } catch (ParsingException e) {
      this.mapErrorLogger.log(
          Level.SEVERE,
          "Failed to parse configuration for "
              + round.map().mapInfo().getName()
              + ": "
              + e.getMessage(),
          e);
    }
    return false;
  }
}

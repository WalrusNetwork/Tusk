package network.walrus.games.core.api.game;

import java.util.Optional;
import network.walrus.games.core.round.GameRound;
import network.walrus.utils.core.config.Node;
import network.walrus.utils.core.config.ParsingException;
import network.walrus.utils.parsing.facet.holder.FacetHolderParser;

/**
 * Handler used to read base map configuration {@link Node}s and determine what {@link Game} that
 * the configuration file is meant to represent. This is used only to get a base game reference for
 * general data, and all {@link GameRound} parsing should be handled by a {@link FacetHolderParser}.
 *
 * @param <G> base type of game that this parser is responsible for attempting to parse
 * @author Austin Mayes
 */
public interface GameParser<G extends Game<?>> {

  /**
   * Attempt to determine which game the supplied node represents, and optionally perform any
   * pre-parsing procedures. If a game cannot be determined from the node, {@link Optional#empty()}
   * should be returned in leu of null. Implementations can also throw {@link ParsingException}s due
   * to general configuration errors, but not if a game cannot be found.
   *
   * @param node root map element node of the configuration document that this parser will attempt
   *     to gather game data from
   * @return a game reference determined from the content of the supplied node. If no game cannot be
   *     determined, {@link Optional#empty()} should be returned
   * @throws ParsingException if initial parsing fails
   */
  Optional<G> parse(Node node) throws ParsingException;
}

package network.walrus.utils.parsing.facet.parse;

import java.util.Optional;
import network.walrus.utils.core.config.Node;
import network.walrus.utils.core.config.ParsingException;
import network.walrus.utils.parsing.facet.Facet;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import org.bukkit.World;

/**
 * Parses {@link Facet}s from {@link Node}s and {@link World}s. Only one of these objects will be
 * created at runtime, so any holder-specific data should be strictly kept out of these. All this
 * object should be responsible for is converting user data into POJOs. No methods should return
 * {@code null}, {@link Optional#empty()} should be used instead to indicate that this parser found
 * no data to support a usable {@link Facet} for the context.
 *
 * @param <F> type of facet this parser will return
 * @author Austin Mayes
 */
public interface FacetParser<F extends Facet> {

  /**
   * Parse a {@link Facet} from a configuration {@link Node}.
   *
   * @param node root node of the configuration document
   * @return a parsed {@link Facet}, or {@link Optional#empty()} if the configuration contains no
   *     definition for the facet.
   * @throws ParsingException if parsing fails due to user error
   */
  Optional<F> parse(FacetHolder holder, Node<?> node) throws ParsingException;

  /**
   * Add additional data to an already parsed {@link Facet} with information gathered from a {@link
   * World}. NOTE: This will not be called if {@link #parse(FacetHolder, Node)} returns {@link
   * Optional#empty()}.
   *
   * @param world that the {@link Facet} will be used in
   * @param fromNode a parsed {@link Facet} from the configuration that this method can hook into to
   *     add additional data
   * @return a parsed {@link Facet}, or {@link Optional#empty()} if the configuration and world
   *     contain no definition for the facet.
   * @throws ParsingException if parsing fails due to user error
   */
  default Optional<F> parse(FacetHolder holder, World world, F fromNode) throws ParsingException {
    return Optional.of(fromNode);
  }

  /**
   * If this parser must return a {@link Facet} in order for overall parsing to complete without
   * failure. If this is {@code true} and {@link #parse(FacetHolder, Node)} or {@link
   * #parse(FacetHolder, World, Facet)} return {@link Optional#empty()}, a {@link ParsingException}
   * will be thrown by the main parser.
   */
  default boolean required() {
    return false;
  }
}

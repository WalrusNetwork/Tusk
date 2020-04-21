package network.walrus.games.core.api.game;

import network.walrus.games.core.api.map.GameMap;
import network.walrus.games.core.round.GameRound;
import network.walrus.utils.parsing.facet.Facet;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import network.walrus.utils.parsing.facet.parse.FacetParser;
import network.walrus.utils.parsing.facet.parse.configurator.FacetConfigurator;

/**
 * A {@link FacetConfigurator} that will only register parsers and facets if the holder is a {@link
 * GameRound} and the {@link Game#slug()} of the round's {@link GameMap} matches {@link
 * #gameSlug()}.
 *
 * @author Austin Mayes
 */
public interface GameBoundConfigurator extends FacetConfigurator {

  /**
   * @return slug(s) which the map's game must match in order for these facets/parsers to be
   *     activated.
   */
  String[] gameSlug();

  @Override
  default void bindParser(Class<? extends FacetParser> parserClazz) {
    bindParser(parserClazz, this::ensureMatch);
  }

  @Override
  default <F extends Facet> void bindFacetDirect(Class<? extends F> facetClazz) {
    bindFacetDirect(facetClazz, this::ensureMatch);
  }

  @Override
  default <F extends Facet> void bindFacetDirect(
      Class<? extends F> facetClazz, Class[] argTypes, Object[] args) {
    bindFacetDirect(facetClazz, argTypes, args, this::ensureMatch);
  }

  default boolean ensureMatch(FacetHolder h) {
    if (!(h instanceof GameRound)) {
      return false;
    }
    for (String s : gameSlug()) {
      if (s.equals(((GameRound) h).map().game().slug())) {
        return true;
      }
    }
    return false;
  }
}

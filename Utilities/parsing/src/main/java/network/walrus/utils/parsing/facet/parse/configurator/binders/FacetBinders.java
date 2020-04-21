package network.walrus.utils.parsing.facet.parse.configurator.binders;

import java.util.function.Predicate;
import network.walrus.utils.parsing.facet.Facet;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import network.walrus.utils.parsing.facet.parse.DocumentParser;
import network.walrus.utils.parsing.facet.parse.configurator.ConstantFacetParser;

/**
 * Binders used to configure {@link Facet}s.
 *
 * @author Austin Mayes
 */
public interface FacetBinders {

  /**
   * Register a facet that will always be enabled for every holder.
   *
   * @param facetClazz to create for each parsing routine
   * @param <F> type of facet being registered
   */
  default <F extends Facet> void bindFacetDirect(Class<? extends F> facetClazz) {
    bindFacetDirect(facetClazz, (h) -> true);
  }

  /**
   * Register a facet that will always be enabled for every holder.
   *
   * @param facetClazz to create for each parsing routine
   * @param usePredicate which is checked for each {@link FacetHolder} to determine if the parser
   *     should be used
   * @param <F> type of facet being registered
   */
  default <F extends Facet> void bindFacetDirect(
      Class<? extends F> facetClazz, Predicate<FacetHolder> usePredicate) {
    bindFacetDirect(facetClazz, new Class[] {}, new Class[] {}, usePredicate);
  }

  /**
   * Register a facet that will always be enabled for every holder.
   *
   * @param facetClazz to create for each parsing routine
   * @param argTypes types of the facet's constructor
   * @param args to pass in for construction
   * @param <F> type of facet being registered
   */
  default <F extends Facet> void bindFacetDirect(
      Class<? extends F> facetClazz, Class[] argTypes, Object[] args) {
    bindFacetDirect(facetClazz, argTypes, args, (h) -> true);
  }

  /**
   * Register a facet that will always be enabled for every holder.
   *
   * @param facetClazz to create for each parsing routine
   * @param argTypes types of the facet's constructor
   * @param usePredicate which is checked for each {@link FacetHolder} to determine if the parser
   *     should be used
   * @param args to pass in for construction
   * @param <F> type of facet being registered
   */
  default <F extends Facet> void bindFacetDirect(
      Class<? extends F> facetClazz,
      Class[] argTypes,
      Object[] args,
      Predicate<FacetHolder> usePredicate) {
    DocumentParser.registerParser(
        new ConstantFacetParser<F>(facetClazz, argTypes, args), usePredicate);
  }
}

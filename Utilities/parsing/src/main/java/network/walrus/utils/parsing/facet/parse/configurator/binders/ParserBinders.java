package network.walrus.utils.parsing.facet.parse.configurator.binders;

import java.util.function.Predicate;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import network.walrus.utils.parsing.facet.parse.DocumentParser;
import network.walrus.utils.parsing.facet.parse.FacetParser;

/**
 * Binders used to configure {@link FacetParser}s.
 *
 * @author Austin Mayes
 */
public interface ParserBinders {

  /**
   * Register a parser class which will be used to parse facets for each configuration document.
   *
   * @param parserClazz to register as capable of parsing
   */
  default void bindParser(Class<? extends FacetParser> parserClazz) {
    bindParser(parserClazz, (h) -> true);
  }

  /**
   * Register a parser class which will be used to parse facets for each configuration document.
   *
   * @param parserClazz to register as capable of parsing
   * @param usePredicate which is checked for each {@link FacetHolder} to determine if the parser
   *     should be used
   */
  default void bindParser(
      Class<? extends FacetParser> parserClazz, Predicate<FacetHolder> usePredicate) {
    DocumentParser.registerParser(parserClazz, usePredicate);
  }
}

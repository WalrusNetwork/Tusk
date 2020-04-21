package network.walrus.utils.parsing.facet.parse.configurator;

import network.walrus.utils.parsing.facet.Facet;
import network.walrus.utils.parsing.facet.parse.FacetParser;
import network.walrus.utils.parsing.facet.parse.configurator.binders.CommandBinders;
import network.walrus.utils.parsing.facet.parse.configurator.binders.FacetBinders;
import network.walrus.utils.parsing.facet.parse.configurator.binders.ListenerBinders;
import network.walrus.utils.parsing.facet.parse.configurator.binders.ParserBinders;

/**
 * An object which is used to configure {@link FacetParser}s and {@link Facet}s.
 *
 * @author Austin Mayes
 */
public interface FacetConfigurator
    extends CommandBinders, ListenerBinders, ParserBinders, FacetBinders {

  /**
   * Configure this configurator. This is only called once and happens before any parsing begins.
   */
  void configure();
}

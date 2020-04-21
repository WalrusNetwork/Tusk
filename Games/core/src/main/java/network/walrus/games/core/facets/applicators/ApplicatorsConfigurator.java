package network.walrus.games.core.facets.applicators;

import network.walrus.utils.parsing.facet.parse.configurator.ActiveTime;
import network.walrus.utils.parsing.facet.parse.configurator.FacetConfigurator;

/**
 * Configures aspects of the applicator system.
 *
 * @author Austin Mayes
 */
public class ApplicatorsConfigurator implements FacetConfigurator {

  @Override
  public void configure() {
    bindParser(ApplicatorsParser.class);
    bindFacetListener(ApplicatorListener.class, ApplicatorsFacet.class, ActiveTime.ENABLED);
  }
}

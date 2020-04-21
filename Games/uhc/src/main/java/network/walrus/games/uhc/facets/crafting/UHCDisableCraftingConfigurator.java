package network.walrus.games.uhc.facets.crafting;

import network.walrus.games.uhc.UHCRound;
import network.walrus.utils.parsing.facet.parse.configurator.FacetConfigurator;

/**
 * Configurator for removing recipes from UHC matches.
 *
 * @author Rafi Baum
 */
public class UHCDisableCraftingConfigurator implements FacetConfigurator {

  @Override
  public void configure() {
    bindParser(UHCDisableCraftingParser.class, h -> h instanceof UHCRound);
  }
}

package network.walrus.games.uhc.facets.entity;

import network.walrus.games.uhc.UHCRound;
import network.walrus.utils.parsing.facet.parse.configurator.FacetConfigurator;

/**
 * Configures all facets which manage {@link org.bukkit.entity.Entity entites}.
 *
 * @author Austin Mayes
 */
public class EntititesConfigurator implements FacetConfigurator {

  @Override
  public void configure() {
    bindFacetDirect(EntityManagementFacet.class, (h) -> h instanceof UHCRound);
  }
}

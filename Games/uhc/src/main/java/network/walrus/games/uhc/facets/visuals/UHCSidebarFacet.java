package network.walrus.games.uhc.facets.visuals;

import network.walrus.games.core.facets.visual.SidebarFacet;
import network.walrus.utils.parsing.facet.holder.FacetHolder;

/**
 * Wrapper class of {@link SidebarFacet} for UHC specific configuration.
 *
 * @author Rafi Baum
 */
public class UHCSidebarFacet extends SidebarFacet {

  /** @param holder that this facet is operating inside of */
  public UHCSidebarFacet(FacetHolder holder) {
    super(holder);
    updateOnTick("next-border");
  }
}

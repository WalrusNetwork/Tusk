package network.walrus.utils.parsing.facet.parse.configurator;

import network.walrus.utils.parsing.facet.holder.FacetHolder;

/**
 * Simple marker to represent when something (listeners, tasks) should be actively operating when
 * installed into a {@link FacetHolder}. This is generally useful when some listeners should only be
 * listening while the holder is in an enabled stage, while some listeners need to be listening as
 * long as the holder is alive.
 *
 * @author Austin Mayes
 */
public enum ActiveTime {
  /**
   * Active between the {@link FacetHolder#loadFacets()} and {@link FacetHolder#unloadFacets()} time
   * period.
   */
  LOADED,
  /**
   * Active only between the {@link FacetHolder#enableFacets()} and {@link
   * FacetHolder#disableFacets()} time period.
   */
  ENABLED
}

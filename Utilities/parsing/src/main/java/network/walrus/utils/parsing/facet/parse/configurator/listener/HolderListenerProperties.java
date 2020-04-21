package network.walrus.utils.parsing.facet.parse.configurator.listener;

import javax.annotation.Nullable;
import network.walrus.utils.parsing.facet.Facet;
import network.walrus.utils.parsing.facet.parse.configurator.ActiveTime;

/**
 * Data class to make passing around listener data a lot easier. THis is used in the facet holder
 * listener registration system, and is part of the core backbone for installing and registering
 * listeners.
 *
 * @param <F> Facet type this listener is for
 * @author Austin Mayes
 */
public class HolderListenerProperties<F extends Facet> {

  private final Class<FacetListener<F>> listenerClass;
  private final ActiveTime activeTime;
  private final @Nullable Class<? extends Facet> facetClass;

  /**
   * Constructor.
   *
   * @param listenerClass listener to register
   * @param activeTime time when the listener should be listening
   * @param facetClass facet which must be parsed for the listener to be registered, If null, the
   *     listener will always be registered at the specified activation time.
   */
  public HolderListenerProperties(
      Class<FacetListener<F>> listenerClass,
      ActiveTime activeTime,
      @Nullable Class<? extends Facet> facetClass) {
    this.listenerClass = listenerClass;
    this.activeTime = activeTime;
    this.facetClass = facetClass;
  }

  public Class<FacetListener<F>> getListenerClass() {
    return listenerClass;
  }

  public ActiveTime getActiveTime() {
    return activeTime;
  }

  /** @return if the listener is bound to a facet */
  public boolean boundToFacet() {
    return facetClass != null;
  }

  @Nullable
  public Class<? extends Facet> getFacetClass() {
    return facetClass;
  }
}

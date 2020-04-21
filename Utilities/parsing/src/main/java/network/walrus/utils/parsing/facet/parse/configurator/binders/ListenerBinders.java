package network.walrus.utils.parsing.facet.parse.configurator.binders;

import network.walrus.utils.parsing.facet.Facet;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import network.walrus.utils.parsing.facet.parse.configurator.ActiveTime;
import network.walrus.utils.parsing.facet.parse.configurator.listener.FacetListener;
import network.walrus.utils.parsing.facet.parse.configurator.listener.HolderListenerProperties;

/**
 * Binders used to configure {@link FacetListener}s.
 *
 * @author Austin Mayes
 */
public interface ListenerBinders {

  /**
   * Register a listener that will always be active for each facet holder. The listener is
   * registered at {@link ActiveTime#LOADED}.
   *
   * <p>NOTE: Listeners must have a one-arg constructor which accepts a {@link FacetHolder} in order
   * to be created for each holder.
   *
   * @param listenerClazz to register
   */
  default void bindConstantListener(Class<? extends FacetListener> listenerClazz) {
    bindFacetListener(listenerClazz, null);
  }

  /**
   * Register a listener that will only be enabled when the corresponding facet is loaded in the
   * holder. The listener is registered at {@link ActiveTime#LOADED}.
   *
   * <p>NOTE: Listeners must have a two-args constructor which accepts a {@link FacetHolder}
   * followed by the type of facet they are bound to in order to be created for each holder.
   *
   * @param listenerClazz to register
   * @param facetClass that must be loaded in order for the listener to be registered
   */
  default void bindFacetListener(
      Class<? extends FacetListener> listenerClazz, Class<? extends Facet> facetClass) {
    bindFacetListener(listenerClazz, facetClass, ActiveTime.LOADED);
  }

  /**
   * Register a listener that will only be enabled when the corresponding facet is loaded in the
   * holder. The listener will be registered using the supplied active time.
   *
   * <p>NOTE: Listeners must have a two-args constructor which accepts a {@link FacetHolder}
   * followed by the type of facet they are bound to in order to be created for each holder.
   *
   * @param listenerClazz to register
   * @param facetClass that must be loaded in order for the listener to be registered
   * @param activeTime when the listener should be registered
   */
  default void bindFacetListener(
      Class<? extends FacetListener> listenerClazz,
      Class<? extends Facet> facetClass,
      ActiveTime activeTime) {
    HolderListenerProperties holderListenerProperties =
        new HolderListenerProperties(listenerClazz, activeTime, facetClass);
    FacetHolder.registerListener(holderListenerProperties);
  }
}

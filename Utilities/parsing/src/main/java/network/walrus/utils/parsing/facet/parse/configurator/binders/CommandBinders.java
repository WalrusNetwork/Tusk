package network.walrus.utils.parsing.facet.parse.configurator.binders;

import network.walrus.utils.parsing.facet.Facet;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import network.walrus.utils.parsing.facet.parse.configurator.ActiveTime;
import network.walrus.utils.parsing.facet.parse.configurator.command.FacetCommandContainer;
import network.walrus.utils.parsing.facet.parse.configurator.command.HolderCommandProperties;

/**
 * Binders used to configure {@link FacetCommandContainer}s.
 *
 * @author Austin Mayes
 */
public interface CommandBinders {
  /**
   * Register a set of commands that will always be active for each facet holder. The commands are
   * registered at {@link ActiveTime#LOADED}.
   *
   * <p>NOTE: Command containers must have a one-arg constructor which accepts a {@link FacetHolder}
   * in order to be created for each holder.
   *
   * @param containerClazz to register
   */
  default void bindConstantCommands(Class<? extends FacetCommandContainer> containerClazz) {
    bindFacetCommands(containerClazz, null);
  }

  /**
   * Register a set of commands that will only be enabled when the corresponding facet is loaded in
   * the holder. The commands are registered at {@link ActiveTime#LOADED}.
   *
   * <p>NOTE: Command containers must have a two-args constructor which accepts a {@link
   * FacetHolder} followed by the type of facet they are bound to in order to be created for each
   * holder.
   *
   * @param containerClazz to register
   * @param facetClass that must be loaded in order for the commands to be registered
   */
  default void bindFacetCommands(
      Class<? extends FacetCommandContainer> containerClazz, Class<? extends Facet> facetClass) {
    bindFacetCommands(containerClazz, facetClass, ActiveTime.LOADED);
  }

  /**
   * Register a set of commands that will only be enabled when the corresponding facet is loaded in
   * the holder. The commands will be registered using the supplied active time.
   *
   * <p>NOTE: Command containers must have a two-args constructor which accepts a {@link
   * FacetHolder} followed by the type of facet they are bound to in order to be created for each
   * holder.
   *
   * @param containerClazz to register
   * @param facetClass that must be loaded in order for the commands to be registered
   * @param activeTime when the commands should be registered
   */
  default void bindFacetCommands(
      Class<? extends FacetCommandContainer> containerClazz,
      Class<? extends Facet> facetClass,
      ActiveTime activeTime) {
    HolderCommandProperties holderListenerProperties =
        new HolderCommandProperties(containerClazz, activeTime, facetClass);
    FacetHolder.registerCommands(holderListenerProperties);
  }
}

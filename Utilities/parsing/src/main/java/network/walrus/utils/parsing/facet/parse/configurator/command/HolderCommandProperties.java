package network.walrus.utils.parsing.facet.parse.configurator.command;

import javax.annotation.Nullable;
import network.walrus.utils.parsing.facet.Facet;
import network.walrus.utils.parsing.facet.parse.configurator.ActiveTime;

/**
 * Data class to make passing around command data a lot easier. This is used in the facet holder
 * command registration system, and is part of the core backbone for installing and registering
 * commands.
 *
 * @param <F> Facet type this command is for
 * @author Austin Mayes
 */
public class HolderCommandProperties<F extends Facet> {

  private final Class<FacetCommandContainer<F>> commandClass;
  private final ActiveTime activeTime;
  private final @Nullable Class<F> facetClass;

  /**
   * @param commandClass commands to register
   * @param activeTime time when the commands should be usable
   * @param facetClass facet which must be parsed for the commands to be registered, If null, the
   *     commands will always be registered at the specified activation time.
   */
  public HolderCommandProperties(
      Class<FacetCommandContainer<F>> commandClass,
      ActiveTime activeTime,
      @Nullable Class<F> facetClass) {
    this.commandClass = commandClass;
    this.activeTime = activeTime;
    this.facetClass = facetClass;
  }

  public Class<FacetCommandContainer<F>> getCommandClass() {
    return commandClass;
  }

  public ActiveTime getActiveTime() {
    return activeTime;
  }

  /** @return if the command container is bound to a facet */
  public boolean boundToFacet() {
    return facetClass != null;
  }

  @Nullable
  public Class<? extends Facet> getFacetClass() {
    return facetClass;
  }
}

package network.walrus.utils.parsing.facet;

import network.walrus.utils.core.config.Node;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import network.walrus.utils.parsing.facet.parse.FacetParser;

/**
 * A specific feature, or container of features, which can be installed into a {@link FacetHolder}.
 * These objects are constant and live only as long as a *single* holder. Because of this,
 * implementations should not be concerned with immutability or multi-holder data.
 *
 * <p>NOTE: This was called a MatchModule in PGM.
 *
 * @author Austin Mayes
 */
public abstract class Facet {

  /**
   * Determine if this facet should be loaded in the current holder. If this returns {@code false},
   * none of the other methods in this class will be called, the facet will be removed from the
   * context, and this object will in all basic principles be discarded.
   */
  public boolean shouldLoad() {
    return true;
  }

  /**
   * Called directly after the holder world has loaded. This is called directly after {@link
   * FacetParser#parse(FacetHolder, Node)} has passed for each {@link Facet} in the context. At this
   * point, facets should be fully constructed and any APIs they expose should be in a ready and
   * usable state.
   *
   * @throws FacetLoadException if the facet fails to load for any reason
   */
  public void load() throws FacetLoadException {}

  /**
   * Called right before the holder has unloaded. At this point, it should be expected that some
   * other facet APIs might be in an unstable state. Any data needed by this method should be
   * collected by {@link #disable()}.
   */
  public void unload() {}

  /**
   * Called when the holder enables. Any facet dependencies will already have their {@code enable()}
   * calls invoked, and everything will be in a ready state.
   */
  public void enable() {}

  /**
   * Called in reverse order of the {@link #enable()} call chain. Any facets which depend on this
   * facet will be disabled first, and any dependencies that this facet has will still be active.
   */
  public void disable() {}
}

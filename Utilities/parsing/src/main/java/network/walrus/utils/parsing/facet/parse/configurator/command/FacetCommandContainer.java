package network.walrus.utils.parsing.facet.parse.configurator.command;

import javax.annotation.Nullable;
import network.walrus.utils.parsing.facet.Facet;
import network.walrus.utils.parsing.facet.holder.FacetHolder;

/**
 * A set of commands which are bound to a {@link FacetHolder} and can optionally be bound to a
 * {@link Facet} of type {@link F}.
 *
 * <p>The main purpose of this is to remind developers that these classes need specific constructors
 * in order to work with the holder registration system.
 *
 * @param <F> facet type this command set is for
 * @author Austin Mayes
 */
public class FacetCommandContainer<F extends Facet> {

  private final FacetHolder holder;
  private final @Nullable F facet;

  /**
   * Constructor for un-bound objects.
   *
   * @param holder which this object is inside of
   */
  public FacetCommandContainer(FacetHolder holder) {
    this(holder, null);
  }

  /**
   * Constructor for bound objects.
   *
   * @param holder which this object is inside of
   * @param facet which this object is bound to
   */
  public FacetCommandContainer(FacetHolder holder, F facet) {
    this.holder = holder;
    this.facet = facet;
  }

  public FacetHolder getHolder() {
    return holder;
  }

  public F getFacet() {
    return facet;
  }

  /** @return root alias for all commands */
  public String[] rootAlias() {
    return null;
  }
}

package network.walrus.utils.parsing.facet.parse.configurator.listener;

import javax.annotation.Nullable;
import network.walrus.utils.parsing.facet.Facet;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import org.bukkit.event.Listener;

/**
 * A listener which is bound to a {@link FacetHolder} and can optionally be bound to a {@link Facet}
 * of type {@link F}.
 *
 * <p>The main purpose of this is to remind developers that these classes need specific constructors
 * in order to work with the holder registration system.
 *
 * @param <F> facet type this listener is for
 * @author Austin Mayes
 */
public class FacetListener<F extends Facet> implements Listener {

  private final FacetHolder holder;
  private final @Nullable F facet;

  /**
   * Constructor for un-bound objects.
   *
   * @param holder which this object is inside of
   */
  public FacetListener(FacetHolder holder) {
    this(holder, null);
  }

  /**
   * Constructor for bound objects.
   *
   * @param holder which this object is inside of
   * @param facet which this object is bound to
   */
  public FacetListener(FacetHolder holder, F facet) {
    this.holder = holder;
    this.facet = facet;
  }

  public FacetHolder getHolder() {
    return holder;
  }

  public F getFacet() {
    return facet;
  }
}

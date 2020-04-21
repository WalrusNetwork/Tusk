package network.walrus.games.core.facets.damage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import network.walrus.games.core.facets.filters.Filter;
import network.walrus.games.core.util.EventUtil;
import network.walrus.utils.parsing.facet.Facet;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import org.bukkit.event.Listener;

/**
 * Facet which registers and un-registers {@link Listener}s if certain {@link Filter}s are present.
 *
 * @author ShinyDialga
 */
public class DamageFacet extends Facet {

  private final List<Listener> listeners;

  /**
   * @param holder which this facet is operating in
   * @param damageFilter filter to decide which damage types to disable
   */
  public DamageFacet(FacetHolder holder, Optional<Filter> damageFilter) {
    this.listeners = new ArrayList<>();
    damageFilter.ifPresent(filter -> this.listeners.add(new DamageListener(holder, filter)));
  }

  @Override
  public void load() {
    EventUtil.register(this.listeners);
  }

  @Override
  public void unload() {
    EventUtil.unregister(this.listeners);
  }
}

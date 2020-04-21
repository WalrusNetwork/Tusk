package network.walrus.games.core.facets.items;

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
 * @author Austin Mayes
 */
public class ItemsFacet extends Facet {

  private final List<Listener> listeners;

  /**
   * @param holder which this facet is operating in
   * @param removeDrops filter to decide which items to remove
   * @param keepItems filter to decide which items to be kept
   * @param repairTools filter to decide which items to repair
   * @param deathDrop filter to decide which items to drop on death
   */
  public ItemsFacet(
      FacetHolder holder,
      Optional<Filter> removeDrops,
      Optional<Filter> keepItems,
      Optional<Filter> repairTools,
      Optional<Filter> deathDrop) {

    this.listeners = new ArrayList<>();
    removeDrops.ifPresent(filter -> this.listeners.add(new RemoveDropsListener(holder, filter)));
    repairTools.ifPresent(filter -> this.listeners.add(new RepairToolsListener(holder, filter)));
    keepItems.ifPresent(filter -> this.listeners.add(new KeepListener(holder, filter)));
    deathDrop.ifPresent(filter -> this.listeners.add(new DeathDropsListener(filter)));
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

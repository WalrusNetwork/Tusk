package network.walrus.games.core.facets.damage;

import network.walrus.games.core.facets.filters.Filter;
import network.walrus.games.core.facets.filters.context.FilterContext;
import network.walrus.games.core.facets.filters.variable.DamageVariable;
import network.walrus.games.core.facets.filters.variable.EntityVariable;
import network.walrus.games.core.facets.filters.variable.LocationVariable;
import network.walrus.games.core.facets.filters.variable.PlayerVariable;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * Listener which solely has the responsibility for canceling damage based on a {@link Filter}.
 *
 * @author ShinyDialga
 */
public class DamageListener implements Listener {

  private final FacetHolder holder;
  private final Filter damageFilter;

  /**
   * @param holder which this listener is operating inside of
   * @param damageFilter filter used to decide which damages to cancel
   */
  DamageListener(FacetHolder holder, Filter damageFilter) {
    this.holder = holder;
    this.damageFilter = damageFilter;
  }

  /** Cancel certain types of damage */
  @EventHandler(priority = EventPriority.HIGH)
  public void onEntityDamage(EntityDamageEvent event) {
    EntityDamageEvent.DamageCause cause = event.getCause();

    FilterContext parent = new FilterContext();
    if (event.getEntity() instanceof Player) {
      parent.add(new PlayerVariable((Player) event.getEntity()));
    } else {
      parent.add(new EntityVariable(event.getEntity()));
    }
    parent.add(new LocationVariable(event.getEntity().getLocation()));

    FilterContext context = parent.duplicate();
    context.add(new DamageVariable(cause));

    boolean damage = this.damageFilter.test(context).passes();
    if (damage) {
      event.setCancelled(true);
    }
  }
}

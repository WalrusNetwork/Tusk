package network.walrus.games.core.facets.items;

import com.google.common.base.Objects;
import java.util.Iterator;
import javax.annotation.Nullable;
import network.walrus.games.core.facets.filters.Filter;
import network.walrus.games.core.facets.filters.FilterCache;
import network.walrus.games.core.facets.filters.context.FilterContext;
import network.walrus.games.core.facets.filters.variable.EntityVariable;
import network.walrus.games.core.facets.filters.variable.ItemVariable;
import network.walrus.games.core.facets.filters.variable.LocationVariable;
import network.walrus.games.core.facets.filters.variable.MaterialVariable;
import network.walrus.games.core.facets.filters.variable.PlayerVariable;
import network.walrus.ubiquitous.bukkit.events.world.EntityChangeEvent;
import network.walrus.ubiquitous.bukkit.events.world.EntityChangeEvent.Action;
import network.walrus.ubiquitous.bukkit.tracker.event.entity.EntityDeathEvent;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import org.bukkit.Material;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

/**
 * Listener which solely has the responsibility for removing items based on a {@link Filter}.
 *
 * @author Austin Mayes
 */
public class RemoveDropsListener implements Listener {

  private final FacetHolder holder;
  private final Filter removeDrops;
  private final FilterCache<MaterialData> cache = new FilterCache<>();
  private final FilterCache<EntityBoundMaterial> entityCache = new FilterCache<>();

  /**
   * @param holder which this listener is operating inside of
   * @param removeDrops filter used to decide which items to remove
   */
  RemoveDropsListener(FacetHolder holder, Filter removeDrops) {
    this.holder = holder;
    this.removeDrops = removeDrops;
  }

  private boolean shouldRemove(MaterialData data, FilterContext parent, @Nullable Entity entity) {
    if (entity != null) {
      return this.entityCache
          .get(
              new EntityBoundMaterial(data, entity),
              (d) -> {
                FilterContext context = parent.duplicate();
                context.add(new MaterialVariable(data));
                return this.removeDrops.test(context);
              })
          .passes();
    }
    return this.cache
        .get(
            data,
            (d) -> {
              FilterContext context = parent.duplicate();
              context.add(new MaterialVariable(data));
              return this.removeDrops.test(context);
            })
        .passes();
  }

  /** Remove items on death */
  @EventHandler(priority = EventPriority.HIGH)
  public void onPlayerDeath(EntityDeathEvent event) {
    Iterator<ItemStack> iterator = event.getDrops().iterator();
    FilterContext parent = new FilterContext();
    if (event.getEntity() instanceof Player) {
      parent.add(new PlayerVariable((Player) event.getEntity()));
    } else {
      parent.add(new EntityVariable(event.getEntity()));
    }
    parent.add(new LocationVariable(event.getEntity().getLocation()));

    while (iterator.hasNext()) {
      ItemStack item = iterator.next();
      boolean remove = shouldRemove(item.getData(), parent, event.getEntity());
      if (remove) {
        iterator.remove();
      }
    }
  }

  /** Remove items on item spawn */
  @EventHandler
  public void spawnItem(ItemSpawnEvent event) {
    FilterContext context = new FilterContext();
    context.add(new LocationVariable(event.getLocation()));
    context.add(new MaterialVariable(event.getEntity().getItemStack().getData()));
    boolean remove = this.removeDrops.test(context).passes();

    if (remove) {
      event.getEntity().remove();
      event.setCancelled(true);
    }
  }

  /** Remove items on entity break */
  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onEntityChange(EntityChangeEvent event) {
    if (event.getAction() != Action.BREAK) {
      return;
    }

    ItemStack item = null;

    if (event.getEntity() instanceof ItemFrame) {
      item = ((ItemFrame) event.getEntity()).getItem();
    } else if (event.getEntity() instanceof Minecart) {
      item = new ItemStack(Material.MINECART);
    } else if (event.getEntity() instanceof Boat) {
      item = new ItemStack(Material.BOAT);
    }

    if (item == null) {
      return;
    }

    FilterContext context = new FilterContext();
    if (event.getWhoChanged() instanceof Player) {
      context.add(new PlayerVariable((Player) event.getWhoChanged()));
    }
    context.add(new LocationVariable(event.getEntity().getLocation()));
    context.add(new MaterialVariable(item.getData()));
    context.add(new ItemVariable(item));

    boolean remove = this.removeDrops.test(context).passes();

    if (remove) {
      // hanging break
      if (event.getCause() instanceof EntityDamageByEntityEvent
          && event.getEntity() instanceof ItemFrame) {
        EntityDamageByEntityEvent damageEvent = (EntityDamageByEntityEvent) event.getCause();
        ItemFrame frame = (ItemFrame) event.getEntity();
        damageEvent.setCancelled(true);
        frame.setItem(null);
      } else {
        event.getEntity().remove();
      }
    }
  }

  /** A wrapper so we can cache entities w/ data */
  private static class EntityBoundMaterial {

    private final MaterialData data;
    private final Entity actor;

    EntityBoundMaterial(MaterialData data, Entity actor) {
      this.data = data;
      this.actor = actor;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      EntityBoundMaterial that = (EntityBoundMaterial) o;
      return Objects.equal(data, that.data) && Objects.equal(actor, that.actor);
    }

    @Override
    public int hashCode() {
      return Objects.hashCode(data, actor);
    }
  }
}

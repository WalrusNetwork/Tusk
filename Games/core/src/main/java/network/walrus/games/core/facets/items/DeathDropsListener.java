package network.walrus.games.core.facets.items;

import network.walrus.games.core.facets.filters.Filter;
import network.walrus.games.core.facets.filters.FilterCache;
import network.walrus.games.core.facets.filters.context.FilterContext;
import network.walrus.games.core.facets.filters.variable.LocationVariable;
import network.walrus.games.core.facets.filters.variable.MaterialVariable;
import network.walrus.games.core.facets.filters.variable.PlayerVariable;
import network.walrus.ubiquitous.bukkit.tracker.event.player.PlayerDeathEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Listener which solely has the responsibility for removing items, on death, based on a {@link Filter}.
 *
 * @author Matthew Arnold
 */
public class DeathDropsListener implements Listener {

    private final Filter dropFilter;
    private final FilterCache<MaterialData> dataCache;

    /**
     * @param dropFilter filter used to decide which items to remove
     */
    public DeathDropsListener(Filter dropFilter) {
        this.dropFilter = dropFilter;
        this.dataCache = new FilterCache<>();
    }

    /** Check items on death */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onDeath(PlayerDeathEvent event) {
        FilterContext context = new FilterContext();
        context.add(new PlayerVariable(event.getPlayer()));
        context.add(new LocationVariable(event.getLocation()));

        event.setDrops(checkItems(context, event.getDrops()));
    }

    /**
     * Calculates what items a player should drop on death from the drops
     *
     * @param context    the filter context
     * @param drops      the drops, items are removed from this list if they are added to the array
     * @return the modified dro plist
     */
    private List<ItemStack> checkItems(FilterContext context, List<ItemStack> drops) {
        return drops.stream().filter(
                x -> !dataCache.get(
                        x.getData(),
                        (d) -> {
                            FilterContext duplicated = context.duplicate();
                            duplicated.add(new MaterialVariable(d));
                            return dropFilter.test(duplicated);
                        }).passes()
        ).collect(Collectors.toList());
    }
}

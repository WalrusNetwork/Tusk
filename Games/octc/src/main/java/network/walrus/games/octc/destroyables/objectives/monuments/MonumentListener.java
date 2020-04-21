package network.walrus.games.octc.destroyables.objectives.monuments;

import java.util.ArrayList;
import java.util.List;
import network.walrus.games.core.facets.group.GroupsManager;
import network.walrus.games.core.facets.visual.SidebarFacet;
import network.walrus.games.octc.destroyables.DestroyableUtils;
import network.walrus.games.octc.destroyables.DestroyablesDisplay;
import network.walrus.games.octc.destroyables.objectives.DestroyableObjective;
import network.walrus.games.octc.destroyables.objectives.DestroyablesFacet;
import network.walrus.ubiquitous.bukkit.events.world.BlockChangeByPlayerEvent;
import network.walrus.ubiquitous.bukkit.events.world.BlockChangeEvent;
import network.walrus.ubiquitous.bukkit.tracker.event.player.PlayerDeathEvent;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import network.walrus.utils.parsing.facet.parse.configurator.listener.FacetListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

/**
 * Listener which handles, and passes on, events to {@link MonumentObjective}s.
 *
 * @author Austin Mayes
 */
public class MonumentListener extends FacetListener<DestroyablesFacet> {

  private final List<MonumentObjective> objectives;
  private final SidebarFacet sidebarFacet;

  /**
   * @param holder which this object is inside of
   * @param facet which this object is bound to
   */
  public MonumentListener(FacetHolder holder, DestroyablesFacet facet) {
    super(holder, facet);
    List<MonumentObjective> monuments = new ArrayList<>();
    for (DestroyableObjective o : facet.getObjectives()) {
      if (o instanceof MonumentObjective) {
        MonumentObjective monumentObjective = (MonumentObjective) o;
        monuments.add(monumentObjective);
      }
    }
    this.objectives = monuments;
    this.sidebarFacet = holder.getFacetRequired(SidebarFacet.class);
  }

  /** @see #onBlockChange(BlockChangeEvent) */
  @EventHandler(priority = EventPriority.HIGH)
  public void blockChangeByPlayer(BlockChangeByPlayerEvent event) {
    this.onBlockChange(event);
  }

  /** Handle all block changes and update monuments accordingly. */
  @EventHandler(priority = EventPriority.HIGH)
  public void onBlockChange(BlockChangeEvent event) {
    for (MonumentObjective objective : this.objectives) {
      if (!objective.isInside(event.getBlock())) {
        continue;
      }

      if (event instanceof BlockChangeByPlayerEvent) {
        boolean blockBreak = event.getCause() instanceof BlockBreakEvent;

        if (objective.getProperties().destroyable) {
          blockBreak = blockBreak || event.isToAir();
        }

        if (blockBreak) {
          if (DestroyableUtils.handleBlockBreak(
              objective,
              (BlockChangeByPlayerEvent) event,
              getHolder().getFacetRequired(GroupsManager.class))) {
            sidebarFacet.update(DestroyablesDisplay.objectiveSlug(objective));
          }
        } else if (event.getCause() instanceof BlockPlaceEvent) {
          GroupsManager manager = getHolder().getFacetRequired(GroupsManager.class);
          if (DestroyableUtils.handleBlockPlace(
              objective,
              (BlockChangeByPlayerEvent) event,
              manager.getGroup(((BlockChangeByPlayerEvent) event).getPlayer()),
              manager)) {
            sidebarFacet.update(DestroyablesDisplay.objectiveSlug(objective));
          }
        } else {
          event.setCancelled(true);
        }
      } else {
        event.setCancelled(true);
      }
    }
  }

  /** Update touch status. */
  @EventHandler
  public void onPlayerDeath(PlayerDeathEvent event) {
    for (MonumentObjective objective : this.objectives) {
      objective.setTouchedRecently(event.getPlayer(), false);
    }
  }
}

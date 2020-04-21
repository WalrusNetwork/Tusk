package network.walrus.games.octc.destroyables.objectives.cores;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import network.walrus.games.core.events.group.PlayerChangedGroupEvent;
import network.walrus.games.core.facets.group.Group;
import network.walrus.games.core.facets.group.GroupsManager;
import network.walrus.games.core.facets.visual.SidebarFacet;
import network.walrus.games.core.util.GameTask;
import network.walrus.games.octc.OCNMessages;
import network.walrus.games.octc.destroyables.DestroyableUtils;
import network.walrus.games.octc.destroyables.DestroyablesDisplay;
import network.walrus.games.octc.destroyables.objectives.DestroyableObjective;
import network.walrus.games.octc.destroyables.objectives.DestroyablesFacet;
import network.walrus.games.octc.destroyables.objectives.events.DestroyableEventInfo;
import network.walrus.ubiquitous.bukkit.events.world.BlockChangeByPlayerEvent;
import network.walrus.ubiquitous.bukkit.events.world.BlockChangeEvent;
import network.walrus.ubiquitous.bukkit.tracker.event.player.PlayerDeathEvent;
import network.walrus.utils.bukkit.translation.text.player.PersonalizedBukkitPlayer;
import network.walrus.utils.core.player.PersonalizedPlayer;
import network.walrus.utils.core.text.LocalizedText;
import network.walrus.utils.core.translation.Localizable;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import network.walrus.utils.parsing.facet.parse.configurator.listener.FacetListener;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;

/**
 * Listener which keeps tracks of all events that potentially have an effect on {@link
 * CoreObjective}s.
 *
 * @author ShinyDialga
 */
@SuppressWarnings("JavaDoc")
public class CoreListener extends FacetListener<DestroyablesFacet> {

  private final List<CoreObjective> cores;
  private final SidebarFacet sidebarFacet;

  /**
   * @param holder which this listener is operating inside of
   * @param facet to pull core data from
   */
  public CoreListener(FacetHolder holder, DestroyablesFacet facet) {
    super(holder, facet);
    List<CoreObjective> list = new ArrayList<>();
    for (DestroyableObjective o : facet.getObjectives()) {
      if (o instanceof CoreObjective) {
        CoreObjective coreObjective = (CoreObjective) o;
        list.add(coreObjective);
      }
    }
    this.cores = list;
    this.sidebarFacet = holder.getFacetRequired(SidebarFacet.class);
  }

  @EventHandler
  public void onPlayerDeath(PlayerDeathEvent event) {
    for (CoreObjective core : this.cores) {
      core.setTouchedRecently(event.getPlayer(), false);
    }
  }

  @EventHandler
  public void onPlayerChangeTeam(PlayerChangedGroupEvent event) {
    for (CoreObjective core : this.cores) {
      core.setTouchedRecently(event.getPlayer(), false);
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void blockChangeByPlayer(BlockChangeByPlayerEvent event) {
    this.onBlockChange(event);
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onBlockChange(BlockChangeEvent event) {
    for (CoreObjective objective : this.cores) {
      if ((objective.isInside(event.getBlock())
          || objective.getLiquidRegion().contains(event.getBlock()))) {

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
              markProperLeaker(objective, (BlockChangeByPlayerEvent) event);
            }
          } else if (event.getCause() instanceof BlockPlaceEvent) {
            if (objective.getLiquidRegion().contains(event.getBlock())) {
              event.setCancelled(true);
              return;
            }

            Player player = ((BlockChangeByPlayerEvent) event).getPlayer();
            Group group =
                getHolder()
                    .getFacetRequired(GroupsManager.class)
                    .getGroup(((BlockChangeByPlayerEvent) event).getPlayer());
            if (DestroyableUtils.handleBlockPlace(
                objective,
                (BlockChangeByPlayerEvent) event,
                group,
                getHolder().getFacetRequired(GroupsManager.class))) {
              Localizable monName;

              if (objective.getProperties().owner.isPresent()) {
                monName =
                    objective
                        .getName()
                        .toText(objective.getProperties().owner.get().getColor().style());
              } else {
                monName = objective.getName().toText(group.getColor().style());
              }

              PersonalizedPlayer playerName = new PersonalizedBukkitPlayer(player);

              LocalizedText broadcast =
                  OCNMessages.GENERIC_OBJECTIVE_REPAIRED.with(monName, playerName);
              getHolder().getContainer().broadcast(broadcast);
              sidebarFacet.update(DestroyablesDisplay.objectiveSlug(objective));
            }
          } else {
            event.setCancelled(true);
          }
        } else if (event.getCause() instanceof BlockFromToEvent) {
          handleLavaFlow((BlockFromToEvent) event.getCause(), objective);
        } else {
          event.setCancelled(true);
        }
        return;
      } else if (event.getCause() instanceof BlockFromToEvent
          && objective.getLeakArea().contains(event.getBlock())) {
        handleLavaFlow((BlockFromToEvent) event.getCause(), objective);
        return;
      }
    }
  }

  @EventHandler
  public void onBucketFill(PlayerBucketFillEvent event) {
    for (CoreObjective objective : this.cores) {
      if (objective.getLiquidRegion().contains(event.getBlockClicked().getLocation().toVector())) {
        event.setCancelled(true);
        return;
      }
    }
  }

  private void markProperLeaker(CoreObjective objective, BlockChangeByPlayerEvent event) {
    Location location = event.getBlock().getLocation();
    GameTask.of(
            "Leaker Mark " + objective.getName().translateDefault(),
            () -> {
              Block block = location.getBlock();
              if (block.getType().equals(Material.LAVA)
                  || block.getType().equals(Material.STATIONARY_LAVA)) {
                if (!objective.getLastBreak().isPresent()) {
                  DestroyableEventInfo info =
                      new DestroyableEventInfo(
                          event.getPlayer(),
                          event.getPlayer().getItemInHand(),
                          event.getBlock().getType(),
                          event.getCause() instanceof BlockBreakEvent);
                  objective.setLastBreak(Optional.of(info));
                }
              }
            })
        .later(45);
  }

  private void handleLavaFlow(BlockFromToEvent event, CoreObjective objective) {
    if (!objective.getAllowedLiquidTransformations().contains(event.getBlock().getType())) {
      event.setCancelled(true);
      return;
    }

    if (objective.getLiquidRegion().contains(event.getToBlock())) {
      event.setCancelled(true);
    }

    if (event.isCancelled()) {
      return;
    }

    Block to = event.getToBlock();
    Block from = event.getBlock();
    if ((from.getType().equals(Material.LAVA) || from.getType().equals(Material.STATIONARY_LAVA))
        && to.getType().equals(Material.AIR)) {
      if (objective.getLeakArea().contains(to) && !objective.isCompleted()) {
        event.setCancelled(false);

        objective.setCompleted(true);

        if (objective.getProperties().owner.isPresent()) {

        } else if (objective.getLastBreak().isPresent()) {

        }

        sidebarFacet.update(DestroyablesDisplay.objectiveSlug(objective));
        objective.getLastBreak().ifPresent(objective::onComplete);
      }
    }
  }
}

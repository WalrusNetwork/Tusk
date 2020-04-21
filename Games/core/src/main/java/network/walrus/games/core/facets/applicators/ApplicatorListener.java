package network.walrus.games.core.facets.applicators;

import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import network.walrus.games.core.GamesPlugin;
import network.walrus.games.core.facets.filters.Filter;
import network.walrus.games.core.facets.filters.FilterResult;
import network.walrus.games.core.facets.filters.context.FilterContext;
import network.walrus.games.core.facets.filters.variable.EntityVariable;
import network.walrus.games.core.facets.filters.variable.LocationVariable;
import network.walrus.games.core.facets.filters.variable.MaterialVariable;
import network.walrus.games.core.facets.filters.variable.PlayerVariable;
import network.walrus.games.core.facets.group.GroupsManager;
import network.walrus.ubiquitous.bukkit.events.world.BlockChangeByPlayerEvent;
import network.walrus.ubiquitous.bukkit.events.world.BlockChangeEvent;
import network.walrus.ubiquitous.bukkit.tracker.event.player.PlayerCoarseMoveEvent;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import network.walrus.utils.parsing.facet.parse.configurator.listener.FacetListener;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Listener which passes events to specific {@link Applicator}s.
 *
 * @author Austin Mayes
 */
@SuppressWarnings("ALL")
public class ApplicatorListener extends FacetListener<ApplicatorsFacet> {

  private static final Set<Material> ALLOW_RIGHT_CLICK =
      Sets.newHashSet(
          Material.BOW,
          Material.SNOW_BALL,
          Material.FISHING_ROD,
          Material.WOOD_SWORD,
          Material.STONE_SWORD,
          Material.IRON_SWORD,
          Material.GOLD_SWORD,
          Material.DIAMOND_SWORD);

  public static boolean logEnter = false;
  public static boolean logLeave = false;
  public static boolean logBreak = false;
  public static boolean logPlace = false;
  public static boolean logUse = false;

  private final GroupsManager manager;
  private final List<Applicator> applicators;

  /**
   * @param holder which this object is inside of
   * @param facet which this object is bound to
   */
  public ApplicatorListener(FacetHolder holder, ApplicatorsFacet facet) {
    super(holder, facet);
    applicators = facet.getApplicators();
    this.manager = holder.getFacetRequired(GroupsManager.class);
  }

  private boolean isObserving(Player player) {
    try {
      return this.manager.isObservingOrDead(player);
    } catch (RuntimeException e) {
      // Not in a group, count as observing.
      return true;
    }
  }

  @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
  public void onTP(PlayerTeleportEvent event) {
    if (isObserving(event.getPlayer())) {
      return;
    }

    for (Applicator applicator : applicators) {
      FilterResult res = handleMove(event.getPlayer(), event.getFrom(), event.getTo(), applicator);
      if (res.fails()) {
        event.setCancelled(true);
      }

      if (res.passes() || res.fails()) {
        break;
      }
    }
  }

  @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
  public void onMove(VehicleMoveEvent event) {
    if (event.getVehicle().getPassenger() != null
        && event.getVehicle().getPassenger() instanceof Player) {
      if (isObserving((Player) event.getVehicle().getPassenger())) {
        return;
      }
      for (Applicator applicator : applicators) {
        FilterResult res =
            handleMove(
                (Player) event.getVehicle().getPassenger(),
                event.getFrom(),
                event.getTo(),
                applicator);

        if (res.fails()) {
          event.getVehicle().remove();
          event.getVehicle().getPassenger().teleport(event.getFrom());
        }

        if (res.passes() || res.fails()) {
          break;
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
  public void onCoarseMove(PlayerCoarseMoveEvent event) {
    if (isObserving(event.getPlayer())) {
      return;
    }

    for (Applicator applicator : applicators) {
      FilterResult res = handleMove(event.getPlayer(), event.getFrom(), event.getTo(), applicator);
      if (res.fails()) {
        event.setCancelled(true);
      }

      if (res.passes() || res.fails()) {
        break;
      }
    }
  }

  private FilterResult handleMove(
      Player player, Location fromLoc, Location toLoc, Applicator applicator) {
    boolean to = applicator.getRegion().contains(toLoc);
    boolean from = applicator.getRegion().contains(fromLoc);

    // ignore if they are not coming from this region
    if (!from && !to) {
      return FilterResult.IGNORE;
    }

    if (from && !to && applicator.getLeave().isPresent()) {
      FilterResult res =
          applicator
              .getLeaveCache()
              .get(
                  player,
                  (p) -> {
                    FilterContext context = new FilterContext();
                    context.add(new PlayerVariable(p));
                    return applicator.getLeave().get().test(context, logLeave);
                  });
      if (logLeave) {
        GamesPlugin.instance
            .mapLogger()
            .info(applicator.getLeave().get().describe() + " - " + res.name());
      }
      if (res.fails()) {
        applicator.message(player);
      }
      if (res.passes() || res.fails()) {
        return res;
      }
    }

    if (to && !from) {
      if (applicator.getEnter().isPresent()) {
        FilterResult res =
            applicator
                .getEnterCache()
                .get(
                    player,
                    (p) -> {
                      FilterContext context = new FilterContext();
                      context.add(new PlayerVariable(p));
                      return applicator.getEnter().get().test(context, logEnter);
                    });
        if (logEnter) {
          GamesPlugin.instance
              .mapLogger()
              .info(applicator.getEnter().get().describe() + " - " + res.name());
        }
        if (res.fails()) {
          applicator.message(player);
        } else if (res.passes()) {
          applicator.onEnter(player);
        }

        if (res.passes() || res.fails()) {
          return res;
        }
      }
      // Assume entry if no filter applies or filter abstains
      applicator.onEnter(player);
    }

    return FilterResult.IGNORE;
  }

  @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
  public void blockChangeByPlayer(BlockChangeByPlayerEvent event) {
    this.onBlockChange(event);
  }

  @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
  public void onBlockChange(BlockChangeEvent event) {
    for (Applicator applicator : applicators) {
      if (!applicator.getRegion().contains(event.getBlock())) {
        continue;
      }

      if (applicator.getBlockBreak().isPresent()
          && (event.getCause() instanceof BlockBreakEvent
              || event.getCause() instanceof EntityExplodeEvent)) {
        FilterResult res = test(event, applicator.getBlockBreak().get(), logBreak);
        if (res.fails()) {
          if (event instanceof BlockChangeByPlayerEvent) {
            applicator.message(((BlockChangeByPlayerEvent) event).getPlayer());
          }
          event.setCancelled(true);
        }
        if (res.passes() || res.fails()) {
          break;
        }
      }

      if (applicator.getBlockPlace().isPresent() && event.getCause() instanceof BlockPlaceEvent) {
        FilterResult res = test(event, applicator.getBlockPlace().get(), logPlace);
        if (res.fails()) {
          if (event instanceof BlockChangeByPlayerEvent) {
            applicator.message(((BlockChangeByPlayerEvent) event).getPlayer());
          }
          event.setCancelled(true);
        }
        if (res.passes() || res.fails()) {
          break;
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onFlow(BlockFromToEvent event) {
    for (Applicator applicator : applicators) {
      if (!applicator.getRegion().contains(event.getToBlock())) {
        continue;
      }

      if (applicator.getBlockPlace().isPresent()) {
        FilterContext context = new FilterContext();
        context.add(new LocationVariable(event.getToBlock().getLocation()));
        FilterResult res = applicator.getBlockPlace().get().test(context);
        if (res.fails()) {
          event.setCancelled(true);
        }

        if (res.passes() || res.fails()) {
          break;
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onIgnite(BlockIgniteEvent event) {
    for (Applicator applicator : applicators) {
      if (event.getIgnitingBlock() != null && !applicator.getRegion().contains(event.getIgnitingBlock())) {
        continue;
      }

      if (applicator.getBlockBreak().isPresent()) {
        FilterContext context = new FilterContext();
        context.add(new LocationVariable(event.getIgnitingBlock().getLocation()));
        if (event.getIgnitingEntity() instanceof Player) {
          context.add(new PlayerVariable((Player) event.getIgnitingEntity()));
        }

        FilterResult res = applicator.getBlockBreak().get().test(context);
        if (res.fails()) {
          event.setCancelled(true);
        }

        if (res.passes() || res.fails()) {
          break;
        }
      }
    }
  }

  @EventHandler
  public void onBucketFill(PlayerBucketFillEvent event) {
    this.onBucket(event.getBlockClicked(), event);
  }

  @EventHandler
  public void onBucketEmpty(PlayerBucketEmptyEvent event) {
    this.onBucket(
        event.getBlockClicked().getRelative(event.getBlockFace()),
        event); // block clicked != where the liquid will be
  }

  private void onBucket(Block block, PlayerBucketEvent event) {
    for (Applicator applicator : applicators) {
      if (!applicator.getRegion().contains(block)) {
        continue;
      }

      if (applicator.getUse().isPresent()) {
        FilterResult res = test(event, block, applicator.getUse().get(), logUse);
        if (res.fails()) {
          applicator.message(event.getPlayer());
          event.setCancelled(true);
        }
        if (res.passes() || res.fails()) {
          break;
        }
      }

      if (applicator.getBlockBreak().isPresent() && event instanceof PlayerBucketFillEvent) {
        FilterResult res = test(event, block, applicator.getBlockBreak().get(), logBreak);
        if (res.fails()) {
          applicator.message(event.getPlayer());
          event.setCancelled(true);
        }
        if (res.passes() || res.fails()) {
          break;
        }
      }

      if (applicator.getBlockPlace().isPresent() && event instanceof PlayerBucketEmptyEvent) {
        FilterResult res = test(event, block, applicator.getBlockPlace().get(), logPlace);
        if (res.fails()) {
          applicator.message(event.getPlayer());
          event.setCancelled(true);
        }

        if (res.passes() || res.fails()) {
          break;
        }
      }
    }
  }

  @EventHandler
  public void onInteract(PlayerInteractEvent event) {
    if (event.getAction() == Action.RIGHT_CLICK_BLOCK
        && !isEdible(event.getItem())
        && (event.getItem() == null || !ALLOW_RIGHT_CLICK.contains(event.getItem().getType()))) {
      for (Applicator applicator : applicators) {
        if (!applicator.getRegion().contains(event.getClickedBlock())) {
          continue;
        }

        if (applicator.getUse().isPresent()) {
          FilterResult res = test(event, applicator.getUse().get(), logUse);
          if (res.fails()) {
            applicator.message(event.getPlayer());
            event.setCancelled(true);
          }

          if (res.passes() || res.fails()) {
            break;
          }
        }
      }
    } else if (event.getAction() == Action.LEFT_CLICK_BLOCK
        && event.getMaterial() == Material.ITEM_FRAME) {
      for (Applicator applicator : applicators) {
        if (!applicator.getRegion().contains(event.getClickedBlock())) {
          continue;
        }

        if (applicator.getBlockBreak().isPresent()) {
          FilterResult res = test(event, applicator.getBlockBreak().get(), logBreak);
          if (res.fails()) {
            applicator.message(event.getPlayer());
            event.setCancelled(true);
          }

          if (res.passes() || res.fails()) {
            break;
          }
        }
      }
    }
  }

  @EventHandler
  public void onItemFrameRotate(PlayerInteractEntityEvent event) {
    if (event.getRightClicked() instanceof ItemFrame) {
      for (Applicator applicator : applicators) {
        if (!applicator.getRegion().contains(event.getRightClicked())) {
          continue;
        }

        if (applicator.getUse().isPresent()) {
          FilterResult res = test(event, applicator.getUse().get(), logUse);
          if (res.fails()) {
            applicator.message(event.getPlayer());
            event.setCancelled(true);
          }

          if (res.passes() || res.fails()) {
            break;
          }
        }
      }
    }
  }

  private FilterResult test(BlockChangeEvent event, Filter check, boolean describe) {
    FilterContext context = new FilterContext();
    context.add(new MaterialVariable(event.getBlock().getState().getData()));
    context.add(new LocationVariable(event.getBlock().getLocation()));
    if (event instanceof BlockChangeByPlayerEvent) {
      context.add(new PlayerVariable(((BlockChangeByPlayerEvent) event).getPlayer()));
    }
    if (event.getCause() instanceof EntityExplodeEvent) {
      context.add(new EntityVariable(((EntityExplodeEvent) event.getCause()).getEntity()));
    }

    return test(check, context, describe);
  }

  private FilterResult test(PlayerBucketEvent event, Block block, Filter check, boolean describe) {
    FilterContext context = new FilterContext();
    context.add(new LocationVariable(block.getLocation()));
    context.add(new PlayerVariable(event.getPlayer()));
    return test(check, context, describe);
  }

  private FilterResult test(PlayerInteractEvent event, Filter check, boolean describe) {
    FilterContext context = new FilterContext();
    context.add(new LocationVariable(event.getClickedBlock().getLocation()));
    context.add(new PlayerVariable(event.getPlayer()));
    return test(check, context, describe);
  }

  private FilterResult test(PlayerInteractEntityEvent event, Filter check, boolean describe) {
    FilterContext context = new FilterContext();
    context.add(new LocationVariable(event.getRightClicked().getLocation()));
    context.add(new PlayerVariable(event.getPlayer()));
    return test(check, context, describe);
  }

  private FilterResult test(Filter check, FilterContext context, boolean describe) {
    FilterResult res = check.test(context, describe);
    if (describe) {
      GamesPlugin.instance.mapLogger().info(check.describe() + " - " + res.name());
    }
    return res;
  }

  private boolean isEdible(ItemStack item) {
    if(item == null) {
      return false;
    }
    return item.getType().isEdible();
  }
}

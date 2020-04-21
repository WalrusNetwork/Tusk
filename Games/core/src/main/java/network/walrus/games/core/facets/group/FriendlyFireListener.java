package network.walrus.games.core.facets.group;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import network.walrus.ubiquitous.bukkit.UbiquitousBukkitPlugin;
import network.walrus.ubiquitous.bukkit.tracker.trackers.DispenserTracker;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import network.walrus.utils.parsing.facet.parse.configurator.listener.FacetListener;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDispenseEntityEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.github.paperspigot.event.entity.EntityRemoveFromWorldEvent;

/**
 * If friendly fire is disabled, there are some types of damage which are not prevented by vanilla
 * Minecraft that we want to prevent (like TNT damage). This class handles those interactions and
 * cancels them where necessary,
 *
 * @author Rafi Baum
 */
public class FriendlyFireListener extends FacetListener<GroupsManager> {

  private final Map<Location, String> placedTnt = new HashMap<>();
  private final Map<TNTPrimed, String> primedTnt = new HashMap<>();
  private final DispenserTracker dispenserTracker;

  /**
   * @param holder which this object is inside of
   * @param facet which this object is bound to
   */
  public FriendlyFireListener(FacetHolder holder, GroupsManager facet) {
    super(holder, facet);
    this.dispenserTracker =
        UbiquitousBukkitPlugin.getInstance()
            .getTrackerSupervisor()
            .getManager()
            .getTracker(DispenserTracker.class);
  }

  /** Tracks TNT placement */
  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onTNTPlace(BlockPlaceEvent event) {
    if (event.getBlockPlaced().getType() != Material.TNT) {
      return;
    }

    Optional<Competitor> placedCompetitor = getFacet().getCompetitorOf(event.getPlayer());
    placedCompetitor.ifPresent(
        competitor -> placedTnt.put(event.getBlockPlaced().getLocation(), competitor.id()));
  }

  /** Tracks when TNT is broken */
  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onTNTBreak(BlockBreakEvent event) {
    placedTnt.remove(event.getBlock().getLocation());
  }

  /** Handle dispensers */
  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onDispense(BlockDispenseEntityEvent event) {
    if (!(event.getEntity() instanceof TNTPrimed)) {
      return;
    }

    if (dispenserTracker.hasPlacer(event.getBlock())) {
      OfflinePlayer player = dispenserTracker.getPlacer(event.getBlock());
      if (player.isOnline()) {
        Optional<Competitor> placedCompetitor = getFacet().getCompetitorOf((Player) player);
        placedCompetitor.ifPresent(
            competitor -> primedTnt.put((TNTPrimed) event.getEntity(), competitor.id()));
      }
    }
  }

  /** Tracks when TNT is primed, moving the mapping to the entity tracker */
  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onTNTPrime(ExplosionPrimeEvent event) {
    if (!(event.getEntity() instanceof TNTPrimed)) {
      return;
    }

    String primingCompetitor = placedTnt.get(event.getEntity().getLocation().toBlockLocation());
    if (primingCompetitor == null) {
      return;
    }
    primedTnt.put((TNTPrimed) event.getEntity(), primingCompetitor);
    placedTnt.remove(event.getEntity().getLocation().toBlockLocation());
  }

  /** Tracks when primed TNT is removed from the world */
  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onTNTRemove(EntityRemoveFromWorldEvent event) {
    if (event.getEntity() instanceof TNTPrimed) {
      primedTnt.remove(event.getEntity());
    }
  }

  /** Cancels own TNT damage */
  @EventHandler(ignoreCancelled = true)
  public void onTNTDamage(EntityDamageByEntityEvent event) {
    if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof TNTPrimed)) {
      return;
    }

    Optional<Competitor> playerCompetitor = getFacet().getCompetitorOf((Player) event.getEntity());
    if (!playerCompetitor.isPresent()
        || playerCompetitor.get().getGroup().isFriendlyFireEnabled()) {
      return;
    }

    if (!primedTnt.containsKey(event.getDamager())) {
      Bukkit.getLogger().warning("TNT has no tracked owner: " + event.getDamager().toString());
      return;
    }

    if (primedTnt.get((TNTPrimed) event.getDamager()).equals(playerCompetitor.get().id())) {
      event.setCancelled(true);
    }
  }
}

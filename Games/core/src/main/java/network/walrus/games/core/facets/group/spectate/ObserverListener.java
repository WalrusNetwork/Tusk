package network.walrus.games.core.facets.group.spectate;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import network.walrus.games.core.GamesPlugin;
import network.walrus.games.core.api.spawns.SpawnsManager;
import network.walrus.games.core.events.group.PlayerChangedGroupEvent;
import network.walrus.games.core.events.group.PlayerObserverStateChangeEvent;
import network.walrus.games.core.events.player.PlayerSpawnBeginEvent;
import network.walrus.games.core.events.round.RoundCloseEvent;
import network.walrus.games.core.events.round.RoundStateChangeEvent;
import network.walrus.games.core.facets.group.GroupsManager;
import network.walrus.ubiquitous.bukkit.events.world.BlockChangeByPlayerEvent;
import network.walrus.ubiquitous.bukkit.events.world.EntityChangeEvent;
import network.walrus.ubiquitous.bukkit.tracker.event.player.PlayerCoarseMoveEvent;
import network.walrus.ubiquitous.bukkit.tracker.event.player.PlayerDeathEvent;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import network.walrus.utils.parsing.facet.parse.configurator.listener.FacetListener;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.entity.EntityCombustByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.permissions.PermissionAttachment;

/**
 * Main listener which prevents players from doing things when they are observing (or dead).
 *
 * @author Avicus Network
 */
@SuppressWarnings("JavaDoc")
public class ObserverListener extends FacetListener<GroupsManager> {

  public static final Set<Material> TOOL_TYPES =
      Sets.newHashSet(Material.COMPASS, Material.WOOD_AXE);
  public static final Set<Material> BAD_TYPES =
      Sets.newHashSet(
          Material.WATER_LILY,
          Material.EYE_OF_ENDER,
          Material.BUCKET,
          Material.LAVA_BUCKET,
          Material.MILK_BUCKET,
          Material.WATER_BUCKET);
  public static final String[] OBSERVER_PERMS = {
    "worldedit.navigation.thru.tool",
    "worldedit.navigation.thru.command",
    "worldedit.navigation.jumpto.tool",
    "worldedit.navigation.jumpto.command"
  };

  private final Map<UUID, PermissionAttachment> attachments = Maps.newHashMap();

  public ObserverListener(FacetHolder holder, GroupsManager facet) {
    super(holder, facet);
  }

  private void recalculatePerms(Player player, boolean add) {
    PermissionAttachment attachment =
        attachments.computeIfAbsent(
            player.getUniqueId(), (uuid) -> player.addAttachment(GamesPlugin.instance));

    for (String permission : OBSERVER_PERMS) {
      if (add) {
        attachment.setPermission(permission, true);
      } else {
        attachment.unsetPermission(permission);
      }
    }

    player.recalculatePermissions();
  }

  private boolean notPlaying(Entity entity) {
    return (entity instanceof Player)
        && ((Player) entity).isOnline()
        && this.getFacet().isObservingOrDead((Player) entity);
  }

  private boolean holdingTool(Player player) {
    return player.getItemInHand() != null && TOOL_TYPES.contains(player.getItemInHand().getType());
  }

  // -----------------
  // -- Permissions --
  // -----------------

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onPlayerChangeGroup(PlayerChangedGroupEvent event) {
    boolean fromObserver =
        event.getGroupFrom().isPresent() && event.getGroupFrom().get().isObserving();
    boolean toObserver = event.getGroup().isObserving();

    // Ignore if observer state is the same
    if (fromObserver == toObserver) {
      return;
    }

    recalculatePerms(event.getPlayer(), toObserver);
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onMatchStateChange(RoundStateChangeEvent event) {
    for (Player player : event.getHolder().players()) {
      recalculatePerms(player, this.getFacet().getGroup(player).isObserving());
    }
  }

  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent event) {
    attachments.remove(event.getPlayer().getUniqueId());
  }

  @EventHandler
  public void onRoundClose(RoundCloseEvent event) {
    for (Entry<UUID, PermissionAttachment> entry : attachments.entrySet()) {
      UUID uuid = entry.getKey();
      PermissionAttachment attachment = entry.getValue();
      Player player = Bukkit.getPlayer(uuid);
      if (player != null) {
        player.removeAttachment(attachment);
      }
    }
  }

  @EventHandler
  public void onObserverChange(PlayerObserverStateChangeEvent event) {
    getFacet().refreshObserver(event.getPlayer());
  }

  @EventHandler
  public void onWorldChange(PlayerChangedWorldEvent event) {
    if (getFacet().isLoaded()) getFacet().refreshObserver(event.getPlayer());
  }

  // ------------
  // -- Spawns --
  // ------------

  @EventHandler
  public void onPlayerSpawn(PlayerSpawnBeginEvent event) {
    boolean observing = event.getGroup().isObserving();

    // Spigot
    event.getPlayer().spigot().setCollidesWithEntities(!observing);
    event.getPlayer().spigot().setAffectsSpawning(!observing);

    if (observing) {
      event.getPlayer().setGameMode(GameMode.CREATIVE);
      event.getPlayer().setAllowFlight(true);
      event.getPlayer().setFlying(true);
    }
  }

  // -----------------
  // -- Interaction --
  // -----------------

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onPlayerInteract(final PlayerInteractEvent event) {
    if (notPlaying(event.getPlayer()) && !holdingTool(event.getPlayer())) {
      event.setCancelled(true);
      event.setUseItemInHand(Event.Result.DENY);
      event.setUseInteractedBlock(Event.Result.DENY);
      // Right clicking armor
      event.getPlayer().updateInventory();
    }
  }

  @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
  public void onPlayerInteract(final PlayerArmorStandManipulateEvent event) {
    if (notPlaying(event.getPlayer())) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
  public void onPlayerInteract(final PlayerInteractEntityEvent event) {
    if (notPlaying(event.getPlayer())) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
  public void onPlayerInteract(final PlayerInteractAtEntityEvent event) {
    if (notPlaying(event.getPlayer())) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
  public void onSplash(PotionSplashEvent event) {
    for (LivingEntity affectedEntity : event.getAffectedEntities()) {
      if (notPlaying(affectedEntity)) {
        event.setIntensity(affectedEntity, 0);
      }
    }
  }

  @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
  public void onPlayerInteract(final ProjectileLaunchEvent event) {
    if (notPlaying(event.getActor())) {
      event.getEntity().remove();
      event.setCancelled(true);
    }
  }

  // ---------------
  // -- Inventory --
  // ---------------

  @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
  public void disallowBadItems(final InventoryClickEvent event) {
    if (!(event.getClickedInventory() instanceof PlayerInventory)) {
      return;
    }

    Player player = (Player) event.getWhoClicked();

    if (event.getCursor() == null) {
      return;
    }

    if (notPlaying(player)) {
      Material item = event.getCursor().getType();
      if (BAD_TYPES.contains(item) || item.name().toLowerCase().contains("door")) {
        event.setCancelled(true);
      }
    }
  }

  @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
  public void onInventoryClick(InventoryClickEvent event) {
    if (event.getWhoClicked() instanceof Player && notPlaying(event.getWhoClicked())) {
      if (event.getInventory().getType() != InventoryType.PLAYER) {
        event.setCancelled(true);
      }
    }
  }

  @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
  public void onPlayerPickupItem(PlayerPickupItemEvent event) {
    if (notPlaying(event.getPlayer())) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
  public void onPlayerDropItem(PlayerDropItemEvent event) {
    if (notPlaying(event.getPlayer())) {
      event.getItemDrop().remove();
    }
  }

  // ------------
  // -- Damage --
  // ------------

  @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
  public void onPlayerDamage(EntityDamageEvent event) {
    if (event.getEntity() instanceof Player && notPlaying(event.getEntity())) {
      event.setDamage(0);
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
  public void onPlayerDeath(PlayerDeathEvent event) {
    if (notPlaying(event.getEntity())) {
      event.getPlayer().setHealth(20);
    }
  }

  @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
  public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
    if (event.getDamager() instanceof Player && notPlaying(event.getDamager())) {
      event.setCancelled(true);
    }
  }

  // ------------
  // -- Entity --
  // ------------

  @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
  public void onEntityTarget(EntityTargetLivingEntityEvent event) {
    if (event.getTarget() instanceof Player && notPlaying(event.getTarget())) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
  public void onEntityChange(EntityChangeEvent event) {
    if (event.getWhoChanged() instanceof Player) {
      if (notPlaying(event.getWhoChanged())) {
        event.setCancelled(true);
      }
    }
  }

  @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
  public void onEntityCombustEvent(EntityCombustByBlockEvent event) {
    if (event.getEntity() instanceof Player && notPlaying(event.getEntity())) {
      event.getEntity().setFireTicks(0);
    }
  }

  // ------------
  // -- Blocks --
  // ------------

  @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
  public void onBlockChange(BlockChangeByPlayerEvent event) {
    if (notPlaying(event.getPlayer()) && !holdingTool(event.getPlayer())) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
  public void onBlockChange(BlockDamageEvent event) {
    if (notPlaying(event.getPlayer()) && !holdingTool(event.getPlayer())) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
  public void onBucketEmpty(PlayerBucketEmptyEvent event) {
    if (notPlaying(event.getPlayer())) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
  public void onBucketFill(PlayerBucketFillEvent event) {
    if (notPlaying(event.getPlayer())) {
      event.setCancelled(true);
    }
  }

  // --------------
  // -- Vehicles --
  // --------------

  @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
  public void onVehicleDamage(VehicleDamageEvent event) {
    if (event.getAttacker() instanceof Player && notPlaying(event.getAttacker())) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
  public void onVehicleEnter(VehicleEnterEvent event) {
    if (event.getEntered() instanceof Player && notPlaying(event.getEntered())) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
  public void onVehicleCollide(VehicleEntityCollisionEvent event) {
    if (event.getActor() instanceof Player && notPlaying(event.getActor())) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
  public void onVehicleExit(VehicleExitEvent event) {
    if (event.getExited() instanceof Player && notPlaying(event.getExited())) {
      event.setCancelled(true);
    }
  }

  /** Prevent observers from falling forever */
  @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
  public void onCoarseMove(PlayerCoarseMoveEvent event) {
    if (notPlaying(event.getPlayer())
        && event.getFrom().getY() >= -50
        && event.getTo().getY() < -50) {
      event.setCancelled(true);
      getHolder().getFacetRequired(SpawnsManager.class).spawn(event.getPlayer(), false);
    }
  }
}

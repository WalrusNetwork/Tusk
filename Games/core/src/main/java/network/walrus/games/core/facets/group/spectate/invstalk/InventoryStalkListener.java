package network.walrus.games.core.facets.group.spectate.invstalk;

import com.google.common.collect.Maps;
import java.util.Map;
import network.walrus.games.core.events.round.RoundCloseEvent;
import network.walrus.games.core.events.round.RoundOpenEvent;
import network.walrus.games.core.facets.group.GroupsManager;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import network.walrus.utils.parsing.facet.parse.configurator.listener.FacetListener;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

/**
 * Listener which creates and passes events to {@link StalkedInventory stalked inventories}.
 *
 * @author Austin Mayes
 */
public class InventoryStalkListener extends FacetListener<GroupsManager> {

  private final Map<Player, StalkedInventory> opened;
  private final InventoryStalker task;

  /**
   * @param holder which this object is inside of
   * @param facet which this object is bound to
   */
  public InventoryStalkListener(FacetHolder holder, GroupsManager facet) {
    super(holder, facet);
    this.opened = Maps.newHashMap();
    this.task = new InventoryStalker(this.opened);
  }

  /** Start the task. */
  @EventHandler
  public void open(RoundOpenEvent event) {
    this.task.repeat(0, 5);
  }

  /** Stop the task. */
  @EventHandler
  public void close(RoundCloseEvent event) {
    this.task.reset();
  }

  /** Helper method for observer checks. */
  private boolean isObserving(Player player) {
    // Only observers, not dead players.
    return this.getHolder().getFacetRequired(GroupsManager.class).isObserving(player);
  }

  /** Creates a tracked inventory and opens up the view. */
  private void trackInventory(Player player, Inventory inventory, String name) {
    Inventory view;
    if (inventory.getType() == InventoryType.CHEST) {
      view = Bukkit.createInventory(inventory.getHolder(), inventory.getSize(), name);
    } else if (inventory.getType() == InventoryType.PLAYER) {
      view = Bukkit.createInventory(inventory.getHolder(), 45, name);
    } else {
      view = Bukkit.createInventory(inventory.getHolder(), inventory.getType(), name);
    }

    StalkedInventory tracked = new StalkedInventory(player, inventory, view);
    tracked.open();
    this.opened.put(player, tracked);
  }

  /** Open player inventories. */
  @EventHandler
  public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
    if (!(event.getRightClicked() instanceof Player)) {
      return;
    }

    if (!isObserving(event.getPlayer())) {
      return;
    }

    Player player = event.getPlayer();
    Player target = (Player) event.getRightClicked();

    if (isObserving(target)) {
      return;
    }

    trackInventory(player, target.getInventory(), target.getName());
  }

  /** Open block inventories. */
  @EventHandler(priority = EventPriority.HIGH)
  public void onPlayerInteract(PlayerInteractEvent event) {
    if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
      return;
    }

    if (!isObserving(event.getPlayer())) {
      return;
    }

    Block block = event.getClickedBlock();

    // Matches all blocks that persist with items
    if (block.getState() instanceof InventoryHolder) {
      InventoryHolder container = (InventoryHolder) block.getState();
      trackInventory(event.getPlayer(), container.getInventory(), "");
    }
  }

  /** Disallow interaction with virtual inventories. */
  @EventHandler
  public void onInventoryClick(InventoryClickEvent event) {
    if (!(event.getWhoClicked() instanceof Player)) {
      return;
    }

    if (this.opened.keySet().contains(event.getWhoClicked())) {
      event.setCancelled(true);
    }
  }

  /** Clear inventory on quit. */
  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent event) {
    this.opened.remove(event.getPlayer());
  }
}

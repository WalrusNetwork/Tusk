package network.walrus.games.octc.ctf.flags;

import java.util.Optional;
import network.walrus.games.core.events.group.PlayerChangedGroupEvent;
import network.walrus.games.core.facets.group.GroupsManager;
import network.walrus.games.octc.OCNMessages;
import network.walrus.ubiquitous.bukkit.events.world.BlockChangeByPlayerEvent;
import network.walrus.ubiquitous.bukkit.events.world.BlockChangeEvent;
import network.walrus.ubiquitous.bukkit.tracker.event.player.PlayerCoarseMoveEvent;
import network.walrus.ubiquitous.bukkit.tracker.event.player.PlayerDeathEvent;
import network.walrus.utils.bukkit.item.ItemAttributesUtils;
import network.walrus.utils.core.color.NetworkColorConstants.Games.OCN.CTF;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import network.walrus.utils.parsing.facet.parse.configurator.listener.FacetListener;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Listener which dispatches events to {@link Net}s and {@link Post}s.
 *
 * @author Austin Mayes
 */
public class FlagListener extends FacetListener<FlagsFacet> {

  /**
   * @param holder which this object is inside of
   * @param facet which this object is bound to
   */
  public FlagListener(FacetHolder holder, FlagsFacet facet) {
    super(holder, facet);
  }

  private boolean atFlag(Location location) {
    if (isFlag(location.getBlock())) {
      return true;
    }

    location = location.clone().add(0, 1, 0);

    return isFlag(location.getBlock());
  }

  private boolean isFlag(Block block) {
    return block.getType() == Material.STANDING_BANNER || block.getType() == Material.WALL_BANNER;
  }

  /** Check if players who are carrying flags are entering net regions. */
  @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
  public void checkCapture(PlayerMoveEvent event) {
    Optional<FlagObjective> flag = getFacet().isCarrying(event.getPlayer());
    if (!flag.isPresent()) {
      return;
    }

    Net net = null;

    for (Net test : getFacet().getNets()) {
      if (test.getRegion().contains(event.getFrom())) {
        continue;
      }

      if (test.getRegion().contains(event.getTo())) {
        net = test;
        break;
      }
    }

    if (net == null) {
      return;
    }

    if (!net.canUse(event.getPlayer())) {
      event.getPlayer().sendMessage(OCNMessages.FLAG_CANT_CAPTURE_NET.with(CTF.CANT_CAPTURE));
      return;
    }

    flag.get().capture(event.getPlayer(), net);
  }

  /** Prevent players from breaking {@link FlagObjective}s. */
  @EventHandler(ignoreCancelled = true)
  public void onPlayerInteract(PlayerInteractEvent event) {
    if (!(event.getAction().equals(Action.LEFT_CLICK_BLOCK)
        || event.getAction().equals(Action.RIGHT_CLICK_BLOCK))) {
      return;
    }

    Block block = event.getClickedBlock();
    if (!isFlag(block)) {
      return;
    }

    Player player = event.getPlayer();

    if (getHolder().getFacetRequired(GroupsManager.class).isObserving(player)) {
      return;
    }

    for (FlagObjective flag : getFacet().getFlags()) {
      if (!flag.getCurrentLocation().equals(Optional.of(block.getLocation()))) {
        continue;
      }

      player.sendMessage(OCNMessages.FLAG_CANT_BREAK_POST.with(CTF.CANT_BREAK_POST));
      event.setCancelled(true);
      break;
    }
  }

  /** Prevent breaking block below flag. */
  @EventHandler(ignoreCancelled = true)
  public void onBreak(BlockBreakEvent event) {
    if (isFlag(event.getBlock())) {
      return; // Covered by above handler
    }

    for (FlagObjective flag : getFacet().getFlags()) {
      if (Optional.of(event.getBlock().getLocation().add(0, 1.0, 0))
          .equals(flag.getCurrentLocation())) {
        event.getPlayer().sendMessage(OCNMessages.FLAG_CANT_BREAK_POST.with(CTF.CANT_BREAK_POST));
        event.setCancelled(true);
        return;
      }
    }
  }

  /** Checks if players are standing over {@link FlagObjective}s and picks them up. */
  @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
  public void checkPickup(PlayerMoveEvent event) {
    if (!atFlag(event.getTo())) {
      return;
    }

    Player player = event.getPlayer();

    if (getHolder().getFacetRequired(GroupsManager.class).isObservingOrDead(player)) {
      return;
    }

    if (getFacet().isCarrying(player).isPresent()) {
      return;
    }

    for (FlagObjective flag : getFacet().getFlags()) {
      if (!flag.canPickup(event.getTo())) {
        continue;
      }

      if (!flag.canComplete(player)) {
        break;
      }

      flag.pickup(player);
      break;
    }
  }

  @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
  public void checkSafeDrops(PlayerCoarseMoveEvent event) {
    Optional<FlagObjective> flag = getFacet().isCarrying(event.getPlayer());
    if (flag.isPresent()) {
      flag.get().verifySafeDrop(event.getPlayer().getEyeLocation());
    }
  }

  /** @see #onBlockChange(BlockChangeEvent) */
  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void blockChangeByPlayer(BlockChangeByPlayerEvent event) {
    this.onBlockChange(event);
  }

  /** Prevent players from breaking {@link Post}s. */
  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onBlockChange(BlockChangeEvent event) {
    for (Post post : getFacet().getPosts()) {
      if (post.getRegion().contains(event.getBlock())) {
        if (event instanceof BlockChangeByPlayerEvent) {
          Player player = ((BlockChangeByPlayerEvent) event).getPlayer();
          player.sendMessage(OCNMessages.FLAG_CANT_BREAK_POST.with(CTF.CANT_BREAK_POST));
        }

        event.setCancelled(true);
        return;
      }
    }
  }

  private boolean dropFlagIfCarrying(Player player) {
    Optional<FlagObjective> flag = getFacet().isCarrying(player);
    if (flag.isPresent()) {
      flag.get().drop();
      return true;
    } else {
      return false;
    }
  }

  /** Drop flags on death. */
  @EventHandler
  public void onPlayerDeath(PlayerDeathEvent event) {
    ItemStack oldHelmet = event.getPlayer().getInventory().getHelmet();
    if (dropFlagIfCarrying(event.getPlayer())) {
      ItemStack newHelmet = event.getPlayer().getInventory().getHelmet();
      if (oldHelmet != null && !oldHelmet.equals(newHelmet)) {
        event.getDrops().remove(oldHelmet);
        if (newHelmet != null && ItemAttributesUtils.shouldDeathDrop(newHelmet)) {
          event.getDrops().add(newHelmet);
        }
      }
    }
  }

  /** Drop flags on quit. */
  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent event) {
    dropFlagIfCarrying(event.getPlayer());
  }

  /** Drop flags on team change. */
  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onPlayerChangeTeam(PlayerChangedGroupEvent event) {
    dropFlagIfCarrying(event.getPlayer());
  }

  /** Prevent manual helmet removal. */
  @EventHandler(ignoreCancelled = true)
  public void onInventoryClick(InventoryClickEvent event) {
    if (!(event.getWhoClicked() instanceof Player)) {
      return;
    }

    if (event.getClickedInventory().getType() != InventoryType.PLAYER) {
      return;
    }

    // Interaction is with player helmet
    if (event.getCurrentItem().equals(event.getWhoClicked().getInventory().getHelmet())) {
      Optional<FlagObjective> flag = getFacet().isCarrying((Player) event.getWhoClicked());
      if (flag.isPresent()) {
        event.setCancelled(true);
      }
    }
  }
}

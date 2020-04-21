package network.walrus.ubiquitous.bukkit.lobby.facets.sterile;

import network.walrus.utils.parsing.facet.holder.FacetHolder;
import network.walrus.utils.parsing.facet.parse.configurator.listener.FacetListener;
import network.walrus.utils.parsing.lobby.facets.spawns.LobbySpawnManager;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.FurnaceBurnEvent;

/**
 * Listener which blocks events from changing the lobby world.
 *
 * @author Austin Mayes
 */
public class WorldProtectionListener extends FacetListener {

  public static boolean IGNORE_ALL = false;

  /**
   * @param holder which this object is inside of
   * @param facet which this object is bound to
   */
  public WorldProtectionListener(FacetHolder holder, LobbySpawnManager facet) {
    super(holder, facet);
  }

  private boolean isInLobby(Location location) {
    return location.getWorld().equals(getHolder().getContainer().mainWorld());
  }

  /** Only allow certain mobs to spawn. */
  @EventHandler
  public void onCreatureSpawn(CreatureSpawnEvent event) {
    if (IGNORE_ALL) {
      return;
    }

    if (!isInLobby(event.getLocation())) {
      return;
    }

    event.setCancelled(true);

    switch (event.getSpawnReason()) {
      case CUSTOM:
      case SPAWNER:
      case SPAWNER_EGG:
      case DISPENSE_EGG:
        event.setCancelled(false);
    }
  }

  /** Block liquid flow. */
  @EventHandler
  public void onLiquidFlow(BlockFromToEvent event) {
    if (IGNORE_ALL) {
      return;
    }
    if (isInLobby(event.getBlock().getLocation())) {
      event.setCancelled(true);
    }
  }

  /** Disable burning. */
  @EventHandler
  public void onBlockBurn(BlockBurnEvent event) {
    if (IGNORE_ALL) {
      return;
    }
    if (isInLobby(event.getBlock().getLocation())) {
      event.setCancelled(true);
    }
  }

  /** Disable block fades. */
  @EventHandler
  public void onBlockFade(BlockFadeEvent event) {
    if (IGNORE_ALL) {
      return;
    }
    if (isInLobby(event.getBlock().getLocation())) {
      event.setCancelled(true);
    }
  }

  /** Disable block forms. */
  @EventHandler
  public void onBlockForm(BlockFormEvent event) {
    if (IGNORE_ALL) {
      return;
    }
    if (isInLobby(event.getBlock().getLocation())) {
      event.setCancelled(true);
    }
  }

  /** Disable blocks from growing. */
  @EventHandler
  public void onBlockGrow(BlockGrowEvent event) {
    if (IGNORE_ALL) {
      return;
    }
    if (isInLobby(event.getBlock().getLocation())) {
      event.setCancelled(true);
    }
  }

  /** Disable blocks from being lit on fire. */
  @EventHandler
  public void onBlockIgnite(BlockIgniteEvent event) {
    if (IGNORE_ALL) {
      return;
    }
    if (isInLobby(event.getBlock().getLocation())) {
      event.setCancelled(true);
    }
  }

  /** Disable redstone. */
  @EventHandler
  public void onBlockRedstone(BlockRedstoneEvent event) {
    if (IGNORE_ALL) {
      return;
    }
    if (isInLobby(event.getBlock().getLocation())) {
      event.setNewCurrent(event.getOldCurrent());
    }
  }

  /** Disable physics. */
  @EventHandler
  public void onBlockPhysics(BlockPhysicsEvent event) {
    if (IGNORE_ALL) {
      return;
    }
    if (isInLobby(event.getBlock().getLocation())) {
      event.setCancelled(true);
    }
  }

  /** Keep furnaces burning eternally. */
  @EventHandler
  public void onFurnaceBurn(FurnaceBurnEvent event) {
    if (IGNORE_ALL) {
      return;
    }
    if (isInLobby(event.getBlock().getLocation())) {
      event.setBurning(true);
      event.setCancelled(true);
    }
  }

  /** Block explosions. */
  @EventHandler
  public void onEntityExplode(EntityExplodeEvent event) {
    if (IGNORE_ALL) {
      return;
    }
    if (isInLobby(event.getLocation())) {
      event.setCancelled(true);
    }
  }
}

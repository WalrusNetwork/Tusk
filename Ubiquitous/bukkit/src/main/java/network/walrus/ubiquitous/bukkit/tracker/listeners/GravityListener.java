package network.walrus.ubiquitous.bukkit.tracker.listeners;

import com.google.common.base.Preconditions;
import javax.annotation.Nonnull;
import network.walrus.ubiquitous.bukkit.tracker.event.tag.NPCBecomePlayerEvent;
import network.walrus.ubiquitous.bukkit.tracker.event.tag.PlayerBecomeNPCEvent;
import network.walrus.ubiquitous.bukkit.tracker.trackers.base.gravity.SimpleGravityKillTracker;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerOnGroundEvent;

/** Listener which passes information to the {@link SimpleGravityKillTracker}. */
@SuppressWarnings("JavaDoc")
public class GravityListener implements Listener {

  private final @Nonnull SimpleGravityKillTracker tracker;

  /**
   * Constructor.
   *
   * @param tracker to pass gravity information to
   */
  public GravityListener(@Nonnull SimpleGravityKillTracker tracker) {
    Preconditions.checkNotNull(tracker, "tracker");
    this.tracker = tracker;
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onPlayerOnGroundChanged(final PlayerOnGroundEvent event) {
    this.tracker.playerOnOrOffGround(event.getPlayer(), event.getOnGround());
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onPlayerMove(final PlayerMoveEvent event) {
    this.tracker.playerMoved(event.getPlayer(), event.getTo());
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onPlayerDeath(final PlayerDeathEvent event) {
    this.tracker.cancelFall(event.getEntity());
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onPlayerGameModeChange(final PlayerGameModeChangeEvent event) {
    if (event.getNewGameMode() == GameMode.CREATIVE) {
      this.tracker.cancelFall(event.getPlayer());
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onBlockBreak(final BlockBreakEvent event) {
    this.tracker.blockBroken(event.getBlock(), event.getPlayer());
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onAttack(final EntityDamageByEntityEvent event) {
    if (event.getEntity() instanceof Player) {
      this.tracker.playerAttacked((Player) event.getEntity(), event.getDamager());
    }
  }

  @EventHandler
  public void onChange(PlayerBecomeNPCEvent event) {
    tracker.transferOwnership(event.getPlayer(), event.getState());
  }

  @EventHandler
  public void onChange(NPCBecomePlayerEvent event) {
    tracker.transferOwnership(event.getState(), event.getPlayer());
  }
}

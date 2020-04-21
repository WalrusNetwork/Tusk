package network.walrus.ubiquitous.bukkit.tracker.listeners;

import network.walrus.ubiquitous.bukkit.tracker.event.tag.NPCBecomePlayerEvent;
import network.walrus.ubiquitous.bukkit.tracker.event.tag.PlayerBecomeNPCEvent;
import network.walrus.ubiquitous.bukkit.tracker.trackers.OwnedMobTracker;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSpawnEntityEvent;

/** Listener which passes information to the {@link OwnedMobTracker}. */
@SuppressWarnings("JavaDoc")
public class OwnedMobListener implements Listener {

  private final OwnedMobTracker tracker;

  /**
   * Constructor.
   *
   * @param tracker to send data to
   */
  public OwnedMobListener(OwnedMobTracker tracker) {
    this.tracker = tracker;
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onMonsterSpawn(PlayerSpawnEntityEvent event) {
    if (!this.tracker.isEnabled(event.getEntity().getWorld())) {
      return;
    }

    if (event.getEntity() instanceof LivingEntity) {
      this.tracker.setOwner((LivingEntity) event.getEntity(), event.getPlayer());
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

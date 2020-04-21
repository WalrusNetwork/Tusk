package network.walrus.ubiquitous.bukkit.tracker.listeners;

import com.google.common.base.Preconditions;
import javax.annotation.Nonnull;
import network.walrus.ubiquitous.bukkit.tracker.manager.TrackerManager;
import network.walrus.ubiquitous.bukkit.tracker.trackers.Tracker;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldUnloadEvent;

/** Listener responsible for clearing tracker data for worlds when they are unloaded. */
@SuppressWarnings("JavaDoc")
public class WorldListener implements Listener {

  private final @Nonnull TrackerManager manager;

  /**
   * Constructor.
   *
   * @param manager to send world updates to
   */
  public WorldListener(@Nonnull TrackerManager manager) {
    Preconditions.checkNotNull(manager, "tracker manager");

    this.manager = manager;
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onWorldUnload(final WorldUnloadEvent event) {
    for (Tracker tracker : this.manager.getTrackers()) {
      tracker.clear(event.getWorld());
    }
  }
}

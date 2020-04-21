package network.walrus.ubiquitous.bukkit.tracker.listeners;

import network.walrus.ubiquitous.bukkit.tracker.trackers.ProjectileDistanceTracker;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;

/** Listener which passes information to the {@link ProjectileDistanceTracker}. */
@SuppressWarnings("JavaDoc")
public class ProjectileDistanceListener implements Listener {

  private final ProjectileDistanceTracker tracker;

  /**
   * Constructor.
   *
   * @param tracker to send data to
   */
  public ProjectileDistanceListener(ProjectileDistanceTracker tracker) {
    this.tracker = tracker;
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onProjectileLaunch(ProjectileLaunchEvent event) {
    if (!this.tracker.isEnabled(event.getEntity().getWorld())) {
      return;
    }

    this.tracker.setLaunchLocation(event.getEntity(), event.getEntity().getLocation());
  }
}

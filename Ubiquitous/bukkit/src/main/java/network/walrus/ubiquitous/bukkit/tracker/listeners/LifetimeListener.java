package network.walrus.ubiquitous.bukkit.tracker.listeners;

import network.walrus.ubiquitous.bukkit.tracker.event.tag.NPCBecomePlayerEvent;
import network.walrus.ubiquitous.bukkit.tracker.event.tag.PlayerBecomeNPCEvent;
import network.walrus.ubiquitous.bukkit.tracker.lifetime.LifetimeManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

/** Listener responsible for keeping the {@link LifetimeManager} up to date. */
@SuppressWarnings("JavaDoc")
public class LifetimeListener implements Listener {

  private final LifetimeManager manager;

  /**
   * Constructor.
   *
   * @param manager to send lifetime data to
   */
  public LifetimeListener(LifetimeManager manager) {
    this.manager = manager;
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onPlayerRespawn(final PlayerRespawnEvent event) {
    manager.newLifetime(event.getPlayer());
  }

  @EventHandler
  public void onChange(PlayerBecomeNPCEvent event) {
    manager.transferOwnership(event.getPlayer(), event.getState());
  }

  @EventHandler
  public void onChange(NPCBecomePlayerEvent event) {
    manager.transferOwnership(event.getState(), event.getPlayer());
  }
}

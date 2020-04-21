package network.walrus.utils.bukkit.visual.hologram;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class HologramManager implements Listener {

  private final Map<String, Hologram> hologramsById = Maps.newHashMap();
  private final Multimap<World, Hologram> activeHolograms = HashMultimap.create();

  /**
   * Moves a Hologram to the provided Location.
   *
   * @param id of the hologram to move
   * @param location location to which the Hologram will be moved to
   */
  public void move(String id, Location location) {
    Hologram hologram = getHologram(id);
    if (!hologram.location().getWorld().equals(location.getWorld())) {
      hologram.location().getWorld().getPlayers().forEach(hologram::despawnEntities);
      activeHolograms.remove(hologram.world(), hologram);
      activeHolograms.put(location.getWorld(), hologram);
    }
    hologram.setLocation(location);
    location.getWorld().getPlayers().forEach(hologram::spawnEntities);
  }

  /**
   * Add a hologram to the manager.
   *
   * @param id used to reference the hologram
   * @param hologram to add
   */
  public void addHologram(String id, Hologram hologram) {
    hologramsById.put(id, hologram);
    activeHolograms.put(hologram.world(), hologram);
  }

  /**
   * Remove a hologram from the manager.
   *
   * @param id of the hologram to remove
   */
  public void removeHologram(String id) {
    Hologram hologram = getHologram(id);
    activeHolograms.remove(hologram.world(), hologram);
  }

  /**
   * Get a hologram by ID.
   *
   * @param id to search for
   * @return the hologram mapped to the specified ID
   */
  public Hologram getHologram(String id) {
    if (!this.hologramsById.containsKey(id))
      throw new IllegalArgumentException("Hologram with ID " + id + " not found!");
    return this.hologramsById.get(id);
  }

  /** Render all holograms on spawn. */
  @EventHandler(priority = EventPriority.MONITOR)
  public void onSpawn(PlayerJoinEvent event) {
    activeHolograms.get(event.getWorld()).forEach(h -> h.spawnEntities(event.getPlayer()));
  }

  /** Destroy all holograms on leave. */
  @EventHandler(priority = EventPriority.LOW)
  public void onQuit(PlayerQuitEvent event) {
    activeHolograms.get(event.getWorld()).forEach(h -> h.despawnEntities(event.getPlayer()));
  }

  /** Destroy all holograms and render new ones for players when they change worlds. */
  @EventHandler(priority = EventPriority.MONITOR)
  public void onChangeWorld(PlayerChangedWorldEvent event) {
    World from = event.getFrom();
    World to = event.getWorld();
    if (from != null) activeHolograms.get(from).forEach(h -> h.despawnEntities(event.getPlayer()));
    activeHolograms.get(to).forEach(h -> h.spawnEntities(event.getPlayer()));
  }
}

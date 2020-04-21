package network.walrus.ubiquitous.bukkit.lobby.facets.parkour;

import java.util.Optional;
import java.util.Set;
import network.walrus.ubiquitous.bukkit.tracker.event.player.PlayerCoarseMoveEvent;
import network.walrus.utils.bukkit.region.BoundedRegion;
import network.walrus.utils.parsing.facet.Facet;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Handles parkour areas for lobbies.
 *
 * @author Austin Mayes
 */
public class ParkourManager extends Facet implements Listener {

  private final Set<Parkour> parkours;

  public ParkourManager(Set<Parkour> parkours) {
    this.parkours = parkours;
  }

  @Override
  public void enable() {}

  private Optional<Parkour> getCurrent(Player player) {
    for (Parkour p : parkours) {
      if (p.isParticipating(player)) {
        return Optional.of(p);
      }
    }
    return Optional.empty();
  }

  @EventHandler
  public void onMove(PlayerCoarseMoveEvent event) {
    Optional<Parkour> current = getCurrent(event.getPlayer());
    if (current.isPresent()) {
      ParkourStage stage = current.get().getStage(event.getPlayer());
      if (!stage.isOutOfBounds(event.getFrom()) && stage.isOutOfBounds(event.getTo())) {
        current.get().failed(event.getPlayer());
        return;
      }
      if (!stage.getEnd().contains(event.getFrom()) && stage.getEnd().contains(event.getTo()))
        current.get().advance(event.getPlayer());
    } else {
      for (Parkour parkour : parkours) {
        BoundedRegion entrance = parkour.entrance();
        if (!entrance.contains(event.getFrom()) && entrance.contains(event.getTo())) {
          parkour.enter(event.getPlayer());
          return;
        }
      }
    }
  }
}

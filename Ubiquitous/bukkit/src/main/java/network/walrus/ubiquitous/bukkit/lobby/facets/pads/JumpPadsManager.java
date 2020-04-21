package network.walrus.ubiquitous.bukkit.lobby.facets.pads;

import java.util.Set;
import network.walrus.ubiquitous.bukkit.tracker.event.player.PlayerCoarseMoveEvent;
import network.walrus.utils.parsing.facet.Facet;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Handles jump pads for lobbies.
 *
 * @author Austin Mayes
 */
public class JumpPadsManager extends Facet implements Listener {

  private final Set<JumpPad> pads;

  /** @param pads which should be usable by players */
  public JumpPadsManager(Set<JumpPad> pads) {
    this.pads = pads;
  }

  /** Make players jump when they get on pads. */
  @EventHandler
  public void onMove(PlayerCoarseMoveEvent event) {
    for (JumpPad pad : pads) {
      if (pad.getWhere().contains(event.getTo()) && !pad.getWhere().contains(event.getFrom())) {
        pad.use(event.getPlayer());
        return;
      }
    }
  }
}

package network.walrus.games.core.facets.portals;

import java.util.List;
import network.walrus.ubiquitous.bukkit.tracker.event.player.PlayerCoarseMoveEvent;
import network.walrus.utils.parsing.facet.Facet;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 * Holds a collection of {@link Portal}s and passes {@link PlayerCoarseMoveEvent}s to them in
 * definition order, stopping when one portal successfully teleports a player.
 *
 * @author Austin Mayes
 */
public class PortalsFacet extends Facet implements Listener {

  private final FacetHolder holder;
  private final List<Portal> portals;

  /**
   * @param holder which these portals are operating in
   * @param portals which have been defined by the user
   */
  PortalsFacet(FacetHolder holder, List<Portal> portals) {
    this.holder = holder;
    this.portals = portals;
  }

  /**
   * Gets the portals that are in this very same portal facet
   *
   * @return the portals
   */
  public List<Portal> portals() {
    return portals;
  }

  /**
   * Only check on moves, since other location change events (tp, vehicle) are unpredictable and
   * cause issues.
   */
  @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
  public void onCoarseMove(PlayerCoarseMoveEvent event) {
    for (Portal portal : portals) {
      if (portal.attemptTeleport(holder, event)) {
        break;
      }
    }
  }
}

package network.walrus.games.octc.hills;

import java.util.Collection;
import network.walrus.games.core.events.group.PlayerChangedGroupEvent;
import network.walrus.games.core.facets.group.GroupsManager;
import network.walrus.games.octc.hills.domination.DomFacet;
import network.walrus.games.octc.hills.koth.KothFacet;
import network.walrus.ubiquitous.bukkit.tracker.event.player.PlayerCoarseMoveEvent;
import network.walrus.ubiquitous.bukkit.tracker.event.player.PlayerDeathEvent;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import network.walrus.utils.parsing.facet.parse.configurator.listener.FacetListener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Listens for events that effect the hills
 *
 * @author Matthew Arnold
 */
public class HillListener extends FacetListener<HillFacet> {

  private final Collection<HillObjective> hills;
  private final GroupsManager groupsManager;

  /**
   * Creates a new hill listener for the domination point facet
   *
   * @param facetHolder the facet holder
   * @param hillFacet the domination point facet
   */
  public HillListener(FacetHolder facetHolder, DomFacet hillFacet) {
    super(facetHolder, hillFacet);
    this.hills = hillFacet.hills();
    this.groupsManager = facetHolder.getFacetRequired(GroupsManager.class);
  }

  /**
   * Creates a new hill listener for the koth facet
   *
   * @param facetHolder the facet holder
   * @param hillFacet the koth facet
   */
  public HillListener(FacetHolder facetHolder, KothFacet hillFacet) {
    super(facetHolder, hillFacet);
    this.hills = hillFacet.hills();
    this.groupsManager = facetHolder.getFacetRequired(GroupsManager.class);
  }

  /** Add/remove players based on location. */
  @EventHandler
  public void onPlayerCoarseMove(PlayerCoarseMoveEvent event) {
    if (groupsManager.isObservingOrDead(event.getPlayer())) {
      return;
    }

    Player player = event.getPlayer();

    for (HillObjective controllable : this.hills) {
      boolean inside = controllable.getCapture().contains(event.getTo().getBlock());

      if (inside) {
        controllable.add(player);
      } else {
        controllable.remove(player);
      }
    }
  }

  /** Remove players when they leave. */
  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent event) {
    for (HillObjective controllable : this.hills) {
      controllable.remove(event.getPlayer());
    }
  }

  /** Remove players when they leave. */
  @EventHandler
  public void onChangeTeam(PlayerChangedGroupEvent event) {
    if (!event.getGroupFrom().isPresent()) {
      return;
    }

    for (HillObjective controllable : this.hills) {
      controllable.remove(event.getPlayer());
    }
  }

  /** Remove players when they die. */
  @EventHandler
  public void onPlayerDeath(PlayerDeathEvent event) {
    for (HillObjective controllable : this.hills) {
      controllable.remove(event.getPlayer());
    }
  }
}

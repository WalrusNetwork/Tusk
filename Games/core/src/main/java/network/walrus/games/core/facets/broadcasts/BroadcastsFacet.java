package network.walrus.games.core.facets.broadcasts;

import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import network.walrus.games.core.events.round.RoundStateChangeEvent;
import network.walrus.utils.parsing.facet.Facet;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Contains a list of {@link Broadcast} and handles their scheduling/cancellation.
 *
 * @author Rafi Baum
 */
public class BroadcastsFacet extends Facet implements Listener {

  private final FacetHolder holder;
  private final List<Broadcast> broadcasts;
  private final Map<Broadcast, Integer> tasks = Maps.newHashMap();

  /**
   * @param holder that the facet is in
   * @param broadcasts list of broadcasts to schedule
   */
  public BroadcastsFacet(FacetHolder holder, List<Broadcast> broadcasts) {
    this.holder = holder;
    this.broadcasts = broadcasts;
  }

  private void init() {
    for (Broadcast broadcast : broadcasts) {
      broadcast.schedule();
    }
  }

  private void cancelAll() {
    for (Broadcast broadcast : broadcasts) {
      broadcast.reset();
    }
  }

  /** Modify tasks based on round state. */
  @EventHandler
  public void onStateChange(RoundStateChangeEvent event) {
    if (event.isChangeToPlaying()) {
      init();
    } else {
      cancelAll();
    }
  }
}

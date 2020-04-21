package network.walrus.games.octc.ctw.wools;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import network.walrus.games.core.facets.stats.trackers.Tracker;
import network.walrus.games.octc.ctw.wools.events.WoolPlaceEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

/**
 * Tracks the number of wools players place.
 *
 * @author Rafi Baum
 */
public class WoolTracker extends Tracker<Integer> {

  private final Map<UUID, AtomicInteger> playerWools;
  private final Map<UUID, AtomicInteger> cachedPlayerWools;

  public WoolTracker() {
    this.playerWools = new HashMap<>();
    this.cachedPlayerWools = new HashMap<>();
  }

  @Override
  public double getScore(UUID uuid) {
    return playerWools.getOrDefault(uuid, new AtomicInteger()).get();
  }

  @Override
  public Integer fetchUpdate(Player player) {
    AtomicInteger wools = cachedPlayerWools.remove(player.getUniqueId());
    if (wools == null) {
      return 0;
    } else {
      return wools.get();
    }
  }

  @Override
  public void reset(UUID uuid) {
    playerWools.remove(uuid);
    cachedPlayerWools.remove(uuid);
  }

  @EventHandler
  public void onWoolPlace(WoolPlaceEvent event) {
    for (Player player : event.getPlayers()) {
      AtomicInteger wools =
          playerWools.computeIfAbsent(player.getUniqueId(), (uuid -> new AtomicInteger()));
      wools.incrementAndGet();

      wools =
          cachedPlayerWools.computeIfAbsent(player.getUniqueId(), (uuid -> new AtomicInteger()));
      wools.incrementAndGet();
    }
  }
}

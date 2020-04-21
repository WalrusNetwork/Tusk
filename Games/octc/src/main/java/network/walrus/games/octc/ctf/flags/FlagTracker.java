package network.walrus.games.octc.ctf.flags;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import network.walrus.games.core.facets.stats.trackers.Tracker;
import network.walrus.games.octc.ctf.flags.events.FlagCaptureEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

/**
 * Tracker for flag captures.
 *
 * @author Rafi Baum
 */
public class FlagTracker extends Tracker<Integer> {
  private final Map<UUID, AtomicInteger> playerFlags;
  private final Map<UUID, AtomicInteger> cachedPlayerFlags;

  public FlagTracker() {
    this.playerFlags = new HashMap<>();
    this.cachedPlayerFlags = new HashMap<>();
  }

  @Override
  public double getScore(UUID uuid) {
    return playerFlags.getOrDefault(uuid, new AtomicInteger()).get();
  }

  @Override
  public Integer fetchUpdate(Player player) {
    AtomicInteger flags = cachedPlayerFlags.remove(player.getUniqueId());
    if (flags == null) {
      return 0;
    } else {
      return flags.get();
    }
  }

  @Override
  public void reset(UUID uuid) {
    playerFlags.remove(uuid);
    cachedPlayerFlags.remove(uuid);
  }

  @EventHandler
  public void onFlagCapture(FlagCaptureEvent event) {
    for (Player player : event.getPlayers()) {
      AtomicInteger flags =
          playerFlags.computeIfAbsent(player.getUniqueId(), (uuid -> new AtomicInteger()));
      flags.incrementAndGet();

      flags =
          cachedPlayerFlags.computeIfAbsent(player.getUniqueId(), (uuid -> new AtomicInteger()));
      flags.incrementAndGet();
    }
  }
}

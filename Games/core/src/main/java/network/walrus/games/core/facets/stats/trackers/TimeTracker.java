package network.walrus.games.core.facets.stats.trackers;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Map;
import java.util.UUID;
import network.walrus.games.core.events.group.PlayerChangedGroupEvent;
import network.walrus.games.core.events.round.RoundStateChangeEvent;
import network.walrus.games.core.round.GameRound;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

/**
 * Tracker which keeps track of how long a user has been participating in a round for.
 *
 * @author Rafi Baum
 */
public class TimeTracker extends PersistentTracker<Long> {

  private final GameRound round;
  private final Map<UUID, Long> sessionTimes;
  private final Map<UUID, Long> startTimes;

  public TimeTracker(FacetHolder holder) {
    this.round = (GameRound) holder;
    this.sessionTimes = Maps.newHashMap();
    this.startTimes = Maps.newHashMap();
  }

  /**
   * @param uuid of the player
   * @return how long the player has been participating in the match for
   */
  public long getTimePlayed(UUID uuid) {
    long timePlayed = 0L;
    if (sessionTimes.containsKey(uuid)) {
      timePlayed += sessionTimes.get(uuid);
    }

    if (startTimes.containsKey(uuid)) {
      timePlayed += round.getPlayingDuration().getSeconds() - startTimes.get(uuid);
    }

    return timePlayed;
  }

  /**
   * @param player
   * @return how long the player has been participating in the match for
   */
  public long getTimePlayed(Player player) {
    return getTimePlayed(player.getUniqueId());
  }

  private void startSession(UUID uuid) {
    startTimes.put(uuid, round.getPlayingDuration().getSeconds());
  }

  private void stopSession(UUID uuid) {
    long sessionSeconds = sessionTimes.getOrDefault(uuid, 0L);
    sessionSeconds += round.getPlayingDuration().getSeconds() - startTimes.get(uuid);
    sessionTimes.put(uuid, sessionSeconds);
    startTimes.remove(uuid);
  }

  @EventHandler
  public void onGroupChange(PlayerChangedGroupEvent event) {
    if (!round.getState().playing()) {
      return;
    }

    boolean fromSpectator = true;
    if (event.getGroupFrom().isPresent()) {
      fromSpectator = event.getGroupFrom().get().isSpectator();
    }

    if (fromSpectator && !event.getGroup().isSpectator()) {
      startSession(event.getPlayer().getUniqueId());
    } else if (!fromSpectator && event.getGroup().isSpectator()) {
      stopSession(event.getPlayer().getUniqueId());
    }
  }

  @EventHandler
  public void onRoundStateChange(RoundStateChangeEvent event) {
    if (event.isChangeToPlaying()) {
      for (Player player : round.playingPlayers()) {
        startSession(player.getUniqueId());
      }
    } else if (event.isChangeToNotPlaying()) {
      for (UUID uuid : Sets.newHashSet(startTimes.keySet())) {
        stopSession(uuid);
      }
    }
  }

  @Override
  public double getScore(UUID uuid) {
    return getTimePlayed(uuid) / 60.0 * .2;
  }

  @Override
  public Long fetchUpdate(Player player) {
    throw new UnsupportedOperationException("Not implemented!"); // TODO
  }

  @Override
  public void reset(UUID uuid) {
    startTimes.remove(uuid);
    sessionTimes.remove(uuid);

    Player player = Bukkit.getPlayer(uuid);
    if (player != null && round.playingPlayers().contains(player)) {
      startSession(uuid);
    }
  }
}

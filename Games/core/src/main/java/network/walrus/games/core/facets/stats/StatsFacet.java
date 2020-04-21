package network.walrus.games.core.facets.stats;

import com.apollographql.apollo.ApolloCall.Callback;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.google.common.collect.Maps;
import gg.walrus.javaapiclient.UserStatsQuery;
import gg.walrus.javaapiclient.UserUpdateAresStatsMutation;
import gg.walrus.javaapiclient.UserUpdateAresStatsMutation.Data;
import gg.walrus.javaapiclient.UserUpdateAresStatsMutation.UpdateAresStats;
import gg.walrus.javaapiclient.type.AresStatsInput;
import gg.walrus.javaapiclient.type.AresStatsInput.Builder;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import network.walrus.games.core.events.competitor.PlayerChangeCompetitorEvent;
import network.walrus.games.core.facets.stats.trackers.DeathTracker;
import network.walrus.games.core.facets.stats.trackers.KillTracker;
import network.walrus.games.core.facets.stats.trackers.PersistentTracker;
import network.walrus.games.core.facets.stats.trackers.TimeTracker;
import network.walrus.games.core.facets.stats.trackers.Tracker;
import network.walrus.nerve.bukkit.NerveBukkitPlugin;
import network.walrus.nerve.core.api.exception.ApiException;
import network.walrus.utils.parsing.facet.Facet;
import network.walrus.utils.parsing.facet.FacetLoadException;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Facet which is responsible for tracking a variety of metrics during the course of a game round.
 *
 * @author Rafi Baum
 */
public abstract class StatsFacet extends Facet {
  // TODO: This API needs to be refactored to take online stats into account
  private final FacetHolder holder;
  private final Map<Class<? extends Tracker>, Tracker> trackers;
  private final Map<Player, UserStatsQuery.Ares> cachedStats;

  public StatsFacet(FacetHolder holder) {
    this.holder = holder;
    this.trackers = Maps.newHashMap();
    this.cachedStats = Maps.newHashMap();
  }

  @Override
  public void load() throws FacetLoadException {
    super.load();
    addTracker(new DeathTracker(holder));
    addTracker(new KillTracker(holder));
    addTracker(new TimeTracker(holder));
  }

  @Override
  public void unload() {
    for (Player player : holder.getContainer().players()) {
      updateStatsFor(player);
    }

    for (Tracker tracker : trackers.values()) {
      tracker.unload();
    }
  }

  /**
   * Add a {@link Tracker} to the facet.
   *
   * @param tracker to add
   */
  public void addTracker(Tracker tracker) {
    trackers.put(tracker.getClass(), tracker);
  }

  /**
   * Get an instance of a tracker from the facet.
   *
   * @param type of tracker to return
   * @param <T> tracker type
   * @return instance of the tracker
   */
  public <T extends Tracker> Optional<T> getTracker(Class<T> type) {
    return (Optional<T>) Optional.ofNullable(trackers.get(type));
  }

  /**
   * Returns the sum total value of a player's contributions during a round.
   *
   * @param uuid of the player
   * @return the total value of a player's contributions
   */
  public int getScore(UUID uuid) {
    int score = 0;
    for (Tracker tracker : trackers.values()) {
      score += tracker.getScore(uuid);
    }

    return score;
  }

  /**
   * Returns the sum total value of a player's contributions during a round.
   *
   * @param player
   * @return the total value of a player's contributions
   */
  public int getScore(Player player) {
    return getScore(player.getUniqueId());
  }

  @EventHandler
  public void onCompChange(PlayerChangeCompetitorEvent event) {
    for (Tracker tracker : trackers.values()) {
      if (!(tracker instanceof PersistentTracker)) {
        tracker.reset(event.getPlayer());
      }
    }
  }

  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent event) {
    updateStatsFor(event.getPlayer());
  }

  void updateStatsFor(Player player) {
    updateStatsFor(player, Optional.empty());
  }

  void updateStatsFor(Player player, Optional<Consumer<UpdateAresStats>> func) {
    ApolloClient client = NerveBukkitPlugin.instance().getApiClient();
    if (client == null) {
      return;
    }

    Builder builder = AresStatsInput.builder();
    updateStats(player, builder);

    client
        .mutate(
            UserUpdateAresStatsMutation.builder()
                .username(player.getName())
                .stats(builder.build())
                .build())
        .enqueue(
            new Callback<Data>() {
              @Override
              public void onResponse(@NotNull Response<Data> response) {
                if (response.hasErrors()) {
                  throw new ApiException(response.errors());
                }

                if (func.isPresent()) {
                  func.get().accept(response.data().updateAresStats());
                }
              }

              @Override
              public void onFailure(@NotNull ApolloException e) {
                e.printStackTrace();
              }
            });
  }

  protected void updateStats(Player player, AresStatsInput.Builder builder) {
    builder.kills(getTracker(KillTracker.class).get().fetchUpdate(player));
    builder.deaths(getTracker(DeathTracker.class).get().fetchUpdate(player));
  }
}

package network.walrus.games.core.facets.stats.trackers;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import network.walrus.games.core.events.round.RoundOpenEvent;
import network.walrus.games.core.facets.group.Competitor;
import network.walrus.games.core.facets.group.GroupsManager;
import network.walrus.ubiquitous.bukkit.tracker.event.player.PlayerDeathEvent;
import network.walrus.ubiquitous.bukkit.tracker.event.tag.TaggedPlayerDeathEvent;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

/**
 * Tracker which tracks player deaths and total deaths per competitor.
 *
 * @author Rafi Baum
 */
public class DeathTracker extends Tracker<Integer> {

  private final FacetHolder holder;
  private final Map<UUID, AtomicInteger> playerDeaths;
  private final Map<UUID, Integer> uncachedDeaths;
  private final Map<Competitor, AtomicInteger> competitorDeaths;
  private GroupsManager groups;

  public DeathTracker(FacetHolder holder) {
    this.holder = holder;
    playerDeaths = Maps.newHashMap();
    uncachedDeaths = Maps.newHashMap();
    competitorDeaths = Maps.newHashMap();
  }

  /**
   * @param uuid of the player
   * @return times the player has died
   */
  public int getDeaths(UUID uuid) {
    return playerDeaths.getOrDefault(uuid, new AtomicInteger()).get();
  }

  /**
   * @param player
   * @return times the player has died
   */
  public int getDeaths(Player player) {
    return getDeaths(player.getUniqueId());
  }

  /**
   * @param competitor
   * @return total deaths of the competitor
   */
  public int getDeaths(Competitor competitor) {
    return competitorDeaths.getOrDefault(competitor, new AtomicInteger()).get();
  }

  @EventHandler
  public void onRoundOpen(RoundOpenEvent event) {
    this.groups = holder.getFacetRequired(GroupsManager.class);
  }

  @EventHandler
  public void onDeath(PlayerDeathEvent event) {
    countDeath(event.getPlayer());
  }

  @EventHandler
  public void onTaggedDeath(TaggedPlayerDeathEvent event) {
    countDeath(event.getPlayer().getPlayer());
  }

  private void countDeath(Player player) {
    if (groups.isObserving(player)) {
      return;
    }

    AtomicInteger deaths = playerDeaths.get(player.getUniqueId());
    if (deaths == null) {
      deaths = new AtomicInteger(1);
      playerDeaths.put(player.getUniqueId(), deaths);
    } else {
      deaths.incrementAndGet();
    }

    // Update uncached deaths
    int uncached = uncachedDeaths.getOrDefault(player.getUniqueId(), 0);
    uncached++;
    uncachedDeaths.put(player.getUniqueId(), uncached);

    Optional<Competitor> comp = groups.getCompetitorOf(player);
    if (!comp.isPresent()) {
      return;
    }

    AtomicInteger compNumDeaths = competitorDeaths.get(comp.get());
    if (compNumDeaths == null) {
      compNumDeaths = new AtomicInteger();
      competitorDeaths.put(comp.get(), compNumDeaths);
    }

    compNumDeaths.incrementAndGet();
  }

  @Override
  public double getScore(UUID uuid) {
    return 0;
  }

  @Override
  public Integer fetchUpdate(Player player) {
    Integer uncached = uncachedDeaths.remove(player.getUniqueId());
    if (uncached == null) {
      uncached = 0;
    }

    return uncached;
  }

  @Override
  public void reset(UUID uuid) {
    playerDeaths.remove(uuid);
  }
}

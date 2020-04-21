package network.walrus.games.core.facets.stats.trackers;

import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import network.walrus.games.core.events.round.RoundOpenEvent;
import network.walrus.games.core.facets.group.Competitor;
import network.walrus.games.core.facets.group.GroupsManager;
import network.walrus.ubiquitous.bukkit.tracker.event.player.PlayerDeathByPlayerEvent;
import network.walrus.ubiquitous.bukkit.tracker.event.player.PlayerDeathEvent;
import network.walrus.ubiquitous.bukkit.tracker.event.tag.TaggedPlayerDeathByPlayerEvent;
import network.walrus.ubiquitous.bukkit.tracker.event.tag.TaggedPlayerDeathByTaggedPlayerEvent;
import network.walrus.ubiquitous.bukkit.tracker.event.tag.TaggedPlayerDeathEvent;
import network.walrus.utils.bukkit.translation.text.player.PersonalizedBukkitPlayer;
import network.walrus.utils.core.translation.Localizable;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Tracker responsible for tracking kills done by each player and the total kills caused by each
 * competitor and the environment (PvE).
 *
 * @author Rafi Baum
 */
public class KillTracker extends Tracker<Integer> {

  private final FacetHolder holder;
  private final Map<UUID, AtomicInteger> playerKills;
  private final Map<UUID, AtomicInteger> cachedPlayerKills;
  private final Map<UUID, Localizable> cachedPlayerNames;
  private final Map<UUID, AtomicInteger> playerStreaks;
  private final Map<Competitor, AtomicInteger> competitorKills;
  private final Map<UUID, Integer> uncachedKills;
  private GroupsManager groups;
  private int environmentKills;

  public KillTracker(FacetHolder holder) {
    this.holder = holder;
    this.playerKills = Maps.newHashMap();
    this.cachedPlayerKills = Maps.newHashMap();
    this.cachedPlayerNames = Maps.newHashMap();
    this.playerStreaks = Maps.newHashMap();
    this.competitorKills = Maps.newHashMap();
    this.uncachedKills = Maps.newHashMap();
    this.environmentKills = 0;
  }

  /**
   * @param uuid of the player
   * @return number of kills achieved by a player
   */
  public int getKills(UUID uuid) {
    return playerKills.getOrDefault(uuid, new AtomicInteger()).get();
  }

  /**
   * @param player
   * @return number of kills achieved by a player
   */
  public int getKills(Player player) {
    return getKills(player.getUniqueId());
  }

  /**
   * @param uuid of the player
   * @return player's kill streak
   */
  public int getStreak(UUID uuid) {
    return playerStreaks.getOrDefault(uuid, new AtomicInteger()).get();
  }

  /**
   * @param player
   * @return player's kill streak
   */
  public int getStreak(Player player) {
    return getStreak(player.getUniqueId());
  }

  /**
   * @param competitor
   * @return total number of kills achieved by a competitor
   */
  public int getKills(Competitor competitor) {
    return competitorKills.getOrDefault(competitor, new AtomicInteger()).get();
  }

  /** @return total number of kills caused by PvE */
  public int getEnvironmentKills() {
    return environmentKills;
  }

  /** @return total number of kills achieved by each player */
  public Map<UUID, AtomicInteger> getPlayerKills() {
    return playerKills;
  }

  public Map<Localizable, Integer> getOfflineKills() {
    Map<Localizable, Integer> map = Maps.newHashMap();
    for (Entry<UUID, AtomicInteger> entry : getPlayerKills().entrySet()) {
      if (Bukkit.getPlayer(entry.getKey()) == null) continue;
      map.put(
          new PersonalizedBukkitPlayer(Bukkit.getPlayer(entry.getKey())), entry.getValue().get());
    }

    for (Entry<UUID, AtomicInteger> entry : cachedPlayerKills.entrySet()) {
      map.put(cachedPlayerNames.get(entry.getKey()), entry.getValue().get());
    }

    return map;
  }

  /** @return total number of kills achieved by each competitor */
  public Map<Competitor, AtomicInteger> getCompetitorKills() {
    return competitorKills;
  }

  /** @return kills for each competitor sorted from most to least */
  public Map<Competitor, AtomicInteger> getSortedKills() {
    List<Entry<Competitor, AtomicInteger>> sortedEntries =
        new ArrayList<>(competitorKills.entrySet());
    // Reverse ints so list is sorted in reverse
    sortedEntries.sort(Entry.comparingByValue((i1, i2) -> Integer.compare(i2.get(), i1.get())));

    Map<Competitor, AtomicInteger> sortedKills = Maps.newLinkedHashMap();
    for (Entry<Competitor, AtomicInteger> sortedEntry : sortedEntries) {
      sortedKills.put(sortedEntry.getKey(), sortedEntry.getValue());
    }

    return sortedKills;
  }

  @EventHandler
  public void onRoundOpen(RoundOpenEvent event) {
    this.groups = holder.getFacetRequired(GroupsManager.class);
  }

  @EventHandler
  public void onKillByPlayer(PlayerDeathByPlayerEvent event) {
    trackKill(event.getCause(), event.getPlayer());
  }

  @EventHandler
  public void onTaggedKillByPlayer(TaggedPlayerDeathByPlayerEvent event) {
    trackKill(event.getCause(), event.getPlayer().getPlayer());
  }

  @EventHandler
  public void onTaggedKillByTagged(TaggedPlayerDeathByTaggedPlayerEvent event) {
    trackKill(event.getCause().getPlayer(), event.getPlayer().getPlayer());
  }

  private void trackKill(Player killer, Player killed) {
    if (groups.isObserving(killer) || killer.equals(killed)) {
      return;
    }

    AtomicInteger playerKillsNum = playerKills.get(killer.getUniqueId());
    if (playerKillsNum == null) {
      playerKillsNum = new AtomicInteger(0);
      playerKills.put(killer.getUniqueId(), playerKillsNum);
    }
    playerKillsNum.incrementAndGet();

    // Track uncached
    int uncached = uncachedKills.getOrDefault(killer.getUniqueId(), 0);
    uncached++;
    uncachedKills.put(killer.getUniqueId(), uncached);

    AtomicInteger playerStreak = playerStreaks.get(killer.getUniqueId());
    if (playerStreak == null) {
      playerStreak = new AtomicInteger(0);
      playerStreaks.put(killer.getUniqueId(), playerStreak);
    }
    playerStreak.incrementAndGet();

    Optional<Competitor> competitor = groups.getCompetitorOf(killer);
    if (!competitor.isPresent()) {
      return;
    }

    AtomicInteger compKillsNum = competitorKills.get(competitor.get());
    if (compKillsNum == null) {
      compKillsNum = new AtomicInteger();
      competitorKills.put(competitor.get(), compKillsNum);
    }
    compKillsNum.incrementAndGet();
  }

  @EventHandler
  public void onPlayerDeath(PlayerDeathEvent event) {
    endStreak(event.getPlayer());

    if (event instanceof PlayerDeathByPlayerEvent) {
      return;
    }

    trackEnvironmentKill(event.getPlayer());
  }

  @EventHandler
  public void onTaggedPlayerDeath(TaggedPlayerDeathEvent event) {
    endStreak(event.getPlayer().getPlayer());

    if (event instanceof TaggedPlayerDeathByPlayerEvent
        || event instanceof TaggedPlayerDeathByTaggedPlayerEvent) {
      return;
    }

    trackEnvironmentKill(event.getPlayer().getPlayer());
  }

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
    if (cachedPlayerKills.containsKey(event.getPlayer().getUniqueId())) {
      playerKills.put(
          event.getPlayer().getUniqueId(),
          cachedPlayerKills.remove(event.getPlayer().getUniqueId()));
      cachedPlayerNames.remove(event.getPlayer().getUniqueId());
    }
  }

  @EventHandler
  public void onPlayerLeave(PlayerQuitEvent event) {
    UUID uuid = event.getPlayer().getUniqueId();
    cachedPlayerKills.put(uuid, new AtomicInteger(getKills(uuid)));
    cachedPlayerNames.put(uuid, new PersonalizedBukkitPlayer(event.getPlayer()));
  }

  private void trackEnvironmentKill(Player dead) {
    if (groups.isObserving(dead)) {
      return;
    }

    environmentKills++;
  }

  private void endStreak(Player dead) {
    playerStreaks.remove(dead.getUniqueId());
  }

  @Override
  public double getScore(UUID uuid) {
    return getKills(uuid);
  }

  @Override
  public Integer fetchUpdate(Player player) {
    Integer uncached = uncachedKills.remove(player.getUniqueId());
    if (uncached == null) {
      uncached = 0;
    }

    return uncached;
  }

  @Override
  public void reset(UUID uuid) {
    playerKills.remove(uuid);
    playerStreaks.remove(uuid);
  }
}

package network.walrus.games.core.facets.death;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import network.walrus.games.core.events.round.RoundOpenEvent;
import network.walrus.games.core.facets.group.Competitor;
import network.walrus.games.core.facets.group.GroupsManager;
import network.walrus.games.core.facets.stats.StatsFacet;
import network.walrus.games.core.facets.stats.trackers.KillTracker;
import network.walrus.ubiquitous.bukkit.tracker.Damage;
import network.walrus.ubiquitous.bukkit.tracker.event.player.PlayerDeathByPlayerEvent;
import network.walrus.ubiquitous.bukkit.tracker.event.player.PlayerDeathEvent;
import network.walrus.ubiquitous.bukkit.tracker.event.tag.TaggedPlayerDeathEvent;
import network.walrus.ubiquitous.bukkit.tracker.info.DamageInfo;
import network.walrus.utils.bukkit.sound.NetworkSoundConstants.Games.Deaths;
import network.walrus.utils.parsing.facet.Facet;
import network.walrus.utils.parsing.facet.FacetLoadException;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Manages the broadcasts of death messages for the current holder.
 *
 * @author Austin Mayes
 */
public class DeathsFacet extends Facet implements Listener {

  private final FacetHolder holder;
  private final List<KillReward> rewards;

  private GroupsManager manager;
  private KillTracker killTracker;

  /**
   * @param holder which this facet is operating in
   * @param rewards that should be given out when someone is killed
   */
  public DeathsFacet(FacetHolder holder, List<KillReward> rewards) {
    this.holder = holder;
    this.rewards = rewards;
  }

  @Override
  public void load() throws FacetLoadException {
    this.manager = holder.getFacetRequired(GroupsManager.class);
  }

  /** caches the kill tracker */
  @EventHandler
  public void openRound(RoundOpenEvent event) {
    this.killTracker = holder.getFacetRequired(StatsFacet.class).getTracker(KillTracker.class).get();
  }

  /**
   * Get a list of kill rewards that a player should receive when they kill a certain other player.
   *
   * @param killer who is getting the reward
   * @param target who the player killed
   * @return all rewards which should be given
   */
  private List<KillReward> rewardsFor(Player killer, Player target) {
    List<KillReward> list = new ArrayList<>();
    for (KillReward reward : this.rewards) {
      if (reward.isStreakValid(killTracker, killer.getUniqueId())) {
        if (reward.passes(killer, target)) {
          list.add(reward);
        }
      }
    }
    return list;
  }

  /** Give out kill rewards and track kills */
  @EventHandler
  public void track(PlayerDeathByPlayerEvent event) {
    Player dead = event.getPlayer();
    Player killer = event.getCause();

    // Disregard when the two players are on the same team
    if (sameTeam(dead, killer)) {
      return;
    }

    if (!rewards.isEmpty()) {
      // Reward relevant kill rewards
      for (KillReward reward : rewardsFor(killer, dead)) {
        Deaths.REWARD_RECEIVE.play(killer);
        reward.give(killer);
      }
    }
  }

  /** Broadcast a message for all deaths. */
  @EventHandler
  public void onPlayerDeath(PlayerDeathEvent event) {
    Player dead = event.getPlayer();

    Damage lastDamage = event.getLifetime().getLastDamage();
    if (lastDamage == null) {
      return;
    }

    DeathMessage.broadcast(this.holder, event);

    DamageInfo info = lastDamage.getInfo();
    if (!(info.getResolvedDamager() instanceof Player)) {
      return;
    }

    Player killer = (Player) info.getResolvedDamager();
    playDeathSounds(killer, dead);
  }

  /** Broadcast a message for all deaths. */
  @EventHandler
  public void onTaggedDeath(TaggedPlayerDeathEvent event) {
    Damage lastDamage = event.getLifetime().getLastDamage();
    if (lastDamage == null) {
      return;
    }

    DamageInfo info = lastDamage.getInfo();
    if (!(info.getResolvedDamager() instanceof Player)) {
      return;
    }

    Player killer = (Player) info.getResolvedDamager();
    playDeathSounds(killer, null);

    DeathMessage.broadcast(this.holder, event);
  }

  private void playDeathSounds(Player killer, @Nullable Player victim) {
    Location location = victim.getLocation();
    Deaths.SELF.play(victim, location);
    Deaths.KILLER.play(killer, location);
    Competitor competitor = manager.getCompetitorOf(killer).get();
    for (Player player : this.holder.players()) {
      if ((victim != null && player.getUniqueId().equals(victim.getUniqueId()))
          || player.getUniqueId().equals(killer.getUniqueId())) {
        continue;
      }

      if (manager.isSpectator(player)) {
        Deaths.SPECTATOR.play(player, location);
      } else if (competitor.getPlayers().contains(player)) {
        Deaths.TEAM.play(player, location);
      } else {
        Deaths.ENEMY.play(player, location);
      }
    }
  }

  private boolean sameTeam(Player player1, Player player2) {
    Optional<Competitor> competitor = manager.getCompetitorOf(player1);
    return competitor.isPresent() && competitor.get().hasPlayer(player2);
  }
}

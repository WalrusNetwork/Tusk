package network.walrus.games.octc.global.world;

import java.util.Optional;
import network.walrus.games.core.facets.group.Competitor;
import network.walrus.games.core.facets.group.GroupsManager;
import network.walrus.games.core.round.GameRound;
import network.walrus.ubiquitous.bukkit.UbiquitousBukkitPlugin;
import network.walrus.ubiquitous.bukkit.tracker.tag.CombatLoggerState;
import network.walrus.ubiquitous.bukkit.tracker.trackers.OwnedMobTracker;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import network.walrus.utils.parsing.facet.parse.configurator.listener.FacetListener;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetEvent;

/**
 * Listener responsible for only allowing certain mobs to spawn and which manages which players mobs
 * can target in a singular {@link GameRound}.
 *
 * @author Austin Mayes
 */
public class MobsListener extends FacetListener<WorldFacet> {

  private final GameRound round;
  private final OwnedMobTracker tracker =
      UbiquitousBukkitPlugin.getInstance()
          .getTrackerSupervisor()
          .getManager()
          .getTracker(OwnedMobTracker.class);
  private final GroupsManager manager;

  /** @param round which this listener is managing mobs in */
  public MobsListener(FacetHolder round, WorldFacet facet) {
    super(round, facet);
    this.round = (GameRound) round;
    manager = round.getFacetRequired(GroupsManager.class);
  }

  /** Only allow certain mobs to spawn. */
  @EventHandler
  public void onCreatureSpawn(CreatureSpawnEvent event) {
    event.setCancelled(true);

    // never allow spawning during non-playing states
    if (!this.round.getState().playing()) {
      return;
    }

    switch (event.getSpawnReason()) {
      case CUSTOM:
      case SPAWNER:
      case SPAWNER_EGG:
      case DISPENSE_EGG:
        event.setCancelled(false);
    }
  }

  /** Ensure mobs only target enemies. */
  @EventHandler
  public void onTarget(EntityTargetEvent event) {
    if (!(event.getTarget() instanceof Player)) {
      return;
    }

    Player targetPlayer = (Player) event.getTarget();
    event.setCancelled(preventTeamMob(event.getEntity(), targetPlayer));
  }

  /** Ensure mobs only damage enemies. */
  @EventHandler
  public void onDamage(EntityDamageByEntityEvent event) {
    if (!(event.getEntity() instanceof Player)) {
      return;
    }

    if (event.getDamager() instanceof Player
        && manager.isObservingOrDead(((Player) event.getDamager()))) {
      return;
    }

    Player targetPlayer = (Player) event.getEntity();
    event.setCancelled(preventTeamMob(event.getDamager(), targetPlayer));
  }

  private boolean preventTeamMob(Entity entity, Player effected) {
    if (!(entity instanceof Creature || entity instanceof Slime || entity instanceof Ghast)) {
      return false;
    }

    if (manager.isObservingOrDead(effected)) {
      return true;
    }

    OfflinePlayer ownerPlayer = tracker.getOwner((LivingEntity) entity);
    if (ownerPlayer == null || ownerPlayer instanceof CombatLoggerState) {
      return false;
    }

    Optional<Competitor> target = manager.getCompetitorOf(effected);
    Optional<Competitor> owner = manager.getCompetitorOf((Player) ownerPlayer);

    return owner.isPresent() && target.isPresent() && owner.get().equals(target.get());
  }
}

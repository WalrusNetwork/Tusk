package network.walrus.games.octc.score;

import java.util.Optional;
import network.walrus.games.core.facets.group.Competitor;
import network.walrus.games.core.facets.group.GroupsManager;
import network.walrus.games.core.facets.visual.SidebarFacet;
import network.walrus.games.core.round.GameRound;
import network.walrus.games.octc.score.event.PointChangeEvent;
import network.walrus.ubiquitous.bukkit.tracker.event.player.PlayerDeathEvent;
import network.walrus.utils.core.math.NumberAction;
import network.walrus.utils.parsing.facet.Facet;
import network.walrus.utils.parsing.facet.FacetLoadException;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Facet which manages and passes events to a {@link ScoreObjective}.
 *
 * @author Austin Mayes
 */
public class ScoreFacet extends Facet implements Listener {

  private final GameRound round;
  private final ScoreObjective objective;
  private GroupsManager groupsManager;
  private SidebarFacet sidebarFacet;

  /**
   * @param round this facet is operating in
   * @param objective which is used to track scores
   */
  ScoreFacet(GameRound round, ScoreObjective objective) {
    this.round = round;
    this.objective = objective;
  }

  @Override
  public void load() throws FacetLoadException {
    this.groupsManager = round.getFacetRequired(GroupsManager.class);
    this.sidebarFacet = round.getFacetRequired(SidebarFacet.class);
  }

  /** Update the sidebar when a point is scored */
  @EventHandler
  public void onPoint(PointChangeEvent event) {
    sidebarFacet.update(event.getCompetitor().id());
  }

  /** Reward points on kill. */
  @EventHandler
  public void onKill(PlayerDeathEvent event) {
    if (event.getLifetime().getLastDamage() == null) {
      return;
    }

    LivingEntity damager = event.getLifetime().getLastDamage().getInfo().getResolvedDamager();

    if (!(damager instanceof Player)
        || damager.getUniqueId().equals(event.getPlayer().getUniqueId())) {
      // Accidental kill or self kill or non-player death
      punishDead(event.getPlayer());
    } else {
      // Other player killed
      rewardKiller((Player) damager);
    }
  }

  private void rewardKiller(Player killer) {
    Competitor competitor =
        this.round.getFacetRequired(GroupsManager.class).getCompetitorOf(killer).orElse(null);

    if (!objective.getKills().isPresent()) {
      return;
    }
    if (competitor == null) {
      return;
    }
    if (!objective.canComplete(competitor)) {
      return;
    }

    int amount = objective.getKills().get();
    objective.modify(competitor, amount, NumberAction.ADD, Optional.of(killer));
  }

  private void punishDead(Player dead) {
    Competitor competitor =
        this.round.getFacetRequired(GroupsManager.class).getCompetitorOf(dead).orElse(null);

    if (!objective.getDeaths().isPresent()) {
      return;
    }
    if (competitor == null) {
      return;
    }
    if (!objective.canComplete(competitor)) {
      return;
    }

    int amount = objective.getDeaths().get();
    objective.modify(competitor, amount, NumberAction.SUBTRACT, Optional.of(dead));
  }

  /** @return the score objective */
  public ScoreObjective getObjective() {
    return objective;
  }
}

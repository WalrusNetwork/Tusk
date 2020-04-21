package network.walrus.games.octc.destroyables.objectives;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import network.walrus.games.core.events.round.RoundStateChangeEvent;
import network.walrus.games.core.facets.group.GroupsManager;
import network.walrus.games.core.facets.visual.PaneGroup;
import network.walrus.games.core.facets.visual.SidebarFacet;
import network.walrus.games.core.round.GameRound;
import network.walrus.games.octc.destroyables.DestroyablesDisplay;
import network.walrus.games.octc.destroyables.modes.DestroyableMode;
import network.walrus.games.octc.destroyables.modes.ModeApplicationCountdown;
import network.walrus.games.octc.destroyables.objectives.cores.CoreObjective;
import network.walrus.games.octc.destroyables.objectives.cores.events.CoreLeakEvent;
import network.walrus.games.octc.destroyables.objectives.events.DestroyableDamageEvent;
import network.walrus.games.octc.destroyables.objectives.monuments.events.MonumentDestroyEvent;
import network.walrus.games.octc.global.results.WinCalculator;
import network.walrus.ubiquitous.bukkit.UbiquitousBukkitPlugin;
import network.walrus.utils.parsing.facet.Facet;
import network.walrus.utils.parsing.facet.FacetLoadException;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Manages all {@link CoreObjective}s for the round.
 *
 * @author ShinyDialga
 */
public class DestroyablesFacet extends Facet implements Listener {

  private final GameRound round;
  private final List<DestroyableObjective> objectives;
  private final WinCalculator winCalculator;

  /**
   * @param holder which is holding this object
   * @param objectives which should be used
   */
  DestroyablesFacet(FacetHolder holder, List<DestroyableObjective> objectives) {
    this.round = (GameRound) holder;
    this.objectives = objectives;
    this.winCalculator =
        new WinCalculator(round, new ArrayList<>(this.objectives), Lists.newArrayList());
    SidebarFacet.PANE_CREATE_FUNCTION =
        (m, c) ->
            new PaneGroup(
                Pair.of(
                    "destroyables",
                    new DestroyablesDisplay(
                        (GameRound) holder, m, new ArrayList<>(objectives), c)));
  }

  @Override
  public void load() throws FacetLoadException {
    for (DestroyableObjective objective : objectives) {
      objective.initialize();
    }
  }

  public List<DestroyableObjective> getObjectives() {
    return objectives;
  }

  /** Start and stop all relevant mode application countdowns. */
  @EventHandler
  public void startModeCountdowns(RoundStateChangeEvent event) {
    if (event.isChangeToPlaying()
        && event.getFrom().isPresent()
        && !event.getFrom().get().playing()) {

      Map<DestroyableMode, List<DestroyableObjective>> modes = new HashMap<>();
      for (DestroyableObjective objective : this.getObjectives()) {
        if (objective.getProperties().mode.isPresent()) {
          DestroyableMode mode = objective.getProperties().mode.get();
          modes.putIfAbsent(mode, new ArrayList<>());
          modes.get(mode).add(objective);
        }
      }

      for (Entry<DestroyableMode, List<DestroyableObjective>> entry : modes.entrySet()) {
        DestroyableMode mode = entry.getKey();
        List<DestroyableObjective> withMode = entry.getValue();
        ModeApplicationCountdown countdown =
            new ModeApplicationCountdown(round, mode.getDelay(), mode, withMode);
        UbiquitousBukkitPlugin.getInstance().getCountdownManager().start(countdown);
      }
      return;
    }
    if (event.isChangeToNotPlaying()
        && event.getFrom().isPresent()
        && event.getFrom().get().playing()) {
      UbiquitousBukkitPlugin.getInstance()
          .getCountdownManager()
          .cancelAll(countdown -> countdown instanceof ModeApplicationCountdown);
    }
  }

  /** Handle wins */
  @EventHandler
  public void onComplete(CoreLeakEvent event) {
    this.winCalculator.check();
    GroupsManager manager = round.getFacetRequired(GroupsManager.class);
  }

  /** Handle wins */
  @EventHandler
  public void onComplete(MonumentDestroyEvent event) {
    this.winCalculator.check();
    Player breaker = event.getInfo().getActor();
    GroupsManager manager = round.getFacetRequired(GroupsManager.class);
  }

  /** Handle wins (win completion isn't 100) */
  @EventHandler
  public void onComplete(DestroyableDamageEvent event) {
    this.winCalculator.check();
    Player breaker = event.getInfo().getActor();
    GroupsManager manager = round.getFacetRequired(GroupsManager.class);
  }
}

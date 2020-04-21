package network.walrus.games.uhc.scenarios;

import com.google.common.collect.Sets;
import java.util.Optional;
import java.util.Set;
import network.walrus.games.core.util.EventUtil;
import network.walrus.games.uhc.UHCManager;
import network.walrus.games.uhc.scenarios.type.BackpackScenario;
import network.walrus.games.uhc.scenarios.type.BatsScenario;
import network.walrus.games.uhc.scenarios.type.BleedingSweetsScenario;
import network.walrus.games.uhc.scenarios.type.BloodDiamondScenario;
import network.walrus.games.uhc.scenarios.type.CutCleanScenario;
import network.walrus.games.uhc.scenarios.type.DiamondlessScenario;
import network.walrus.games.uhc.scenarios.type.GoldenRetrieverScenario;
import network.walrus.games.uhc.scenarios.type.HasteyBoysScenario;
import network.walrus.games.uhc.scenarios.type.InfiniteEnchanterScenario;
import network.walrus.games.uhc.scenarios.type.NoCleanScenario;
import network.walrus.games.uhc.scenarios.type.RodlessScenario;
import network.walrus.games.uhc.scenarios.type.TimberScenario;
import network.walrus.games.uhc.scenarios.type.TimeBombScenario;
import network.walrus.games.uhc.scenarios.type.WeakestLinkScenario;
import network.walrus.ubiquitous.bukkit.UbiquitousBukkitPlugin;

/**
 * Manager which holds all registered and active {@link Scenario}s.
 *
 * @author Austin Mayes
 */
public class ScenarioManager {

  private final Set<Scenario> registered = Sets.newHashSet();
  private final Set<Scenario> active = Sets.newHashSet();

  /** Register scenarios. */
  public ScenarioManager() {
    register(new CutCleanScenario());
    register(new HasteyBoysScenario());
    register(new TimberScenario());
    register(new TimeBombScenario());
    register(new BleedingSweetsScenario());
    register(new RodlessScenario());
    register(new NoCleanScenario());
    register(new BloodDiamondScenario());
    register(new GoldenRetrieverScenario());
    register(new BackpackScenario());
    register(new DiamondlessScenario());
    register(new WeakestLinkScenario());
    register(new BatsScenario());
    register(new InfiniteEnchanterScenario());
  }

  public Set<Scenario> getRegistered() {
    return registered;
  }

  public Set<Scenario> getActive() {
    return active;
  }

  /**
   * Get an active scenario by class.
   *
   * @param clazz to search for
   * @param <S> type of scenario being returned
   * @return an instance of the active scenario found for the class, if one exists
   */
  public <S extends Scenario> S getActive(Class<S> clazz) {
    for (Scenario s : getActive()) {
      if (s.getClass().isAssignableFrom(clazz)) {
        return (S) s;
      }
    }
    throw new IllegalArgumentException(clazz.getSimpleName() + " is not active");
  }

  private void register(Scenario scenario) {
    registered.add(scenario);
  }

  /**
   * Find a scenario using a search query.
   *
   * <p>The search first checks by name, and if a result cannot be determined, falls back to slug.
   *
   * @param query to search by
   * @return the found scenario, if present
   */
  public Optional<Scenario> search(String query) {
    query = query.toLowerCase();
    Scenario closest = null;
    for (Scenario scenario : this.registered) {
      String name = scenario.name().toLowerCase();
      if (name.equals(query)) {
        return Optional.of(scenario);
      } else if (name.startsWith(query)) {
        closest = scenario;
      }
    }
    if (closest == null) {
      for (Scenario scenario : this.registered) {
        String slug = scenario.slug();
        if (slug.equals(query)) {
          return Optional.of(scenario);
        } else if (slug.startsWith(query)) {
          closest = scenario;
        }
      }
    }
    return Optional.ofNullable(closest);
  }

  /**
   * Full activate a scenario.
   *
   * @param scenario to activate
   */
  public void activate(Scenario scenario) {
    if (active.contains(scenario)) {
      return;
    }
    active.add(scenario);
    EventUtil.register(scenario);
    scenario.activated(
        UHCManager.instance.getUHC() != null && UHCManager.instance.getUHC().getState().playing());
    UbiquitousBukkitPlugin.getInstance().getDisplayManager().update("scen-list");
  }

  /**
   * Full deactivate a scenario.
   *
   * @param scenario to deactivate
   */
  public void deActivate(Scenario scenario) {
    if (!active.contains(scenario)) {
      return;
    }
    active.remove(scenario);
    EventUtil.unregister(scenario);
    scenario.deActivated(UHCManager.instance.getUHC().getState().playing());
    UbiquitousBukkitPlugin.getInstance().getDisplayManager().update("scen-list");
  }

  /**
   * Determine if a scenario is currently active.
   *
   * @param scenario to check
   * @return if the scenario is active
   */
  public boolean isActive(Scenario scenario) {
    return active.contains(scenario);
  }

  /**
   * Determine if a scenario with the specified class is currently active.
   *
   * @param scenarioClass to check
   * @return if the scenario is active
   */
  public boolean isActive(Class<? extends Scenario> scenarioClass) {
    for (Scenario s : active) {
      if (s.getClass().isAssignableFrom(scenarioClass)) {
        return true;
      }
    }
    return false;
  }
}

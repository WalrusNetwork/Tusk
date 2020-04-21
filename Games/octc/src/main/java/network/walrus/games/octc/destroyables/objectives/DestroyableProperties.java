package network.walrus.games.octc.destroyables.objectives;

import java.util.Optional;
import network.walrus.games.core.facets.filters.Filter;
import network.walrus.games.core.facets.objectives.touchable.TouchableDistanceMetrics;
import network.walrus.games.octc.destroyables.modes.DestroyableMode;
import network.walrus.games.octc.global.groups.teams.Team;
import network.walrus.utils.bukkit.inventory.MultiMaterialMatcher;
import network.walrus.utils.bukkit.inventory.SingleMaterialMatcher;
import network.walrus.utils.bukkit.region.BoundedRegion;
import network.walrus.utils.bukkit.translation.LocalizedConfigurationProperty;

/**
 * Basic properties of all {@link DestroyableObjective} types.
 *
 * @author Austin Mayes
 */
public class DestroyableProperties {

  public final Optional<Team> owner;
  public final LocalizedConfigurationProperty name;
  public final BoundedRegion region;
  public final MultiMaterialMatcher materials;
  public final boolean destroyable;
  public final boolean repairable;
  public final boolean enforceAntiRepair;
  public final boolean fireworks;
  public final Optional<Filter> breakFilter;
  public final Optional<Filter> repairFilter;
  public final SingleMaterialMatcher completedState;
  public final double neededCompletion;
  public final boolean anyRepair;
  public final Optional<DestroyableMode> mode;
  public final TouchableDistanceMetrics metrics;

  /**
   * @param owner of the objective
   * @param name of the objective
   * @param region the the objective is in
   * @param materials that make up the objective
   * @param destroyable 9f TNT can break the blocks of the objective
   * @param repairable if the objective can be repaired
   * @param enforceAntiRepair if strict repair rules should be enforced
   * @param fireworks if fireworks should be spawned on objective completion
   * @param breakFilter filter to be ran to allow players to break blocks of the objective
   * @param repairFilter filter to be ran to allow players to repair the objective
   * @param completedState state that blocks should be set to upon being broken
   * @param neededCompletion percentage of the objective that must be completed in order to be
   *     marked as completed
   * @param anyRepair if any material can be used to repair the objective
   * @param mode first {@link DestroyableMode} that should be applied to this objective
   * @param metrics to be used when calculating distance
   */
  public DestroyableProperties(
      Optional<Team> owner,
      LocalizedConfigurationProperty name,
      BoundedRegion region,
      MultiMaterialMatcher materials,
      boolean destroyable,
      boolean repairable,
      boolean enforceAntiRepair,
      boolean fireworks,
      Optional<Filter> breakFilter,
      Optional<Filter> repairFilter,
      SingleMaterialMatcher completedState,
      double neededCompletion,
      boolean anyRepair,
      Optional<DestroyableMode> mode,
      TouchableDistanceMetrics metrics) {
    this.owner = owner;
    this.name = name;
    this.region = region;
    this.materials = materials;
    this.destroyable = destroyable;
    this.repairable = repairable;
    this.enforceAntiRepair = enforceAntiRepair;
    this.fireworks = fireworks;
    this.breakFilter = breakFilter;
    this.repairFilter = repairFilter;
    this.completedState = completedState;
    this.neededCompletion = neededCompletion;
    this.anyRepair = anyRepair;
    this.mode = mode;
    this.metrics = metrics;
  }
}

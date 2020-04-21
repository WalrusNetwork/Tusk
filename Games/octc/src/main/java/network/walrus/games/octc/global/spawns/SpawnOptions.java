package network.walrus.games.octc.global.spawns;

import java.util.List;
import java.util.Optional;
import network.walrus.games.core.api.spawns.SpawnRegion;
import network.walrus.games.core.facets.filters.Filter;
import network.walrus.games.core.facets.group.Group;
import network.walrus.games.core.facets.group.spectate.Spectators;
import network.walrus.games.core.facets.kits.Kit;
import network.walrus.utils.bukkit.points.AngleProvider;

/**
 * Data class to represent the large set of configuration properties that an {@link OCNSpawn} will
 * use when generating spawn locations at runtime. This makes the factory and constructor less
 * daunting.
 *
 * @author Austin Mayes
 */
public class SpawnOptions {

  final Group group;
  final Optional<Kit> kit;
  final List<SpawnRegion> regions;
  final Optional<Filter> filter;
  final AngleProvider yaw;
  final AngleProvider pitch;
  final SelectionMode selectionMode;
  final boolean checkAir;
  final boolean safe;
  final boolean sequential;

  /**
   * Constructor.
   *
   * @param group this spawn is for, empty for {@link Spectators}
   * @param kit that should be given when the player spanws here
   * @param regions which make up this spawn
   * @param filter to decide if this spawn should even be used
   * @param yaw provider used to generate yaw values for spawn positions
   * @param pitch provider used to generate pitch values for spawn positions
   * @param selectionMode mode used to select which region to use
   * @param checkAir if the player should not be able to spawn in the air (10 blocks under them are
   *     air)
   * @param safe if the spawn must not be obstructed
   * @param sequential if each region should be tried in order when a location cannot be chosen due
   *     to safety
   */
  public SpawnOptions(
      Group group,
      Optional<Kit> kit,
      List<SpawnRegion> regions,
      Optional<Filter> filter,
      AngleProvider yaw,
      AngleProvider pitch,
      SelectionMode selectionMode,
      boolean checkAir,
      boolean safe,
      boolean sequential) {
    this.group = group;
    this.kit = kit;
    this.regions = regions;
    this.filter = filter;
    this.yaw = yaw;
    this.pitch = pitch;
    this.selectionMode = selectionMode;
    this.checkAir = checkAir;
    this.safe = safe;
    this.sequential = sequential;
  }
}

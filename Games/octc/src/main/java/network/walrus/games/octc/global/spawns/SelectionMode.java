package network.walrus.games.octc.global.spawns;

import network.walrus.games.core.api.spawns.Spawn;
import network.walrus.games.core.api.spawns.SpawnRegion;

/**
 * The selection algorithm that should be used when selecting a {@link SpawnRegion} for a certain
 * {@link Spawn}.
 *
 * @author Austin Mayes
 */
public enum SelectionMode {
  /** Select one randomly. */
  RANDOM,

  /** Select furthest away from enemies (other competitors). */
  SAFE,

  /** Try each region in order if one fails for safety. */
  SEQUENTIAL,

  /** Select furthest away from all players. */
  SPREAD
}

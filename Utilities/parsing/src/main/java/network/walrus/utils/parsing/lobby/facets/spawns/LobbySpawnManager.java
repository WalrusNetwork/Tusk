package network.walrus.utils.parsing.lobby.facets.spawns;

import network.walrus.utils.bukkit.region.BoundedRegion;
import network.walrus.utils.parsing.facet.Facet;

/**
 * Simple facet which holds a {@link BoundedRegion} where players should spawn in lobbies.
 *
 * @author Austin Mayes
 */
public class LobbySpawnManager extends Facet {

  private final BoundedRegion spawn;

  /** @param spawn area where players should be spawned */
  public LobbySpawnManager(BoundedRegion spawn) {
    this.spawn = spawn;
  }

  public BoundedRegion getSpawn() {
    return spawn;
  }
}

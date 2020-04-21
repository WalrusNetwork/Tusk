package network.walrus.games.core.api.spawns;

import java.util.Optional;
import network.walrus.games.core.facets.kits.Kit;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Object which routes {@link Player}s to specific {@link Location}s where they should be placed
 * when they join a game or die.
 *
 * @author Austin Mayes
 */
public interface Spawn {

  /** @return the kit which should be applied to players when they are spawned using this spawn. */
  Optional<Kit> kit();

  /**
   * Select a location where a player should spawn for this spawn.
   *
   * @param round that the player is spawning inside of
   * @param player that a location should be selected for
   * @return location the player should be placed when spawning
   * @throws SpawnLocationUnavailableException if the spawn is unable to generate a position meeting
   *     the requirements
   */
  Location selectLocation(FacetHolder round, Player player)
      throws SpawnLocationUnavailableException;
}

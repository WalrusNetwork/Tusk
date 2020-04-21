package network.walrus.utils.bukkit.distance;

import java.util.Set;
import org.bukkit.entity.Player;

/**
 * A simple object to represent a collection of players.
 *
 * @author Avicus Network
 */
public interface PlayerStore {

  /** @return the players in this object */
  Set<Player> getPlayers();
}

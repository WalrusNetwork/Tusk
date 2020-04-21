package network.walrus.games.uhc.facets.tpall;

import java.util.Iterator;
import network.walrus.games.uhc.UHCRound;
import org.bukkit.entity.Player;

/**
 * Task which handles teleporting the player.
 *
 * @author Rafi Baum
 */
public class TpAllTask implements Runnable {

  private UHCRound round;
  private Player player;
  private Iterator<Player> players;

  TpAllTask(UHCRound round, Player player) {
    this.round = round;
    this.player = player;
    players = round.playingPlayers().iterator();
  }

  @Override
  public void run() {
    // Tp to the next player
    if (!players.hasNext()) {
      players = round.playingPlayers().iterator();
    }

    player.teleport(players.next());
  }
}

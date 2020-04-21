package network.walrus.welcomemat.facets.spawn;

import java.util.Random;
import network.walrus.utils.bukkit.PlayerUtils;
import network.walrus.utils.bukkit.region.PositionUnavailableException;
import network.walrus.utils.parsing.facet.Facet;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import network.walrus.utils.parsing.lobby.Lobby;
import network.walrus.utils.parsing.lobby.facets.spawns.LobbySpawnManager;
import network.walrus.welcomemat.WelcomeMatPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.util.Vector;

/**
 * Facet used to spawn players at the lobby's spawn point.
 *
 * @author Rafi Baum
 */
public class SpawnFacet extends Facet implements Listener {

  private static final Random RANDOM = new Random();
  private final FacetHolder holder;
  private Lobby lobby;

  /**
   * Constructor.
   *
   * @param holder
   */
  public SpawnFacet(FacetHolder holder) {
    this.holder = holder;
  }

  @EventHandler
  public void onJoin(PlayerJoinEvent event) {
    try {
      if (lobby == null) {
        lobby = WelcomeMatPlugin.getInstance().getLobbyLoader().getLobby();
      }

      Vector spawnVector =
          lobby.getFacetRequired(LobbySpawnManager.class).getSpawn().getRandomPosition(RANDOM);

      PlayerUtils.reset(event.getPlayer());
      event.getPlayer().teleport(spawnVector.toLocation(lobby.getContainer().mainWorld()));
    } catch (PositionUnavailableException e) {
      event.getPlayer().kickPlayer("Error: Can't teleport player to spawn position!");
      e.printStackTrace();
    }
  }
}

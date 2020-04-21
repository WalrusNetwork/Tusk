package network.walrus.games.uhc.facets.tpall;

import com.google.common.collect.Maps;
import java.time.Duration;
import java.util.Map;
import network.walrus.games.core.events.group.PlayerChangedGroupEvent;
import network.walrus.games.core.util.GameTask;
import network.walrus.games.uhc.UHCRound;
import network.walrus.utils.parsing.facet.Facet;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Facet which allows hosts to tp to every player in a match automatically.
 *
 * @author Rafi Baum
 */
public class TpAllFacet extends Facet implements Listener {

  private final UHCRound holder;
  private final Map<Player, GameTask> tpTasks;

  public TpAllFacet(FacetHolder holder) {
    this.holder = (UHCRound) holder;
    this.tpTasks = Maps.newHashMap();
  }

  /**
   * @param player to check
   * @return if a player is currently tpall'ing
   */
  public boolean isTping(Player player) {
    return tpTasks.containsKey(player);
  }

  /**
   * Start tpall'ing a player
   *
   * @param player
   * @param interval between tp's
   */
  public void startTp(Player player, Duration interval) {
    if (isTping(player)) {
      return;
    }

    GameTask task = GameTask.of("TP all", new TpAllTask(holder, player));
    task.repeat(0, (int) (20 * interval.getSeconds()));
    tpTasks.put(player, task);
  }

  /**
   * Stop tpall'ing a player
   *
   * @param player
   */
  public void stopTp(Player player) {
    if (!isTping(player)) {
      return;
    }

    tpTasks.remove(player).reset();
  }

  @EventHandler
  public void onGroupChange(PlayerChangedGroupEvent event) {
    stopTp(event.getPlayer());
  }

  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent event) {
    stopTp(event.getPlayer());
  }
}

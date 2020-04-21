package network.walrus.games.core.facets.group.spectate;

import network.walrus.games.core.facets.group.GroupsManager;
import network.walrus.games.core.util.GameTask;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Task used to manage player visibility for all players.
 *
 * @author Avicus Network
 */
public class ObserverTask extends GameTask {

  private final FacetHolder holder;
  private final GroupsManager manager;

  /**
   * @param holder this task is operating in
   * @param manager used to pull group data from
   */
  public ObserverTask(FacetHolder holder, GroupsManager manager) {
    super("Observer Task");
    this.holder = holder;
    this.manager = manager;
  }

  @Override
  public void run() {
    execute();
  }

  /**
   * Start the task.
   *
   * @return the started task
   */
  public ObserverTask start() {
    this.repeat(0, 20);
    return this;
  }

  /** Refresh attributes for observers. */
  public void execute() {
    for (Player player : Bukkit.getOnlinePlayers()) {
      boolean observing = manager.isObserving(player);

      if (observing) {
        player.setFireTicks(0);
        player.setRemainingAir(20);
      }
    }
  }
}

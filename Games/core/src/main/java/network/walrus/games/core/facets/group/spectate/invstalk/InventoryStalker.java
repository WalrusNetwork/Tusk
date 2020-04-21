package network.walrus.games.core.facets.group.spectate.invstalk;

import java.util.Iterator;
import java.util.Map;
import network.walrus.games.core.util.GameTask;
import org.bukkit.entity.Player;

/**
 * Task that updates {@link StalkedInventory stalked inventories} based on item changes.
 *
 * @author Austin Mayes
 */
public class InventoryStalker extends GameTask {

  private final Map<Player, StalkedInventory> opened;

  /** @param opened map of opened inventories */
  InventoryStalker(Map<Player, StalkedInventory> opened) {
    super("stalker");
    this.opened = opened;
  }

  @Override
  public void run() {
    Iterator<Player> iterator = this.opened.keySet().iterator();

    while (iterator.hasNext()) {
      Player player = iterator.next();
      StalkedInventory tracked = this.opened.get(player);

      if (!tracked.isOpen()) {
        // Remove tracked inventories if they aren't open anymore
        iterator.remove();
      } else {
        // Otherwise, update them
        tracked.update();
      }
    }
  }
}

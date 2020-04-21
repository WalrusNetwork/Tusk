package network.walrus.duels;

import network.walrus.utils.bukkit.WalrusBukkitPlugin;
import org.bukkit.Bukkit;

/**
 * Main class for the plugin which handles all duels.
 *
 * @author Austin Mayes
 */
public class DuelsPlugin extends WalrusBukkitPlugin {

  static DuelsPlugin instance;
  private DuelsManager manager;

  @Override
  public void load() {
    instance = this;
  }

  @Override
  public void enable() {
    try {
      manager.enable();
    } catch (Exception e) {
      e.printStackTrace();
      Bukkit.shutdown();
    }
  }

  @Override
  public void disable() {
    manager.disable();
  }
}

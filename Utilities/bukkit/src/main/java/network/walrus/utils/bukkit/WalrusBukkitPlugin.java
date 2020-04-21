package network.walrus.utils.bukkit;

import network.walrus.utils.bukkit.task.TickTimer;
import network.walrus.utils.core.stage.Stage;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Base class for all plugins which adds extra functionality on top of the base {@link JavaPlugin}
 * which makes life easier and more streamlined.
 *
 * <p>This allows us to add global functionality to the {@link #onEnable()} and {@link #onDisable()}
 * callbacks which will be executed for each plugin.
 *
 * <p>Currently all this does is: - Start the {@link TickTimer}
 *
 * @author Austin Mayes
 */
public abstract class WalrusBukkitPlugin extends JavaPlugin {

  private static final Stage STAGE =
      Enum.valueOf(Stage.class, System.getProperty("stage", "production").toUpperCase());
  private static final String SERVER_ID = System.getenv("SERVER_ID");
  private static WalrusBukkitPlugin instance;
  TickTimer timer = new TickTimer(this);

  public static Stage getStage() {
    return STAGE;
  }

  public static String getServerId() {
    return SERVER_ID;
  }

  /** @return the current instance of walrus plugin */
  public static WalrusBukkitPlugin getWalrusPlugin() {
    return instance;
  }

  @Override
  public void onLoad() {
    this.saveDefaultConfig();
    this.reloadConfig();
    load();
  }

  @Override
  public void onEnable() {
    timer.start();
    if (instance == null) {
      instance = this;
    }
    long start = System.currentTimeMillis();
    enable();
    long end = System.currentTimeMillis();
    System.out.println(
        "========= Took " + (end - start) + "ms to load " + getName() + " =========");
  }

  @Override
  public void onDisable() {
    timer.stop();
    disable();
    if (instance == this) {
      instance = null;
    }
  }

  /** Called when the plugin is enabled after initial setup is complete. */
  public abstract void enable();

  /** Called when the plugin is disabled after initial teardown is complete. */
  public abstract void disable();

  /** Called when the plugin is loaded after initial setup is complete. */
  public abstract void load();

  /** @return timer used to always keep track of how long the plugin has been running (in ticks) */
  public TickTimer timer() {
    return timer;
  }
}

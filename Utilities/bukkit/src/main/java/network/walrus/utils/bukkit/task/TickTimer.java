package network.walrus.utils.bukkit.task;

import org.bukkit.plugin.Plugin;

/**
 * Simple task which keeps a live tick count of how long the task has been running.
 *
 * @author Overcast Network
 */
public class TickTimer {

  private final Plugin plugin;
  private int taskId;
  private long ticks = 0;
  private boolean running;

  /**
   * Constructor.
   *
   * @param plugin to register the class for
   */
  public TickTimer(Plugin plugin) {
    this.plugin = plugin;
  }

  public long getTicks() {
    return this.ticks;
  }

  public void setTicks(long ticks) {
    this.ticks = ticks;
  }

  public boolean isRunning() {
    return this.running;
  }

  /** Start the timer. */
  public void start() {
    if (this.running) {
      throw new RuntimeException("Timer already running");
    }

    this.taskId =
        this.plugin
            .getServer()
            .getScheduler()
            .scheduleSyncRepeatingTask(
                this.plugin,
                new Runnable() {
                  public void run() {
                    TickTimer.this.ticks++;
                  }
                },
                1L,
                1L);

    this.running = true;
  }

  /** Stop the timer. */
  public void stop() {
    if (!this.running) {
      throw new RuntimeException("Timer not running");
    }

    this.plugin.getServer().getScheduler().cancelTask(this.taskId);
  }
}

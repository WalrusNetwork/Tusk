package network.walrus.ubiquitous.bukkit.task;

import co.aikar.timings.Timing;
import co.aikar.timings.Timings;
import network.walrus.ubiquitous.bukkit.UbiquitousBukkitPlugin;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

/**
 * Wrapper for {@link BukkitTask} which adds some useful methods and lambda support.
 *
 * @author Dean
 */
@FunctionalInterface
public interface BetterRunnable {

  /** Called when the task is executed. */
  void run();

  /**
   * Run a task right now on the main thread.
   *
   * @return the task which is being handled by Bukkit
   */
  default BukkitTask runTask(String id) {
    Timing timing = Timings.of(UbiquitousBukkitPlugin.getInstance(), id);
    return Bukkit.getScheduler()
        .runTask(
            UbiquitousBukkitPlugin.getInstance(),
            () -> {
              try (Timing t = timing.startClosable()) {
                run();
              }
            });
  }

  /**
   * Run a task after a fixed delay on the main thread.
   *
   * @param delay before the task should be executed
   * @return the task which is being handled by Bukkit
   */
  default BukkitTask runTaskLater(long delay, String id) {
    Timing timing = Timings.of(UbiquitousBukkitPlugin.getInstance(), id);
    return Bukkit.getScheduler()
        .runTaskLater(
            UbiquitousBukkitPlugin.getInstance(),
            () -> {
              try (Timing t = timing.startClosable()) {
                run();
              }
            },
            delay);
  }

  /**
   * Run a task after a fixed delay on the main thread repeatably separated by a timer.
   *
   * @param delay before the task should be executed
   * @param timer between each subsequent execution
   * @return the task which is being handled by Bukkit
   */
  default BukkitTask runTaskTimer(long delay, long timer, String id) {
    Timing timing = Timings.of(UbiquitousBukkitPlugin.getInstance(), id);
    return Bukkit.getScheduler()
        .runTaskTimer(
            UbiquitousBukkitPlugin.getInstance(),
            () -> {
              try (Timing t = timing.startClosable()) {
                run();
              }
            },
            delay,
            timer);
  }

  /**
   * Run a task right now on a separate thread.
   *
   * @return the task which is being handled by Bukkit
   */
  default BukkitTask runTaskAsynchronously() {
    return Bukkit.getScheduler()
        .runTaskAsynchronously(UbiquitousBukkitPlugin.getInstance(), this::run);
  }

  /**
   * Run a task after a fixed delay on a separate thread.
   *
   * @param delay before the task should be executed
   * @return the task which is being handled by Bukkit
   */
  default BukkitTask runTaskLaterAsynchronously(long delay) {
    return Bukkit.getScheduler()
        .runTaskLaterAsynchronously(UbiquitousBukkitPlugin.getInstance(), this::run, delay);
  }

  /**
   * Run a task after a fixed delay on the separate thread repeatably separated by a timer.
   *
   * @param delay before the task should be executed
   * @param timer between each subsequent execution
   * @return the task which is being handled by Bukkit
   */
  default BukkitTask runTaskTimerAsynchronously(long delay, long timer) {
    return Bukkit.getScheduler()
        .runTaskTimerAsynchronously(UbiquitousBukkitPlugin.getInstance(), this::run, delay, timer);
  }
}

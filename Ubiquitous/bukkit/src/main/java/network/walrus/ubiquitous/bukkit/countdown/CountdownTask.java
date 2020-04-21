package network.walrus.ubiquitous.bukkit.countdown;

import co.aikar.timings.Timing;
import co.aikar.timings.Timings;
import java.time.Duration;
import network.walrus.ubiquitous.bukkit.UbiquitousBukkitPlugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * A task which is used to keep track of a single countdown and update it's duration.
 *
 * @author Avicus Network
 */
public class CountdownTask extends BukkitRunnable {

  private final CountdownManager manager;
  private final Countdown countdown;
  private final Timing tickTimer;
  private int elapsedSeconds;
  private int previousElapsed;
  private long lastSecond;

  /**
   * @param manager which this countdown is running inside of
   * @param countdown which this task is for
   */
  public CountdownTask(CountdownManager manager, Countdown countdown) {
    this.manager = manager;
    this.countdown = countdown;
    lastSecond = -1;
    previousElapsed = 0;
    this.tickTimer = Timings.of(UbiquitousBukkitPlugin.getInstance(), countdown.name() + " tick");
  }

  // NOTE: If this method needs to edited in the future it's probably worth re-writing it, it's been
  // edited
  // so much at this point that it's gotten fairly messy and any more edits will just make it
  // uneditable
  @Override
  public void run() {
    if (this.countdown.getDuration().getSeconds() - this.elapsedSeconds <= 0) {
      try (Timing time =
          Timings.ofStart(UbiquitousBukkitPlugin.getInstance(), countdown.name() + " end")) {
        this.countdown.onEnd();
      }
      this.cancel();
      this.manager.remove(this.countdown);
      return;
    }

    Duration elapsed = Duration.ofSeconds(this.elapsedSeconds);
    Duration remaining = this.countdown.getDuration().minus(elapsed);

    // prevents this from ticking multiple times a second
    for (; previousElapsed < elapsedSeconds; previousElapsed++) {
      try (Timing time = tickTimer.startClosable()) {
        this.countdown.onTick(Duration.ofSeconds(previousElapsed), remaining);
      }
    }
    if (previousElapsed != elapsedSeconds) {
      try (Timing time = tickTimer.startClosable()) {
        this.countdown.onTick(elapsed, remaining);
      }
    }
    previousElapsed = elapsedSeconds;

    if (this.countdown.resetPending()) {
      this.elapsedSeconds = 0;
      lastSecond = -1;
    } else if (lastSecond == -1) {
      // First run
      this.elapsedSeconds++;
      lastSecond = System.currentTimeMillis();
    } else {
      long curTime = System.currentTimeMillis();
      while (curTime - lastSecond >= 1000) {
        this.elapsedSeconds++;
        lastSecond += 1000;
      }
    }
  }

  @Override
  public synchronized void cancel() throws IllegalStateException {
    super.cancel();
    try (Timing time =
        Timings.ofStart(UbiquitousBukkitPlugin.getInstance(), countdown.name() + " cancel")) {
      this.countdown.onCancel();
    }
  }

  public CountdownManager getManager() {
    return manager;
  }

  public Countdown getCountdown() {
    return countdown;
  }

  public int getElapsedSeconds() {
    return elapsedSeconds;
  }
}

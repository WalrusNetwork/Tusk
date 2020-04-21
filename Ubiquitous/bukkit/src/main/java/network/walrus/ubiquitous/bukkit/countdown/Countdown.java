package network.walrus.ubiquitous.bukkit.countdown;

import com.google.common.collect.Maps;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import network.walrus.ubiquitous.bukkit.UbiquitousBukkitPlugin;
import network.walrus.ubiquitous.bukkit.boss.BossBar;
import network.walrus.utils.core.translation.Localizable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * A generic timer class that ticks down from a specified duration to 0.
 *
 * @author Avicus Network
 */
public abstract class Countdown {

  final Map<UUID, BossBar> bars = Maps.newHashMap();
  protected Duration duration;
  /** If the countdown has a pending reset. */
  private boolean resetPending;

  protected Countdown(Duration duration) {
    this.duration = duration;
  }

  /** Called when the countdown starts. */
  protected void onStart() {}

  /**
   * Called in one second intervals.
   *
   * @param elapsedTime The amount of time elapsed.
   * @param remainingTime The amount of time remaining in the countdown.
   */
  protected abstract void onTick(Duration elapsedTime, Duration remainingTime);

  /** Called when this countdown ends (at 0 seconds remaining). */
  protected abstract void onEnd();

  /** Called when this countdown is cancelled. */
  protected void onCancel() {}

  /** Name of the countdown, used for timings */
  protected abstract String name();

  /**
   * Gets a {@link BossBar boss bar} for the specified player for this countdown.
   *
   * @param player the player
   * @return a boss bar
   */
  protected final BossBar getBossBar(Player player) {
    return this.bars.computeIfAbsent(
        player.getUniqueId(),
        uniqueId -> UbiquitousBukkitPlugin.getInstance().getBossBarManager().create(player));
  }

  /**
   * Update all boss bars in this countdown with a new name, and percent calculated from the elapsed
   * time.
   *
   * @param name the name for the boss bar
   * @param elapsed the elapsed time
   */
  protected void updateBossBar(Localizable name, Duration elapsed) {
    final float percent =
        (float) 1 - ((float) elapsed.getSeconds() / (float) this.duration.getSeconds());
    this.updateBossBar(name, percent);
  }

  /**
   * Update all boss bars in this countdown with a new name and percent.
   *
   * @param name the name for the boss bar
   * @param percent the percentage
   */
  protected void updateBossBar(Localizable name, float percent) {
    for (Player player : Bukkit.getOnlinePlayers()) {
      this.getBossBar(player).setName(name.render(player)).setPercent(percent);
    }
  }

  /** Clear all boss bars created during the lifetime of this countdown. */
  protected void clearBossBars() {
    for (final BossBar bar : this.bars.values()) {
      bar.destroy();
    }

    this.bars.clear();
  }

  /**
   * Resets the countdown.
   *
   * <p>
   *
   * <p>Resetting the countdown will cause it to start counting down from the beginning.
   */
  protected final void resetElapsedTime() {
    this.resetPending = true;
  }

  /**
   * Determines if a reset is pending, and resets the reset pending flag.
   *
   * @return {@code true} if a reset is pending, {@code false} otherwise
   */
  final boolean resetPending() {
    final boolean previous = this.resetPending;
    this.resetPending = false;
    return previous;
  }

  public Duration getDuration() {
    return duration;
  }
}

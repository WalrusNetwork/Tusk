package network.walrus.games.core.api.results;

import java.time.Duration;
import net.md_5.bungee.api.ChatColor;
import network.walrus.games.core.GamesCoreMessages;
import network.walrus.games.core.api.results.scenario.EndScenario;
import network.walrus.games.core.round.GameRound;
import network.walrus.ubiquitous.bukkit.countdown.Countdown;
import network.walrus.utils.bukkit.sound.NetworkSoundConstants.Games.Results;
import network.walrus.utils.core.color.NetworkColorConstants.Games;
import network.walrus.utils.core.text.UnlocalizedText;
import network.walrus.utils.core.translation.Localizable;
import network.walrus.utils.core.util.StringUtils;

/**
 * A countdown that is used to end a round.
 *
 * @author Austin Mayes
 */
public class RoundEndCountdown extends Countdown {

  private final EndScenario scenario;
  private final GameRound round;

  /**
   * @param duration of the countdown
   * @param scenario that will be executed on end
   * @param round that is being ended
   */
  public RoundEndCountdown(Duration duration, EndScenario scenario, GameRound round) {
    super(duration);
    this.scenario = scenario;
    this.round = round;
  }

  @Override
  protected void onTick(Duration elapsedTime, Duration remainingTime) {
    int sec = (int) remainingTime.getSeconds();

    Localizable message = timeRemainingMessage(elapsedTime, remainingTime);

    // Boss bar
    updateBossBar(message, elapsedTime);

    // Periodic chat broadcast
    if (shouldBroadcast(sec)) {
      this.round.getContainer().broadcast(Results.END_COUNTDOWN_TICK);
      this.round.getContainer().broadcast(message);
    }
  }

  /**
   * Generate the text that is displayed to users.
   *
   * @param elapsedTime time elapsed
   * @param remainingTime time remaining
   * @return text for display
   */
  public Localizable timeRemainingMessage(Duration elapsedTime, Duration remainingTime) {
    ChatColor color = Games.OCN.Countdowns.determineTimeColor(elapsedTime, this.duration);
    UnlocalizedText time =
        new UnlocalizedText(StringUtils.secondsToClock((int) remainingTime.getSeconds()), color);

    return GamesCoreMessages.TIME_REMAINING.with(Games.OCN.Countdowns.TIME_REMAINING, time);
  }

  @Override
  protected void onEnd() {
    this.clearBossBars();
    this.scenario.execute();
  }

  @Override
  protected void onCancel() {
    this.clearBossBars();
  }

  /**
   * Determine if chat messages should be sent to players.
   *
   * @param secs time remaining
   * @return if chat messages should be sent to players
   */
  private boolean shouldBroadcast(int secs) {
    return secs % 600 == 0
        || // 10 minutes
        (secs <= 600 && secs % 120 == 0)
        || // 2 minutes for last 10 minutes
        (secs <= 120 && secs % 30 == 0)
        || // 30 seconds for last 2 minutes
        (secs <= 30 && secs % 5 == 0)
        || // 5 seconds for last 30 seconds
        (secs <= 5); // Every second for last 5 seconds
  }

  @Override
  protected String name() {
    return "round-end";
  }
}

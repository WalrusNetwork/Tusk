package network.walrus.games.octc.destroyables.modes;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import net.md_5.bungee.api.ChatColor;
import network.walrus.games.core.round.GameRound;
import network.walrus.games.octc.destroyables.objectives.DestroyableObjective;
import network.walrus.games.octc.destroyables.objectives.cores.events.CoreLeakEvent;
import network.walrus.games.octc.destroyables.objectives.monuments.events.MonumentDestroyEvent;
import network.walrus.ubiquitous.bukkit.UbiquitousBukkitPlugin;
import network.walrus.ubiquitous.bukkit.countdown.Countdown;
import network.walrus.utils.bukkit.sound.NetworkSoundConstants.Games.OCN.DTCM.Modes;
import network.walrus.utils.core.color.NetworkColorConstants.Games;
import network.walrus.utils.core.text.LocalizedTextFormat;
import network.walrus.utils.core.text.UnlocalizedText;
import network.walrus.utils.core.translation.GlobalLocalizations;
import network.walrus.utils.core.translation.Localizable;
import network.walrus.utils.core.util.StringUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * A countdown that is used to apply a mode.
 *
 * @author Austin Mayes
 */
public class ModeApplicationCountdown extends Countdown implements Listener {

  private final GameRound round;
  /** The mode that this countdown is attempting to apply. */
  private final DestroyableMode mode;

  private final List<DestroyableObjective> objectives;

  /**
   * Constructor.
   *
   * @param round round the countdown is being run inside of
   * @param duration duration of the countdown
   * @param mode mode that this countdown is attempting to apply
   * @param objectives the objectives that follow this mode
   */
  public ModeApplicationCountdown(
      GameRound round,
      Duration duration,
      DestroyableMode mode,
      List<DestroyableObjective> objectives) {
    super(duration);
    this.round = round;
    this.mode = mode;
    this.objectives = objectives;
  }

  @Override
  protected void onTick(Duration elapsedTime, Duration remainingTime) {
    int sec = (int) remainingTime.getSeconds();
    Localizable message = timeRemainingMessage(elapsedTime, remainingTime);

    // Boss bar
    updateBossBar(message, elapsedTime);

    // Periodic chat broadcast
    if (shouldBroadcast(sec)) {
      this.round.getContainer().broadcast(message);
      this.round.getContainer().broadcast(Modes.CHANGE_COUNTDOWN);
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

    return new LocalizedTextFormat(
        GlobalLocalizations.INSTANCE.getBundle(),
        this.mode.getCountdownMessage(),
        this.mode.getName().toText(),
        time);
  }

  @Override
  protected void onEnd() {
    this.clearBossBars();
    Optional<ModeApplicationCountdown> count = mode.attemptApply(this.objectives);
    if (count.isPresent()) {
      UbiquitousBukkitPlugin.getInstance().getCountdownManager().start(count.get());
    }
  }

  /**
   * checks to see if the countdown should be cancelled (if all the objectives are completed)
   *
   * @param event the core leaking event
   */
  @EventHandler
  public void onCoreLeak(CoreLeakEvent event) {
    completeObjective(event.getObjective());
  }

  /**
   * checks to see if the countdown should be cancelled (if all the objectives are completed)
   *
   * @param event the monument breaking event
   */
  @EventHandler
  public void onMonumentBreak(MonumentDestroyEvent event) {
    completeObjective(event.getObjective());
  }

  // checks to see if the mode countdown should be cancelled
  private void completeObjective(DestroyableObjective objective) {
    objectives.remove(objective);
    if (objectives.size() == 0) {
      UbiquitousBukkitPlugin.getInstance().getCountdownManager().cancel(this);
    }
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
  protected void onCancel() {
    this.clearBossBars();
  }

  @Override
  protected String name() {
    return "mode-apply-" + mode.id();
  }
}

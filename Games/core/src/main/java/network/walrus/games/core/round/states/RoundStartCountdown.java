package network.walrus.games.core.round.states;

import co.aikar.timings.Timing;
import co.aikar.timings.Timings;
import java.time.Duration;
import network.walrus.games.core.GamesCoreMessages;
import network.walrus.games.core.GamesPlugin;
import network.walrus.games.core.facets.group.Competitor;
import network.walrus.games.core.facets.group.GroupsManager;
import network.walrus.games.core.round.GameRound;
import network.walrus.ubiquitous.bukkit.UbiquitousBukkitPlugin;
import network.walrus.ubiquitous.bukkit.compat.CompatTitleScreen;
import network.walrus.ubiquitous.bukkit.countdown.Countdown;
import network.walrus.utils.bukkit.sound.NetworkSoundConstants.Games.Countdowns.Start;
import network.walrus.utils.core.color.NetworkColorConstants.Games;
import network.walrus.utils.core.text.LocalizedFormat;
import network.walrus.utils.core.text.LocalizedNumber;
import network.walrus.utils.core.text.LocalizedText;
import network.walrus.utils.core.translation.Localizable;
import org.bukkit.entity.Player;
import org.github.paperspigot.Title;

/**
 * Countdown used to start a round. This assumes that all needed conditions have been met in order
 * for the round to start successfully.
 *
 * @author Austin Mayes
 */
public class RoundStartCountdown extends Countdown {

  final GameRound round;

  /**
   * @param duration until the round starts
   * @param round which is starting
   */
  public RoundStartCountdown(Duration duration, GameRound round) {
    super(duration);
    this.round = round;
  }

  @Override
  protected void onTick(Duration elapsedTime, Duration remainingTime) {
    int elapsed = (int) elapsedTime.getSeconds();
    int remaining = (int) remainingTime.getSeconds();

    // "Round starting in X seconds!"
    Localizable text = createText(remaining);

    // Boss Bar
    float portion = (float) remaining / ((float) elapsed + (float) remaining);
    this.updateBossBar(text, portion);

    // Broadcast
    if (remaining % 10 == 0 || remaining <= 5) {
      this.round.getContainer().broadcast(text);
      this.round.getContainer().broadcast(Start.MESSAGE_BROADCAST);
    }

    // Title + Ding at 3, 2, 1
    if (remaining <= 3) {
      Localizable time = new LocalizedNumber(remaining, Games.OCN.Starting.TITLE);
      for (Competitor competitor :
          this.round.getFacetRequired(GroupsManager.class).getCompetitors()) {
        for (Player player : competitor.getPlayers()) {
          CompatTitleScreen titleManager =
              UbiquitousBukkitPlugin.getInstance().getCompatManager().getCompatTitleScreen();
          if (!titleManager.isLegacy(player)) {
            Title title = new Title("", time.render(player).toLegacyText(), 4, 10, 4);
            titleManager.sendTitle(player, title);
          }

          Start.TITLE_TICK.play(player);
        }
      }
    }
  }

  /**
   * Generate the text that is displayed to users.
   *
   * @param remaining time remaining
   * @return text for display
   */
  private Localizable createText(int remaining) {
    Localizable time = new LocalizedNumber(remaining, Games.OCN.Countdowns.STARTING_TIME);

    LocalizedFormat formatter = GamesCoreMessages.UI_ROUND_STARTING_PLURAL;
    if (remaining == 1) {
      formatter = GamesCoreMessages.UI_ROUND_STARTING;
    }

    return formatter.with(Games.OCN.Countdowns.STARTING_TEXT, time);
  }

  @Override
  protected void onEnd() {
    // Hide boss bar
    this.clearBossBars();

    this.round.setState(RoundState.PLAYING);
    try (Timing t =
        Timings.ofStart(GamesPlugin.instance, "Facets enable: " + this.round.map().name())) {
      this.round.enableFacets();
    }

    // Broadcast
    LocalizedText message = GamesCoreMessages.UI_ROUND_STARTED.with(Games.OCN.Starting.STARTED);
    this.round.getContainer().broadcast(message);

    // "Play" Title and Ding
    for (Competitor competitor :
        this.round.getFacetRequired(GroupsManager.class).getCompetitors()) {
      for (Player player : competitor.getPlayers()) {
        Localizable startMessage = GamesCoreMessages.UI_PLAY.with(Games.OCN.Starting.PLAY);
        CompatTitleScreen titleManager =
            UbiquitousBukkitPlugin.getInstance().getCompatManager().getCompatTitleScreen();
        if (!titleManager.isLegacy(player)) {
          Title title = new Title("", startMessage.render(player).toLegacyText(), 4, 10, 4);
          titleManager.sendTitle(player, title);
        } else {
          player.sendMessage(startMessage);
        }

        Start.STARTED.play(player);
      }
    }
  }

  @Override
  protected void onCancel() {
    this.clearBossBars();
  }

  @Override
  protected String name() {
    return "round-start";
  }
}

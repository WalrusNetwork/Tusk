package network.walrus.games.uhc.facets.delay;

import java.time.Duration;
import net.md_5.bungee.api.ChatColor;
import network.walrus.games.uhc.UHCMessages;
import network.walrus.ubiquitous.bukkit.countdown.Countdown;
import network.walrus.utils.bukkit.sound.NetworkSoundConstants;
import network.walrus.utils.core.color.NetworkColorConstants;
import network.walrus.utils.core.color.NetworkColorConstants.Games;
import network.walrus.utils.core.text.UnlocalizedText;
import network.walrus.utils.core.translation.Localizable;
import network.walrus.utils.core.util.StringUtils;
import network.walrus.utils.parsing.facet.holder.FacetHolder;

/**
 * Countdown which enables PVP when it ends.
 *
 * @author Austin Mayes
 */
public class PVPDelayCountdown extends Countdown {

  private final FacetHolder holder;
  private final DelayedActionsFacet facet;

  /**
   * @param duration of the countdown
   * @param holder to broadcast alerts to
   * @param facet to set PVP with
   */
  PVPDelayCountdown(Duration duration, FacetHolder holder, DelayedActionsFacet facet) {
    super(duration);
    this.holder = holder;
    this.facet = facet;
  }

  @Override
  protected void onTick(Duration elapsedTime, Duration remainingTime) {
    Localizable message = timeRemainingMessage(elapsedTime, remainingTime);

    if (duration.toMinutes() < 2) {
      updateBossBar(message, elapsedTime);
      if (duration.toMinutes() == 1 && duration.getSeconds() == 0) {
        holder
            .getContainer()
            .broadcast(NetworkSoundConstants.Games.UHC.PVP.Countdown.MINUTE_WARNING);
      } else if (duration.toMinutes() == 0 && duration.getSeconds() <= 30)
        holder.getContainer().broadcast(NetworkSoundConstants.Games.UHC.PVP.Countdown.FINAL_30);
    } else if (duration.getSeconds() == 30 || duration.getSeconds() == 0) {
      holder.getContainer().broadcast(message);
      holder.getContainer().broadcast(NetworkSoundConstants.Games.UHC.PVP.Countdown.EVERY_30);
    }
  }

  /**
   * Generate the text that is displayed to users.
   *
   * @param elapsedTime time elapsed
   * @param remainingTime time remaining
   * @return text for display
   */
  private Localizable timeRemainingMessage(Duration elapsedTime, Duration remainingTime) {
    ChatColor color = Games.OCN.Countdowns.determineTimeColor(elapsedTime, this.duration);
    UnlocalizedText time =
        new UnlocalizedText(StringUtils.secondsToClock((int) remainingTime.getSeconds()), color);

    return UHCMessages.PVP_COUNTDOWN.with(Games.UHC.PVP.COUNTDOWN_COLOR, time);
  }

  @Override
  protected void onEnd() {
    facet.pvp = true;
    clearBossBars();
    holder
        .getContainer()
        .broadcast(UHCMessages.PVP_ENABLED.with(NetworkColorConstants.Games.UHC.PVP.ENABLED));
    holder.getContainer().broadcast(NetworkSoundConstants.Games.UHC.PVP.ENABLED);
  }

  @Override
  protected void onCancel() {
    clearBossBars();
  }

  @Override
  protected String name() {
    return "pvp-delay";
  }
}

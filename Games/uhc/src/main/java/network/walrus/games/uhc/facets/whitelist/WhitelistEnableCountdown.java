package network.walrus.games.uhc.facets.whitelist;

import java.time.Duration;
import network.walrus.games.uhc.UHCMessages;
import network.walrus.ubiquitous.bukkit.countdown.Countdown;
import network.walrus.utils.bukkit.sound.NetworkSoundConstants.Games.UHC;
import network.walrus.utils.core.color.NetworkColorConstants.Games.UHC.Whitelist;
import network.walrus.utils.core.text.LocalizedFormat;
import network.walrus.utils.core.text.UnlocalizedText;
import network.walrus.utils.core.translation.Localizable;
import network.walrus.utils.core.util.StringUtils;
import org.bukkit.Bukkit;

/**
 * Command which enables this whitelist after a specified amount of time.
 *
 * @author Austin Mayes
 */
public class WhitelistEnableCountdown extends Countdown {

  /** @param duration of the countdown */
  WhitelistEnableCountdown(Duration duration) {
    super(duration);
  }

  @Override
  protected void onStart() {
    Bukkit.getServer().setWhitelist(false);
  }

  @Override
  protected void onTick(Duration elapsedTime, Duration remainingTime) {
    int elapsed = (int) elapsedTime.getSeconds();
    int remaining = (int) remainingTime.getSeconds();

    // "Whitelist on in X:XX!"
    Localizable text = createText(StringUtils.secondsToClock(remaining));

    // Boss Bar
    float portion = (float) remaining / ((float) elapsed + (float) remaining);
    this.updateBossBar(text, portion);

    // Broadcast
    if (remaining % 120 == 0 || remaining <= 5) {
      Bukkit.broadcast(UHCMessages.prefix(text));
      UHC.Whitelist.COUNTDOWN.play(Bukkit.getOnlinePlayers());
    }
  }

  /**
   * Generate the text that is displayed to users.
   *
   * @param remaining time remaining
   * @return text for display
   */
  private Localizable createText(String remaining) {
    Localizable time = new UnlocalizedText(remaining, Whitelist.Countdown.TIME);

    LocalizedFormat formatter = UHCMessages.UI_WHITELIST_ON_COUNTDOWN;

    return formatter.with(Whitelist.Countdown.TEXT, time);
  }

  @Override
  protected void onEnd() {
    clearBossBars();
    Bukkit.getServer().setWhitelist(true);
    Bukkit.broadcast(UHCMessages.prefix(UHCMessages.UI_WHITELIST_ON.with(Whitelist.Countdown.ON)));
    UHC.Whitelist.ON.play(Bukkit.getOnlinePlayers());
  }

  @Override
  protected void onCancel() {
    this.clearBossBars();
    Bukkit.getServer().setWhitelist(true);
  }

  @Override
  protected String name() {
    return "whitelist-enable";
  }
}

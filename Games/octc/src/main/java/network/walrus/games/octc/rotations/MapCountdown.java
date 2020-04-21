package network.walrus.games.octc.rotations;

import java.time.Duration;
import network.walrus.games.octc.OCNGameManager;
import network.walrus.games.octc.OCNMap;
import network.walrus.games.octc.OCNMessages;
import network.walrus.ubiquitous.bukkit.countdown.Countdown;
import network.walrus.utils.core.color.NetworkColorConstants.Games.Maps;
import network.walrus.utils.core.text.UnlocalizedText;
import network.walrus.utils.core.translation.Localizable;
import network.walrus.utils.core.util.StringUtils;

/**
 * Countdown that cycles to a map after a specified duration has elapsed.
 *
 * @author Rafi Baum
 */
class MapCountdown extends Countdown {

  private final OCNMap toSwitch;

  MapCountdown(Duration duration, OCNMap toSwitch) {
    super(duration);
    this.toSwitch = toSwitch;
  }

  @Override
  protected void onTick(Duration elapsedTime, Duration remainingTime) {
    Localizable bossbarTitle =
        OCNMessages.MAP_CHANGE_COUNTDOWN.with(
            Maps.Random.TITLE_COUNTDOWN_TEXT,
            new UnlocalizedText(toSwitch.name(), Maps.Random.TITLE_COUNTDOWN_MAP),
            new UnlocalizedText(
                StringUtils.secondsToClock(remainingTime.getSeconds()),
                Maps.Random.TITLE_COUNTDOWN_TIME));
    updateBossBar(bossbarTitle, elapsedTime);
  }

  @Override
  protected void onEnd() {
    OCNGameManager.instance.setMap(toSwitch);
    this.clearBossBars();
  }

  @Override
  protected void onCancel() {
    super.onCancel();
    this.clearBossBars();
  }

  @Override
  protected String name() {
    return "random-map-countdown";
  }
}

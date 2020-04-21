package network.walrus.games.octc.ctf.flags;

import java.time.Duration;
import java.util.Collections;
import network.walrus.games.core.facets.group.GroupsManager;
import network.walrus.games.core.util.EventUtil;
import network.walrus.games.octc.OCNMessages;
import network.walrus.games.octc.ctf.flags.events.FlagRecoverEvent;
import network.walrus.ubiquitous.bukkit.countdown.Countdown;
import network.walrus.utils.bukkit.sound.NetworkSoundConstants.Games.OCN.CTF.Respawn;
import network.walrus.utils.core.color.NetworkColorConstants.Games.OCN.CTF;

/**
 * Countdown which places a {@link FlagObjective} at a {@link Post} after a specified delay.
 *
 * @author Austin Mayes
 */
public class FlagCountdown extends Countdown {

  private final FlagObjective flag;
  private Duration remainingTime;

  /**
   * @param duration of the countdown
   * @param flag to place at the post
   */
  public FlagCountdown(Duration duration, FlagObjective flag) {
    super(duration);
    this.flag = flag;
    this.remainingTime = duration;
  }

  @Override
  public String name() {
    return "flag respawn";
  }

  @Override
  protected void onTick(Duration elapsedTime, Duration remainingTime) {
    this.remainingTime = remainingTime;
    this.flag.updateSidebar();
  }

  @Override
  protected void onEnd() {
    if (this.flag.getCurrentLocation().isPresent()) {
      EventUtil.call(new FlagRecoverEvent(this.flag));
    }
    this.flag.clear();
    this.flag.place();
    this.flag.clearCountdown();
    flag.getHolder()
        .getContainer()
        .broadcast(
            OCNMessages.FLAG_RESPAWN_END.with(
                CTF.RESPAWN, flag.getName().toText(flag.getChatColor())));
    flag.getHolder()
        .getFacetRequired(GroupsManager.class)
        .playScopedSound(
            Collections.emptyList(), Respawn.SELF, Respawn.TEAM, Respawn.ENEMY, Respawn.SPECTATOR);
  }

  @Override
  protected void onCancel() {
    this.flag.clearCountdown();
  }

  public Duration getRemainingTime() {
    return remainingTime;
  }
}

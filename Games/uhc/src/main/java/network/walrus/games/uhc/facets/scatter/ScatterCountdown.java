package network.walrus.games.uhc.facets.scatter;

import com.google.common.collect.Lists;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import network.walrus.games.core.facets.group.Competitor;
import network.walrus.games.uhc.UHCManager;
import network.walrus.games.uhc.UHCMessages;
import network.walrus.games.uhc.spawn.SpawnManager;
import network.walrus.ubiquitous.bukkit.UbiquitousBukkitPlugin;
import network.walrus.ubiquitous.bukkit.countdown.Countdown;
import network.walrus.ubiquitous.bukkit.freeze.FreezeManager;
import network.walrus.utils.bukkit.sound.NetworkSoundConstants.Games.UHC;
import network.walrus.utils.core.color.NetworkColorConstants.Games.UHC.Scatter;
import network.walrus.utils.core.text.LocalizedNumber;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Countdown which scatters competitors in a stagnated manner.
 *
 * @author Austin Mayes
 */
public class ScatterCountdown extends Countdown {

  private final List<Competitor> competitors;
  private final List<List<Competitor>> scatterQueue;
  private final SpawnManager spawnsManager;
  private final FreezeManager freezeManager;

  /**
   * @param competitors to scatter
   * @param spawnsManager used to spawn players
   */
  ScatterCountdown(List<Competitor> competitors, SpawnManager spawnsManager) {
    super(Duration.ofSeconds(Math.max(((int) Math.ceil((competitors.size() / 3)) * 5) + 2, 10)));
    this.competitors = competitors;
    this.scatterQueue = new ArrayList<>(Lists.partition(competitors, 3));
    this.spawnsManager = spawnsManager;
    this.freezeManager = UbiquitousBukkitPlugin.getInstance().getFreezeManager();
  }

  @Override
  protected void onTick(Duration elapsedTime, Duration remainingTime) {
    int elapsed = (int) elapsedTime.getSeconds();
    int remaining = (int) remainingTime.getSeconds();

    float portion = (float) elapsed / ((float) elapsed + (float) remaining);
    this.updateBossBar(
        UHCMessages.SCATTERING.with(
            Scatter.COUNTDOWN_TEXT,
            new LocalizedNumber(portion * 100, 1, 1, Scatter.COUNTDOWN_PERCENT)),
        portion);

    if (this.scatterQueue.isEmpty()) {
      return;
    }

    if (elapsed % 5 != 0) {
      return;
    }

    List<Competitor> toScatter = this.scatterQueue.remove(0);
    for (Competitor competitor : toScatter) {
      if (competitor == null || competitor.getPlayers().isEmpty()) continue;

      this.spawnsManager.spawn(competitor);
      for (Player p : competitor.getPlayers()) {
        UHC.Scatter.TELEPORTED.play(p);
        this.freezeManager.freeze(p);
        p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 1));
      }
    }
  }

  @Override
  protected void onEnd() {
    clearBossBars();

    UHCManager.instance.getUHC().getContainer().broadcast(UHC.Scatter.COMPLETE);
    UbiquitousBukkitPlugin.getInstance()
        .getCountdownManager()
        .start(new ReleaseCountdown(Duration.ofSeconds(20), competitors));
  }

  @Override
  protected String name() {
    return "scatter";
  }
}

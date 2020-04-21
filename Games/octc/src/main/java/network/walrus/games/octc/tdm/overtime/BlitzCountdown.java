package network.walrus.games.octc.tdm.overtime;

import java.time.Duration;
import net.md_5.bungee.api.ChatColor;
import network.walrus.games.octc.OCNMessages;
import network.walrus.ubiquitous.bukkit.border.CylinderBorder;
import network.walrus.ubiquitous.bukkit.border.IWorldBorder;
import network.walrus.ubiquitous.bukkit.countdown.Countdown;
import network.walrus.ubiquitous.bukkit.task.BetterRunnable;
import network.walrus.utils.bukkit.region.BoundedRegion;
import network.walrus.utils.bukkit.region.shapes.CylinderRegion;
import network.walrus.utils.core.color.NetworkColorConstants.Games.OCN.TDM.Overtime;
import network.walrus.utils.core.text.LocalizedText;
import network.walrus.utils.core.text.UnlocalizedText;
import network.walrus.utils.core.util.StringUtils;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

/**
 * Countdown used for the Blitz gamemode when TDM goes into overtime
 *
 * @author David Rodriguez
 */
public class BlitzCountdown extends Countdown {

  private final FacetHolder holder;
  private final BoundedRegion maxRegion;
  private final BoundedRegion minRegion;
  private IWorldBorder currentBorder;
  private BukkitTask runningTask;

  protected BlitzCountdown(
      Duration duration,
      FacetHolder holder,
      BoundedRegion maxRegion,
      BoundedRegion minRegion,
      IWorldBorder currentBorder) {
    super(duration);
    this.holder = holder;
    this.maxRegion = maxRegion;
    this.minRegion = minRegion;
    this.currentBorder = currentBorder;
  }

  @Override
  protected void onTick(Duration elapsedTime, Duration remainingTime) {
    UnlocalizedText time = new UnlocalizedText(StringUtils.durationToClock(remainingTime));
    time.style().color(ChatColor.WHITE);
    LocalizedText localizedText = OCNMessages.TDM_OVERTIME_BOSSBAR.with(Overtime.BOSSBAR, time);
    updateBossBar(localizedText, elapsedTime);
  }

  @Override
  public void onStart() {
    double distance =
        Math.max(
            maxRegion.max().distance(minRegion.max()), maxRegion.min().distance(minRegion.min()));
    Vector minRegionMax;
    Vector minRegionMin;

    // I'm doing this because CylinderRegion returns its radius as x and z instead of actual
    // coordinates
    if (currentBorder instanceof CylinderBorder) {
      Vector minCenter = minRegion.getCenter().clone().setY(minRegion.min().getY());
      minRegionMax = minRegion.max().clone().add(minCenter);
      minRegionMin = minRegion.min().clone().add(minCenter);
      distance = ((CylinderRegion) maxRegion).radius() - ((CylinderRegion) minRegion).radius();
    } else {
      minRegionMax = minRegion.max();
      minRegionMin = minRegion.min();
    }

    int timer = (int) Math.floor((duration.toMillis() / 50F) / distance);

    World world = Bukkit.getWorld(holder.getWorldProvider().worldName());
    runningTask =
        ((BetterRunnable)
                () -> {
                  if (!currentBorder.contains(minRegionMax)
                      || !currentBorder.contains(minRegionMin)) return;
                  currentBorder.apply(world);
                  currentBorder.expand(new Vector(-1, 0, -1));
                })
            .runTaskTimer(0, timer, "border-countdown-task");
  }

  @Override
  protected void onEnd() {
    clearBossBars();
    runningTask.cancel();
  }

  @Override
  protected void onCancel() {
    onEnd();
  }

  @Override
  protected String name() {
    return "blitz-countdown";
  }
}

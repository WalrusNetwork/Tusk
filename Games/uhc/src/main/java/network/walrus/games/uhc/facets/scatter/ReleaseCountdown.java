package network.walrus.games.uhc.facets.scatter;

import java.time.Duration;
import java.util.List;
import network.walrus.games.core.facets.group.Competitor;
import network.walrus.games.core.round.states.RoundState;
import network.walrus.games.core.util.GameTask;
import network.walrus.games.uhc.UHCManager;
import network.walrus.games.uhc.UHCMessages;
import network.walrus.games.uhc.facets.combatlog.CombatLogTracker;
import network.walrus.ubiquitous.bukkit.UbiquitousBukkitPlugin;
import network.walrus.ubiquitous.bukkit.compat.CompatActionBar;
import network.walrus.ubiquitous.bukkit.countdown.Countdown;
import network.walrus.ubiquitous.bukkit.freeze.FreezeManager;
import network.walrus.ubiquitous.bukkit.lobby.facets.sterile.WorldProtectionListener;
import network.walrus.utils.bukkit.PlayerUtils;
import network.walrus.utils.bukkit.sound.NetworkSoundConstants.Games.UHC.Scatter;
import network.walrus.utils.core.color.NetworkColorConstants.Games.UHC;
import network.walrus.utils.core.text.LocalizedNumber;
import network.walrus.utils.core.translation.Localizable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Countdown which releases players at their spawn points after a delay.
 *
 * @author Austin Mayes
 */
public class ReleaseCountdown extends Countdown {

  private final List<Competitor> competitors;
  private final FreezeManager freezeManager;
  private final CompatActionBar compatActionBar;

  /**
   * @param duration of the countdown
   * @param competitors to release
   */
  ReleaseCountdown(Duration duration, List<Competitor> competitors) {
    super(duration);
    this.competitors = competitors;
    this.freezeManager = UbiquitousBukkitPlugin.getInstance().getFreezeManager();
    this.compatActionBar =
        UbiquitousBukkitPlugin.getInstance().getCompatManager().getCompatActionBar();
  }

  @Override
  protected void onTick(Duration elapsedTime, Duration remainingTime) {
    Localizable message =
        UHCMessages.RELEASE.with(
            UHC.Scatter.RELEASE_TEXT,
            new LocalizedNumber(remainingTime.getSeconds(), UHC.Scatter.RELEASE_NUMBER));
    for (Competitor c : competitors) {
      for (Player p : c.getPlayers()) {
        if (p == null) continue;
        if (!compatActionBar.isLegacy(p)) {
          compatActionBar.sendActionBar(p, message.render(p));
        } else {
          long seconds = remainingTime.getSeconds();
          if (seconds % 10 == 0 || seconds <= 5) {
            p.sendMessage(message);
          }
        }

        Scatter.RELEASE_TICK.play(p);
      }
    }
  }

  @Override
  protected void onEnd() {
    UHCManager.instance
        .getUHC()
        .getContainer()
        .actOnAllWorlds(
            w -> {
              w.setGameRuleValue("doDaylightCycle", "true");
            });
    UHCManager.instance.getUHC().setState(RoundState.PLAYING);
    UHCManager.instance.getUHC().enableFacets();
    ItemStack starter =
        UHCManager.instance.getConfig().starterFood.get().getValue() > 0
            ? new ItemStack(
                UHCManager.instance.getConfig().starterFood.get().getKey(),
                UHCManager.instance.getConfig().starterFood.get().getValue())
            : null;

    for (Competitor c : competitors) {
      for (Player p : c.getPlayers()) {
        if (p == null) continue;
        this.freezeManager.thaw(p);
        PlayerUtils.reset(p);
        Scatter.UNFROZEN.play(p);
        if (starter != null) p.getInventory().addItem(starter);
      }
    }
    UHCManager.instance
        .getUHC()
        .getFacet(CombatLogTracker.class)
        .ifPresent(CombatLogTracker::thawAll);

    GameTask.of(
            "World protection enable",
            () -> {
              WorldProtectionListener.IGNORE_ALL = false;
              System.gc();
            })
        .later(60 * 20);
  }

  @Override
  protected String name() {
    return "release";
  }
}

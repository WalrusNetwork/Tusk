package network.walrus.games.uhc.facets.delay;

import network.walrus.games.core.util.GameTask;
import network.walrus.games.uhc.UHCManager;
import network.walrus.games.uhc.commands.PlayerModificationCommands;
import network.walrus.ubiquitous.bukkit.UbiquitousBukkitPlugin;
import network.walrus.ubiquitous.bukkit.tracker.event.entity.EntityDamageEvent;
import network.walrus.ubiquitous.bukkit.tracker.event.player.PlayerDamageEvent;
import network.walrus.ubiquitous.bukkit.tracker.event.tag.TaggedPlayerDamageEvent;
import network.walrus.utils.parsing.facet.Facet;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 * Performs delayed actions on the round based on configured values.
 *
 * @author Austin Mayes
 */
public class DelayedActionsFacet extends Facet implements Listener {

  private final GameTask healTask;
  private final GameTask permaDayTask;
  private final PVPDelayCountdown countdown;
  boolean pvp = false;

  /** @param holder which contains this facet */
  public DelayedActionsFacet(FacetHolder holder) {
    healTask =
        GameTask.of(
            "Final heal",
            () -> {
              for (Player player : holder.players()) {
                PlayerModificationCommands.heal(player);
              }
            });
    this.permaDayTask =
        GameTask.of(
            "perma-day",
            () ->
                holder
                    .getContainer()
                    .actOnAllWorlds(
                        (w) -> {
                          w.setTime(6000);
                          w.setGameRuleValue("doDaylightCycle", "false");
                        }));
    this.countdown =
        new PVPDelayCountdown(UHCManager.instance.getConfig().pvpDelay.get(), holder, this);
  }

  /** Block PVP before it is enabled */
  @EventHandler(priority = EventPriority.HIGH)
  public void blockPVP(PlayerDamageEvent event) {
    handlePVP(event);
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void blockPVP(TaggedPlayerDamageEvent event) {
    handlePVP(event);
  }

  private void handlePVP(EntityDamageEvent event) {
    if (event.getInfo().getResolvedDamager() instanceof Player && !this.pvp) {
      if (event.getInfo().getResolvedDamager() == event.getEntity()) {
        return;
      }

      event.setCancelled(true);
    }
  }

  @Override
  public void enable() {
    healTask.later((int) UHCManager.instance.getConfig().finalHealAt.get().getSeconds() * 20);
    permaDayTask.later(((int) UHCManager.instance.getConfig().permaDayAt.get().getSeconds() * 20));
    UbiquitousBukkitPlugin.getInstance().getCountdownManager().start(this.countdown);
  }

  @Override
  public void disable() {
    healTask.reset();
    permaDayTask.reset();
    UbiquitousBukkitPlugin.getInstance()
        .getCountdownManager()
        .cancelAll((c) -> c instanceof PVPDelayCountdown);
  }
}

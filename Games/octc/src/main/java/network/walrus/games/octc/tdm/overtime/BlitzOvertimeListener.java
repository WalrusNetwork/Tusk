package network.walrus.games.octc.tdm.overtime;

import net.md_5.bungee.api.chat.TextComponent;
import network.walrus.games.core.facets.group.GroupsManager;
import network.walrus.games.core.util.EventUtil;
import network.walrus.games.octc.OCNMessages;
import network.walrus.games.octc.global.spawns.PlayerStartRespawnEvent;
import network.walrus.ubiquitous.bukkit.UbiquitousBukkitPlugin;
import network.walrus.ubiquitous.bukkit.compat.CompatTitleScreen;
import network.walrus.ubiquitous.bukkit.task.BetterRunnable;
import network.walrus.utils.bukkit.sound.NetworkSoundConstants.Games.Deaths;
import network.walrus.utils.core.color.NetworkColorConstants.Games.OCN.TDM.Overtime;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import network.walrus.utils.parsing.facet.parse.configurator.listener.FacetListener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.github.paperspigot.Title;

/**
 * Listener that enables 1 life blitz when the {@link BlitzOvertimeFacet} is enabled
 *
 * @author David Rodriguez
 */
public class BlitzOvertimeListener extends FacetListener<BlitzOvertimeFacet> {

  private final BlitzOvertimeFacet blitzOvertimeFacet;

  /**
   * @param holder which this listener is operating inside of
   * @param blitzOvertimeFacet to pull data from
   */
  public BlitzOvertimeListener(FacetHolder holder, BlitzOvertimeFacet blitzOvertimeFacet) {
    super(holder);
    this.blitzOvertimeFacet = blitzOvertimeFacet;
  }

  @EventHandler
  public void onPlayerStartRespawn(PlayerStartRespawnEvent event) {
    if (!blitzOvertimeFacet.isActive()) return;
    event.setCanceled(true);
    revivePlayer(event.getPlayer());
  }

  private void revivePlayer(Player player) {
    // Hack so that the player is revived with the teleport tool
    ((BetterRunnable)
            () -> {
              CompatTitleScreen compatTitle =
                  UbiquitousBukkitPlugin.getInstance().getCompatManager().getCompatTitleScreen();

              GroupsManager groupsManager = getHolder().getFacetRequired(GroupsManager.class);

              // Move to spectators and freeze
              groupsManager.changeGroup(player, groupsManager.getSpectators(), true, true);
              UbiquitousBukkitPlugin.getInstance().getFreezeManager().freeze(player);
              Deaths.SELF.play(player);

              if (compatTitle.isLegacy(player)) {
                player.sendMessage(
                    OCNMessages.TDM_OVERTIME_DEATH.with(Overtime.DEATH).render(player));
              } else {
                Title title =
                    new Title(
                        OCNMessages.TDM_OVERTIME_DEATH.with(Overtime.DEATH).render(player),
                        new TextComponent(""),
                        5,
                        15,
                        5);
                compatTitle.sendTitle(player, title);
              }

              EventUtil.call(new BlitzOvertimeDeathEvent(player));
            })
        .runTaskLater(1, "blitz-respawn");
  }
}

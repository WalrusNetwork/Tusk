package network.walrus.games.octc.tdm.overtime;

import java.time.Duration;
import network.walrus.games.core.events.group.PlayerChangeGroupEvent;
import network.walrus.games.core.events.round.RoundStateChangeEvent;
import network.walrus.games.core.facets.group.GroupsManager;
import network.walrus.games.octc.OCNMessages;
import network.walrus.games.octc.global.spawns.OCNSpawnManager;
import network.walrus.ubiquitous.bukkit.UbiquitousBukkitPlugin;
import network.walrus.ubiquitous.bukkit.border.IWorldBorder;
import network.walrus.ubiquitous.bukkit.countdown.Countdown;
import network.walrus.utils.bukkit.region.BoundedRegion;
import network.walrus.utils.core.color.NetworkColorConstants.Games.OCN.TDM;
import network.walrus.utils.parsing.facet.Facet;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Overtime facet that executes the Blitz gamemode
 *
 * @author David Rodriguez
 */
public class BlitzOvertimeFacet extends Facet implements Listener {

  private final FacetHolder facetHolder;
  private final boolean teleportToSpawn;
  private final Countdown countdown;
  private boolean active;

  /**
   * Creates a new blitz overtime facet
   *
   * @param facetHolder which is holding this facet
   * @param teleportToSpawn whether to teleport the player to their respective spawn when the
   *     overtime starts
   * @param shrinkTime time for world border to reach the minRegion
   * @param maxRegion max region for the world border
   * @param minRegion min region for the world border
   */
  public BlitzOvertimeFacet(
      FacetHolder facetHolder,
      boolean teleportToSpawn,
      Duration shrinkTime,
      BoundedRegion maxRegion,
      BoundedRegion minRegion,
      IWorldBorder worldBorder) {
    this.active = false;
    this.facetHolder = facetHolder;
    this.teleportToSpawn = teleportToSpawn;
    this.countdown = new BlitzCountdown(shrinkTime, facetHolder, maxRegion, minRegion, worldBorder);
  }

  /** Cancel countdown and set to ended when match ends */
  @EventHandler
  public void onOvertimeEnd(RoundStateChangeEvent event) {
    if (event.isChangeToNotPlaying()) {
      UbiquitousBukkitPlugin.getInstance().getCountdownManager().cancel(countdown);
      this.active = false;
    }
  }

  /** Prevent from joining while in overtime */
  @EventHandler
  public void onPlayerGroupChange(PlayerChangeGroupEvent event) {
    if (!active) return;
    if (event.getGroup().isSpectator()) return;
    event.getPlayer().sendMessage(OCNMessages.TDM_OVERTIME_ACTIVE.with(TDM.Overtime.ACTIVE));
    event.setCancelled(true);
  }

  void setEnabled() {
    this.active = true;

    GroupsManager groupsManager = facetHolder.getFacetRequired(GroupsManager.class);
    if (teleportToSpawn) {
      OCNSpawnManager ocnSpawnManager = facetHolder.getFacetRequired(OCNSpawnManager.class);
      for (Player player : facetHolder.getContainer().players()) {
        if (!groupsManager.isSpectator(player) && !groupsManager.isObserving(player)) {
          ocnSpawnManager.spawn(player);
        }
      }
    }

    // Start countdown
    UbiquitousBukkitPlugin.getInstance().getCountdownManager().start(countdown);
  }

  /** @return Whether or not the overtime scenario is active */
  public boolean isActive() {
    return active;
  }
}

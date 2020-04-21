package network.walrus.ubiquitous.bukkit.doublejump;

import network.walrus.ubiquitous.bukkit.UbiquitousBukkitPlugin;
import network.walrus.ubiquitous.bukkit.tracker.event.player.PlayerCoarseMoveEvent;
import network.walrus.utils.bukkit.cooldown.ObjectCooledEvent;
import network.walrus.utils.bukkit.sound.NetworkSoundConstants.Games;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.util.Vector;

/**
 * Listener which handles when a player double jumps
 *
 * @author David Rodriguez
 */
public class DoubleJumpListener implements Listener {

  private final DoubleJumpManager doubleJumpManager;

  DoubleJumpListener(DoubleJumpManager doubleJumpManager) {
    this.doubleJumpManager = doubleJumpManager;
  }

  /** Removes the double jump ability when a player leaves */
  @EventHandler
  public void onDisconnect(PlayerQuitEvent event) {
    doubleJumpManager.disableDoubleJump(event.getPlayer());
  }

  /** Allows flight whenever the object cools downs */
  @EventHandler
  public void onCoolDown(ObjectCooledEvent event) {
    Player player = event.getOwner();

    if (!doubleJumpManager.hasKit(player)) return;
    if (doubleJumpManager.getPlayerInfo(player).getCooldownTracker().isCooling(player)) return;

    player.setAllowFlight(true);
  }

  /**
   * Handles when the player double jumps and recharges the cool down if {@link
   * DoubleJumpInfo#rechargeBeforeLanding()} is true
   */
  @EventHandler
  public void onToggleFlight(PlayerToggleFlightEvent event) {
    Player player = event.getPlayer();
    if (!doubleJumpManager.hasKit(player)) return;

    // Disallow double jump for frozen players
    if (UbiquitousBukkitPlugin.getInstance().getFreezeManager().isFrozen(player)) return;

    DoubleJumpInfo doubleJumpInfo = doubleJumpManager.getPlayerInfo(player);
    if (doubleJumpInfo.getCooldownTracker().isCooling(player)) return;

    int power = doubleJumpInfo.getPower();

    if (event.isFlying()) {
      event.setCancelled(true);

      Vector direction = player.getLocation().getDirection();

      direction.setY(0.75 + Math.abs(direction.getY()) * 0.5);
      direction.multiply(power / 3f);
      player.setVelocity(direction);

      Games.Kits.DOUBLE_JUMP.play(player);

      // Register cool down
      doubleJumpManager.setCharge(player, 0F);
      player.setAllowFlight(false);
      if (doubleJumpInfo.rechargeBeforeLanding()) doubleJumpManager.setCoolingDown(player);
    }
  }

  /** Recharges the cool down when the player hits the ground */
  @EventHandler
  public void onPlayerMove(PlayerCoarseMoveEvent event) {
    Player player = event.getPlayer();
    if (!doubleJumpManager.hasKit(player)) return;

    DoubleJumpInfo doubleJumpInfo = doubleJumpManager.getPlayerInfo(player);
    if (doubleJumpInfo.rechargeBeforeLanding()) return;
    if (doubleJumpInfo.getCooldownTracker().isCooling(player)) return;
    if (player.isFlying()) return;

    Location from = event.getFrom();
    Location to = event.getTo();

    if (from.getY() <= to.getY()) return;
    if (!to.clone().subtract(0, 1, 0).getBlock().getType().isSolid()) return;
    if (player.getExp() > 0) return;

    doubleJumpManager.setCoolingDown(player);
  }
}

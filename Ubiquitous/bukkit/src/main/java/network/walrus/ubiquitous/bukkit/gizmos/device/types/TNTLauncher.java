package network.walrus.ubiquitous.bukkit.gizmos.device.types;

import com.google.gson.JsonObject;
import java.time.Duration;
import network.walrus.ubiquitous.bukkit.UbiquitousMessages;
import network.walrus.ubiquitous.bukkit.gizmos.device.DeviceContext;
import network.walrus.ubiquitous.bukkit.gizmos.device.DeviceGizmo;
import network.walrus.ubiquitous.bukkit.task.BetterRunnable;
import network.walrus.utils.bukkit.VectorUtils;
import network.walrus.utils.core.color.NetworkColorConstants.Gizmo.Device;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.inventory.ItemStack;

/**
 * Device which shoots out a primed TNT from a player's location.
 *
 * @author Austin Mayes
 */
public class TNTLauncher extends DeviceGizmo<DeviceContext> {

  /**
   * @param cost droplet cost of the gizmo
   * @param coolDown between usages of this gizmo
   * @param maxUsages number of usages before the gizmo is removed from the player's purchase list
   */
  public TNTLauncher(int cost, Duration coolDown, int maxUsages) {
    super(
        "tnt-launcher",
        UbiquitousMessages.GIZMO_NAME_TNT.with(Device.TNT.NAME),
        UbiquitousMessages.GIZMO_DESC_TNT,
        new ItemStack(Material.TNT),
        cost,
        coolDown,
        maxUsages);
  }

  @Override
  public void onUse(Player player) {
    final TNTPrimed tnt =
        player.getWorld().spawn(player.getLocation().add(0.0D, 1.0D, 0.0D), TNTPrimed.class);
    tnt.setFuseTicks(30);
    tnt.setVelocity(player.getLocation().getDirection().multiply(1));

    ((BetterRunnable)
            () -> {
              tnt.getWorld().playSound(tnt.getLocation(), Sound.EXPLODE, 1.0f, 1.0f);
              tnt.getWorld().spigot().playEffect(tnt.getLocation(), Effect.EXPLOSION_HUGE);
              tnt.getNearbyEntities(5, 5, 5)
                  .forEach(e -> VectorUtils.deflect(tnt.getLocation().toVector(), e, 3));
            })
        .runTaskLater(30, "tnt-explode");
  }

  @Override
  public DeviceContext deserializeContext(JsonObject json) {
    return DeviceContext.deSerializeDefault(this, json);
  }
}

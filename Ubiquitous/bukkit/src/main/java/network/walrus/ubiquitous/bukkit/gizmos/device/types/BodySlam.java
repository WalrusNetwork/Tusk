package network.walrus.ubiquitous.bukkit.gizmos.device.types;

import com.google.gson.JsonObject;
import java.time.Duration;
import network.walrus.ubiquitous.bukkit.UbiquitousMessages;
import network.walrus.ubiquitous.bukkit.gizmos.device.DeviceContext;
import network.walrus.ubiquitous.bukkit.gizmos.device.DeviceGizmo;
import network.walrus.utils.bukkit.VectorUtils;
import network.walrus.utils.core.color.NetworkColorConstants.Gizmo.Device;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Device which deflects nearby players.
 *
 * @author Austin Mayes
 */
public class BodySlam extends DeviceGizmo<DeviceContext> {

  /**
   * @param cost droplet cost of the gizmo
   * @param coolDown between usages of this gizmo
   * @param maxUsages number of usages before the gizmo is removed from the player's purchase list
   */
  public BodySlam(int cost, Duration coolDown, int maxUsages) {
    super(
        "body-slam",
        UbiquitousMessages.GIZMO_NAME_BODY_SLAM.with(Device.BodySlam.NAME),
        UbiquitousMessages.GIZMO_DESC_BODY_SLAM,
        new ItemStack(Material.IRON_AXE),
        cost,
        coolDown,
        maxUsages);
  }

  @Override
  public void onUse(Player player) {
    for (int x = -2; x <= 2; x++) {
      for (int z = -2; z <= 2; z++) {
        player
            .getWorld()
            .playEffect(
                player.getLocation().clone().add(x, 0, z),
                Effect.TILE_BREAK,
                player
                    .getLocation()
                    .clone()
                    .subtract(0, 1, 0)
                    .getBlock()
                    .getState()
                    .getMaterialData());
      }
    }
    player.getWorld().playSound(player.getLocation(), Sound.ANVIL_LAND, 1, 1);
    player
        .getNearbyEntities(4, 1, 4)
        .forEach(
            e -> {
              VectorUtils.deflect(player.getLocation().toVector(), e, 5);
            });
  }

  @Override
  public DeviceContext deserializeContext(JsonObject json) {
    return DeviceContext.deSerializeDefault(this, json);
  }
}

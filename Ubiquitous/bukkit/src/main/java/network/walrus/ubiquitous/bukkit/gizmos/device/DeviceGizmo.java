package network.walrus.ubiquitous.bukkit.gizmos.device;

import java.time.Duration;
import network.walrus.ubiquitous.bukkit.gizmos.Gizmo;
import network.walrus.utils.core.text.LocalizedFormat;
import network.walrus.utils.core.translation.Localizable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * A gizmo which players can directly use to perform actions. All legacy OCN gizmos fall under this
 * category.
 *
 * @param <C> type of context attached to this device
 * @author Ausin Mayes
 */
public abstract class DeviceGizmo<C extends DeviceContext> extends Gizmo<C> {

  private final int maxUsages;

  /**
   * @param id unique identifier of this gizmo used for storage
   * @param name of the gizmo
   * @param description of the gizmo
   * @param icon used to represent this gizmo
   * @param cost droplet cost of the gizmo
   * @param coolDown between usages of this gizmo
   * @param maxUsages number of usages before the gizmo is removed from the player's purchase list
   */
  public DeviceGizmo(
      String id,
      Localizable name,
      LocalizedFormat[] description,
      ItemStack icon,
      int cost,
      Duration coolDown,
      int maxUsages) {
    super(id, name, description, icon, cost, coolDown);
    this.maxUsages = maxUsages;
  }

  /**
   * Method called whenever a player uses the device. This is called after all prerequisite checks
   * have passed and the player is allowed to use the device.
   *
   * @param player who is using the device
   */
  public abstract void onUse(Player player);

  @Override
  public void activated(Player player, C context) {}

  @Override
  public void deActivated(Player player) {}
}

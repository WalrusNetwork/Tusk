package network.walrus.ubiquitous.bukkit.gizmos;

import com.google.gson.JsonObject;
import java.time.Duration;
import network.walrus.utils.core.text.LocalizedFormat;
import network.walrus.utils.core.translation.Localizable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Anything that can be purchased and used by {@link Player}s.
 *
 * <p>Gizmos are managed by their respective {@link GizmoManager}, which is responsible for handling
 * tasks such as user updates and cooldowns. A single manager instance should be expected to exist
 * for each gizmo type at runtime.
 *
 * <p>Users can, in the default implementation, only have one instance of a certain gizmo type
 * active at once, For example, if there was a "Pet" gizmo type, with different implementations for
 * Cow, Sheep, and Slime, the user could only have one pet type active at once.
 *
 * <p>{@link GizmoContext}s are also attached to gizmos on a per-user level. These contain
 * user-specific gizmo information that is used to define custom gizmo properties for each user.
 * Using the pet example, this would contain data such as a custom name for the pet that the user
 * defined.
 *
 * @param <C> type of context that should be associated with this gizmo
 * @author Austin Mayes
 */
public abstract class Gizmo<C extends GizmoContext> {

  protected final String id;
  protected final Localizable name;
  protected final LocalizedFormat[] description;
  protected final ItemStack iconBase;
  protected final int cost;
  protected final Duration coolDown;

  /**
   * @param id unique identifier of this gizmo used for storage
   * @param name of the gizmo
   * @param description of the gizmo
   * @param icon used to represent this gizmo
   * @param cost droplet cost of the gizmo
   * @param coolDown between usages of this gizmo
   */
  public Gizmo(
      String id,
      Localizable name,
      LocalizedFormat[] description,
      ItemStack icon,
      int cost,
      Duration coolDown) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.iconBase = icon;
    this.cost = cost;
    this.coolDown = coolDown;
  }

  /**
   * Determine if a player (who already owns this gizmo) is allowed to use it at the current time.
   * This is checked after all other pre-use requirements, such as cooldown.
   *
   * @param player to check
   * @param context of the gizmo for the player
   * @return if the player can use the gizmo
   */
  public boolean canUse(Player player, C context) {
    return true;
  }

  /**
   * Callback executed when a player enables this gizmo.
   *
   * @param player who is enabling the gizmo
   * @param context associated with the player
   */
  public abstract void activated(Player player, C context);

  /**
   * Callback executed when a player disables this gizmo.
   *
   * @param player who is disabling the gizmo
   */
  public abstract void deActivated(Player player);

  /**
   * Construct a context from a {@link JsonObject}. This should be the exact opposite of {@link
   * GizmoContext#serialize()}.
   *
   * @param json containing data used to construct the context
   * @return a context constructed from the data
   */
  public abstract C deserializeContext(JsonObject json);

  /** @return the unique identifier of this gizmo used for storage */
  public String id() {
    return id;
  }
}

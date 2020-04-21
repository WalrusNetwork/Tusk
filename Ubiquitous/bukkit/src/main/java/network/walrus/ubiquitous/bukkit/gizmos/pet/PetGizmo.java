package network.walrus.ubiquitous.bukkit.gizmos.pet;

import com.google.gson.JsonObject;
import java.time.Duration;
import java.util.Optional;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_8_R3.EntityInsentient;
import network.walrus.ubiquitous.bukkit.UbiquitousMessages;
import network.walrus.ubiquitous.bukkit.gizmos.Gizmo;
import network.walrus.utils.core.color.NetworkColorConstants.Gizmo.Pet;
import network.walrus.utils.core.text.LocalizedFormat;
import org.apache.commons.lang.WordUtils;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * A {@link LivingEntity} which belongs to a {@link Player} and follows them around.
 *
 * @author Austin Mayes
 * @param <C> type of context that should be associated with this gizmo
 */
public abstract class PetGizmo<C extends PetContext> extends Gizmo<C> {

  /**
   * Constructor which fills in values based on a supplied {@link EntityType}.
   *
   * @param type of the pet entity
   * @param description of the gizmo
   * @param cost droplet cost of the gizmo
   */
  public PetGizmo(EntityType type, LocalizedFormat[] description, int cost) {
    this(
        PetUtils.entityColor(type)
            + WordUtils.capitalize(type.name().toLowerCase().replace("_", " ")),
        description,
        PetUtils.entityEgg(type),
        cost);
  }

  /**
   * @param name of the pet without the "pet" part
   * @param description of the gizmo
   * @param icon used to represent this gizmo
   * @param cost droplet cost of the gizmo
   */
  public PetGizmo(String name, LocalizedFormat[] description, ItemStack icon, int cost) {
    super(
        "pet-" + ChatColor.stripColor(name).toLowerCase().replace(" ", "_"),
        UbiquitousMessages.GIZMO_NAME_PET.with(Pet.NAME, name),
        description,
        icon,
        cost,
        Duration.ZERO);
  }

  protected static Optional<String> getName(JsonObject object) {
    return object.get("has-custom-name").getAsBoolean()
        ? Optional.ofNullable(object.get("custom-name").getAsString())
        : Optional.empty();
  }

  @Override
  public void activated(Player player, C context) {
    LivingEntity entity = spawn(player, context);
    // TODO: Track
  }

  @Override
  public void deActivated(Player player) {
    // TODO: Despawn
  }

  /**
   * Spawn this pet for a specific player.
   *
   * @param player to spawn the pet for
   * @param context containing special player-specific options
   * @return the spawned pet
   */
  public abstract LivingEntity spawn(Player player, C context);

  /**
   * Callback executed every second for every online player who has an active pet.
   *
   * @param player the tick is for
   * @param entity being ticked
   */
  public void tick(Player player, LivingEntity entity) {
    moveTo(player, entity);
  }

  protected void moveTo(Player player, LivingEntity pet) {
    if (player.getLocation().distanceSquared(pet.getLocation()) > 14) {
      pet.teleport(player);
    } else
      // Pathfind to player
      ((EntityInsentient) ((CraftEntity) pet).getHandle())
          .getNavigation()
          .a(((CraftEntity) player).getHandle());
  }
}

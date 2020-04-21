package network.walrus.ubiquitous.bukkit.gizmos.pet.types.player;

import com.google.gson.JsonObject;
import java.util.UUID;
import network.walrus.ubiquitous.bukkit.gizmos.pet.PetGizmo;
import network.walrus.utils.bukkit.item.ItemUtils;
import network.walrus.utils.core.text.LocalizedFormat;
import org.bukkit.Material;
import org.bukkit.Skin;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

/**
 * A baby zombie with a custom player head that follows a player around.
 *
 * @author Austin Mayes
 */
public class PlayerPet extends PetGizmo<PlayerPetContext> {

  private final UUID playerUUID;
  private final Skin skin;

  /**
   * @param name of the pet without the "pet" part
   * @param description of the gizmo
   * @param cost droplet cost of the gizmo
   * @param playerUUID UUID of the player to assign the skull to
   * @param skin to apply to the skull
   */
  public PlayerPet(
      String name, LocalizedFormat[] description, int cost, UUID playerUUID, Skin skin) {
    super(name, description, makeSkull(name, playerUUID, skin), cost);
    this.playerUUID = playerUUID;
    this.skin = skin;
  }

  private static ItemStack makeSkull(String name, UUID owner, Skin skin) {
    ItemStack skull = new ItemStack(Material.SKULL_ITEM, 3);
    SkullMeta meta = (SkullMeta) skull.getItemMeta();
    meta.setOwner(name, owner, skin);
    skull.setItemMeta(meta);
    return skull;
  }

  @Override
  public LivingEntity spawn(Player player, PlayerPetContext context) {
    Zombie zombie = (Zombie) player.getWorld().spawnEntity(player.getLocation(), EntityType.ZOMBIE);
    zombie.setBaby(true);
    context.apply(zombie);
    zombie
        .getEquipment()
        .setHelmet(makeSkull(this.name.render(player).toLegacyText(), this.playerUUID, this.skin));

    return zombie;
  }

  @Override
  public PlayerPetContext deserializeContext(JsonObject json) {
    ItemStack chest = ItemUtils.fromJson(json.get("chest-plate").getAsJsonObject());
    ItemStack legs = ItemUtils.fromJson(json.get("leggings").getAsJsonObject());
    ItemStack boots = ItemUtils.fromJson(json.get("boots").getAsJsonObject());
    return new PlayerPetContext(this, getName(json), chest, legs, boots);
  }
}

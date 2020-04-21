package network.walrus.ubiquitous.bukkit.gizmos.pet.types.player;

import com.google.gson.JsonObject;
import java.util.Optional;
import network.walrus.ubiquitous.bukkit.gizmos.pet.PetContext;
import network.walrus.utils.bukkit.item.ItemUtils;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

public class PlayerPetContext extends PetContext<PlayerPet> {

  private final ItemStack chestPlate;
  private final ItemStack legs;
  private final ItemStack boots;

  public PlayerPetContext(
      PlayerPet gizmo,
      Optional<String> customName,
      ItemStack chestPlate,
      ItemStack legs,
      ItemStack boots) {
    super(gizmo, customName);
    this.chestPlate = chestPlate;
    this.legs = legs;
    this.boots = boots;
  }

  @Override
  public JsonObject serialize() {
    JsonObject object = super.serialize();
    object.add("chest-plate", ItemUtils.toJson(this.chestPlate));
    object.add("leggings", ItemUtils.toJson(this.legs));
    object.add("boots", ItemUtils.toJson(this.boots));
    return object;
  }

  @Override
  public void apply(LivingEntity entity) {
    super.apply(entity);
  }
}

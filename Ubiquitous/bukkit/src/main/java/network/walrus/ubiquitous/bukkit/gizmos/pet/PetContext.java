package network.walrus.ubiquitous.bukkit.gizmos.pet;

import com.google.gson.JsonObject;
import java.util.Optional;
import network.walrus.ubiquitous.bukkit.gizmos.GizmoContext;
import org.bukkit.entity.LivingEntity;

/**
 * Base context class for all {@link PetGizmo}s.
 *
 * @author Austin Mayes
 * @param <P> type of pet this context is for
 */
public class PetContext<P extends PetGizmo> extends GizmoContext<P> {

  private final Optional<String> customName;

  /**
   * @param gizmo that this context is for
   * @param customName that the player applied to the pet
   */
  public PetContext(P gizmo, Optional<String> customName) {
    super(gizmo);
    this.customName = customName;
  }

  @Override
  public JsonObject serialize() {
    JsonObject object = new JsonObject();
    object.addProperty(
        "has-custom-name", this.customName.isPresent() && !this.customName.get().isEmpty());
    object.addProperty("custom-name", this.customName.orElse(null));
    return object;
  }

  public void apply(LivingEntity entity) {
    if (customName.isPresent()) {
      entity.setCustomNameVisible(true);
      entity.setCustomName(customName.get());
    }
  }
}

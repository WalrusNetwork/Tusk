package network.walrus.ubiquitous.bukkit.gizmos.device;

import com.google.gson.JsonObject;
import network.walrus.ubiquitous.bukkit.gizmos.GizmoContext;

/**
 * Base class for all contexts related to {@link DeviceGizmo}s.
 *
 * @param <D> type of device the context is for
 * @author Austin Mayes
 */
public class DeviceContext<D extends DeviceGizmo> extends GizmoContext<D> {

  private final int usages;

  /**
   * @param gizmo that this context is for
   * @param usages number of times the player has used the device
   */
  public DeviceContext(D gizmo, int usages) {
    super(gizmo);
    this.usages = usages;
  }

  /**
   * Default deserialization for contexts that only require usages
   *
   * @param device the context is for
   * @param object containing the data to parse
   * @param <D> type of device the context is for
   * @return a generic {@link DeviceContext} containing only usages
   */
  public static <D extends DeviceGizmo> DeviceContext<D> deSerializeDefault(
      D device, JsonObject object) {
    return new DeviceContext<D>(device, object.get("usages").getAsInt());
  }

  @Override
  public JsonObject serialize() {
    JsonObject object = new JsonObject();
    object.addProperty("usages", this.usages);
    return object;
  }
}

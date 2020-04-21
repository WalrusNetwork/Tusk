package network.walrus.ubiquitous.bukkit.gizmos.device;

import network.walrus.ubiquitous.bukkit.gizmos.GizmoManager;

/**
 * Manager for all {@link DeviceGizmo}s.
 *
 * @author Austin Mayes
 */
public class DeviceManager extends GizmoManager<DeviceGizmo> {

  @Override
  public String baseType() {
    return "device";
  }
}

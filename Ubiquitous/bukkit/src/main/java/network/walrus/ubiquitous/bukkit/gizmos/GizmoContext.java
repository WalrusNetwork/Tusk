package network.walrus.ubiquitous.bukkit.gizmos;

import com.google.gson.JsonObject;

/**
 * User-specific contextual information attached to a {@link Gizmo}.
 *
 * @param <G> type of gizmo this context is for
 * @author Austin Mayes
 */
public abstract class GizmoContext<G extends Gizmo> {

  private final G gizmo;

  /** @param gizmo that this context is for */
  public GizmoContext(G gizmo) {
    this.gizmo = gizmo;
  }

  /**
   * @return a serialized form of this context. This should be the direct opposite of {@link
   *     Gizmo#deserializeContext(JsonObject)}
   */
  public abstract JsonObject serialize();
}

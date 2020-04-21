package network.walrus.ubiquitous.bukkit.gizmos;

/**
 * Object responsible for managing all instances of {@link G} currently equipped by {@link
 * org.bukkit.entity.Player}s on the server.
 *
 * @param <G> type of gizmo this manager handles
 * @author Austin Mayes
 */
public abstract class GizmoManager<G extends Gizmo> {

  /** @return the unique identifier of the base gizmo type this manager is for */
  public abstract String baseType();
}

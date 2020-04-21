package network.walrus.ubiquitous.bukkit.border;

import org.bukkit.Material;

/**
 * A base {@link IWorldBorder} that implements default logic
 *
 * @author David Rodriguez
 */
public abstract class WorldBorder implements IWorldBorder {

  private Material type;
  private boolean passable;

  /**
   * Creates a world border
   *
   * @param type the type to be used when applying the border
   * @param passable if the border can be trespassed
   */
  public WorldBorder(Material type, boolean passable) {
    this.type = type;
    this.passable = passable;
  }

  @Override
  public Material getType() {
    return type;
  }

  @Override
  public boolean isPassable() {
    return passable;
  }

  @Override
  public void setPassable(boolean passable) {
    this.passable = passable;
  }
}

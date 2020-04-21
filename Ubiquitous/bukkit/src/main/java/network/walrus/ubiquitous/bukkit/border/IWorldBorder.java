package network.walrus.ubiquitous.bukkit.border;

import java.util.List;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

/**
 * Interface that represents a world border that can be applied to a world
 *
 * @author David Rodriguez
 */
public interface IWorldBorder {

  /**
   * Draws the world border to a certain world
   *
   * @param world to draw border
   */
  default void apply(World world) {
    MaterialData materialData = new MaterialData(getType());
    for (Vector vector : getCachedVectors()) {
      world.fastBlockChange(vector, materialData);
    }
  }

  /**
   * Erases the border if it was previously applied
   *
   * @param world that the border was applied on
   */
  default void erase(World world) {
    MaterialData materialData = new MaterialData(Material.AIR);
    for (Vector vector : getCachedVectors()) {
      world.fastBlockChange(vector, materialData);
    }
  }

  /**
   * Expands the world border by the amount of blocks specified in the {@link Vector}
   *
   * <p>If the amount of blocks is negative, then it will shrink the border
   *
   * @param vector containing amount of blocks
   */
  void expand(Vector vector);

  /**
   * Expands the world border by the amount of blocks specified
   *
   * @param blocks amount of blocks to expand
   */
  default void expand(double blocks) {
    expand(new Vector(blocks, blocks, blocks));
  }

  /** Resets the world border to its original position */
  void reset();

  /**
   * Checks if the border contains a {@link Vector}
   *
   * @param vector to be checked
   * @return If the vector is inside the border
   */
  boolean contains(Vector vector);

  /** @return {@link Material} used at the bounds of the vector */
  Material getType();

  /** @return List of {@link Vector}s that contains the current modified blocks */
  List<Vector> getCachedVectors();

  /** @return If the border can be trespassed by players */
  boolean isPassable();

  /**
   * Sets whether the border can be trespassed by players
   *
   * @param passable if the border can be trespassed by players
   */
  void setPassable(boolean passable);
}

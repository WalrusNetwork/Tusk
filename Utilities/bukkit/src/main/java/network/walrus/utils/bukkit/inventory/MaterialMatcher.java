package network.walrus.utils.bukkit.inventory;

import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.material.MaterialData;

/**
 * A class which is used to thoroughly check the equality of Minecraft materials.
 *
 * @author Avicus Network
 */
public interface MaterialMatcher {

  /**
   * Test is a material and data matches another.
   *
   * @param material to check equality for
   * @param data to check equality for
   * @return if the material matches
   */
  boolean matches(Material material, byte data);

  /**
   * Test if {@link MaterialData} matches the material for this matcher.
   *
   * @param materialData to check equality for
   * @return if the material matches
   */
  default boolean matches(MaterialData materialData) {
    return matches(materialData.getItemType(), materialData.getData());
  }

  /**
   * Test if a {@link BlockState}'s material and data matches the material for this matcher.
   *
   * @param state to check equality for
   * @return if the material of the state matches
   */
  default boolean matches(BlockState state) {
    return matches(state.getData());
  }
}

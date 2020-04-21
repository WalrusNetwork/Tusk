package network.walrus.utils.bukkit.inventory;

import java.util.Optional;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.material.MaterialData;

/**
 * A simple implementation of a {@link MaterialMatcher} which stores one material to match against.
 *
 * @author Avicus Network
 */
public class SingleMaterialMatcher implements MaterialMatcher {

  final Material material;
  final Optional<Byte> data;

  /**
   * Constructor that will match all data for the supplied material.
   *
   * @param material to match for
   */
  public SingleMaterialMatcher(Material material) {
    this(material, Optional.empty());
  }

  /**
   * Constructor.
   *
   * @param material to match for
   * @param data to match for
   */
  public SingleMaterialMatcher(Material material, byte data) {
    this(material, Optional.of(data));
  }

  /**
   * Constructor.
   *
   * @param state to get material data from
   */
  public SingleMaterialMatcher(BlockState state) {
    this(state.getType(), state.getRawData());
  }

  /**
   * Constructor.
   *
   * @param material to match for
   * @param data to match for, or empty to match all data
   */
  public SingleMaterialMatcher(Material material, Optional<Byte> data) {
    this.material = material;
    this.data = data;
  }

  /** Create a {@link MaterialData} using the data from this matcher. */
  public MaterialData toMaterialData() {
    return new MaterialData(this.material, data().orElse((byte) 0));
  }

  public boolean matches(Material material, byte data) {
    if (this.material != material) {
      return false;
    } else {
      return !this.data.isPresent() || this.data.get() == data;
    }
  }

  public boolean isDataPresent() {
    return data().isPresent();
  }

  /** @return the material that this matcher is using for equality checks */
  public Material material() {
    return material;
  }

  /** @return the data that this matcher is using for equality checks */
  public Optional<Byte> data() {
    return data;
  }

  @Override
  public String toString() {
    return "SingleMaterialMatcher{" + "material=" + material + ", data=" + data + '}';
  }
}

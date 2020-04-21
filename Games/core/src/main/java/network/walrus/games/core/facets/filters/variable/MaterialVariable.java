package network.walrus.games.core.facets.filters.variable;

import network.walrus.games.core.facets.filters.Variable;
import org.bukkit.material.MaterialData;

/**
 * The material variable is similar to the item variable but only includes the material of the item
 * (or block) being checked against.
 *
 * @author Avicus Network
 */
public class MaterialVariable implements Variable {

  private final MaterialData data;

  /**
   * Constructor.
   *
   * @param data of the material contained in the variable
   */
  public MaterialVariable(MaterialData data) {
    this.data = data;
  }

  public MaterialData getData() {
    return data;
  }
}

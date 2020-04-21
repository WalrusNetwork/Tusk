package network.walrus.games.octc.hills;

import java.util.List;
import network.walrus.utils.parsing.facet.Facet;

/**
 * Provides common functionality between the hill facets Also used for common facet listeners
 *
 * @author Matthew Arnold
 */
public abstract class HillFacet extends Facet {

  private final List<HillObjective> hills;

  /**
   * Creates a new abstract hill facet from a list of hills
   *
   * @param hills the list of hills
   */
  public HillFacet(List<HillObjective> hills) {
    this.hills = hills;
  }

  /**
   * Gets the list of hills
   *
   * @return the list of hills
   */
  public List<HillObjective> hills() {
    return hills;
  }
}

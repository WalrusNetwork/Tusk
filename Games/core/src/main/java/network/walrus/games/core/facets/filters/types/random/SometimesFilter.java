package network.walrus.games.core.facets.filters.types.random;

/**
 * A sometimes filter is a random filter with a randomness of 50%.
 *
 * @author Avicus Network
 */
public class SometimesFilter extends RandomFilter {

  /** @see RandomFilter#RandomFilter(double) */
  public SometimesFilter() {
    super(0.5);
  }
}

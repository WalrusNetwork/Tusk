package network.walrus.games.core.facets.applicators;

import java.util.List;
import network.walrus.utils.parsing.facet.Facet;

/**
 * Facet which holds all parsed {@link Applicator}s.
 *
 * @author Austin Mayes
 */
public class ApplicatorsFacet extends Facet {

  private final List<Applicator> applicators;

  /** @param applicators which have been parsed */
  public ApplicatorsFacet(List<Applicator> applicators) {
    this.applicators = applicators;
  }

  public List<Applicator> getApplicators() {
    return applicators;
  }
}

package network.walrus.games.octc.hills.koth;

import java.util.HashMap;
import java.util.Map;
import network.walrus.games.core.facets.group.Competitor;
import network.walrus.games.core.util.GameTask;
import network.walrus.games.octc.hills.HillObjective;
import network.walrus.games.octc.score.ScoreFacet;

/**
 * Special game task designed to update the points for the hills
 *
 * @author Matthew Arnold
 */
public class HillScoreTask extends GameTask {

  private final KothFacet kothFacet;
  private final ScoreFacet scoreFacet;

  /**
   * Creates a new hill score task
   *
   * @param kothFacet the koth facet
   * @param scoreFacet the scoer facet
   */
  public HillScoreTask(KothFacet kothFacet, ScoreFacet scoreFacet) {
    super("hill score task");
    this.kothFacet = kothFacet;
    this.scoreFacet = scoreFacet;
  }

  @Override
  public void run() {
    for (Map.Entry<Competitor, Integer> entry : hillPoints().entrySet()) {
      // called 10 times a second, hence dividing by 10
      double points = entry.getValue() / 10.0;
      scoreFacet.getObjective().modify(entry.getKey(), points);
    }
  }

  private Map<Competitor, Integer> hillPoints() {
    Map<Competitor, Integer> map = new HashMap<>();
    for (HillObjective hill : kothFacet.hills()) {
      if (!hill.owner().isPresent()) {
        // hill must have an owner to give points
        continue;
      }

      // merge the points into a map of competitor -> total points for this time period
      map.merge(hill.owner().get(), hill.options().points, Integer::sum);
    }
    return map;
  }
}

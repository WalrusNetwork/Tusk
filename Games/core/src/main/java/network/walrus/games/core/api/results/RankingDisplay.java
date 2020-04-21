package network.walrus.games.core.api.results;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import network.walrus.games.core.facets.group.Competitor;
import network.walrus.utils.core.text.LocalizableFormat;
import network.walrus.utils.core.text.LocalizedNumber;
import network.walrus.utils.core.text.UnlocalizedFormat;
import network.walrus.utils.core.text.UnlocalizedText;
import network.walrus.utils.core.translation.FormatUtils;
import network.walrus.utils.core.translation.Localizable;

/**
 * Helper encapsulation class used to render rankings for games which have multiple winners.
 *
 * @author Austin Mayes
 */
public class RankingDisplay {

  private final int toResolve;
  private TreeMap<Integer, HashSet<Competitor>> ranking;

  /**
   * Constructor
   *
   * @param toResolve Number of places to resolve to.
   * @param ranking Map of ranked competitors in DESCENDING order.
   */
  public RankingDisplay(int toResolve, TreeMap<Integer, HashSet<Competitor>> ranking) {
    this.toResolve = toResolve;

    this.ranking = normalizeRanking(ranking);
  }

  private TreeMap<Integer, HashSet<Competitor>> normalizeRanking(
      TreeMap<Integer, HashSet<Competitor>> original) {
    // Fix for callers providing raw integer values based on other stats than true rank position.
    TreeMap<Integer, HashSet<Competitor>> normalizedRanking = new TreeMap<>();
    int rank = 0;
    for (Map.Entry<Integer, HashSet<Competitor>> entry : original.entrySet()) {
      rank++;
      normalizedRanking.put(rank, entry.getValue());
    }
    return normalizedRanking;
  }

  private List<Localizable> getRankDisplay(boolean showAll) {
    List<Localizable> res = new ArrayList<>();
    for (Map.Entry<Integer, HashSet<Competitor>> entry : ranking.entrySet()) {
      if (toResolve < entry.getKey() && !showAll) {
        break;
      }

      if (entry.getValue().isEmpty()) {
        continue;
      }

      LocalizableFormat format = FormatUtils.humanList(entry.getValue().size());

      List<Localizable> args = new ArrayList<>();
      for (Competitor competitor : entry.getValue()) {
        Localizable coloredName = competitor.getColoredName();
        args.add(coloredName);
      }

      LocalizableFormat placeFormat = new UnlocalizedFormat("{0}. ");

      res.add(
          new UnlocalizedText(
              "{0} {1}",
              placeFormat.with(new LocalizedNumber(ranking.headMap(entry.getKey()).size() + 1)),
              format.with((Localizable[]) args.toArray(new Localizable[0]))));
    }
    return res;
  }

  List<Localizable> getRankDisplay() {
    return getRankDisplay(false);
  }

  /**
   * Re-calculate all rankings with new data.
   *
   * @param ranking to update this class with
   */
  public void updateRankings(TreeMap<Integer, HashSet<Competitor>> ranking) {
    this.ranking = normalizeRanking(ranking);
  }

  TreeMap<Integer, HashSet<Competitor>> getRanking() {
    return ranking;
  }
}

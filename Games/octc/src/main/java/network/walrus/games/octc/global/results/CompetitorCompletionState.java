package network.walrus.games.octc.global.results;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;
import javax.annotation.Nonnull;
import network.walrus.games.core.facets.group.Competitor;
import network.walrus.games.core.facets.objectives.Objective;
import network.walrus.games.core.facets.objectives.StagnatedCompletionObjective;
import network.walrus.games.core.facets.objectives.locatable.LocatableObjective;
import network.walrus.games.core.facets.objectives.touchable.TouchableObjective;
import network.walrus.games.core.round.GameRound;

/**
 * A comprehensive data class representing how much a competitor has done in a specific {@link
 * GameRound} for a set of {@link Objective}s.
 *
 * @author Austin Mayes
 */
public class CompetitorCompletionState implements Comparable<CompetitorCompletionState> {

  private final Competitor competitor;
  private final int needed;
  private final List<Objective> neededObjectives;
  private final int completed;
  private final int touched;
  private final ImmutableList<Double> progress;
  private final ImmutableList<Integer> completionDistance;
  private final ImmutableList<Integer> touchDistance;

  /**
   * @param objectives that should be tracked
   * @param competitor that this data is for
   */
  public CompetitorCompletionState(List<Objective> objectives, Competitor competitor) {
    this.competitor = competitor;

    int completed = 0;
    int touched = 0;
    List<Double> progress = new ArrayList<>();
    List<Integer> completionDistance = new ArrayList<>();
    List<Integer> touchDistance = new ArrayList<>();

    this.neededObjectives = new ArrayList<>();

    for (Objective objective : objectives) {
      if (!objective.canComplete(competitor)) {
        continue;
      }

      this.neededObjectives.add(objective);

      if (objective.isCompleted(competitor)) {
        completed++;
      } else if (objective instanceof StagnatedCompletionObjective
          && ((StagnatedCompletionObjective) objective).isCompleted()
          && ((StagnatedCompletionObjective) objective).getHighestCompleter().isPresent()
          && ((StagnatedCompletionObjective) objective)
              .getHighestCompleter()
              .get()
              .equals(competitor)) {
        completed++;
      } else {
        TouchableObjective touchable =
            objective instanceof TouchableObjective ? (TouchableObjective) objective : null;
        if (objective instanceof LocatableObjective
            && false) { // TODO: Only matters during competitive
          LocatableObjective locatable = (LocatableObjective) objective;
          if (locatable.isDistanceRelevant(competitor)) {
            touchDistance.add(locatable.getDistance(competitor));
          }
        }

        if (objective.isIncremental()) {
          progress.add(objective.getCompletion(competitor));
        } else if (touchable != null && touchable.hasTouched(competitor)) {
          touched++;
          // A touched objective is worth 50% completion
          progress.add(0.5);
        }
      }
    }

    Collections.sort(progress, Collections.reverseOrder());
    Collections.sort(completionDistance);
    Collections.sort(touchDistance);

    this.needed = neededObjectives.size();
    this.completed = completed;
    this.touched = touched;
    this.progress = ImmutableList.copyOf(progress);
    this.completionDistance = ImmutableList.copyOf(completionDistance);
    this.touchDistance = ImmutableList.copyOf(touchDistance);
  }

  private static int compareDoubles(List<Double> a, List<Double> b) {
    int count = Math.max(a.size(), b.size());
    double aHigh = 0, bHigh = 0;

    for (int i = 0; i < count; i++) {
      aHigh = i < a.size() ? a.get(i) : 0;
      bHigh = i < b.size() ? b.get(i) : 0;
      if (aHigh != bHigh) {
        break; // Find first differing double
      }
    }

    return Double.compare(bHigh, aHigh);
  }

  private static int compareIntegers(List<Integer> a, List<Integer> b) {
    int count = Math.min(a.size(), b.size());
    int aHigh = Integer.MAX_VALUE, bHigh = Integer.MAX_VALUE;

    for (int i = 0; i < count; i++) {
      aHigh = a.get(i);
      bHigh = b.get(i);
      if (aHigh != bHigh) {
        break;
      }
    }

    return Integer.compare(aHigh, bHigh);
  }

  /**
   * Create a map of competitor completions to lists of states that have that number of completions,
   * and then sort the map.
   *
   * @param list to sort
   * @return a sorted map of competitor states by completion
   */
  public static TreeMap<Integer, List<CompetitorCompletionState>> getRankedCompletions(
      List<CompetitorCompletionState> list) {
    TreeMap<Integer, List<CompetitorCompletionState>> set =
        Maps.newTreeMap(Collections.reverseOrder());
    for (CompetitorCompletionState state : list) {
      set.putIfAbsent(state.getCompleted(), Lists.newArrayList());
      set.get(state.getCompleted()).add(state);
    }
    for (List<CompetitorCompletionState> l : set.values()) {
      l.sort(CompetitorCompletionState::compareTo);
    }
    return set;
  }

  /** @return if this competitor should win the game */
  boolean shouldWin() {
    return completed >= needed;
  }

  /** @return if the competitor has done anything to attempt to complete an objective */
  public boolean hasDoneAnything() {
    return completed > 0 || touched > 0 || (!progress.isEmpty() && progress.get(0) > 0);
  }

  @Override
  public int compareTo(@Nonnull CompetitorCompletionState that) {
    if (this.completed > that.completed) {
      return -1;
    }
    if (this.completed < that.completed) {
      return 1;
    }

    if (this.touched > that.touched) {
      return -1;
    }
    if (this.touched < that.touched) {
      return 1;
    }

    int compareProgress = compareDoubles(this.progress, that.progress);
    if (compareProgress != 0) {
      return compareProgress;
    }

    int compareCompletionDistance =
        compareIntegers(this.completionDistance, that.completionDistance);
    if (compareCompletionDistance != 0) {
      return compareCompletionDistance;
    }

    int compareTouchDistance = compareIntegers(this.touchDistance, that.touchDistance);
    if (compareTouchDistance != 0) {
      return compareTouchDistance;
    }

    // Both teams are equal in every measurable respect
    return 0;
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof CompetitorCompletionState
        && this.competitor.equals(((CompetitorCompletionState) obj).competitor);
  }

  public Competitor getCompetitor() {
    return competitor;
  }

  private int getCompleted() {
    return completed;
  }

  List<Objective> getNeededObjectives() {
    return neededObjectives;
  }
}

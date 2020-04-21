package network.walrus.games.core.facets.filters.types;

import java.time.Duration;
import network.walrus.games.core.facets.filters.Filter;
import network.walrus.games.core.facets.filters.FilterResult;
import network.walrus.games.core.facets.filters.context.FilterContext;
import network.walrus.games.core.round.GameRound;
import network.walrus.utils.core.math.NumberComparator;

/**
 * A time filter checks the round time in comparison with the supplied value.
 *
 * @author Austin Mayes
 */
public class TimeFilter implements Filter {

  private final GameRound round;
  private final Duration value;
  private final NumberComparator comparator;

  /**
   * @param round which holds this filter
   * @param value that the time is being compared against
   * @param comparator used to compare the time
   */
  public TimeFilter(GameRound round, Duration value, NumberComparator comparator) {
    this.round = round;
    this.value = value;
    this.comparator = comparator;
  }

  @Override
  public FilterResult test(FilterContext context, boolean describe) {
    long time = round.getPlayingDuration().getSeconds();
    long compare = this.value.getSeconds();

    return FilterResult.valueOf(this.comparator.perform(time, compare));
  }

  public Duration getValue() {
    return value;
  }

  public NumberComparator getComparator() {
    return comparator;
  }

  @Override
  public String describe() {
    return "time is " + comparator.toString() + " " + value.getSeconds() + " seconds";
  }
}

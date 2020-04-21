package network.walrus.games.core.facets.filters;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import network.walrus.games.core.GamesPlugin;
import network.walrus.games.core.facets.filters.context.FilterContext;

/**
 * A filter is an object that will dynamically return a {@link FilterResult} based on {@link
 * Variable}s from the supplied {@link FilterContext}.
 *
 * @author Avicus Network
 */
public interface Filter {

  /**
   * Execute a batch of checks and get a map of how many checks returned each results.
   *
   * @param list list of checks to be executed
   * @param context context to be checked against
   * @return a map of {@link FilterResult}s with the number of checks that returned this result
   */
  static Map<FilterResult, Integer> test(
      List<Filter> list, FilterContext context, boolean describe) {
    Map<FilterResult, Integer> results = new HashMap<>(list.size());
    results.put(FilterResult.ALLOW, 0);
    results.put(FilterResult.DENY, 0);
    results.put(FilterResult.IGNORE, 0);

    for (Filter filter : list) {
      FilterResult result = filter.test(context, describe);
      if (describe) {
        GamesPlugin.instance.mapLogger().info(filter.describe() + " - " + result.name());
      }
      results.put(result, results.get(result) + 1);
    }

    return results;
  }

  /**
   * Checks if the context contains data that will allow the filter to pass.
   *
   * @param context context to be checked against
   * @param describe if information should be send to the map dev logger describing what the filter
   *     did during the test
   * @return if the test passes
   */
  FilterResult test(FilterContext context, boolean describe);

  /** Test with out describing what happened. */
  default FilterResult test(FilterContext context) {
    return test(context, false);
  }

  /** @return a debug message describing what this filter checks for */
  String describe();
}

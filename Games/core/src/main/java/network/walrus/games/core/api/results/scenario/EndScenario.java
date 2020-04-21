package network.walrus.games.core.api.results.scenario;

import java.util.Optional;
import network.walrus.games.core.api.results.RoundEndCountdown;
import network.walrus.games.core.facets.filters.Filter;
import network.walrus.games.core.facets.filters.context.FilterContext;
import network.walrus.games.core.facets.filters.modifiers.All;
import network.walrus.games.core.facets.filters.modifiers.Allow;
import network.walrus.games.core.facets.filters.modifiers.Any;
import network.walrus.games.core.facets.filters.modifiers.Deny;
import network.walrus.games.core.facets.filters.modifiers.Not;
import network.walrus.games.core.facets.filters.types.TimeFilter;
import network.walrus.games.core.round.GameRound;
import network.walrus.utils.core.math.NumberComparator;

/**
 * An action that is executed in order to end a {@link GameRound} when a {@link Filter} passes.
 *
 * @author Austin Mayes
 */
public abstract class EndScenario {

  private final GameRound round;
  private final Filter filter;
  private final int places;
  private Optional<RoundEndCountdown> countdown = Optional.empty();

  /**
   * @param round that the scenario is executing in
   * @param filter that should be executed to see if this scenario should execute
   * @param places to show winners by
   */
  public EndScenario(GameRound round, Filter filter, int places) {
    this.round = round;
    this.filter = filter;
    this.places = places;

    validateTimeFilter(filter);
  }

  private void validateTimeFilter(Filter holder) {
    if (holder instanceof All) {
      for (Filter c : ((All) holder).getChildren()) {
        validateTimeFilter(c);
      }
    }

    if (holder instanceof Any) {
      for (Filter c : ((Any) holder).getChildren()) {
        validateTimeFilter(c);
      }
    }

    if (holder instanceof Allow) {
      validateTimeFilter(((Allow) holder).getChild());
    }

    if (holder instanceof Deny) {
      validateTimeFilter(((Deny) holder).getChild());
    }

    if (holder instanceof Not) {
      validateTimeFilter(((Not) holder).getChild());
    }

    if (holder instanceof TimeFilter) {
      if (!((TimeFilter) holder).getComparator().equals(NumberComparator.EQUALS)) {
        throw new RuntimeException(
            "Time filters can only use equals comparators in end scenarios.");
      } else {
        this.countdown =
            Optional.of(
                new RoundEndCountdown(
                    ((TimeFilter) holder).getValue().minus(round.getPlayingDuration()),
                    this,
                    getRound()));
      }
    }
  }

  public GameRound getRound() {
    return round;
  }

  protected int getPlaces() {
    return places;
  }

  /** Execute this scenario. */
  public abstract void execute();

  /** @return if this scenario should be executed */
  public boolean test() {
    FilterContext context = new FilterContext();
    return this.filter.test(context).passes();
  }

  public Optional<RoundEndCountdown> getCountdown() {
    return countdown;
  }
}

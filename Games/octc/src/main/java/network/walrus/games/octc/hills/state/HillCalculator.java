package network.walrus.games.octc.hills.state;

import java.time.Duration;
import java.util.Objects;
import java.util.Optional;
import network.walrus.games.core.facets.filters.context.FilterContext;
import network.walrus.games.core.facets.filters.variable.GroupVariable;
import network.walrus.games.core.facets.group.Competitor;
import network.walrus.games.octc.hills.HillProperties;
import network.walrus.utils.core.util.TimeUtils;

/**
 * Contains the logic for a specific hill, calculates the current state of a specific hill
 *
 * @author Matthew Arnold
 * @author Overcast Network (adapted from their hill objective)
 */
public class HillCalculator {

  private final HillProperties options;
  private Optional<Competitor> owner;
  private Optional<Competitor> competition;
  private Duration progress = Duration.ZERO;

  /**
   * Creates a new hill calculator with a specific set of hill properties
   *
   * @param options the options that determine how the hill shall function
   */
  public HillCalculator(HillProperties options) {
    this.options = options;
    this.owner = options.initialOwner;
    this.competition = Optional.empty();
  }

  /**
   * Does a cycle of domination on the hill
   *
   * @param dominator the current dominator of the hill
   * @param duration how long the dominator has been dominating for since the last cycle of
   *     domination
   * @return the state of the hill after this cycle of overtime has been completed
   */
  public ControlState dominate(Optional<Competitor> dominator, Duration duration) {
    if (owner.isPresent() && options.neutralState) {
      // Point is owned and has a neutral state
      if (dominator.equals(owner)) {
        // Owner is recovering the point
        recover(duration, dominator.get()); // get is safe, as dominator == owner (which is safe)
      } else if (dominator.isPresent()) {
        // Non-owner is uncapturing the point
        uncapture(duration, dominator.get());
      } else {
        // Point is decaying towards the owner
        decay(duration);
      }
    } else if (competition.isPresent()) {
      // Point is partly captured by someone
      if (Objects.equals(dominator, competition)) {
        // Capturer is making progress
        capture(duration);
      } else if (dominator.isPresent()) {
        // Non-capturer is reversing progress
        recover(duration, dominator.get());
      } else {
        // Point is decaying towards owner or neutral
        decay(duration);
      }
    } else if (dominator.isPresent()
        && !Objects.equals(dominator, owner)
        && canCapture(dominator.get())) {
      // Point is not being captured and there is a dominant team that is not the owner, so they
      // start capturing
      competition = dominator;
      dominate(dominator, duration);
    }

    return new ControlState(
        dominator,
        owner,
        (int)
            (((double) 100
                * (double) progress.toMillis()
                / (double) options.captureDuration.toMillis())),
        competition);
  }

  private void uncapture(Duration duration, Competitor dominator) {
    if (!canCapture(dominator)) {
      return;
    }

    duration = addCaptureTime(duration);
    if (duration != null) {
      // If uncapture is complete, recurse with the dominant team's remaining time
      owner = Optional.empty();
      dominate(Optional.of(dominator), duration);
    }
  }

  /** Point is either owned or neutral, and someone is pushing it towards themselves */
  private void capture(Duration duration) {
    if (competition.isPresent() && !canCapture(competition.get())) {
      return;
    }

    duration = addCaptureTime(duration);
    if (duration != null) {
      if (options.neutralState && owner.isPresent()) {
        owner = Optional.empty();
      } else {
        owner = competition;
      }
      competition = Optional.empty();
    }
  }

  /** Point is being pulled back towards its current state */
  private void recover(Duration duration, Competitor dominator) {
    if (!canCapture(dominator)) {
      return;
    }

    duration = TimeUtils.multiply(duration, options.recoveryRate);
    duration = subtractCaptureTime(duration);
    if (duration != null) {
      competition = Optional.empty();
      if (!Objects.equals(Optional.of(dominator), owner)) {
        // If the dominant team is not the controller, recurse with the remaining time
        dominate(Optional.of(dominator), TimeUtils.multiply(duration, 1D / options.recoveryRate));
      }
    }
  }

  /** Point is decaying back towards its current state */
  private void decay(Duration duration) {
    duration = TimeUtils.multiply(duration, options.decayRate);
    duration = subtractCaptureTime(duration);
    if (duration != null) {
      competition = Optional.empty();
    }
  }

  private Duration addCaptureTime(final Duration duration) {
    progress = progress.plus(duration);
    if (progress.compareTo(options.captureDuration) < 0) {
      return null;
    } else {
      final Duration remainder = progress.minus(options.captureDuration);
      progress = Duration.ZERO;
      return remainder;
    }
  }

  private Duration subtractCaptureTime(final Duration duration) {
    if (progress.compareTo(duration) > 0) {
      progress = progress.minus(duration);
      return null;
    } else {
      final Duration remainder = duration.minus(progress);
      progress = Duration.ZERO;
      return remainder;
    }
  }

  // checks to see if a certain competitor can capture the hill
  private boolean canCapture(Competitor dom) {
    if (!options.captureFilter.isPresent()) {
      return true;
    }

    FilterContext filterContext = FilterContext.of(new GroupVariable(dom.getGroup()));
    return options.captureFilter.get().test(filterContext).passes();
  }
}

package network.walrus.games.octc.hills.domination.overtime;

import com.google.common.base.Objects;
import java.time.Duration;
import java.util.Optional;
import network.walrus.games.core.facets.group.Competitor;

/**
 * The state of the "domination timer" at any moment in a domination game, immutable object that
 * keeps track of the state and logic of updating the state
 *
 * @author Matthew Arnold
 */
public class DomOvertimeState {

  // the starting time
  public static final DomOvertimeState STARTING_TIME =
      new DomOvertimeState(Optional.empty(), Optional.empty(), Duration.ZERO);

  public final Optional<Competitor> owner;
  public final Optional<Competitor> dominating;

  public final Duration duration;

  /**
   * Creates a new domination overtime state
   *
   * @param owner the current owner of the state (the team who has a higher domination timer)
   * @param dominating the current team dominating the hills (the domination timer is increasing for
   *     them)
   * @param duration the time the current owner has dominated for
   */
  public DomOvertimeState(
      Optional<Competitor> owner, Optional<Competitor> dominating, Duration duration) {
    this.owner = owner;
    this.dominating = dominating;
    this.duration = duration;
  }

  /**
   * Ticks this, calculates a new state and returns the new state
   *
   * @param duration the duration since this was last ticked
   * @return the new domination overtime state
   */
  public DomOvertimeState tick(Duration duration) {
    if (!dominating.isPresent()) {
      return this;
    }
    Duration newDur;
    if (!Objects.equal(owner, dominating)) {
      Optional<Competitor> newOwner;
      if (this.duration.compareTo(duration) <= 0) {
        // would go negative
        newDur = duration.minus(this.duration);
        if (newDur.isZero()) {
          newOwner = Optional.empty();
        } else {
          newOwner = dominating;
        }
        return new DomOvertimeState(newOwner, dominating, newDur);
      } else {
        return new DomOvertimeState(owner, dominating, this.duration.minus(duration));
      }
    }
    return new DomOvertimeState(owner, dominating, this.duration.plus(duration));
  }

  /**
   * Sets the dominator to a new team
   *
   * @param dominator the new dominator
   * @return the new state
   */
  public DomOvertimeState setDominator(Optional<Competitor> dominator) {
    return new DomOvertimeState(owner, dominator, duration);
  }
}

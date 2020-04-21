package network.walrus.games.octc.hills.state;

import java.util.Optional;
import network.walrus.games.core.facets.group.Competitor;

/**
 * The state a hill can be in at any particular time
 *
 * @author Matthew Arnold
 */
public class ControlState {

  private final Optional<Competitor> dominator;
  private final Optional<Competitor> owner;
  private final Optional<Competitor> highestCompetition;

  private final int completionPercentage;

  /**
   * Creates a new Control State, which is the state a hill can be at any instant in time
   *
   * @param dominator the current dominator of the hill
   * @param owner the current owner of the hill
   * @param completionPercentage the current completion percentage of the hill
   * @param highestCompetition the current highest competitor of the hill
   */
  public ControlState(
      Optional<Competitor> dominator,
      Optional<Competitor> owner,
      int completionPercentage,
      Optional<Competitor> highestCompetition) {
    this.dominator = dominator;
    this.owner = owner;
    this.completionPercentage = completionPercentage;
    this.highestCompetition = highestCompetition;
  }

  /**
   * Creates a new control state, with the domination set to a completely neutral setting
   *
   * @return the neutral control state
   */
  public static ControlState emptyState() {
    return new ControlState(Optional.empty(), Optional.empty(), 0, Optional.empty());
  }

  public Optional<Competitor> dominator() {
    return dominator;
  }

  public Optional<Competitor> owner() {
    return owner;
  }

  public Optional<Competitor> highestCompetition() {
    return highestCompetition;
  }

  public int completionPercentage() {
    return completionPercentage;
  }
}

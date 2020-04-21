package network.walrus.games.octc.score;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Optional;
import network.walrus.games.core.facets.group.Competitor;
import network.walrus.games.core.facets.objectives.IntegerObjective;
import network.walrus.games.core.round.GameRound;
import network.walrus.games.core.util.EventUtil;
import network.walrus.games.octc.OCNMessages;
import network.walrus.games.octc.score.event.PointChangeEvent;
import network.walrus.utils.bukkit.translation.LocalizedConfigurationProperty;
import network.walrus.utils.core.math.NumberAction;
import org.bukkit.entity.Player;

/**
 * An objective which rewards points and deducts points for kills and measures completion by how
 * many points have been earned or, if there is a limit, how close to the limit the points earned
 * is.
 *
 * @author Austin Mayes
 */
public class ScoreObjective implements IntegerObjective {

  private final GameRound round;
  private final Map<Competitor, Double> points;
  private final Optional<Integer> kills;
  private final Optional<Integer> deaths;
  private Optional<Integer> limit;

  /**
   * @param round this objective is operating in
   * @param limit that must be reached in order for this objective to be considered complete
   * @param kills points per kill
   * @param deaths points removed per death
   */
  public ScoreObjective(
      GameRound round, Optional<Integer> limit, Optional<Integer> kills, Optional<Integer> deaths) {
    this.round = round;
    this.limit = limit;
    this.kills = kills;
    this.deaths = deaths;
    this.points = Maps.newHashMap();
  }

  @Override
  public int getPoints(Competitor competitor) {
    return this.points.getOrDefault(competitor, 0.0).intValue();
  }

  /**
   * Gets the points as a double
   *
   * @param competitor the competitor
   * @return the real points
   */
  public double getRealPoints(Competitor competitor) {
    return this.points.getOrDefault(competitor, 0.0);
  }

  /** @see #modify(Competitor, int, NumberAction, Optional) */
  public void modify(Competitor competitor, int amount) {
    modify(competitor, amount, Optional.empty());
  }

  /** @see #modify(Competitor, int, NumberAction, Optional) */
  public void modify(Competitor competitor, int amount, Optional<Player> actor) {
    modify(competitor, amount, NumberAction.ADD, actor);
  }

  /** @see #modify(Competitor, double, NumberAction, Optional) */
  public void modify(Competitor competitor, double amount) {
    modify(competitor, amount, Optional.empty());
  }

  /** @see #modify(Competitor, double, NumberAction, Optional) */
  public void modify(Competitor competitor, double amount, Optional<Player> actor) {
    modify(competitor, amount, NumberAction.ADD, actor);
  }

  /**
   * Modify the number of points a competitor has.
   *
   * <p>The actor field is not required, but is used for UI and player-specific statics tracking.
   *
   * @param competitor to modify points for
   * @param amount to change points by
   * @param numberAction used to act on the current points the competitor has
   * @param actor who caused the points to change
   */
  public void modify(
      Competitor competitor, double amount, NumberAction numberAction, Optional<Player> actor) {
    if (!canComplete(competitor)) {
      throw new IllegalArgumentException();
    }

    double oldPoints = getRealPoints(competitor);
    amount = numberAction.perform(oldPoints, amount);
    this.points.put(competitor, amount);

    if ((int) oldPoints != (int) amount) {
      PointChangeEvent event = new PointChangeEvent(this, actor, competitor, (int) amount);
      EventUtil.call(event);
    }
  }

  /**
   * Gets the team in the lead, if no team is in the lead (tie) an empty optional is returned
   *
   * @return the team in the lead or an empty optional
   */
  public Optional<Competitor> leader() {
    int highestPoints = -1;
    Optional<Competitor> leader = Optional.empty();

    // greedy algo to find the team with the highest points
    for (Map.Entry<Competitor, Double> entry : points.entrySet()) {
      int points = entry.getValue().intValue();
      if (points == highestPoints) {
        // if points are the same to highest it's a tie, so no one is the leader (therefore
        // competitor is empty)
        leader = Optional.empty();
      } else if (points > highestPoints) {
        // points are greater, therefore this team should be the new leader
        highestPoints = points;
        leader = Optional.of(entry.getKey());
      }
    }
    return leader;
  }

  /**
   * Returns the highest score by anyone in this objective
   *
   * @return the highest score or 0 if there's no highest score (no teams in the score objective)
   */
  public int highestScore() {
    boolean seen = false;
    int best = 0;
    for (Double score : points.values()) {
      int intValue = score.intValue();
      if (!seen || intValue > best) {
        seen = true;
        best = intValue;
      }
    }
    return seen ? best : 0;
  }

  @Override
  public void modify(
      Competitor competitor, int amount, NumberAction action, Optional<Player> actor) {
    if (!canComplete(competitor)) {
      throw new IllegalArgumentException();
    }
    double dAmount = amount;

    dAmount = action.perform(getRealPoints(competitor), dAmount);

    this.points.put(competitor, dAmount);
    PointChangeEvent event = new PointChangeEvent(this, actor, competitor, amount);
    EventUtil.call(event);
  }

  @Override
  public void initialize() {}

  @Override
  public LocalizedConfigurationProperty getName() {
    return new LocalizedConfigurationProperty(OCNMessages.UI_POINTS);
  }

  @Override
  public boolean canComplete(Competitor competitor) {
    return true;
  }

  @Override
  public boolean isCompleted(Competitor competitor) {
    if (!canComplete(competitor)) {
      throw new IllegalArgumentException("Competitor cannot complete objective.");
    }
    if (!this.limit.isPresent()) {
      return false;
    }
    return getPoints(competitor) >= this.limit.get();
  }

  @Override
  public double getCompletion(Competitor competitor) {
    if (!canComplete(competitor)) {
      throw new IllegalArgumentException("Competitor cannot complete objective.");
    }
    if (!this.limit.isPresent()) {
      return 0.5;
    }
    return (double) getPoints(competitor) / Math.max(this.limit.get(), 1);
  }

  @Override
  public boolean isIncremental() {
    return true;
  }

  public Optional<Integer> getLimit() {
    return limit;
  }

  /**
   * Sets the score limit to a certain value
   *
   * @param limit the new value for the score limit
   */
  public void setLimit(int limit) {
    this.limit = Optional.of(limit);
  }

  public Optional<Integer> getKills() {
    return kills;
  }

  public Optional<Integer> getDeaths() {
    return deaths;
  }
}

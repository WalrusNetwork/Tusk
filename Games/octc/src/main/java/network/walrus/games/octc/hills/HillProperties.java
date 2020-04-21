package network.walrus.games.octc.hills;

import java.time.Duration;
import java.util.Optional;
import network.walrus.games.core.facets.filters.Filter;
import network.walrus.games.core.facets.group.Competitor;
import network.walrus.utils.bukkit.inventory.MultiMaterialMatcher;
import network.walrus.utils.bukkit.region.BoundedRegion;
import network.walrus.utils.bukkit.region.Region;
import network.walrus.utils.bukkit.translation.LocalizedConfigurationProperty;
import org.bukkit.Material;

/**
 * Represents the options that a hill can have
 *
 * @author Matthew Arnold
 */
public class HillProperties {

  // the default values for hills
  public static final Optional<Filter> DEFAULT_CAPTURE_FILTER =
      Optional.empty(); // capture-captureFilter
  public static final Optional<Filter> DEFAULT_PLAYER_FILTER =
      Optional.empty(); // default player filter, allow all
  public static final int DEFAULT_POINTS = 0; // default points per second for an objective is 0
  public static final Duration DEFAULT_CAPTURE_TIME =
      Duration.ofSeconds(30); // default capture time is 30 seconds
  public static final Optional<BoundedRegion> DEFAULT_CONTROL_REGION = Optional.empty();
  public static final CaptureRule DEFAULT_CAPTURE_RULE = CaptureRule.EXCLUSIVE;
  public static final Optional<Competitor> INITIAL_OWNER = Optional.empty();
  public static final boolean DEFAULT_NEUTRAL_STATE = false;
  public static final double DEFAULT_TIME_MULTIPLIER = 0.1;
  public static final boolean DEFAULT_PERMANENT = false;
  public static final boolean DEFAULT_REQUIRED = true;
  public static final double DEFAULT_RECOVERY_RATE = 1;
  public static final double DEFAULT_DECAY_RATE = 1;
  // sequential defaults
  public static final boolean DEFAULT_SEQUENTIAL = true;
  // the legal blocks that can modified by the block filter, only these can be edited
  private static final MultiMaterialMatcher LEGAL_BLOCK_FILTER =
      new MultiMaterialMatcher(
          Material.STAINED_CLAY,
          Material.STAINED_GLASS,
          Material.WOOL,
          Material.STAINED_GLASS_PANE,
          Material.CARPET);
  public static final MultiMaterialMatcher DEFAULT_BLOCK_FILTER =
      LEGAL_BLOCK_FILTER; // default block filter, allow all
  public final Optional<Filter> captureFilter; // capture-captureFilter
  public final Optional<Filter> playerFilter; // domination-filter
  public final MultiMaterialMatcher blockFilter;

  public final Region captureRegion; // capture-region
  public final BoundedRegion progressRegion;
  public final Optional<BoundedRegion> controlRegion;
  public final CaptureRule captureRule; // function to represent capture rule

  public final Optional<Competitor> initialOwner; // initial owner

  public final boolean neutralState; // can there be a neutral state?
  public final boolean required;

  public final double timeMultiplier;
  public final int points;

  public final boolean permanent;
  public final double recoveryRate;
  public final double decayRate;
  public final Duration captureDuration;

  // sequential properties
  public final boolean sequential;
  public final Optional<Competitor> sequentialOwner;

  public final LocalizedConfigurationProperty name;

  /**
   * Creates a new hill properties, used to define how a hill should operate
   *
   * @param captureFilter the capture filter is a filter that tests a competitor to see if they can
   *     capture the hill.
   *     <p>NOTE: A player doesn't need to pass the filter to contest the point, a player just needs
   *     to pass this filter when they're trying to capture it (they're currently already dominating
   *     the point)
   * @param playerFilter the player filter is a filter that tests to see if a competitor can
   *     cominate the hill, if a player doesn't pass this filter they can't interact with the hill
   *     at all (eg, they can't contest a point at all)
   * @param blockFilter the block filter is a filter that checks which blocks are modified by the
   *     hill when the progress region is being displayed
   * @param captureRegion the region which players have to be in to be able to contest/capture the
   *     hill
   * @param controlRegion the region in which the owner of the hill is diaplyed
   * @param progressRegion the region in which the competition is displayed
   * @param captureRule the type of capture rule the domination point uses to determine who is the
   *     current dominator of the domination point, the options are exclusive, majority or lead.
   * @param initialOwner the inital owner of the hill
   * @param neutralState whether or not the domination point should revert to neutral after a team
   *     is trying to capture the hill from another team, or if it should just directly go to their
   *     domination.
   * @param timeMultiplier the time multiplier controls how the time multiplied for each person on
   *     the point, so that if more poeple are dominating the point the point should be captured
   *     faster
   * @param points the number of points to give per second to the owning team
   * @param permanent whether the domination point is captured permanently after it has een captured
   *     once
   * @param required whether the hill is required to win the game
   * @param recoveryRate the rate at which domination reverts to the owner when they are removing
   *     the competition from the domination point
   * @param decayRate the rate at which domination is decayed to neutral after someone tries to
   *     capture the point
   * @param captureDuration the time it takes to capture the point completely
   * @param name the name of the hill, used to display the hill in the side bar and in announcements
   *     in chat
   */
  public HillProperties(
      Optional<Filter> captureFilter,
      Optional<Filter> playerFilter,
      MultiMaterialMatcher blockFilter,
      Region captureRegion,
      Optional<BoundedRegion> controlRegion,
      BoundedRegion progressRegion,
      CaptureRule captureRule,
      Optional<Competitor> initialOwner,
      boolean neutralState,
      double timeMultiplier,
      int points,
      boolean permanent,
      boolean required,
      double recoveryRate,
      double decayRate,
      Duration captureDuration,
      boolean sequential,
      Optional<Competitor> sequentialOwner,
      LocalizedConfigurationProperty name) {
    this.captureFilter = captureFilter;
    this.playerFilter = playerFilter;
    this.blockFilter =
        LEGAL_BLOCK_FILTER.combineMaterialMatch(
            blockFilter); // needs to also comply with the default block filter (list of
    // legal blocks that can change)
    this.captureRegion = captureRegion;
    this.controlRegion = controlRegion;
    this.progressRegion = progressRegion;
    this.captureRule = captureRule;
    this.initialOwner = initialOwner;
    this.neutralState = neutralState;
    this.timeMultiplier = timeMultiplier;
    this.points = points;
    this.permanent = permanent;
    this.recoveryRate = recoveryRate;
    this.decayRate = decayRate;
    this.captureDuration = captureDuration;
    this.sequential = sequential;
    this.sequentialOwner = sequentialOwner;
    this.name = name;
    this.required = required;
  }

  /**
   * The limited constructor using only defaults that are different for each gamemode, for a more
   * detailed overview look at the previous contructor
   *
   * @param neutralState the neutral state
   * @param timeMultiplier the time multiplier
   * @param points the points per second
   * @param recoveryRate the recovery rate of the hill
   * @param decayRate the decay rate of the hill
   * @param sequential whether the hill should use sequential capture
   * @param captureRule the capture rule of this hill
   */
  public HillProperties(
      boolean neutralState,
      double timeMultiplier,
      int points,
      double recoveryRate,
      double decayRate,
      boolean sequential,
      CaptureRule captureRule) {
    this(
        DEFAULT_CAPTURE_FILTER,
        DEFAULT_PLAYER_FILTER,
        DEFAULT_BLOCK_FILTER,
        null,
        DEFAULT_CONTROL_REGION,
        null,
        captureRule,
        INITIAL_OWNER,
        neutralState,
        timeMultiplier,
        points,
        DEFAULT_PERMANENT,
        DEFAULT_REQUIRED,
        recoveryRate,
        decayRate,
        DEFAULT_CAPTURE_TIME,
        sequential,
        INITIAL_OWNER,
        null);
  }

  /**
   * The type of capture rule the hill may have exclusive = only dominating team can have players on
   * hill majority = dominating team must have the most players on the hill lead = dominating team
   * must have more players than the second highest team's player count
   */
  public enum CaptureRule {
    EXCLUSIVE,
    MAJORITY,
    LEAD
  }
}

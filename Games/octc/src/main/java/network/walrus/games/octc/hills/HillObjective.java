package network.walrus.games.octc.hills;

import com.google.api.client.util.Sets;
import com.google.common.collect.Maps;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import net.md_5.bungee.api.ChatColor;
import network.walrus.games.core.facets.filters.context.FilterContext;
import network.walrus.games.core.facets.filters.variable.GroupVariable;
import network.walrus.games.core.facets.filters.variable.PlayerVariable;
import network.walrus.games.core.facets.group.Competitor;
import network.walrus.games.core.facets.group.GroupsManager;
import network.walrus.games.core.facets.objectives.GlobalObjective;
import network.walrus.games.core.facets.objectives.Objective;
import network.walrus.games.core.round.GameRound;
import network.walrus.games.core.util.EventUtil;
import network.walrus.games.octc.hills.events.HillChangeCompletionEvent;
import network.walrus.games.octc.hills.events.HillChangeDominatorEvent;
import network.walrus.games.octc.hills.events.HillChangeOwnerEvent;
import network.walrus.games.octc.hills.state.ControlState;
import network.walrus.games.octc.hills.state.HillCalculator;
import network.walrus.utils.bukkit.region.Region;
import network.walrus.utils.bukkit.translation.LocalizedConfigurationProperty;
import network.walrus.utils.core.translation.Localizable;
import network.walrus.utils.core.util.TimeUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Represents a hill objective itself
 *
 * @author Matthew Arnold
 */
public class HillObjective implements Objective, GlobalObjective {

  private static final Pair<Optional<Competitor>, Long> BLANK_PAIR = Pair.of(Optional.empty(), 0L);

  private final Set<UUID> playersOnPoint = Sets.newHashSet();
  private final Map<String, HillObjective> sequentialMap = Maps.newHashMap();

  private final HillProperties options;
  private final GameRound gameRound;

  private final HillCalculator logic;
  private final HillBlockProgress blockChange;
  private final HillAnnouncer hillAnnouncer;

  private boolean captured = false;

  private ControlState currentState;
  private GroupsManager groupsManager;

  /**
   * Creates a new Hill Objective
   *
   * @param options the properties that represent how the hill shall function
   * @param gameRound the gameround of the hill
   * @param id of objective if it should be registered
   */
  public HillObjective(HillProperties options, GameRound gameRound, Optional<String> id) {
    this.options = options;
    this.gameRound = gameRound;
    this.blockChange = new HillBlockProgress(this, gameRound);
    this.hillAnnouncer = new HillAnnouncer(this, gameRound);
    this.currentState =
        new ControlState(Optional.empty(), options.initialOwner, 0, Optional.empty());
    this.logic = new HillCalculator(options);

    id.ifPresent(s -> gameRound.getRegistry().add(s, this));
  }

  /**
   * Ticks the hill
   *
   * @param duration how much time has elapsed since the hill has last been ticked
   */
  public void tick(Duration duration) {
    if (captured && options.permanent) {
      // already captured, and permanent
      return;
    }

    if (groupsManager == null) {
      groupsManager = gameRound.getFacetRequired(GroupsManager.class);
    }

    // ticks the objective, interval is how many ticks since last tick
    Map<Competitor, Long> map = new HashMap<>();
    for (UUID x : playersOnPoint) {
      Optional<Competitor> competitorIfValid =
          getCompetitorIfValid(Bukkit.getPlayer(x), groupsManager);
      if (competitorIfValid.isPresent()) {
        Competitor competitor = competitorIfValid.get();
        map.merge(competitor, 1L, Long::sum);
      }
    }

    Pair<Optional<Competitor>, Long> dominating = BLANK_PAIR;

    switch (options.captureRule) {
      case EXCLUSIVE:
        dominating = exclusive(map);
        break;
      case MAJORITY:
        dominating = majority(map);
        break;
      case LEAD:
        dominating = lead(map);
        break;
    }

    callEvents(
        logic.dominate(
            dominating.getKey(),
            TimeUtils.multiply(
                duration, 1 + ((dominating.getValue() - 1) * options.timeMultiplier))));
  }

  private void callEvents(ControlState newState) {
    ControlState oldState = currentState;
    currentState = newState;
    if (!Objects.equals(newState.dominator(), oldState.dominator())) {
      EventUtil.call(
          new HillChangeDominatorEvent(this, newState.dominator(), oldState.dominator()));
    }
    if (!Objects.equals(newState.completionPercentage(), oldState.completionPercentage())) {
      HillChangeCompletionEvent timeEvent =
          new HillChangeCompletionEvent(
              this,
              newState.dominator(),
              newState.completionPercentage(),
              oldState.completionPercentage());

      blockChange.updateHillProgress(oldState.completionPercentage());
      EventUtil.call(timeEvent);
      // same owner
    }
    if (!Objects.equals(newState.owner(), oldState.owner())) {
      if (newState.owner().isPresent()) {
        captured = true;
      }
      EventUtil.call(new HillChangeOwnerEvent(this, newState.owner(), oldState.owner()));
      blockChange.reset(owner()); // reset the block state of the hill for the new owner
      hillAnnouncer.announceHillCapture(oldState.owner());
    }
  }

  // maps a player to a competitor if the player is valid (meets the player filter and sequential
  // requirements)
  private Optional<Competitor> getCompetitorIfValid(Player player, GroupsManager groupsManager) {
    Optional<Competitor> competitor = groupsManager.getCompetitorOf(player);
    if (!competitor.isPresent()) {
      // observer, not a competitor
      // returns the competitor as is
      return competitor;
    }
    if (options.playerFilter.isPresent()) {
      FilterContext context =
          FilterContext.of(
              new PlayerVariable(player), new GroupVariable(competitor.get().getGroup()));
      if (options.playerFilter.get().test(context).fails()) {
        // fails, therefore return empty
        return Optional.empty();
      }
    }
    if (sequentialMap.containsKey(competitor.get().id())) {
      // test if the sequential dependency passes
      if (!sequentialMap.get(competitor.get().id()).isCompleted(competitor.get())) {
        return Optional.empty();
      }
    }
    return competitor;
  }

  /**
   * Gets the colored name of the hill, with the color being either the owner's color or white if
   * there is no owner
   *
   * @return the colored name of the hill
   */
  public Localizable getColoredName() {
    return this.getName()
        .toText(owner().map(x -> x.getColor().getChatColor()).orElse(ChatColor.WHITE));
  }

  /**
   * Adds a sequential objective to this one, this adds an objective as a dependency for capturing
   * this objective for a certain team
   *
   * @param competitor the competitor to add the dependency for
   * @param hillObjective the dependent hill objective, the one the team needs to capture this
   */
  public void addSequential(Competitor competitor, HillObjective hillObjective) {
    sequentialMap.put(competitor.id(), hillObjective);
  }

  /**
   * Adds a player to the objective, they are now contesting the hill
   *
   * @param player the player to add to the objective
   */
  public void add(Player player) {
    // add a player to the objective
    playersOnPoint.add(player.getUniqueId());
  }

  /**
   * Removes a player from contesting the hill
   *
   * @param player the player to remove from contesting the hill
   */
  public void remove(Player player) {
    // remove a player from the objective
    playersOnPoint.remove(player.getUniqueId());
  }

  /** @return the options that dictate how the hill works */
  public HillProperties options() {
    return options;
  }

  /** @return the owner of the hill at this instant in time */
  public Optional<Competitor> owner() {
    return currentState.owner();
  }

  /** @return the current dominator of the hill, at this instant in time */
  public Optional<Competitor> dominator() {
    return currentState.dominator();
  }

  /** @return the current competitor with the highest competition on the hill */
  public Optional<Competitor> highestCompetition() {
    return currentState.highestCompetition();
  }

  /** @return the current completion percentage of the hill */
  public int completionPercentage() {
    return currentState.completionPercentage();
  }

  /** @return the capture region for the objective */
  public Region getCapture() {
    return options.captureRegion;
  }

  @Override
  public boolean isCompleted() {
    // returns true or false depending on whether or not the objective is completed
    return owner().isPresent();
  }

  @Override
  public double getCompletion() {
    // return a value between [0,1] to signify the completion of the hill
    if (owner().isPresent()) {
      return 1;
    }
    return 0;
  }

  @Override
  public void initialize() {
    // Don't need to spawn any entities for hills, shouldn't need to use this at all.
    if (owner().isPresent()) {
      blockChange.reset(owner());
    }
  }

  @Override
  public LocalizedConfigurationProperty getName() {
    // ignore this for now, no clue what to do (look at cores module for names)
    return options.name;
  }

  @Override
  public boolean canComplete(Competitor competitor) {
    // if a certain competitor can complete the objective
    return !(captured && options.permanent);
  }

  @Override
  public boolean isCompleted(Competitor competitor) {
    return currentState.owner().equals(Optional.of(competitor));
  }

  @Override
  public double getCompletion(Competitor competitor) {
    // gets the completion of the objective for a certain competitor
    if (isCompleted(competitor)) {
      return 1.0;
    }
    return 0;
  }

  @Override
  public boolean isIncremental() {
    return true;
  }

  /**
   * Calculates the exclusive capture rule
   *
   * @param competitors map of competitors on the hill and number
   * @return the dominating competitor and the number they're dominating with
   */
  private Pair<Optional<Competitor>, Long> exclusive(Map<Competitor, Long> competitors) {
    Optional<Competitor> dominator = Optional.empty();
    long dominating = 0;
    for (Map.Entry<Competitor, Long> entry : competitors.entrySet()) {
      if (dominator.isPresent()) {
        return BLANK_PAIR;
      }
      dominator = Optional.of(entry.getKey());
      dominating = entry.getValue();
    }
    return Pair.of(dominator, dominating);
  }

  /**
   * Calculates the majority capture rule
   *
   * @param competitors map of competitors on the hill and number
   * @return the dominating competitor and the number they're dominating with
   */
  private Pair<Optional<Competitor>, Long> majority(Map<Competitor, Long> competitors) {
    Optional<Competitor> dominator = Optional.empty();
    long dominatingPeople = 0, otherTotal = 0;

    for (Map.Entry<Competitor, Long> entry : competitors.entrySet()) {
      if (entry.getValue() > dominatingPeople) {
        otherTotal += dominatingPeople;
        dominatingPeople = entry.getValue();
        dominator = Optional.of(entry.getKey());
      } else {
        otherTotal += entry.getValue();
      }
    }

    if (dominatingPeople > otherTotal) {
      return Pair.of(dominator, dominatingPeople);
    }
    return BLANK_PAIR;
  }

  /**
   * Calculates the lead capture rule
   *
   * @param competitors map of competitors on the hill and number
   * @return the dominating competitor and the number they're dominating with
   */
  private Pair<Optional<Competitor>, Long> lead(Map<Competitor, Long> competitors) {
    Optional<Competitor> dominating = Optional.empty();
    long dominatingPeople = 0, secondPeople = 0;

    for (Map.Entry<Competitor, Long> entry : competitors.entrySet()) {
      if (entry.getValue() > dominatingPeople) {
        secondPeople = dominatingPeople;
        dominatingPeople = entry.getValue();
        dominating = Optional.of(entry.getKey());
      } else if (entry.getValue() > secondPeople) {
        secondPeople = entry.getValue();
      }
    }

    if (dominatingPeople > secondPeople) {
      return Pair.of(dominating, dominatingPeople);
    }
    return BLANK_PAIR;
  }
}

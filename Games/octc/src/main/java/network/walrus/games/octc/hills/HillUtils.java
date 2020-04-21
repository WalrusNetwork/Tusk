package network.walrus.games.octc.hills;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import network.walrus.games.core.facets.filters.Filter;
import network.walrus.games.core.facets.group.Competitor;
import network.walrus.games.core.round.GameRound;
import network.walrus.games.octc.global.groups.teams.Team;
import network.walrus.utils.bukkit.inventory.MultiMaterialMatcher;
import network.walrus.utils.bukkit.parse.BukkitParserRegistry;
import network.walrus.utils.bukkit.region.BoundedRegion;
import network.walrus.utils.bukkit.region.Region;
import network.walrus.utils.bukkit.translation.LocalizedConfigurationProperty;
import network.walrus.utils.core.config.Node;
import network.walrus.utils.core.config.StringHolder;
import network.walrus.utils.parsing.facet.holder.FacetHolder;

/**
 * Set of useful/shared functionality for hill based game-modes
 *
 * @author Matthew Arnold
 */
public class HillUtils {

  /**
   * Calculates if the hill is reverting to a neutral state or not
   *
   * @param hillObjective the hill itself
   * @return true if the hill is current reverting to a neutral state, false otherwise
   */
  public static boolean isRevertingNeutral(HillObjective hillObjective) {
    // check if has neutral state first
    if (!hillObjective.options().neutralState) {
      return false;
    }

    Optional<Competitor> owner = hillObjective.owner();
    Optional<Competitor> dominator = hillObjective.dominator();
    if (owner.isPresent()) {
      return dominator.isPresent() && !Objects.equals(dominator, owner);
    }

    Optional<Competitor> highestComp = hillObjective.highestCompetition();
    if (highestComp.isPresent()) {
      return !Objects.equals(dominator, highestComp);
    }

    return false;
  }

  /**
   * Calculates the majority owner of the hills, if there is one
   *
   * @param hills the hills to check over
   * @return the majority owner or an empty optional if there is not a majority owner
   */
  public static Optional<Competitor> majorityOwner(Collection<HillObjective> hills) {
    int size = hills.size();
    List<Competitor> owning = owning(hills, (size / 2) + 1);
    if (!owning.isEmpty()) return Optional.ofNullable(owning.get(0));
    return Optional.empty();
  }

  /**
   * Gets a list of competitors that own at least the needed number of hills from the list of hills
   *
   * @param hills the hills
   * @param needed the number of hills the teams need to own
   * @return the competitors that own at least that many hills
   */
  public static List<Competitor> owning(Collection<HillObjective> hills, int needed) {
    Map<Competitor, Integer> hillsByOwner = new HashMap<>();
    for (HillObjective hill : hills) {
      if (hill.owner().isPresent()) {
        Competitor owner = hill.owner().get();
        hillsByOwner.merge(owner, 1, Integer::sum);
      }
    }
    List<Competitor> ownersWithEnough = new ArrayList<>();
    for (Entry<Competitor, Integer> x : hillsByOwner.entrySet()) {
      if (x.getValue() >= needed) {
        Competitor key = x.getKey();
        ownersWithEnough.add(key);
      }
    }
    return ownersWithEnough;
  }

  /**
   * Parses a list of child hills from a parent node, usually a <hills></hills> node
   *
   * @param gameRound The current game round
   * @param parentNode the parent node of the child hills
   * @param def the default values for each of the hill elements, if an element is required the
   *     default value will be null. Otherwise the default value is to be respected when the
   *     attribute/element is not defined itself
   * @return A list of the defined child hills
   */
  public static List<HillObjective> parseHills(
      GameRound gameRound, Node<?> parentNode, HillProperties def) {
    List<HillObjective> hills = new ArrayList<>();
    for (Node<?> x : parentNode.children("hill")) {
      HillObjective hillObjective = parseHill(gameRound, parentNode, x, def);
      hills.add(hillObjective);
    }
    makeSequential(hills);
    return hills;
  }

  /**
   * Parse a time limit from the map root
   *
   * @param root the map root node
   * @return a duration if a time limit is present, empty optional otherwise
   */
  public static Optional<Duration> parseDuration(Node<?> root) {
    Optional<Duration> timeLimit = Optional.empty();
    if (root.hasChild("time-limit")) {
      timeLimit =
          BukkitParserRegistry.durationParser().parse(root.childRequired("time-limit").text());
    }
    return timeLimit;
  }

  private static HillObjective parseHill(
      GameRound gameRound, Node<?> parent, Node<?> node, HillProperties def) {
    // inherit the parent attributes
    node.inheritAttributes(parent.name());

    // capture is a required child element
    Region capture =
        gameRound
            .getRegistry()
            .get(Region.class, node.attribute("capture-region").asRequiredString(), true)
            .get();

    BoundedRegion progress =
        gameRound
            .getRegistry()
            .get(BoundedRegion.class, node.attribute("progress-region").asRequiredString(), true)
            .get();

    // name is a required element of every hill
    LocalizedConfigurationProperty name =
        BukkitParserRegistry.localizedPropertyParser().parseRequired(node.attribute("name"));

    Optional<BoundedRegion> captured =
        node.attribute("control-region")
            .value()
            .flatMap((n) -> gameRound.getRegistry().get(BoundedRegion.class, n, true));

    Duration captureDuration =
        BukkitParserRegistry.durationParser()
            .parse(node.attribute("capture-time"))
            .orElse(def.captureDuration);

    Optional<Filter> captureFilter =
        node.attribute("capture-filter")
            .value()
            .flatMap((n) -> gameRound.getRegistry().get(Filter.class, n, true));

    Optional<Filter> playerFilter =
        node.attribute("player-filter")
            .value()
            .flatMap((n) -> gameRound.getRegistry().get(Filter.class, n, true));

    MultiMaterialMatcher blockFilter =
        BukkitParserRegistry.multiMaterialMatcherParser()
            .parse(node.attribute("visual-materials"))
            .orElse(def.blockFilter);

    Optional<Competitor> initialOwner = def.initialOwner;
    if (node.hasAttribute("initial-owner")) {
      initialOwner =
          gameRound
              .getRegistry()
              .get(Team.class, node.attribute("initial-owner").asRequiredString(), true)
              .map(
                  Function
                      .identity()); // required because team extends competitor, and to simplify it
      // from being a bounded generic
    }

    boolean permanent =
        BukkitParserRegistry.booleanParser()
            .parse(node.attribute("permanent"))
            .orElse(def.permanent);

    boolean required =
        BukkitParserRegistry.booleanParser().parse(node.attribute("required")).orElse(def.required);

    double recovery =
        BukkitParserRegistry.doubleParser()
            .parse(node.attribute("recovery"))
            .orElse(def.recoveryRate);

    double decay =
        BukkitParserRegistry.doubleParser().parse(node.attribute("decay")).orElse(def.decayRate);

    boolean neutralState =
        BukkitParserRegistry.booleanParser()
            .parse(node.attribute("neutral-state"))
            .orElse(def.neutralState);

    double timeMultiplier =
        BukkitParserRegistry.doubleParser()
            .parse(node.attribute("time-multiplier"))
            .orElse(def.timeMultiplier);

    HillProperties.CaptureRule captureRule =
        BukkitParserRegistry.ofEnum(HillProperties.CaptureRule.class)
            .parse(node.attribute("capture-rule"))
            .orElse(def.captureRule);

    int points =
        BukkitParserRegistry.integerParser().parse(node.attribute("points")).orElse(def.points);

    boolean sequential =
        BukkitParserRegistry.booleanParser()
            .parse(node.attribute("sequential"))
            .orElse(def.sequential);

    Optional<Competitor> sequentialOwner = initialOwner;
    if (node.hasAttribute("sequential-owner")) {
      sequentialOwner =
          gameRound
              .getRegistry()
              .get(Team.class, node.attribute("sequential-owner").asRequiredString(), true)
              .map(
                  Function
                      .identity()); // required because team extends competitor, and to simplify it
      // from being a bounded generic
    }

    HillProperties properties =
        new HillProperties(
            captureFilter,
            playerFilter,
            blockFilter,
            capture,
            captured,
            progress,
            captureRule,
            initialOwner,
            neutralState,
            timeMultiplier,
            points,
            permanent,
            required,
            recovery,
            decay,
            captureDuration,
            sequential,
            sequentialOwner,
            name);

    Optional<String> id = Optional.empty();
    if (node.hasAttribute("id")) {
      id = Optional.of(node.attribute("id").asRequiredString());
    }

    return new HillObjective(properties, gameRound, id);
  }

  private static void makeSequential(List<HillObjective> objectives) {
    // no point in making something sequential of the size is less than 2
    if (objectives.size() < 2) {
      return;
    }

    // make sure both ends are sequential
    if (!isSequential(objectives.get(0)) || !isSequential(objectives.get(objectives.size() - 1))) {
      return;
    }

    // do team 1
    HillObjective previous = objectives.get(0);
    // safe get, fear not (called is Sequential prior to this)
    Competitor competitor = previous.options().sequentialOwner.get();

    for (int i = 1; i < objectives.size(); i++) {
      HillObjective objective = objectives.get(i);
      if (!objective.options().sequential) {
        continue;
      }

      objective.addSequential(competitor, previous);
      previous = objective;
    }

    // now for the other team, go in the other direction doing the same thing
    previous = objectives.get(objectives.size() - 1);
    // safe, see above a little
    competitor = previous.options().sequentialOwner.get();

    for (int i = objectives.size() - 2; i >= 0; i--) {
      HillObjective objective = objectives.get(i);
      if (!objective.options().sequential) {
        continue;
      }

      objective.addSequential(competitor, previous);
      previous = objective;
    }
  }

  private static boolean isSequential(HillObjective hillObjective) {
    return hillObjective.options().sequential
        && hillObjective.options().sequentialOwner.isPresent();
  }

  // parses a region,
  private static <T extends Region> T parseRegion(
      FacetHolder holder, Class<T> regionType, StringHolder node) {
    return holder.getRegistry().get(regionType, node.asRequiredString(), true).get();
  }
}

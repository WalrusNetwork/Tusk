package network.walrus.games.core.facets.filters;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import network.walrus.games.core.facets.filters.context.AttackerContext;
import network.walrus.games.core.facets.filters.context.VictimContext;
import network.walrus.games.core.facets.filters.modifiers.All;
import network.walrus.games.core.facets.filters.modifiers.Allow;
import network.walrus.games.core.facets.filters.modifiers.Any;
import network.walrus.games.core.facets.filters.modifiers.Deny;
import network.walrus.games.core.facets.filters.modifiers.Not;
import network.walrus.games.core.facets.filters.types.ActorFilter;
import network.walrus.games.core.facets.filters.types.CarryingFilter;
import network.walrus.games.core.facets.filters.types.CompletionFilter;
import network.walrus.games.core.facets.filters.types.DamageFilter;
import network.walrus.games.core.facets.filters.types.EntityTypeFilter;
import network.walrus.games.core.facets.filters.types.FlyingFilter;
import network.walrus.games.core.facets.filters.types.GroupFilter;
import network.walrus.games.core.facets.filters.types.HoldingFilter;
import network.walrus.games.core.facets.filters.types.InsideFilter;
import network.walrus.games.core.facets.filters.types.ItemFilter;
import network.walrus.games.core.facets.filters.types.LambdaFilter;
import network.walrus.games.core.facets.filters.types.MaterialFilter;
import network.walrus.games.core.facets.filters.types.OnGroundFilter;
import network.walrus.games.core.facets.filters.types.SneakingFilter;
import network.walrus.games.core.facets.filters.types.SpawnFilter;
import network.walrus.games.core.facets.filters.types.SprintingFilter;
import network.walrus.games.core.facets.filters.types.TimeFilter;
import network.walrus.games.core.facets.filters.types.VoidFilter;
import network.walrus.games.core.facets.filters.types.WearingFilter;
import network.walrus.games.core.facets.filters.types.random.RandomFilter;
import network.walrus.games.core.facets.filters.types.random.SometimesFilter;
import network.walrus.games.core.facets.filters.variable.EntityVariable;
import network.walrus.games.core.facets.group.Group;
import network.walrus.games.core.round.GameRound;
import network.walrus.utils.bukkit.inventory.MultiMaterialMatcher;
import network.walrus.utils.bukkit.inventory.ScopableItemStack;
import network.walrus.utils.bukkit.inventory.SingleMaterialMatcher;
import network.walrus.utils.bukkit.parse.BukkitParserRegistry;
import network.walrus.utils.bukkit.region.Region;
import network.walrus.utils.core.config.Attribute;
import network.walrus.utils.core.config.Node;
import network.walrus.utils.core.config.ParsingException;
import network.walrus.utils.core.math.NumberComparator;
import network.walrus.utils.core.parse.ComplexParser;
import network.walrus.utils.core.parse.SimpleParser;
import network.walrus.utils.core.parse.named.NamedParser;
import network.walrus.utils.core.parse.named.NamedParsers;
import network.walrus.utils.core.registry.StaticReference;
import network.walrus.utils.core.registry.WeakReference;
import network.walrus.utils.parsing.facet.facets.region.RegionsFacetParser;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import network.walrus.utils.parsing.facet.parse.DocumentParser;
import network.walrus.utils.parsing.facet.parse.FacetParser;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

/** @author Avicus Network */
public class FilterDefinitionParser implements FacetParser {

  private static final SimpleParser<Integer> integerParser = BukkitParserRegistry.integerParser();
  private static final SimpleParser<Double> doubleParser = BukkitParserRegistry.doubleParser();
  private static final SimpleParser<Boolean> booleanParser = BukkitParserRegistry.booleanParser();
  private static final SimpleParser<SpawnReason> spawnReasonParser =
      BukkitParserRegistry.ofEnum(SpawnReason.class);
  private static final SimpleParser<DamageCause> damageCauseParser =
      BukkitParserRegistry.ofEnum(DamageCause.class);
  private static final SimpleParser<EntityType> entityTypeParser =
      BukkitParserRegistry.ofEnum(EntityType.class);
  private static final SimpleParser<Actor> actorParser = BukkitParserRegistry.ofEnum(Actor.class);
  private static final SimpleParser<MultiMaterialMatcher> materialMatcherParser =
      BukkitParserRegistry.multiMaterialMatcherParser();
  private static final SimpleParser<SingleMaterialMatcher> singleMaterialMatcherParser =
      BukkitParserRegistry.singleMaterialMatcherParser();
  private static final ComplexParser<ScopableItemStack> itemParser =
      BukkitParserRegistry.itemParser();
  private static final Table<Object, String, Method> parseMethods = HashBasedTable.create();

  /** Constructor. */
  public FilterDefinitionParser() {
    parseMethods.row(this).putAll(NamedParsers.methods(FilterDefinitionParser.class));
  }

  @Override
  public Optional parse(FacetHolder holder, Node node) throws ParsingException {
    holder.getRegistry().add("always", new StaticResultFilter(FilterResult.ALLOW));
    holder.getRegistry().add("never", new StaticResultFilter(FilterResult.DENY));

    if (node.hasChild("filters")) {
      for (Node<?> regionNode : ((Node<?>) node).children("filters")) {
        for (Node<?> c : regionNode.children()) {
          String id = c.attribute("id").asRequiredString();
          Filter registered =
              NamedParsers.invokeMethod(
                  parseMethods, c, "Unknown filter type.", new Object[] {holder, c, c.children()});
          holder.getRegistry().add(id, registered);
        }
      }
    }

    return Optional.empty();
  }

  /**
   * Parse a filter from a configuration node.
   *
   * @param node node that should be parsed
   * @return a parsed check
   */
  public Filter parseFilter(FacetHolder holder, Node node) {
    Attribute id = node.attribute("id");
    Filter filter =
        NamedParsers.invokeMethod(
            parseMethods,
            node,
            "Invalid filter type specified.",
            new Object[] {holder, node, node.children()});

    if (id.isValuePresent() && !node.name().equals("filter")) {
      holder.getRegistry().add(id.asRequiredString(), filter);
    }

    return filter;
  }

  @NamedParser("filter")
  private Filter parseFilter(FacetHolder holder, Node node, List<Node> children) {
    return holder
        .getRegistry()
        .get(Filter.class, node.attribute("id").asRequiredString(), true)
        .get();
  }

  @NamedParser("always")
  private Filter parseAlways(FacetHolder holder, Node node, List<Node> children) {
    return new StaticResultFilter(FilterResult.ALLOW);
  }

  @NamedParser("never")
  private Filter parseNever(FacetHolder holder, Node node, List<Node> children) {
    return new StaticResultFilter(FilterResult.DENY);
  }

  @NamedParser("all")
  private All parseAll(FacetHolder holder, Node node, List<Node> children) {
    return new All(this.parseFilters(holder, node, children));
  }

  @NamedParser("allow")
  private Allow parseAllow(FacetHolder holder, Node node, List<Node> children) {
    return new Allow(this.parseFiltersSingleChild(holder, node, children));
  }

  @NamedParser("any")
  private Any parseAny(FacetHolder holder, Node node, List<Node> children) {
    return new Any(this.parseFilters(holder, node, children));
  }

  @NamedParser("deny")
  private Deny parseDeny(FacetHolder holder, Node node, List<Node> children) {
    return new Deny(this.parseFiltersSingleChild(holder, node, children));
  }

  @NamedParser("not")
  private Not parseNot(FacetHolder holder, Node node, List<Node> children) {
    return new Not(this.parseFiltersSingleChild(holder, node, children));
  }

  /**
   * Parse a collection of filters within an node.
   *
   * @param parent parent that holds the filters (for the exception)
   * @param children list of nodes that are filters
   * @return a list of parsed filters
   */
  public List<Filter> parseFilters(FacetHolder holder, Node parent, List<Node> children) {
    if (children.size() == 0) {
      throw new ParsingException(parent, "At least one filter must be specified.");
    }

    List<Filter> list =
        children.stream().map(child -> parseFilter(holder, child)).collect(Collectors.toList());
    return list;
  }

  /**
   * Parse a filter where having only one child tag is a requirement.
   *
   * @param parent parent that holds the filters (for the exception)
   * @param children list of nodes that are filters
   * @return the parsed filter
   */
  public Filter parseFiltersSingleChild(FacetHolder holder, Node parent, List<Node> children) {
    if (children.size() > 1) {
      throw new ParsingException(parent, "Only one child filter is allowed for the node.");
    }
    List<Filter> filters = parseFilters(holder, parent, children);
    return filters.get(0);
  }

  /**
   * Parse an actor filter.
   *
   * @param node that the filter is in
   * @return the parsed filter
   */
  @NamedParser("actor")
  private ActorFilter parseActor(FacetHolder holder, Node node, List<Node> children) {
    return new ActorFilter(actorParser.parseRequired(node.text()));
  }

  /**
   * Parse an attacker filter from an configuration node.
   *
   * @param node node that the filter is is in
   * @return the parsed filter
   */
  @NamedParser("attacker")
  private AttackerContext parseAttacker(FacetHolder holder, Node node, List<Node> children) {
    return new AttackerContext(parseFiltersSingleChild(holder, node, node.children()));
  }

  /**
   * Parse a carrying filter.
   *
   * @param node node that the filter is is in
   * @return the parsed filter
   */
  @NamedParser("carrying")
  private CarryingFilter parseCarrying(FacetHolder holder, Node node, List<Node> children) {
    return new CarryingFilter(itemParser.parse(node));
  }

  /**
   * Parse a damage filter.
   *
   * @param node node that the filter is is in
   * @return the parsed filter
   */
  @NamedParser("damage")
  private DamageFilter parseDamage(FacetHolder holder, Node node, List<Node> children) {
    DamageCause cause = damageCauseParser.parseRequired(node.text());
    return new DamageFilter(cause);
  }

  /**
   * Parse an entity effect filter.
   *
   * @param node node that the filter is is in
   * @return the parsed filter
   */
  @NamedParser("entity")
  private EntityTypeFilter parseEntityType(FacetHolder holder, Node node, List<Node> children) {
    EntityType type = entityTypeParser.parseRequired(node.text());
    return new EntityTypeFilter(type);
  }

  /**
   * Parse a flying filter.
   *
   * @param node node that the filter is is in
   * @return the parsed filter
   */
  @NamedParser("flying")
  private FlyingFilter parseFlying(FacetHolder holder, Node node, List<Node> children) {
    return new FlyingFilter(booleanParser.parseRequired(node.attribute("state")));
  }

  /**
   * Parse a holding filter.
   *
   * @param node node that the filter is is in
   * @return the parsed filter
   */
  @NamedParser("holding")
  private HoldingFilter parseHolding(FacetHolder holder, Node node, List<Node> children) {
    return new HoldingFilter(itemParser.parse(node));
  }

  /**
   * Parse an inside filter.
   *
   * @param node node that the filter is is in
   * @return the parsed filter
   */
  @NamedParser("inside")
  private InsideFilter parseInside(FacetHolder holder, Node node, List<Node> children) {
    Optional<Node> inlineRegion = node.child("region");
    Attribute regionAttribute = node.attribute("region");

    Optional<WeakReference<Region>> region = Optional.empty();

    if (inlineRegion.isPresent()) {
      region =
          Optional.of(
              new StaticReference<>(
                  DocumentParser.getParser(RegionsFacetParser.class)
                      .parseRegionAs(inlineRegion.get(), Region.class, holder.getRegistry())));
    } else if (regionAttribute.isValuePresent()) {
      region =
          Optional.of(
              holder.getRegistry().getReference(Region.class, regionAttribute.asRequiredString()));
    }

    if (!region.isPresent()) {
      throw new ParsingException(node, "No region provided.");
    }

    return new InsideFilter(region);
  }

  /**
   * Parse an item filter.
   *
   * @param node node that the filter is is in
   * @return the parsed filter
   */
  @NamedParser("item")
  private ItemFilter parseItemFilter(FacetHolder holder, Node node, List<Node> children) {
    return new ItemFilter(itemParser.parse(node));
  }

  /**
   * Parse a material filter.
   *
   * @param node node that the filter is is in
   * @return the parsed filter
   */
  @NamedParser("material")
  private MaterialFilter parseMaterial(FacetHolder holder, Node node, List<Node> children) {
    SingleMaterialMatcher matcher = singleMaterialMatcherParser.parseRequired(node.text());
    return new MaterialFilter(matcher);
  }

  @NamedParser("explosion")
  private LambdaFilter parseExplosion(FacetHolder holder, Node node, List<Node> children) {
    return new LambdaFilter(
        (c) -> {
          Optional<EntityVariable> var = c.getFirst(EntityVariable.class);
          if (!var.isPresent()) {
            return FilterResult.IGNORE;
          }

          EntityType entity = var.get().getEntity().getType();
          return FilterResult.valueOf(
              entity == EntityType.WITHER
                  || entity == EntityType.WITHER_SKULL
                  || entity == EntityType.ENDER_CRYSTAL
                  || entity == EntityType.CREEPER
                  || entity == EntityType.MINECART_TNT
                  || entity == EntityType.PRIMED_TNT
                  || entity == EntityType.FIREBALL
                  || entity == EntityType.SMALL_FIREBALL);
        });
  }

  /**
   * Parse a onGround filter.
   *
   * @param node node that the filter is is in
   * @return the parsed filter
   */
  @NamedParser("on-ground")
  private OnGroundFilter parseOnGroundFilter(FacetHolder holder, Node node, List<Node> children) {
    return new OnGroundFilter(booleanParser.parseRequired(node.attribute("state")));
  }

  /**
   * Parse a random filter.
   *
   * @param node node that the filter is is in
   * @return the parsed filter
   */
  @NamedParser("random")
  private RandomFilter parseRandomFilter(FacetHolder holder, Node node, List<Node> children) {
    double value = doubleParser.parseRequired(node.text());
    return new RandomFilter(value);
  }

  /**
   * Parse a sneaking filter.
   *
   * @param node node that the filter is is in
   * @return the parsed filter
   */
  @NamedParser("sneaking")
  private SneakingFilter parseSneakingFilter(FacetHolder holder, Node node, List<Node> children) {
    return new SneakingFilter(booleanParser.parseRequired(node.attribute("state")));
  }

  @NamedParser("sometimes")
  private Filter parseSometimes(FacetHolder holder, Node node, List<Node> children) {
    return new SometimesFilter();
  }

  /**
   * Parse a spawn reason filter.
   *
   * @param node node that the filter is is in
   * @return the parsed filter
   */
  @NamedParser({"spawn", "spawn-reason"})
  private SpawnFilter parseSpawnReason(FacetHolder holder, Node node, List<Node> children) {
    SpawnReason reason = spawnReasonParser.parseRequired(node.text());
    return new SpawnFilter(reason);
  }

  /**
   * Parse a sprinting filter.
   *
   * @param node node that the filter is is in
   * @return the parsed filter
   */
  @NamedParser("sprinting")
  private SprintingFilter parseSprinting(FacetHolder holder, Node node, List<Node> children) {
    return new SprintingFilter(booleanParser.parseRequired(node.attribute("state")));
  }

  /**
   * Parse a team filter.
   *
   * @param node node that the filter is is in
   * @return the parsed filter
   */
  @NamedParser({"team", "group"})
  private GroupFilter parseTeam(FacetHolder holder, Node node, List<Node> children) {
    return new GroupFilter(
        holder, holder.getRegistry().getReference(Group.class, node.text().asRequiredString()));
  }

  /**
   * Parse a time filter.
   *
   * @param node node that the filter is is in
   * @return the parsed filter
   */
  @NamedParser("time")
  private TimeFilter parseTime(FacetHolder holder, Node node, List<Node> children) {
    if (!(holder instanceof GameRound)) {
      throw new ParsingException(node, "This filter is not supported outside of game rounds.");
    }

    Duration duration = BukkitParserRegistry.durationParser().parseRequired(node.text());
    NumberComparator comparator =
        BukkitParserRegistry.numberComparatorParser()
            .parse(node.attribute("compare"))
            .orElse(NumberComparator.GREATER_THAN_EQUAL);
    return new TimeFilter((GameRound) holder, duration, comparator);
  }

  /**
   * Parse a victim filter.
   *
   * @param node node that the filter is is in
   * @return the parsed filter
   */
  @NamedParser("victim")
  private VictimContext parseVictim(FacetHolder holder, Node node, List<Node> children) {
    return new VictimContext(parseFiltersSingleChild(holder, node, node.children()));
  }

  /**
   * Parse a void filter
   *
   * @param node node that the filter is is in
   * @return the parsed filter
   */
  @NamedParser("void")
  public VoidFilter parseVoid(FacetHolder holder, Node node, List<Node> children) {
    int min = integerParser.parse(node.attribute("min")).orElse(0);
    int max = integerParser.parse(node.attribute("max")).orElse(5);
    Optional<MultiMaterialMatcher> ignoredBlocks =
        materialMatcherParser.parse(node.attribute("ignored-blocks"));
    return new VoidFilter(min, max, ignoredBlocks);
  }

  /**
   * Parse a wearing filter.
   *
   * @param node node that the filter is is in
   * @return the parsed filter
   */
  @NamedParser("wearing")
  private WearingFilter parseWearing(FacetHolder holder, Node node, List<Node> children) {
    return new WearingFilter(itemParser.parse(node));
  }

  /**
   * Parse an objective filter.
   *
   * @param node node that the filter is in
   * @return the parsed filter
   */
  @NamedParser("objective")
  private CompletionFilter parseCompletion(FacetHolder holder, Node node, List<Node> children) {
    return new CompletionFilter(holder, node.text().asRequiredString());
  }
}

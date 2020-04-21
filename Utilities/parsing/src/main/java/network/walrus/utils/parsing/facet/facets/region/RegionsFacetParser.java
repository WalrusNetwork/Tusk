package network.walrus.utils.parsing.facet.facets.region;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import network.walrus.utils.bukkit.parse.BukkitParserRegistry;
import network.walrus.utils.bukkit.region.BoundedRegion;
import network.walrus.utils.bukkit.region.Region;
import network.walrus.utils.bukkit.region.modifiers.BoundedJoinRegion;
import network.walrus.utils.bukkit.region.modifiers.BoundedMirrorRegion;
import network.walrus.utils.bukkit.region.modifiers.BoundedSubtractRegion;
import network.walrus.utils.bukkit.region.modifiers.BoundedTranslateRegion;
import network.walrus.utils.bukkit.region.modifiers.IntersectRegion;
import network.walrus.utils.bukkit.region.modifiers.InvertRegion;
import network.walrus.utils.bukkit.region.modifiers.JoinRegion;
import network.walrus.utils.bukkit.region.modifiers.MirrorRegion;
import network.walrus.utils.bukkit.region.modifiers.SubtractRegion;
import network.walrus.utils.bukkit.region.modifiers.TranslateRegion;
import network.walrus.utils.bukkit.region.shapes.BlockRegion;
import network.walrus.utils.bukkit.region.shapes.BoxRegion;
import network.walrus.utils.bukkit.region.shapes.CircleRegion;
import network.walrus.utils.bukkit.region.shapes.CuboidRegion;
import network.walrus.utils.bukkit.region.shapes.CylinderRegion;
import network.walrus.utils.bukkit.region.shapes.PointRegion;
import network.walrus.utils.bukkit.region.shapes.RectangleRegion;
import network.walrus.utils.bukkit.region.shapes.SphereRegion;
import network.walrus.utils.bukkit.region.special.AboveRegion;
import network.walrus.utils.bukkit.region.special.BelowRegion;
import network.walrus.utils.bukkit.region.special.BoundsRegion;
import network.walrus.utils.bukkit.region.special.EverywhereRegion;
import network.walrus.utils.bukkit.region.special.NowhereRegion;
import network.walrus.utils.bukkit.region.special.SectorRegion;
import network.walrus.utils.core.config.Node;
import network.walrus.utils.core.config.ParsingException;
import network.walrus.utils.core.config.StringHolder;
import network.walrus.utils.core.parse.named.NamedParser;
import network.walrus.utils.core.parse.named.NamedParsers;
import network.walrus.utils.core.registry.Registry;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import network.walrus.utils.parsing.facet.parse.FacetParser;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.util.Vector;

/**
 * Parser for parsing region definitions in the map config and registering them in the {@link
 * Registry}.
 *
 * @author Austin Mayes
 */
public class RegionsFacetParser implements FacetParser {

  private final Table<Object, String, Method> parseMethods = HashBasedTable.create();

  /** Constructor. */
  public RegionsFacetParser() {
    this.parseMethods.row(this).putAll(NamedParsers.methods(RegionsFacetParser.class));
  }

  @Override
  public Optional parse(FacetHolder holder, Node node) throws ParsingException {
    holder.getRegistry().add("everywhere", new EverywhereRegion());
    holder.getRegistry().add("nowhere", new NowhereRegion());

    if (node.hasChild("regions")) {
      for (Node<?> regionNode : ((Node<?>) node).children("regions")) {
        for (Node<?> c : regionNode.children()) {
          parseRegion(c, holder.getRegistry());
        }
      }
    }

    return Optional.empty();
  }

  @NamedParser("region")
  private Region parseRegionId(Node node, Registry registry) {
    String id = node.attribute("id").asRequiredString();
    return registry.get(Region.class, id, true).get();
  }

  @NamedParser("block")
  private BlockRegion parseBlock(Node node, Registry registry) {
    Vector vector = BukkitParserRegistry.vectorParser().parseRequired(node.text());
    return new BlockRegion(vector);
  }

  @NamedParser("box")
  private BoxRegion parseBox(Node node, Registry registry) {
    Vector center = BukkitParserRegistry.vectorParser().parseRequired(node.attribute("center"));
    double x = BukkitParserRegistry.doubleParser().parse(node.attribute("x")).orElse(0.0);
    double y = BukkitParserRegistry.doubleParser().parse(node.attribute("y")).orElse(0.0);
    double z = BukkitParserRegistry.doubleParser().parse(node.attribute("z")).orElse(0.0);
    return new BoxRegion(center, x, y, z);
  }

  @NamedParser("circle")
  private CircleRegion parseCircle(Node node, Registry registry) {
    Vector center = BukkitParserRegistry.vectorParser().parse2D(node.attribute("center"));
    double radius = BukkitParserRegistry.doubleParser().parseRequired(node.attribute("radius"));
    return new CircleRegion(center, radius);
  }

  @NamedParser("cuboid")
  private CuboidRegion parseCuboid(Node node, Registry registry) {
    Vector min = BukkitParserRegistry.vectorParser().parseRequired(node.attribute("min"));
    Vector max = BukkitParserRegistry.vectorParser().parseRequired(node.attribute("max"));
    return new CuboidRegion(min, max);
  }

  @NamedParser("cylinder")
  private CylinderRegion parseCylinder(Node node, Registry registry) {
    Vector base = BukkitParserRegistry.vectorParser().parseRequired(node.attribute("base"));
    double radius = BukkitParserRegistry.doubleParser().parseRequired(node.attribute("radius"));
    int height = BukkitParserRegistry.integerParser().parseRequired(node.attribute("height"));
    return new CylinderRegion(base, radius, height);
  }

  @NamedParser("point")
  private PointRegion parsePoint(Node node, Registry registry) {
    Vector vector = BukkitParserRegistry.vectorParser().parseRequired(node.text());
    return new PointRegion(vector);
  }

  @NamedParser("rectangle")
  private RectangleRegion parseRectangle(Node node, Registry registry) {
    List<StringHolder> min =
        BukkitParserRegistry.listParser().parseRequiredList(node.attribute("min"), ",", true);
    List<StringHolder> max =
        BukkitParserRegistry.listParser().parseRequiredList(node.attribute("max"), ",", true);
    if (min.size() != 2 || max.size() != 2) {
      throw new ParsingException(node, "Min/Max coordinates must be in the \"x, z\" format");
    }

    double xMin = BukkitParserRegistry.doubleParser().parseRequired(min.get(0));
    double zMin = BukkitParserRegistry.doubleParser().parseRequired(min.get(1));
    double xMax = BukkitParserRegistry.doubleParser().parseRequired(max.get(0));
    double zMax = BukkitParserRegistry.doubleParser().parseRequired(max.get(1));

    return new RectangleRegion(xMin, zMin, xMax, zMax);
  }

  @NamedParser("sphere")
  private SphereRegion parseSphere(Node node, Registry registry) {
    Vector origin = BukkitParserRegistry.vectorParser().parseRequired(node.attribute("origin"));
    double radius = BukkitParserRegistry.doubleParser().parseRequired(node.attribute("radius"));
    return new SphereRegion(origin, radius);
  }

  @NamedParser("intersect")
  private IntersectRegion parseIntersect(Node node, Registry registry) {
    JoinRegion children = parseJoin(node, registry);
    return new IntersectRegion(children);
  }

  @NamedParser({"invert", "negative"})
  private InvertRegion parseInvert(Node node, Registry registry) {
    JoinRegion children = parseJoin(node, registry);
    return new InvertRegion(children);
  }

  @NamedParser({"join", "union"})
  @SuppressWarnings("unchecked")
  protected <T extends JoinRegion> T parseJoin(Node node, Registry registry) {
    Pair<List<Region>, List<BoundedRegion>> regions = parseGroup(node, registry);
    List<Region> list = regions.getKey();
    List<BoundedRegion> bounded = regions.getValue();

    if (bounded.size() == list.size()) {
      return (T) new BoundedJoinRegion(bounded);
    }
    return (T) new JoinRegion(list);
  }

  /**
   * Parser a region and, if it has an ID, add it to the registry
   *
   * @param node to parse the region from
   * @param registry used for references
   * @return the parsed region
   */
  public Region parseRegion(Node node, Registry registry) {
    Region region =
        NamedParsers.invokeMethod(
            parseMethods, node, "Unknown region type.", new Object[] {node, registry});

    if (region instanceof BoundedRegion) {
      validateBoundedRegion((BoundedRegion) region, node);
    }

    if (!node.name().equals("region")) {
      node.attribute("id").value().ifPresent(i -> registry.add(i, region));
    }
    return region;
  }

  private void validateBoundedRegion(BoundedRegion region, Node node) {
    Vector min = region.min();
    Vector max = region.max();
    if (anyInfinite(min.getX(), min.getY(), min.getZ(), max.getX(), max.getY(), max.getZ())) {
      throw new ParsingException(node, "This region cannot have infinite coordinates");
    }
  }

  private boolean anyInfinite(Double... toCheck) {
    for (Double num : toCheck) {
      if (num.equals(Double.NEGATIVE_INFINITY) || num.equals(Double.POSITIVE_INFINITY)) {
        return true;
      }
    }

    return false;
  }

  private Pair<List<Region>, List<BoundedRegion>> parseGroup(Node node, Registry registry) {
    List<Region> list = new ArrayList<>();
    List<BoundedRegion> bounded = new ArrayList<>();

    List<Node> children = node.children();

    for (Node child : children) {
      Region region = parseRegion(child, registry);
      list.add(region);
      if (region instanceof BoundedRegion) {
        bounded.add((BoundedRegion) region);
      }
    }

    if (list.isEmpty()) {
      throw new ParsingException(node, "This region requires children.");
    }

    return Pair.of(list, bounded);
  }

  @NamedParser({"subtract", "complement"})
  private SubtractRegion parseSubtract(Node node, Registry registry) {
    Pair<List<Region>, List<BoundedRegion>> regions = parseGroup(node, registry);
    List<Region> list = regions.getKey();
    List<BoundedRegion> bounded = regions.getValue();
    if (bounded.size() == list.size()) {
      BoundedRegion first = bounded.remove(0);
      return new BoundedSubtractRegion(first, new BoundedJoinRegion(bounded));
    } else {
      Region first = list.remove(0);
      return new SubtractRegion(first, new JoinRegion<>(list));
    }
  }

  @NamedParser("translate")
  private TranslateRegion parseTranslate(Node node, Registry registry) {
    Vector offset = BukkitParserRegistry.vectorParser().parseRequired(node.attribute("offset"));
    JoinRegion children = parseJoin(node, registry);
    if (children instanceof BoundedRegion) {
      return new BoundedTranslateRegion((BoundedRegion) children, offset);
    }
    return new TranslateRegion(children, offset);
  }

  @NamedParser("above")
  private AboveRegion parseAbove(Node node, Registry registry) {
    Optional<Integer> x = BukkitParserRegistry.integerParser().parse(node.attribute("x"));
    Optional<Integer> y = BukkitParserRegistry.integerParser().parse(node.attribute("y"));
    Optional<Integer> z = BukkitParserRegistry.integerParser().parse(node.attribute("z"));

    return new AboveRegion(x, y, z);
  }

  @NamedParser("below")
  private BelowRegion parseBelow(Node node, Registry registry) {
    Optional<Integer> x = BukkitParserRegistry.integerParser().parse(node.attribute("x"));
    Optional<Integer> y = BukkitParserRegistry.integerParser().parse(node.attribute("y"));
    Optional<Integer> z = BukkitParserRegistry.integerParser().parse(node.attribute("z"));

    return new BelowRegion(x, y, z);
  }

  @NamedParser("bounds")
  private BoundsRegion parseBounds(Node node, Registry registry) {
    BoundedRegion base =
        resolveRequiredRegionChild(
            BoundedRegion.class, registry, node.attribute("region"), node.child("region"));
    boolean xSide =
        BukkitParserRegistry.booleanParser().parse(node.attribute("x-axis")).orElse(true);
    boolean ySide =
        BukkitParserRegistry.booleanParser().parse(node.attribute("y-axis")).orElse(true);
    boolean zSide =
        BukkitParserRegistry.booleanParser().parse(node.attribute("z-axis")).orElse(true);

    return new BoundsRegion(base, xSide, ySide, zSide);
  }

  @NamedParser("sector")
  private SectorRegion parseSector(Node node, Registry registry) {
    int x = BukkitParserRegistry.integerParser().parseRequired(node.attribute("x"));
    int z = BukkitParserRegistry.integerParser().parseRequired(node.attribute("z"));
    double start = BukkitParserRegistry.doubleParser().parseRequired(node.attribute("start"));
    double end = BukkitParserRegistry.doubleParser().parseRequired(node.attribute("end"));
    return new SectorRegion(x, z, start, end);
  }

  @NamedParser("everywhere")
  private EverywhereRegion parseEverywhere(Node node) {
    return new EverywhereRegion();
  }

  @NamedParser("nowhere")
  private NowhereRegion parseNowhere(Node node) {
    return new NowhereRegion();
  }

  @NamedParser("mirror")
  private MirrorRegion parseMirror(Node node, Registry registry) {
    Region base =
        resolveRequiredRegionAs(Region.class, registry, node.attribute("base"), Optional.of(node));
    Vector origin = BukkitParserRegistry.vectorParser().parseRequired(node.attribute("origin"));
    Vector normal = BukkitParserRegistry.vectorParser().parseRequired(node.attribute("normal"));
    if (base instanceof BoundedRegion) {
      return new BoundedMirrorRegion((BoundedRegion) base, origin, normal);
    } else {
      return new MirrorRegion(base, origin, normal);
    }
  }

  /**
   * Resolve a region by either a {@link Registry} ID or by using an inline node. If a region can be
   * returned by using the aforementioned means, a type check will be run and a {@link
   * ParsingException} will be thrown if the types do not match. If you are expecting the inline
   * node to not be a region itself, but to wrap a region, then {@link
   * #resolveRequiredRegionChild(Class, Registry, StringHolder, Optional)} should be used instead.
   *
   * @param type of region being parsed
   * @param regionId holder containing referential region ID for lookup in the {@link Registry}.
   * @param inlineRegion fallback {@link Node} to be used if no ID was provided
   * @param <T> region type
   * @return a parsed region, or {@link Optional#empty()} if no ID and fallback node were provided
   */
  @SuppressWarnings("unchecked")
  public <T extends Region> Optional<T> resolveRegionAs(
      Class<T> type, Registry registry, StringHolder regionId, Optional<Node> inlineRegion) {
    Optional<Region> region = Optional.empty();

    if (regionId.isValuePresent()) {
      region = registry.get(Region.class, regionId.asRequiredString(), true);
    } else if (inlineRegion.isPresent()) {
      region = Optional.of(parseRegionAs(inlineRegion.get(), type, registry));
    }

    if (region.isPresent()) {
      if (!type.isAssignableFrom(region.get().getClass())) {
        String error =
            "Region type mismatch. Expected \""
                + type.getSimpleName()
                + "\" but got \""
                + region.get().getClass().getSimpleName()
                + "\".";
        throw new ParsingException(regionId.parent(), error);
      }
    }

    return (Optional<T>) region;
  }

  /**
   * Parse the supplied node as a certain type of region, or throw a {@link ParsingException} if the
   * region types do not match.
   *
   * @param node to parse
   * @param type of region being parsed
   * @param <T> region type
   * @return the parsed region
   */
  public <T extends Region> T parseRegionAs(Node node, Class<T> type, Registry registry) {
    if (node.name().equalsIgnoreCase("region") && node.children().isEmpty()) {
      String id = node.attribute("id").asRequiredString();
      return registry.get(type, id, true).get();
    }
    if (!node.children().isEmpty()
        && (type == Region.class
            || type == BoundedJoinRegion.class
            || type == BoundedRegion.class)) {
      return (T) parseJoin(node, registry);
    }

    T t =
        NamedParsers.invokeMethod(
            parseMethods, type, node, "Unknown region type.", new Object[] {node, registry});

    if (t instanceof BoundedRegion) {
      validateBoundedRegion((BoundedRegion) t, node);
    }
    node.attribute("id").value().ifPresent(i -> registry.add(i, t));

    return t;
  }

  /**
   * Resolve a region by either a {@link Registry} ID or by using an inline node. If a region can be
   * returned by using the aforementioned means, a type check will be run and a {@link
   * ParsingException} will be thrown if the types do not match. If the node is present and does not
   * contain only on child node, a {@link ParsingException} will be thrown. The inline node should
   * be a wrapped, and not the region node itself.
   *
   * @param type of region being parsed
   * @param regionId holder containing referential region ID for lookup in the {@link Registry}.
   * @param inlineRegion wrapper node containing a singular child node
   * @param <T> type of region
   * @return a parsed region
   */
  public <T extends Region> T resolveRequiredRegionChild(
      Class<T> type, Registry registry, StringHolder regionId, Optional<Node> inlineRegion) {
    Optional<Node> child = Optional.empty();
    if (inlineRegion.isPresent()) {
      List<Node> children = inlineRegion.get().children();
      if (children.size() > 1 || children.isEmpty()) {
        throw new ParsingException(inlineRegion.get(), "Region count mismatch. Expected 1 child.");
      }
      child = Optional.of(children.get(0));
    }
    Optional<T> region = resolveRegionAs(type, registry, regionId, child);
    if (region.isPresent()) {
      return region.get();
    }
    throw new ParsingException(regionId.parent(), "Missing required region.");
  }

  /**
   * Evoke {@link #resolveRegionAs(Class, Registry, StringHolder, Optional)} and throw a {@link
   * ParsingException} if no region was parsed.
   */
  public <T extends Region> T resolveRequiredRegionAs(
      Class<T> type, Registry registry, StringHolder regionId, Optional<Node> inlineRegion) {
    Optional<T> region = resolveRegionAs(type, registry, regionId, inlineRegion);
    if (region.isPresent()) {
      return region.get();
    }
    throw new ParsingException(regionId.parent(), "Missing required region.");
  }
}

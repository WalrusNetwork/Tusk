package network.walrus.games.core.facets.kits;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import network.walrus.games.core.facets.group.color.GroupColorProvider;
import network.walrus.games.core.facets.kits.type.CompassKit;
import network.walrus.games.core.facets.kits.type.DoubleJumpKit;
import network.walrus.games.core.facets.kits.type.EffectKit;
import network.walrus.games.core.facets.kits.type.FoodKit;
import network.walrus.games.core.facets.kits.type.HealthKit;
import network.walrus.games.core.facets.kits.type.ItemKit;
import network.walrus.games.core.facets.kits.type.KitNode;
import network.walrus.games.core.facets.kits.type.MovementKit;
import network.walrus.games.core.facets.kits.type.ReversibleKitNode;
import network.walrus.games.core.facets.kits.type.SkinKit;
import network.walrus.games.core.facets.kits.type.VisualKit;
import network.walrus.games.core.facets.kits.type.XPKit;
import network.walrus.ubiquitous.bukkit.item.DefuseListener;
import network.walrus.utils.bukkit.inventory.ScopableItemStack;
import network.walrus.utils.bukkit.parse.BukkitParserRegistry;
import network.walrus.utils.bukkit.parse.complex.ItemParser;
import network.walrus.utils.core.config.Node;
import network.walrus.utils.core.config.ParsingException;
import network.walrus.utils.core.math.NumberAction;
import network.walrus.utils.core.math.PreparedNumberAction;
import network.walrus.utils.core.parse.named.NamedParser;
import network.walrus.utils.core.parse.named.NamedParsers;
import network.walrus.utils.core.parse.simple.BooleanParser;
import network.walrus.utils.core.parse.simple.number.NumberParser;
import network.walrus.utils.core.registry.IdentifiedObject;
import network.walrus.utils.parsing.facet.Facet;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import network.walrus.utils.parsing.facet.parse.FacetParser;
import org.bukkit.Skin;
import org.bukkit.WeatherType;
import org.bukkit.potion.PotionEffect;

/**
 * Parser responsible for parsing and registering {@link Kit}s.
 *
 * @author Avicus Network
 */
@SuppressWarnings("JavaDoc")
public class KitsParser implements FacetParser<Facet> {

  private static final Map<String, Method> NAMED_PARSERS = Maps.newHashMap();
  private final ItemParser itemParser = BukkitParserRegistry.itemParser();
  private final BooleanParser booleanParser = BukkitParserRegistry.booleanParser();
  private final NumberParser<Integer> integerParser = BukkitParserRegistry.integerParser();

  /** Constructor. */
  public KitsParser() {
    NAMED_PARSERS.putAll(NamedParsers.methods(KitsParser.class));
  }

  @Override
  public Optional<Facet> parse(FacetHolder holder, Node node) throws ParsingException {
    ItemParser.teamColorProvider = new GroupColorProvider(holder);
    List<Node<?>> nodes = node.children("kits");

    if (nodes.isEmpty()) {
      return Optional.empty();
    }

    List<Kit> kits = Lists.newArrayList();

    for (Node<?> kitNode : nodes) {
      for (Node<?> child : kitNode.children()) {
        String id = child.attribute("id").asRequiredString();
        Kit kit = parseKit(holder, child);

        kits.add(kit);
        // register
        holder.getRegistry().add(new IdentifiedObject(id, kit));
      }
    }
    return Optional.empty();
  }

  @SuppressWarnings("unchecked")
  public Kit parseKit(FacetHolder facetHolder, Node node) {
    Kit parent = null;
    if (node.attribute("parent").isValuePresent()) {
      parent =
          facetHolder
              .getRegistry()
              .get(Kit.class, node.attribute("parent").asRequiredString(), true)
              .orElse(null);
    }

    boolean force = booleanParser.parse(node.attribute("force")).orElse(false);

    List<Kit> parsed = new ArrayList<>();
    List<ReversibleKit> reversibleKits = new ArrayList<>();

    for (Map.Entry<String, Method> parser : NAMED_PARSERS.entrySet()) {
      if (node.hasChild(parser.getKey())) {
        try {
          Kit kit = (Kit) parser.getValue().invoke(this, facetHolder, node, force, parent);
          parsed.add(kit);

          if (kit instanceof ReversibleKit) {
            reversibleKits.add((ReversibleKit) kit);
          }
        } catch (Exception e) {
          if (e.getCause() != null) {
            if (e.getCause() instanceof ParsingException) {
              throw (ParsingException) e.getCause();
            }
            e.printStackTrace();
            throw new ParsingException(node, e.getCause());
          }
          throw new ParsingException(node, e);
        }
      }
    }

    if (parsed.isEmpty()) {
      throw new ParsingException(node, "Node has no valid kit children.");
    }

    if (parsed.size() == 1) {
      return parsed.get(0);
    } else if (reversibleKits.size() == parsed.size()
        && (parent == null || parent instanceof ReversibleKit)) {
      return new ReversibleKitNode(force, (ReversibleKit) parent, reversibleKits);
    } else {
      return new KitNode(force, parent, parsed);
    }
  }

  @NamedParser({"item", "helmet", "chestplate", "leggings", "boots"})
  public ItemKit parseItemKit(FacetHolder facetHolder, Node<?> node, boolean force, Kit parent) {
    Map<Integer, ScopableItemStack> slotted = Maps.newHashMap();
    List<ScopableItemStack> unSlotted = new ArrayList<>();

    for (Node child : node.children()) {
      String name = child.name();
      boolean item = false;
      int slot = -1;

      switch (name) {
        case "item":
          item = true;
          if (child.attribute("slot").isValuePresent()) {
            slot = integerParser.parseRequired(child.attribute("slot"));
          }
          break;
        case "helmet":
          item = true;
          slot = 103;
          break;
        case "chestplate":
          item = true;
          slot = 102;
          break;
        case "leggings":
          item = true;
          slot = 101;
          break;
        case "boots":
          item = true;
          slot = 100;
          break;
      }

      // Item
      if (item) {
        ScopableItemStack stack = itemParser.parseItemStack(child);
        if (slot == -1) {
          unSlotted.add(stack);
        } else {
          slotted.put(slot, stack);
        }
      }
    }
    return new ItemKit(force, parent, slotted, unSlotted);
  }

  @NamedParser("extinguisher")
  public ItemKit parseExtinguisher(FacetHolder holder, Node<?> node, boolean force, Kit parent) {
    Map<Integer, ScopableItemStack> slotted = Maps.newHashMap();
    List<ScopableItemStack> unSlotted = new ArrayList<>();
    Optional<Integer> slot =
        integerParser.parse(node.childRequired("extinguisher").attribute("slot"));

    if (slot.isPresent()) {
      slotted.put(slot.get(), DefuseListener.EXTINGUISHER);
    } else {
      unSlotted.add(DefuseListener.EXTINGUISHER);
    }

    return new ItemKit(force, parent, slotted, unSlotted);
  }

  @NamedParser("defuser")
  public ItemKit parseDefuser(FacetHolder holder, Node<?> node, boolean force, Kit parent) {
    Map<Integer, ScopableItemStack> slotted = Maps.newHashMap();
    List<ScopableItemStack> unSlotted = new ArrayList<>();
    Optional<Integer> slot = integerParser.parse(node.childRequired("defuser").attribute("slot"));

    if (slot.isPresent()) {
      slotted.put(slot.get(), DefuseListener.DEFUSER);
    } else {
      unSlotted.add(DefuseListener.DEFUSER);
    }

    return new ItemKit(force, parent, slotted, unSlotted);
  }

  @NamedParser("effect")
  public EffectKit parseEffectKit(
      FacetHolder facetHolder, Node<?> node, boolean force, Kit parent) {
    List<PotionEffect> effects = new ArrayList<>();

    for (Node<?> e : node.children("effect")) {
      effects.add(itemParser.parsePotionEffect(e));
    }

    return new EffectKit(force, parent, effects);
  }

  @NamedParser("compass")
  public CompassKit parseCompassKit(FacetHolder facetHolder, Node node, boolean force, Kit parent) {
    return new CompassKit(
        force,
        parent,
        BukkitParserRegistry.vectorParser().parseRequired(node.childRequired("compass").text()));
  }

  @NamedParser({"weather", "time"})
  public VisualKit parseVisualKit(FacetHolder facetHolder, Node node, boolean force, Kit parent) {
    WeatherType weather = null;
    PreparedNumberAction time = null;
    if (node.hasChild("weather")) {
      weather =
          BukkitParserRegistry.ofEnum(WeatherType.class)
              .parseRequired(node.childRequired("weather").text());
    }

    if (node.hasChild("time")) {
      time = parsePreparedNumberAction(node.childRequired("time"));
    }

    return new VisualKit(force, parent, weather, time);
  }

  @NamedParser({"health", "max-health", "health-scale"})
  public HealthKit parseHealthKit(FacetHolder facetHolder, Node node, boolean force, Kit parent) {
    PreparedNumberAction health = null;
    PreparedNumberAction maxHealth = null;
    PreparedNumberAction healthScale = null;

    if (node.hasChild("health")) {
      health = parsePreparedNumberAction(node.childRequired("health"));
    }

    if (node.hasChild("max-health")) {
      maxHealth = parsePreparedNumberAction(node.childRequired("max-health"));
    }

    if (node.hasChild("health-scale")) {
      healthScale = parsePreparedNumberAction(node.childRequired("health-scale"));
    }

    return new HealthKit(force, parent, health, maxHealth, healthScale);
  }

  @NamedParser({"food-level", "saturation"})
  public FoodKit parseFoodKit(FacetHolder facetHolder, Node node, boolean force, Kit parent) {
    PreparedNumberAction foodLevel = null;
    PreparedNumberAction saturation = null;

    if (node.hasChild("food-level")) {
      foodLevel = parsePreparedNumberAction(node.childRequired("food-level"));
    }

    if (node.hasChild("saturation")) {
      saturation = parsePreparedNumberAction(node.childRequired("saturation"));
    }

    return new FoodKit(force, parent, foodLevel, saturation);
  }

  @NamedParser({"exp-level", "exp-points", "exp-total"})
  public XPKit parseXPKit(FacetHolder facetHolder, Node node, boolean force, Kit parent) {
    PreparedNumberAction points = null;
    PreparedNumberAction level = null;

    if (node.hasChild("exp-level")) {
      level = parsePreparedNumberAction(node.childRequired("exp-level"));
    }

    if (node.hasChild("exp-points")) {
      points = parsePreparedNumberAction(node.childRequired("exp-points"));
    }

    return new XPKit(force, parent, level, points);
  }

  @NamedParser({"exhaustion", "walk-speed", "fly-speed"})
  public MovementKit parseMovementKit(
      FacetHolder facetHolder, Node node, boolean force, Kit parent) {
    PreparedNumberAction exhaustion = null;
    PreparedNumberAction walkSpeed = null;
    PreparedNumberAction flySpeed = null;

    if (node.hasChild("exhaustion")) {
      exhaustion = parsePreparedNumberAction(node.childRequired("exhaustion"));
    }

    if (node.hasChild("walk-speed")) {
      walkSpeed = parsePreparedNumberAction(node.childRequired("walk-speed"));
    }

    if (node.hasChild("fly-speed")) {
      flySpeed = parsePreparedNumberAction(node.childRequired("fly-speed"));
    }

    return new MovementKit(force, parent, exhaustion, flySpeed, walkSpeed);
  }

  @NamedParser({"skin", "reset-skin", "clear-skin"})
  public SkinKit parseSkinKit(FacetHolder facetHolder, Node node, boolean force, Kit parent) {
    if (node.hasChild("skin")) {
      Node child = node.childRequired("skin");
      String data = child.attribute("data").asRequiredString();
      String signature = child.attribute("signature").asRequiredString();
      Skin skin = new Skin(data, signature);
      return new SkinKit(force, parent, Optional.of(skin));
    }

    if (node.hasChild("reset-skin")) {
      return new SkinKit(force, parent, Optional.empty());
    }

    if (node.hasChild("clear-skin")) {
      return new SkinKit(force, parent, Optional.of(Skin.EMPTY));
    }

    throw new IllegalStateException("Unknown skin kit type: " + node.name());
  }

  @NamedParser("double-jump")
  public DoubleJumpKit parseDoubleJumpKit(
      FacetHolder facetHolder, Node node, boolean force, Kit parent) {
    Node child = node.childRequired("double-jump");
    boolean enabled = booleanParser.parse(child.attribute("enabled")).orElse(true);
    int power = integerParser.parse(child.attribute("power")).orElse(3);
    Duration rechargeTime =
        BukkitParserRegistry.durationParser()
            .parse(child.attribute("recharge-time"))
            .orElse(Duration.ofMillis(2500));
    boolean rechargeBeforeLanding =
        booleanParser.parse(child.attribute("recharge-before-landing")).orElse(false);

    return new DoubleJumpKit(force, parent, enabled, power, rechargeTime, rechargeBeforeLanding);
  }

  private PreparedNumberAction parsePreparedNumberAction(Node node) {
    Number value = BukkitParserRegistry.baseNumberParser().parseRequired(node.text());
    NumberAction action =
        BukkitParserRegistry.numberActionParser()
            .parse(node.attribute("action"))
            .orElse(NumberAction.SET);
    return new PreparedNumberAction(value, action);
  }
}

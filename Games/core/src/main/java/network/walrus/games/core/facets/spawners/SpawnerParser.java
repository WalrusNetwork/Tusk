package network.walrus.games.core.facets.spawners;

import com.google.api.client.util.Sets;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import network.walrus.games.core.facets.filters.Filter;
import network.walrus.games.core.facets.spawners.spawneritems.SpawnItemTNT;
import network.walrus.games.core.facets.spawners.spawneritems.SpawnerEntity;
import network.walrus.games.core.facets.spawners.spawneritems.SpawnerEntry;
import network.walrus.games.core.facets.spawners.spawneritems.SpawnerEntryAll;
import network.walrus.games.core.facets.spawners.spawneritems.SpawnerEntryAny;
import network.walrus.games.core.facets.spawners.spawneritems.SpawnerItem;
import network.walrus.games.core.facets.spawners.spawneritems.SpawnerSplashPotion;
import network.walrus.utils.bukkit.inventory.ScopableItemStack;
import network.walrus.utils.bukkit.parse.BukkitParserRegistry;
import network.walrus.utils.bukkit.region.BoundedRegion;
import network.walrus.utils.bukkit.region.Region;
import network.walrus.utils.core.config.Node;
import network.walrus.utils.core.config.ParsingException;
import network.walrus.utils.core.parse.named.NamedParser;
import network.walrus.utils.core.parse.named.NamedParsers;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import network.walrus.utils.parsing.facet.parse.FacetParser;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

/**
 * Parses the spawner facet
 *
 * <p>Normal parser. First finds the spawners section before finding each spawner child, parses
 * these children for the information before parsing their own children (for the items that are
 * being spawned).
 *
 * @author Matthew Arnold
 */
public class SpawnerParser implements FacetParser<SpawnerFacet> {

  private static final Duration DEFAULT_DELAY = Duration.ofSeconds(1);
  private static final int DEFAULT_SPAWN_AMOUNT = 1;

  private static final int MAX_TNT_FUSE = 127;
  private static final int DEFAULT_TNT_FUSE = 4 * 20;
  private static final float DEFAULT_TNT_POWER = 4F;

  private final Table<Object, String, Method> parseMethods = HashBasedTable.create();

  public SpawnerParser() {
    this.parseMethods.row(this).putAll(NamedParsers.methods(SpawnerParser.class));
  }

  @Override
  public Optional<SpawnerFacet> parse(FacetHolder holder, Node<?> node) throws ParsingException {
    if (!node.hasChild("spawners")) {
      return Optional.empty();
    }

    Set<SpawnerOptions> options = Sets.newHashSet();
    for (Node<?> spawners : node.children("spawners")) {
      for (Node<?> spawnerNode : spawners.children("spawner")) {
        Optional<SpawnerOptions> maybeSpawnerOptions = spawner(holder, spawnerNode);
        maybeSpawnerOptions.ifPresent(options::add);
      }
    }

    if (options.size() == 0) {
      return Optional.empty();
    }

    SpawnerFacet facet = new SpawnerFacet(holder);
    for (SpawnerOptions spawnerOptions : options) {
      facet.addSpawner(new Spawner(facet, spawnerOptions));
    }

    return Optional.of(facet);
  }

  private Optional<SpawnerOptions> spawner(FacetHolder holder, Node<?> spawnerNode) {
    Duration delay =
        BukkitParserRegistry.durationParser()
            .parse(spawnerNode.attribute("delay"))
            .orElse(DEFAULT_DELAY);

    int amount =
        BukkitParserRegistry.integerParser()
            .parse(spawnerNode.attribute("amount"))
            .orElse(DEFAULT_SPAWN_AMOUNT);

    BoundedRegion spawnRegion =
        holder
            .getRegistry()
            .get(
                BoundedRegion.class, spawnerNode.attribute("spawn-region").asRequiredString(), true)
            .get();

    // If there's no detect region use the spawn region
    Region detectRegion = spawnRegion;
    if (spawnerNode.attribute("detect-region").isValuePresent()) {
      detectRegion =
          holder
              .getRegistry()
              .get(Region.class, spawnerNode.attribute("detect-region").asRequiredString(), true)
              .get();
    }

    Optional<Filter> spawnFilter = Optional.empty();
    if (spawnerNode.attribute("spawn-filter").isValuePresent()) {
      spawnFilter =
          Optional.of(
              holder
                  .getRegistry()
                  .get(Filter.class, spawnerNode.attribute("spawn-filter").asRequiredString(), true)
                  .get());
    }

    Optional<Vector> velocity =
        BukkitParserRegistry.vectorParser().parse(spawnerNode.attribute("velocity"));

    Optional<SpawnerEntry> item = parseItemsInitial(spawnerNode);
    if (!item.isPresent()) {
      throw new ParsingException(spawnerNode, "There are no child elements saying what to spawn!");
    }

    return Optional.of(
        new SpawnerOptions(
            delay, amount, spawnRegion, detectRegion, item.get(), spawnFilter, velocity));
  }

  private Optional<SpawnerEntry> parseItemsInitial(Node<?> spawner) {
    List<SpawnerEntry> items = parseItemsRoot(spawner);
    if (items.size() == 1) {
      return Optional.of(items.get(0));
    }
    if (items.size() == 0) {
      return Optional.empty();
    }
    return Optional.of(new SpawnerEntryAny(items));
  }

  private List<SpawnerEntry> parseItemsRoot(Node<?> root) {
    return root.children().stream()
        .map(this::parseItemElement)
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }

  private SpawnerEntry parseItemElement(Node<?> element) {
    return NamedParsers.invokeMethod(
        parseMethods, element, "Unknown spawner type.", new Object[] {element});
  }

  @NamedParser("any")
  private SpawnerEntry parseItemsAny(Node<?> any) {
    return new SpawnerEntryAny(parseItemsRoot(any));
  }

  @NamedParser("all")
  private SpawnerEntry parseItemsAll(Node<?> all) {
    return new SpawnerEntryAll(new HashSet<>(parseItemsRoot(all)));
  }

  @NamedParser("item")
  private SpawnerEntry parseItem(Node<?> item) {
    ScopableItemStack itemStack = BukkitParserRegistry.itemParser().parseItemStack(item);
    return new SpawnerItem(itemStack.getItemStack());
  }

  @NamedParser("effect")
  private SpawnerEntry parseEffect(Node<?> effect) {
    PotionEffect potionEffect = BukkitParserRegistry.itemParser().parsePotionEffect(effect);
    return new SpawnerSplashPotion(potionEffect);
  }

  @NamedParser("entity")
  private SpawnerEntry parseEntity(Node<?> entity) {
    EntityType type = BukkitParserRegistry.ofEnum(EntityType.class).parseRequired(entity.text());

    return new SpawnerEntity(type);
  }

  private SpawnerEntry parseTNT(Node<?> effect) {
    int fuse =
        BukkitParserRegistry.durationParser()
            .parse(effect.attribute("fuse"))
            .map(Duration::getSeconds)
            .map(x -> (int) (x * 20))
            .orElse(DEFAULT_TNT_FUSE);

    if (fuse > MAX_TNT_FUSE || fuse < 0) {
      throw new ParsingException(
          effect,
          "Fuse value does not vall into legal range (0 to " + MAX_TNT_FUSE + " inclusive)");
    }

    float power =
        BukkitParserRegistry.floatParser()
            .parse(effect.attribute("power"))
            .orElse(DEFAULT_TNT_POWER);

    // Make sure the power value falls into the legal range
    if (power > 20 || power < 0) {
      throw new ParsingException(
          effect, "Power value does not fall into the legal range (0 to 20 inclusive)");
    }

    return new SpawnItemTNT(power, fuse);
  }
}

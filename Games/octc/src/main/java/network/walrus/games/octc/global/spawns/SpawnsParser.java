package network.walrus.games.octc.global.spawns;

import com.google.common.collect.Lists;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import network.walrus.games.core.GamesPlugin;
import network.walrus.games.core.api.spawns.SpawnRegion;
import network.walrus.games.core.facets.filters.Filter;
import network.walrus.games.core.facets.group.Group;
import network.walrus.games.core.facets.kits.Kit;
import network.walrus.games.octc.Match;
import network.walrus.utils.bukkit.parse.BukkitParserRegistry;
import network.walrus.utils.bukkit.points.AngleProvider;
import network.walrus.utils.bukkit.points.ProviderParsingUtils;
import network.walrus.utils.bukkit.points.StaticAngleProvider;
import network.walrus.utils.bukkit.region.BoundedRegion;
import network.walrus.utils.core.config.Node;
import network.walrus.utils.core.config.ParsingException;
import network.walrus.utils.parsing.facet.facets.region.RegionsFacetParser;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import network.walrus.utils.parsing.facet.parse.DocumentParser;
import network.walrus.utils.parsing.facet.parse.FacetParser;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Parser which parses spawns and respawn options.
 *
 * @author Austin Mayes
 */
public class SpawnsParser implements FacetParser<OCNSpawnManager> {

  @Override
  public Optional<OCNSpawnManager> parse(FacetHolder holder, Node<?> node) throws ParsingException {
    List<OCNSpawn> spawns = Lists.newArrayList();

    RespawnOptions options = new RespawnOptions(Duration.ofSeconds(2), true, false, false);
    Optional<? extends Node<?>> respawnElement = node.child("respawn");
    if (respawnElement.isPresent()) {
      Node respawnFinal = respawnElement.get();
      Duration respawnTime =
          BukkitParserRegistry.durationParser()
              .parse(respawnFinal.attribute("delay"))
              .orElse(Duration.ofSeconds(2));
      boolean spectate =
          BukkitParserRegistry.booleanParser()
              .parse(respawnFinal.attribute("spectate"))
              .orElse(false);
      boolean blind =
          BukkitParserRegistry.booleanParser()
              .parse(respawnFinal.attribute("blackout"))
              .orElse(false);
      boolean auto =
          BukkitParserRegistry.booleanParser().parse(respawnFinal.attribute("auto")).orElse(false);
      options = new RespawnOptions(respawnTime, spectate, blind, auto);
    }

    for (Node<?> c : node.childRequired("spawns").children()) {
      spawns.add(buildSpawn(holder, c));
    }

    return Optional.of(
        new OCNSpawnManager((Match) holder, spawns, GamesPlugin.instance.mapLogger(), options));
  }

  private OCNSpawn buildSpawn(FacetHolder holder, Node node) {
    node.inheritAttributes("spawns");
    Group group = null;
    if (node.hasAttribute("team")) {
      String teamId = node.attribute("team").asRequiredString();
      group = holder.getRegistry().get(Group.class, teamId, true).get();
    }

    if (group == null && node.name().equalsIgnoreCase("default")) {
      group = holder.getRegistry().get(Group.class, "spectators", true).get();
    }

    if (group == null) {
      throw new ParsingException(
          node, "All spawns must either belong to a team or be marked as <default>");
    }

    Optional<Kit> kit = Optional.empty();
    if (node.hasAttribute("kit")) {
      String kitId = node.attribute("kit").asRequiredString();
      kit = holder.getRegistry().get(Kit.class, kitId, true);
    }

    List<SpawnRegion> regions = null;

    if (node.hasAttribute("region")) {
      regions = Lists.newArrayList();
      Pair<Optional<AngleProvider>, Optional<AngleProvider>> yawPitch =
          ProviderParsingUtils.parseYawPitch(node);
      BoundedRegion region =
          DocumentParser.getParser(RegionsFacetParser.class)
              .resolveRequiredRegionAs(
                  BoundedRegion.class,
                  holder.getRegistry(),
                  node.attribute("region"),
                  Optional.empty());
      regions.add(new SpawnRegion(region, yawPitch.getKey(), yawPitch.getValue()));
    } else if (node.hasChild("region") || node.hasChild("regions")) {
      regions = buildRegions(holder, node.children());
    }

    Optional<Filter> filter = Optional.empty();

    if (node.hasAttribute("filter")) {
      filter =
          holder.getRegistry().get(Filter.class, node.attribute("filter").asRequiredString(), true);
    }

    SelectionMode mode =
        BukkitParserRegistry.ofEnum(SelectionMode.class)
            .parse(node.attribute("mode"))
            .orElse(SelectionMode.RANDOM);

    boolean checkAir =
        BukkitParserRegistry.booleanParser().parse(node.attribute("check-air")).orElse(false);

    boolean safe = BukkitParserRegistry.booleanParser().parse(node.attribute("safe")).orElse(false);
    boolean sequential =
        BukkitParserRegistry.booleanParser().parse(node.attribute("sequential")).orElse(false);

    SpawnOptions options;

    if (regions.size() == 1) {
      regions = Collections.singletonList(regions.get(0));
    }

    if (regions.isEmpty()) {
      throw new ParsingException(node, "No spawn regions were defined.");
    }

    Pair<Optional<AngleProvider>, Optional<AngleProvider>> yawPitch =
        ProviderParsingUtils.parseYawPitch(node);

    options =
        new SpawnOptions(
            group,
            kit,
            regions,
            filter,
            yawPitch.getKey().orElse(new StaticAngleProvider(0)),
            yawPitch.getValue().orElse(new StaticAngleProvider(0)),
            mode,
            checkAir,
            safe,
            sequential);

    return new OCNSpawn(options, holder);
  }

  private List<SpawnRegion> buildRegions(FacetHolder holder, List<Node> regionParents) {
    List<SpawnRegion> regions = Lists.newArrayList();
    for (Node<?> parent : regionParents) {
      for (Node child : parent.children()) {
        child.inheritAttributes(parent.name());
        Pair<Optional<AngleProvider>, Optional<AngleProvider>> yawPitch =
            ProviderParsingUtils.parseYawPitch(child);
        BoundedRegion region =
            DocumentParser.getParser(RegionsFacetParser.class)
                .resolveRequiredRegionAs(
                    BoundedRegion.class,
                    holder.getRegistry(),
                    parent.attribute("region"),
                    Optional.of(child));
        regions.add(new SpawnRegion(region, yawPitch.getKey(), yawPitch.getValue()));
      }
    }

    return regions;
  }
}

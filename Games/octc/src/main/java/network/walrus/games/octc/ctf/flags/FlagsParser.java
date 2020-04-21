package network.walrus.games.octc.ctf.flags;

import com.google.common.collect.Lists;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import network.walrus.games.core.facets.kits.Kit;
import network.walrus.games.core.round.GameRound;
import network.walrus.games.octc.OCNMessages;
import network.walrus.games.octc.global.groups.teams.Team;
import network.walrus.utils.bukkit.distance.DistanceCalculationMetric;
import network.walrus.utils.bukkit.parse.BukkitParserRegistry;
import network.walrus.utils.bukkit.region.BoundedRegion;
import network.walrus.utils.bukkit.region.shapes.BlockRegion;
import network.walrus.utils.bukkit.translation.LocalizedConfigurationProperty;
import network.walrus.utils.core.config.Node;
import network.walrus.utils.core.config.ParsingException;
import network.walrus.utils.core.parse.simple.EnumParser;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import network.walrus.utils.parsing.facet.parse.FacetParser;
import org.bukkit.DyeColor;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;

/**
 * Parser which parses the {@link FlagsFacet}.
 *
 * @author Austin Mayes
 */
public class FlagsParser implements FacetParser<FlagsFacet> {

  @Override
  public Optional<FlagsFacet> parse(FacetHolder holder, Node<?> node) throws ParsingException {
    List<FlagObjective> flags = Lists.newArrayList();
    List<Post> posts = Lists.newArrayList();
    List<Net> nets = Lists.newArrayList();

    for (Node<?> parent : node.children("flags")) {
      for (Node<?> child : parent.children("flag")) {
        child.inheritAttributes("flags");

        List<Post> postsForFlag = Lists.newArrayList();

        Optional<Team> owner = Optional.empty();
        if (child.hasAttribute("owner")) {
          owner =
              holder
                  .getRegistry()
                  .get(Team.class, child.attribute("owner").asRequiredString(), true);
        }

        DyeColor color =
            BukkitParserRegistry.ofEnum(DyeColor.class).parseRequired(child.attribute("color"));
        LocalizedConfigurationProperty name =
            BukkitParserRegistry.localizedPropertyParser()
                .parse(child.attribute("name"))
                .orElse(new LocalizedConfigurationProperty(OCNMessages.forFlagColor(color)));
        int carryingPoints =
            BukkitParserRegistry.integerParser()
                .parse(child.attribute("carrying-points"))
                .orElse(0);

        FlagDistanceMetrics metrics =
            new FlagDistanceMetrics.Builder()
                .carry(
                    child,
                    "net",
                    new DistanceCalculationMetric(DistanceCalculationMetric.Type.PLAYER, true))
                .preComplete(
                    child,
                    "flag",
                    new DistanceCalculationMetric(DistanceCalculationMetric.Type.PLAYER, true))
                .build();

        for (Node<?> postNode : child.childRequired("posts").children("post")) {
          postNode.inheritAttributes("posts");
          postNode.inheritAttributes("flag");
          BlockRegion region =
              holder
                  .getRegistry()
                  .get(BlockRegion.class, postNode.attribute("region").asRequiredString(), true)
                  .get();
          float yaw = BukkitParserRegistry.floatParser().parseRequired(postNode.attribute("yaw"));
          postsForFlag.add(new Post(region, yaw));
        }

        Duration recoverTime =
            BukkitParserRegistry.durationParser()
                .parse(child.attribute("recover-time"))
                .orElse(Duration.ofSeconds(12));
        Duration respawnTime =
            BukkitParserRegistry.durationParser()
                .parse(child.attribute("respawn-time"))
                .orElse(Duration.ofSeconds(8));
        int pointsNeeded =
            BukkitParserRegistry.baseNumberParser()
                .parse(child.attribute("points"))
                .map(Number::intValue)
                .orElse(1);
        boolean sequential =
            BukkitParserRegistry.booleanParser().parse(child.attribute("sequential")).orElse(false);

        List<Pattern> flagPatterns;
        if (!child.children("pattern").isEmpty()) {
          List<? extends Node<?>> patternChildren = child.children("pattern");
          flagPatterns = Lists.newArrayList();
          EnumParser<DyeColor> colorParser = BukkitParserRegistry.ofEnum(DyeColor.class);
          EnumParser<PatternType> patternParser = BukkitParserRegistry.ofEnum(PatternType.class);

          for (Node<?> patternNode : patternChildren) {
            DyeColor patternColor =
                colorParser.parse(patternNode.attribute("color")).orElse(DyeColor.WHITE);
            PatternType patternType = patternParser.parseRequired(patternNode.text());
          }
        } else {
          PatternType flagPattern =
              BukkitParserRegistry.ofEnum(PatternType.class)
                  .parse(child.attribute("pattern"))
                  .orElse(PatternType.FLOWER);

          flagPatterns = Collections.singletonList(new Pattern(DyeColor.WHITE, flagPattern));
        }

        Optional<Kit> kit =
            child
                .attribute("carrying-kit")
                .value()
                .flatMap(id -> holder.getRegistry().get(Kit.class, id, false));

        flags.add(
            new FlagObjective(
                metrics,
                holder,
                postsForFlag,
                owner,
                color,
                name,
                recoverTime,
                respawnTime,
                carryingPoints,
                pointsNeeded,
                sequential,
                flagPatterns,
                kit));
        posts.addAll(postsForFlag);
      }
    }

    for (Node<?> parent : node.children("nets")) {
      for (Node<?> child : parent.children("net")) {
        child.inheritAttributes("nets");
        Optional<Team> owner = Optional.empty();
        if (child.hasAttribute("owner")) {
          owner =
              holder
                  .getRegistry()
                  .get(Team.class, child.attribute("owner").asRequiredString(), true);
        }

        BoundedRegion region =
            holder
                .getRegistry()
                .get(BoundedRegion.class, child.attribute("region").asRequiredString(), true)
                .get();
        int reward =
            BukkitParserRegistry.integerParser().parse(child.attribute("points")).orElse(0);
        nets.add(new Net(region, owner, reward));
      }
    }

    return Optional.of(new FlagsFacet((GameRound) holder, flags, posts, nets));
  }

  @Override
  public boolean required() {
    // Required since only active when the map is set to CTF
    return true;
  }
}

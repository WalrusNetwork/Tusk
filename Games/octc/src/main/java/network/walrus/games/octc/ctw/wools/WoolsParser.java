package network.walrus.games.octc.ctw.wools;

import com.google.common.collect.Lists;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import network.walrus.games.core.facets.objectives.touchable.TouchableDistanceMetrics;
import network.walrus.games.core.round.GameRound;
import network.walrus.games.octc.OCNGameManager;
import network.walrus.games.octc.global.groups.teams.Team;
import network.walrus.utils.bukkit.distance.DistanceCalculationMetric;
import network.walrus.utils.bukkit.parse.BukkitParserRegistry;
import network.walrus.utils.bukkit.region.BoundedRegion;
import network.walrus.utils.core.config.Node;
import network.walrus.utils.core.config.ParsingException;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import network.walrus.utils.parsing.facet.parse.FacetParser;
import org.bukkit.DyeColor;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Parser which parses {@link WoolObjective}s and creates a {@link WoolsFacet}.
 *
 * @author Austin Mayes
 */
public class WoolsParser implements FacetParser<WoolsFacet> {

  @Override
  public boolean required() {
    // Required since only active when the map is set to CTW
    return true;
  }

  @Override
  public Optional<WoolsFacet> parse(FacetHolder holder, Node<?> node) throws ParsingException {
    List<WoolObjective> wools = Lists.newArrayList();
    ConfigurationSection ctwSection =
        OCNGameManager.instance.getConfig().getConfigurationSection("ctw");
    int startingWools = ctwSection.getInt("start-wools");
    int woolsPerKill = ctwSection.getInt("wools-per-kill");

    for (Node<?> parent : node.children("wools")) {
      for (Node<?> child : parent.children("wool")) {
        child.inheritAttributes("wools");

        // Owner
        Optional<Team> team = Optional.empty();
        if (child.hasAttribute("team")) {
          team =
              holder
                  .getRegistry()
                  .get(Team.class, child.attribute("team").asRequiredString(), true);
        }

        // color
        DyeColor color =
            BukkitParserRegistry.ofEnum(DyeColor.class).parseRequired(child.attribute("color"));

        // source
        Optional<BoundedRegion> source = Optional.empty();
        if (child.hasAttribute("source")) {
          source =
              holder
                  .getRegistry()
                  .get(BoundedRegion.class, child.attribute("source").asRequiredString(), true);
        }

        // destination
        BoundedRegion destination =
            holder
                .getRegistry()
                .get(BoundedRegion.class, child.attribute("destination").asRequiredString(), true)
                .get();

        // refill
        boolean refill =
            BukkitParserRegistry.booleanParser().parse(child.attribute("refill")).orElse(true);

        int maxRefill =
            BukkitParserRegistry.integerParser()
                .parse(child.attribute("max-refill"))
                .orElse(Integer.MAX_VALUE);

        Optional<Duration> refillDelay =
            BukkitParserRegistry.durationParser().parse(child.attribute("refill-delay"));

        // craftable
        boolean craftable =
            BukkitParserRegistry.booleanParser().parse(child.attribute("craftable")).orElse(false);

        // fireworks
        boolean fireworks =
            BukkitParserRegistry.booleanParser().parse(child.attribute("fireworks")).orElse(true);

        TouchableDistanceMetrics metrics =
            new TouchableDistanceMetrics.Builder()
                .postTouch(
                    child,
                    "dest",
                    new DistanceCalculationMetric(DistanceCalculationMetric.Type.PLAYER, true))
                .preComplete(
                    child,
                    "source",
                    new DistanceCalculationMetric(DistanceCalculationMetric.Type.PLAYER, true))
                .build();

        WoolObjective objective =
            new WoolObjective(
                (GameRound) holder,
                metrics,
                team,
                color,
                source,
                destination,
                refill,
                maxRefill,
                refillDelay,
                craftable,
                fireworks,
                startingWools,
                woolsPerKill);
        wools.add(objective);
      }
    }

    if (wools.isEmpty()) {
      return Optional.empty();
    }

    return Optional.of(new WoolsFacet(holder, wools));
  }
}

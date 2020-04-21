package network.walrus.games.octc.tdm.overtime;

import java.time.Duration;
import java.util.Optional;
import network.walrus.ubiquitous.bukkit.border.CuboidBorder;
import network.walrus.ubiquitous.bukkit.border.CylinderBorder;
import network.walrus.ubiquitous.bukkit.border.IWorldBorder;
import network.walrus.utils.bukkit.parse.BukkitParserRegistry;
import network.walrus.utils.bukkit.region.BoundedRegion;
import network.walrus.utils.bukkit.region.shapes.CuboidRegion;
import network.walrus.utils.bukkit.region.shapes.CylinderRegion;
import network.walrus.utils.core.config.Node;
import network.walrus.utils.core.config.ParsingException;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import network.walrus.utils.parsing.facet.parse.FacetParser;
import org.bukkit.Material;

/**
 * Parser for the {@link BlitzOvertimeFacet}
 *
 * @author David Rodriguez
 */
public class BlitzOvertimeParser implements FacetParser<BlitzOvertimeFacet> {

  @Override
  public Optional<BlitzOvertimeFacet> parse(FacetHolder holder, Node node) throws ParsingException {
    if (!node.hasChild("overtime")) return Optional.empty();

    Node overtimeNode = node.childRequired("overtime");
    boolean teleportToSpawn = true;
    Optional<BoundedRegion> maxRegion;
    Optional<BoundedRegion> minRegion;
    Optional<IWorldBorder> worldBorder;
    Optional<Duration> duration =
        Optional.ofNullable(
            BukkitParserRegistry.durationParser()
                .parseRequired(node.childRequired("time-limit").text()));

    // Duration is 15% of match by default
    duration = duration.map(found -> Duration.ofSeconds((long) (found.getSeconds() * 0.15)));

    if (overtimeNode.hasChild("duration")) {
      duration =
          Optional.of(
              BukkitParserRegistry.durationParser()
                  .parseRequired(overtimeNode.childRequired("duration").text()));
    }

    if (overtimeNode.hasChild("teleport-to-spawn")) {
      teleportToSpawn =
          BukkitParserRegistry.booleanParser()
              .parseRequired(overtimeNode.childRequired("teleport-to-spawn").text());
    }

    // Parse border
    Node borderNode = overtimeNode.childRequired("border");
    minRegion =
        holder
            .getRegistry()
            .get(BoundedRegion.class, borderNode.attribute("min").asRequiredString(), true);

    maxRegion =
        holder
            .getRegistry()
            .get(BoundedRegion.class, borderNode.attribute("max").asRequiredString(), true);

    worldBorder = Optional.of(parseBorder(minRegion.get(), maxRegion.get()));

    return Optional.of(
        new BlitzOvertimeFacet(
            holder,
            teleportToSpawn,
            duration.get(),
            maxRegion.get(),
            minRegion.get(),
            worldBorder.get()));
  }

  private IWorldBorder parseBorder(BoundedRegion min, BoundedRegion max) {
    Material material = Material.AIR;
    if (min instanceof CylinderRegion && max instanceof CylinderRegion) {
      CylinderRegion maxRegion = (CylinderRegion) max;
      CylinderRegion minRegion = (CylinderRegion) min;
      return new CylinderBorder(
          material, true, minRegion.base(), maxRegion.radius(), maxRegion.height());
    } else if (min instanceof CuboidRegion && max instanceof CuboidRegion) {
      return new CuboidBorder(material, true, (CuboidRegion) min, (CuboidRegion) max);
    } else {
      throw new ParsingException("Border must be a cylinder or a cuboid");
    }
  }
}

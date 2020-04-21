package network.walrus.ubiquitous.bukkit.lobby.facets.parkour;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import network.walrus.ubiquitous.bukkit.lobby.facets.parkour.Parkour.RespawnPolicy;
import network.walrus.utils.bukkit.parse.BukkitParserRegistry;
import network.walrus.utils.bukkit.region.BoundedRegion;
import network.walrus.utils.bukkit.translation.LocalizedConfigurationProperty;
import network.walrus.utils.core.config.Node;
import network.walrus.utils.core.config.ParsingException;
import network.walrus.utils.core.parse.simple.EnumParser;
import network.walrus.utils.core.registry.Registry;
import network.walrus.utils.parsing.facet.facets.region.RegionsFacetParser;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import network.walrus.utils.parsing.facet.parse.DocumentParser;
import network.walrus.utils.parsing.facet.parse.FacetParser;
import org.bukkit.potion.PotionEffectType;

/**
 * Parser for {@link Parkour}s and {@link ParkourStage}s.
 *
 * @author Austin Mayes
 */
public class ParkourParser implements FacetParser<ParkourManager> {

  @Override
  public Optional<ParkourManager> parse(FacetHolder holder, Node<?> node) throws ParsingException {
    List<? extends Node<?>> parkourNodes = node.children("parkours");
    if (parkourNodes.isEmpty()) return Optional.empty();

    Set<Parkour> parkours = Sets.newHashSet();
    EnumParser<RespawnPolicy> policyParser = BukkitParserRegistry.ofEnum(RespawnPolicy.class);
    for (Node<?> parkorNode : parkourNodes) {
      List<ParkourStage> stages = Lists.newArrayList();
      for (Node<?> stage : parkorNode.children("stage")) {
        stages.add(parseStage(stage, holder.getRegistry()));
      }
      String id = parkorNode.attribute("id").asRequiredString();
      LocalizedConfigurationProperty name =
          BukkitParserRegistry.localizedPropertyParser()
              .parseRequired(parkorNode.attribute("name"));
      boolean multipleCompletions =
          BukkitParserRegistry.booleanParser()
              .parse(parkorNode.attribute("multiple-completions"))
              .orElse(false);
      RespawnPolicy policy =
          policyParser
              .parse(parkorNode.attribute("respawn-policy"))
              .orElse(RespawnPolicy.LAST_STAGE);
      parkours.add(new Parkour(id, name, stages, multipleCompletions, policy));
    }
    return Optional.of(new ParkourManager(parkours));
  }

  private ParkourStage parseStage(Node node, Registry registry) throws ParsingException {
    String id = node.attribute("id").asRequiredString();
    LocalizedConfigurationProperty name =
        BukkitParserRegistry.localizedPropertyParser().parseRequired(node.attribute("name"));
    RegionsFacetParser regionsParser = DocumentParser.getParser(RegionsFacetParser.class);
    BoundedRegion bounds =
        regionsParser.resolveRequiredRegionAs(
            BoundedRegion.class, registry, node.attribute("bounds"), node.child("bounds"));
    BoundedRegion start =
        regionsParser.resolveRequiredRegionAs(
            BoundedRegion.class, registry, node.attribute("start"), node.child("start"));
    BoundedRegion end =
        regionsParser.resolveRequiredRegionAs(
            BoundedRegion.class, registry, node.attribute("end"), node.child("end"));
    Set<PotionEffectType> effects = Sets.newHashSet();
    effects.addAll(
        BukkitParserRegistry.listParser().parse(node.attribute("effects"))
            .orElse(Lists.newArrayList()).stream()
            .map(
                (h) -> {
                  PotionEffectType type =
                      PotionEffectType.getByName(
                          h.asRequiredString().toUpperCase().replace(' ', '_'));
                  if (type == null) {
                    throw new ParsingException(node, "Effect not found.");
                  }
                  return type;
                })
            .collect(Collectors.toList()));
    double completionReward =
        BukkitParserRegistry.doubleParser().parse(node.attribute("completion-reward")).orElse(0.0);
    return new ParkourStage(id, name, bounds, start, end, effects, completionReward);
  }
}

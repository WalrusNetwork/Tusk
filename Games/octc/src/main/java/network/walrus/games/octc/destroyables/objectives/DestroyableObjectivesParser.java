package network.walrus.games.octc.destroyables.objectives;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import network.walrus.games.core.round.GameRound;
import network.walrus.games.octc.destroyables.DestroyableUtils;
import network.walrus.games.octc.destroyables.objectives.cores.CoreObjective;
import network.walrus.games.octc.destroyables.objectives.monuments.MonumentObjective;
import network.walrus.utils.bukkit.parse.BukkitParserRegistry;
import network.walrus.utils.core.config.Node;
import network.walrus.utils.core.config.ParsingException;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import network.walrus.utils.parsing.facet.parse.FacetParser;

/**
 * Parser which parses {@link DestroyableObjective}s and creates a {@link DestroyablesFacet}.
 *
 * @author ShinyDialga & Austin Mayes
 */
public class DestroyableObjectivesParser implements FacetParser<DestroyablesFacet> {

  @Override
  public boolean required() {
    // Required since only active when the map is set to DTC/M
    return true;
  }

  @Override
  public Optional<DestroyablesFacet> parse(FacetHolder holder, Node<?> node)
      throws ParsingException {
    List<DestroyableObjective> objectives = Lists.newArrayList();

    if (node.hasChild("modes")) {
      // Copy children for safety reasons.
      List<Node> childrenCopy = new ArrayList<>();
      for (Node<?> e : node.children("modes")) {
        childrenCopy.addAll(e.children());
      }
      /*
         We reverse the children so that the XML can be written in logical order but loaded in reverse so
         that earlier modes can reference later modes.
      */
      Collections.reverse(childrenCopy);
      for (Node m : childrenCopy) {
        holder.getRegistry().add(DestroyableUtils.parseMode((GameRound) holder, m));
      }
    }

    for (Node<?> parent : node.children("monuments")) {
      for (Node<?> child : parent.children("monument")) {
        child.inheritAttributes("monuments");

        DestroyableProperties properties = DestroyableUtils.parseProperties(child, holder);

        MonumentObjective objective = new MonumentObjective((GameRound) holder, properties);
        objectives.add(objective);
      }
    }

    for (Node<?> parent : node.children("cores")) {
      for (Node<?> child : parent.children("core")) {
        child.inheritAttributes("cores");

        DestroyableProperties properties = DestroyableUtils.parseProperties(child, holder);

        // leak level
        int leak = BukkitParserRegistry.integerParser().parse(child.attribute("leak")).orElse(5);

        CoreObjective objective = new CoreObjective((GameRound) holder, properties, leak);
        objectives.add(objective);
      }
    }

    if (objectives.isEmpty()) {
      return Optional.empty();
    }

    return Optional.of(new DestroyablesFacet(holder, objectives));
  }
}

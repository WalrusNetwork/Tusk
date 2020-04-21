package network.walrus.games.core.facets.death;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import network.walrus.games.core.facets.filters.Filter;
import network.walrus.games.core.facets.kits.Kit;
import network.walrus.games.core.facets.kits.KitsParser;
import network.walrus.utils.bukkit.parse.BukkitParserRegistry;
import network.walrus.utils.core.config.Node;
import network.walrus.utils.core.config.ParsingException;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import network.walrus.utils.parsing.facet.parse.DocumentParser;
import network.walrus.utils.parsing.facet.parse.FacetParser;

/**
 * Parser which parses {@link KillReward}s and supplied them to the {@link DeathsFacet}.
 *
 * @author Austin Mayes
 */
public class DeathsParser implements FacetParser<DeathsFacet> {

  @Override
  public Optional<DeathsFacet> parse(FacetHolder holder, Node<?> node) throws ParsingException {
    List<KillReward> rewards = Lists.newArrayList();

    for (Node<?> root : node.children("kill-rewards")) {
      for (Node<?> child : root.children()) {
        Optional<Filter> rewardFilter = Optional.empty();
        if (child.hasAttribute("filter")) {
          rewardFilter =
              holder
                  .getRegistry()
                  .get(Filter.class, child.attribute("filter").asRequiredString(), true);
        }

        Kit kit;
        if (child.hasAttribute("kit")) {
          kit =
              holder
                  .getRegistry()
                  .get(Kit.class, child.attribute("kit").asRequiredString(), true)
                  .get();
        } else if (!child.children().isEmpty()) {
          kit = DocumentParser.getParser(KitsParser.class).parseKit(holder, child);
        } else {
          throw new ParsingException(
              child, "Rewards must either have a sub-kit or reference a kit by ID.");
        }
        Optional<Integer> afterKills =
            BukkitParserRegistry.integerParser().parse(child.attribute("after"));
        Optional<Integer> everyKills =
            BukkitParserRegistry.integerParser().parse(child.attribute("every"));

        rewards.add(new KillReward(holder, kit, rewardFilter, afterKills, everyKills));
      }
    }

    return Optional.of(new DeathsFacet(holder, rewards));
  }
}

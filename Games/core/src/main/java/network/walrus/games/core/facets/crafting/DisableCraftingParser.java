package network.walrus.games.core.facets.crafting;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import network.walrus.utils.bukkit.inventory.MultiMaterialMatcher;
import network.walrus.utils.bukkit.inventory.SingleMaterialMatcher;
import network.walrus.utils.bukkit.parse.BukkitParserRegistry;
import network.walrus.utils.core.config.Node;
import network.walrus.utils.core.config.ParsingException;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import network.walrus.utils.parsing.facet.parse.FacetParser;

/**
 * Parses the `<disable-crafting>` tag
 *
 * @author Wesley Smith
 */
public class DisableCraftingParser implements FacetParser<DisableCraftingFacet> {

  @Override
  public Optional<DisableCraftingFacet> parse(FacetHolder holder, Node root)
      throws ParsingException {
    Optional<Node> parent = root.child("disable-crafting");

    if (parent.isPresent()) {
      List<SingleMaterialMatcher> materials = Lists.newArrayList();
      for (Node node : (List<Node>) parent.get().children("recipe")) {
        if (node.hasText()) {
          materials.add(
              BukkitParserRegistry.singleMaterialMatcherParser().parseRequired(node.text()));
        } else {
          throw new ParsingException(node, "<recipe> tag cannot have children");
        }
      }
      return Optional.of(new DisableCraftingFacet(holder, new MultiMaterialMatcher(materials)));
    } else {
      return Optional.empty();
    }
  }
}

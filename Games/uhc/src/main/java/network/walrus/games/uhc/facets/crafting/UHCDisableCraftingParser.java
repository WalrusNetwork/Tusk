package network.walrus.games.uhc.facets.crafting;

import java.util.Optional;
import network.walrus.games.core.facets.crafting.DisableCraftingFacet;
import network.walrus.games.core.facets.crafting.DisableCraftingParser;
import network.walrus.games.uhc.UHCManager;
import network.walrus.utils.bukkit.inventory.MaterialMatcher;
import network.walrus.utils.bukkit.inventory.SingleMaterialMatcher;
import network.walrus.utils.core.config.Node;
import network.walrus.utils.core.config.ParsingException;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import org.bukkit.Material;

/**
 * Parser for {@link DisableCraftingFacet} which disables god apples when necessary.
 *
 * @author Rafi Baum
 */
public class UHCDisableCraftingParser extends DisableCraftingParser {

  private static MaterialMatcher uhcDisabled =
      new SingleMaterialMatcher(Material.GOLDEN_APPLE, (byte) 1);

  @Override
  public Optional<DisableCraftingFacet> parse(FacetHolder holder, Node root)
      throws ParsingException {
    Optional<DisableCraftingFacet> disableFacet = super.parse(holder, root);
    if (UHCManager.instance.getConfig().godApples.get()) {
      return disableFacet;
    }

    // Should disable god apples
    if (disableFacet.isPresent()) {
      disableFacet.get().disableItem(uhcDisabled);
    } else {
      disableFacet = Optional.of(new DisableCraftingFacet(holder, uhcDisabled));
    }

    return disableFacet;
  }
}

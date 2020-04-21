package network.walrus.games.core.facets.filters.types;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import network.walrus.games.core.facets.filters.Filter;
import network.walrus.games.core.facets.filters.FilterResult;
import network.walrus.games.core.facets.filters.context.FilterContext;
import network.walrus.games.core.facets.filters.variable.PlayerVariable;
import network.walrus.utils.bukkit.inventory.ScopableItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * A holding filter checks the type of item a player is holding in their hand.
 *
 * @author Avicus Network
 */
public class HoldingFilter implements Filter {

  private final ScopableItemStack itemStack;

  /**
   * Constructor.
   *
   * @param itemStack to check against
   */
  public HoldingFilter(ScopableItemStack itemStack) {
    this.itemStack = itemStack;
  }

  @Override
  public FilterResult test(FilterContext context, boolean describe) {
    Optional<PlayerVariable> optional = context.getFirst(PlayerVariable.class);

    if (!optional.isPresent()) {
      return FilterResult.IGNORE;
    }

    Player player = optional.get().getPlayer();

    List<ItemStack> contents = new ArrayList<>();
    contents.add(player.getInventory().getItemInHand());

    for (ItemStack test : contents) {
      boolean matches = this.itemStack.equals(player, test);
      if (matches) {
        return FilterResult.ALLOW;
      }
    }

    return FilterResult.DENY;
  }

  @Override
  public String describe() {
    return "holding " + itemStack.getBaseItemStack().getType().name();
  }
}

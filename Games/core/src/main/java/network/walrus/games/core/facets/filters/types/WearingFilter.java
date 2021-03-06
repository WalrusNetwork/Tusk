package network.walrus.games.core.facets.filters.types;

import java.util.Optional;
import network.walrus.games.core.facets.filters.Filter;
import network.walrus.games.core.facets.filters.FilterResult;
import network.walrus.games.core.facets.filters.context.FilterContext;
import network.walrus.games.core.facets.filters.variable.PlayerVariable;
import network.walrus.utils.bukkit.inventory.ScopableItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * A wearing filter checks the type of item a player is wearing in any of their armor slots.
 *
 * @author Avicus Network
 */
public class WearingFilter implements Filter {

  private final ScopableItemStack itemStack;

  /**
   * Constructor.
   *
   * @param itemStack to check that the player is wearing
   */
  public WearingFilter(ScopableItemStack itemStack) {
    this.itemStack = itemStack;
  }

  @Override
  public FilterResult test(FilterContext context, boolean describe) {
    Optional<PlayerVariable> optional = context.getFirst(PlayerVariable.class);

    if (!optional.isPresent()) {
      return FilterResult.IGNORE;
    }

    Player player = optional.get().getPlayer();
    ItemStack[] contents = player.getInventory().getArmorContents();

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
    return "wearing " + itemStack.getBaseItemStack().getType().name();
  }
}

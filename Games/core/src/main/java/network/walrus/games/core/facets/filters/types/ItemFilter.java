package network.walrus.games.core.facets.filters.types;

import java.util.Optional;
import network.walrus.games.core.facets.filters.Filter;
import network.walrus.games.core.facets.filters.FilterResult;
import network.walrus.games.core.facets.filters.context.FilterContext;
import network.walrus.games.core.facets.filters.variable.ItemVariable;
import network.walrus.utils.bukkit.inventory.ScopableItemStack;

/**
 * An item filter checks the type of item involved in the event.
 *
 * @author Avicus Network
 */
public class ItemFilter implements Filter {

  private final ScopableItemStack itemStack;

  /**
   * Constructor.
   *
   * @param itemStack to check against
   */
  public ItemFilter(ScopableItemStack itemStack) {
    this.itemStack = itemStack;
  }

  @Override
  public FilterResult test(FilterContext context, boolean describe) {
    Optional<ItemVariable> optional = context.getFirst(ItemVariable.class);

    if (!optional.isPresent()) {
      return FilterResult.IGNORE;
    }

    ScopableItemStack stack = optional.get().getItemStack();
    return FilterResult.valueOf(this.itemStack.equals(stack));
  }

  @Override
  public String describe() {
    return "item is " + itemStack.getBaseItemStack().getType().name();
  }
}

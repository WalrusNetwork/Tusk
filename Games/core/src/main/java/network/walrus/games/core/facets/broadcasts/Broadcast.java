package network.walrus.games.core.facets.broadcasts;

import java.util.Optional;
import network.walrus.games.core.GamesCoreMessages;
import network.walrus.games.core.facets.filters.Filter;
import network.walrus.games.core.facets.filters.context.FilterContext;
import network.walrus.games.core.util.GameTask;
import network.walrus.utils.bukkit.translation.LocalizedConfigurationProperty;
import network.walrus.utils.core.text.LocalizedFormat;
import network.walrus.utils.parsing.facet.holder.FacetHolder;

enum Type {
  ALERT(GamesCoreMessages.UI_ALERT),
  TIP(GamesCoreMessages.UI_TIP);
  final LocalizedFormat prefix;

  Type(LocalizedFormat prefix) {
    this.prefix = prefix;
  }
}

/**
 * A customisable message that is sent out after a delay on a regular interval.
 *
 * @author Rafi Baum
 */
public class Broadcast extends GameTask {

  private final FacetHolder holder;
  private final Type type;
  private final LocalizedConfigurationProperty message;
  private final int after;
  private final Optional<Integer> every;
  private final Optional<Integer> count;
  private final Optional<Filter> filter;
  private int times;

  /**
   * @param holder that the facet is in
   * @param type of broadcast
   * @param message to be printed
   * @param after delay before first print (in ticks)
   * @param every interval between first and subsequent prints (in ticks)
   * @param count number of prints to perform
   * @param filter whether message should be broadcast or not
   */
  Broadcast(
      FacetHolder holder,
      Type type,
      LocalizedConfigurationProperty message,
      int after,
      Optional<Integer> every,
      Optional<Integer> count,
      Optional<Filter> filter) {
    super("Broadcast");
    this.holder = holder;
    this.type = type;
    this.message = message;
    this.after = after;
    this.every = every;
    this.count = count;
    this.filter = filter;

    count.ifPresent(c -> times = c);
  }

  @Override
  public void run() {
    if (filter.isPresent()) {
      FilterContext context = new FilterContext();
      if (filter.get().test(context).fails()) {
        return;
      }
    }

    LocalizedFormat broadcast = type.prefix;
    holder.getContainer().broadcast(broadcast.with(message.toText()));

    if (count.isPresent() && --times <= 0) {
      reset();
    }
  }

  void schedule() {
    if (every.isPresent()) {
      this.repeat(after, every.get());
    } else {
      this.later(after);
    }
  }
}

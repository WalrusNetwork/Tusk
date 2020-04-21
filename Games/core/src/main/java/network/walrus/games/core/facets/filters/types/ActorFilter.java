package network.walrus.games.core.facets.filters.types;

import java.util.Optional;
import network.walrus.games.core.facets.filters.Actor;
import network.walrus.games.core.facets.filters.Filter;
import network.walrus.games.core.facets.filters.FilterResult;
import network.walrus.games.core.facets.filters.context.FilterContext;
import network.walrus.games.core.facets.filters.variable.EntityVariable;
import network.walrus.games.core.facets.filters.variable.PlayerVariable;

/**
 * Used to filter for what is causing an action.
 *
 * @author Rafi Baum
 */
public class ActorFilter implements Filter {

  private final Actor actor;

  public ActorFilter(Actor actor) {
    this.actor = actor;
  }

  @Override
  public FilterResult test(FilterContext context, boolean describe) {
    Optional<EntityVariable> entity = context.getFirst(EntityVariable.class);
    Optional<PlayerVariable> player = context.getFirst(PlayerVariable.class);

    switch (actor) {
      case PLAYER:
        return FilterResult.valueOf(player.isPresent());
      case LIVING:
        return FilterResult.valueOf(player.isPresent() || entity.isPresent());
      case WORLD:
        return FilterResult.valueOf(!player.isPresent() && !entity.isPresent());
      case MOB:
        return FilterResult.valueOf(entity.isPresent());
    }

    return FilterResult.IGNORE;
  }

  @Override
  public String describe() {
    return "caused by " + actor.name();
  }
}

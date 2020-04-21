package network.walrus.games.core.facets.filters.types;

import java.util.Optional;
import network.walrus.games.core.facets.filters.Filter;
import network.walrus.games.core.facets.filters.FilterResult;
import network.walrus.games.core.facets.filters.context.FilterContext;
import network.walrus.games.core.facets.filters.variable.GroupVariable;
import network.walrus.games.core.facets.filters.variable.PlayerVariable;
import network.walrus.games.core.facets.group.Group;
import network.walrus.games.core.facets.group.GroupsManager;
import network.walrus.utils.core.registry.WeakReference;
import network.walrus.utils.parsing.facet.holder.FacetHolder;

/**
 * A group filter checks what group a player is in.
 *
 * @author Avicus Network
 */
public class GroupFilter implements Filter {

  private final FacetHolder holder;
  private final WeakReference<Group> groupRef;

  /**
   * Constructor.
   *
   * @param holder which holds this filter
   * @param groupRef reference to the group which should be checked
   */
  public GroupFilter(FacetHolder holder, WeakReference<Group> groupRef) {
    this.holder = holder;
    this.groupRef = groupRef;
  }

  @Override
  public FilterResult test(FilterContext context, boolean describe) {
    Optional<Group> optional = this.groupRef.getObject();

    if (optional.isPresent()) {
      Optional<GroupVariable> groupVar = context.getFirst(GroupVariable.class);
      Optional<PlayerVariable> playerVar = context.getFirst(PlayerVariable.class);

      if (groupVar.isPresent() || playerVar.isPresent()) {
        if (groupVar.isPresent()) {
          return FilterResult.valueOf(optional.get().equals(groupVar.get().getGroup()));
        } else {
          Group group =
              this.holder
                  .getFacetRequired(GroupsManager.class)
                  .getGroup(playerVar.get().getPlayer());
          return FilterResult.valueOf(optional.get().equals(group));
        }
      }
    }

    return FilterResult.IGNORE;
  }

  @Override
  public String describe() {
    return "group is " + groupRef.getObject().get().id();
  }
}

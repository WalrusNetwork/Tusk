package network.walrus.games.core.facets.filters.types;

import java.util.Optional;
import network.walrus.games.core.facets.filters.Filter;
import network.walrus.games.core.facets.filters.FilterResult;
import network.walrus.games.core.facets.filters.context.FilterContext;
import network.walrus.games.core.facets.filters.variable.GroupVariable;
import network.walrus.games.core.facets.filters.variable.PlayerVariable;
import network.walrus.games.core.facets.group.Competitor;
import network.walrus.games.core.facets.group.Group;
import network.walrus.games.core.facets.group.GroupsManager;
import network.walrus.games.core.facets.objectives.Objective;
import network.walrus.utils.parsing.facet.holder.FacetHolder;

/**
 * Filter to check if a group has completed a certain objective.
 *
 * @author Rafi Baum
 */
public class CompletionFilter implements Filter {

  private final FacetHolder holder;
  private final String objectiveId;
  private Optional<Objective> objective;

  private GroupsManager groupsManager;

  /**
   * Constructor.
   *
   * @param holder facet holder
   * @param objectiveId id of objective to check for completion
   */
  public CompletionFilter(FacetHolder holder, String objectiveId) {
    this.holder = holder;
    this.objectiveId = objectiveId;
    this.objective = Optional.empty();
  }

  @Override
  public FilterResult test(FilterContext context, boolean describe) {
    fetchObjective();

    Optional<Group> optionalGroup = getGroup(holder, context);
    if (!optionalGroup.isPresent() || !(optionalGroup.get() instanceof Competitor)) {
      return FilterResult.IGNORE;
    }

    if (objective.get().getCompletion((Competitor) optionalGroup.get()) == 1.0) {
      return FilterResult.ALLOW;
    } else {
      return FilterResult.DENY;
    }
  }

  @Override
  public String describe() {
    fetchObjective();
    return "completed " + objective.get().getName().translateDefault();
  }

  private Optional<Group> getGroup(FacetHolder facetHolder, FilterContext context) {
    Optional<GroupVariable> groupVar = context.getFirst(GroupVariable.class);
    Optional<PlayerVariable> playerVar = context.getFirst(PlayerVariable.class);

    if (groupVar.isPresent() || playerVar.isPresent()) {
      if (groupVar.isPresent()) {
        return Optional.of(groupVar.get().getGroup());
      } else {
        if (groupsManager == null) {
          groupsManager = facetHolder.getFacetRequired(GroupsManager.class);
        }
        Group group = groupsManager.getGroup(playerVar.get().getPlayer());
        return Optional.of(group);
      }
    }

    return Optional.empty();
  }

  private void fetchObjective() {
    if (!objective.isPresent()) {
      objective = Optional.of(holder.getRegistry().get(Objective.class, objectiveId, true).get());
    }
  }
}

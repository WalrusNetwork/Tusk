package network.walrus.games.core.facets.filters.variable;

import network.walrus.games.core.facets.filters.Variable;
import network.walrus.games.core.facets.group.Group;

/**
 * The group variable contains information about the group associated with the player who is
 * performing a checked action.
 *
 * @author Avicus Network
 */
public class GroupVariable implements Variable {

  private final Group group;

  /**
   * Constructor.
   *
   * @param group which is being stored
   */
  public GroupVariable(Group group) {
    this.group = group;
  }

  public Group getGroup() {
    return group;
  }
}

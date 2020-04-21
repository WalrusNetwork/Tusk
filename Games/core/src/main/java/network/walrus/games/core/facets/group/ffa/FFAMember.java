package network.walrus.games.core.facets.group.ffa;

import java.util.Collections;
import java.util.Set;
import network.walrus.games.core.facets.group.Group;
import network.walrus.games.core.facets.group.GroupMember;
import network.walrus.games.core.facets.group.color.GroupColor;
import org.bukkit.entity.Player;

/**
 * A member of a {@link FFATeam}.
 *
 * @author Avicus Network
 */
public class FFAMember extends GroupMember {

  private final GroupColor color;

  /**
   * Constructor.
   *
   * @param id of the member
   * @param group that this object is a member of
   * @param player that this member is for
   * @param color of this member
   */
  FFAMember(String id, Group group, Player player, GroupColor color) {
    super(id, group, player);
    this.color = color;
  }

  @Override
  public GroupColor getColor() {
    return this.color;
  }

  @Override
  public Set<Player> getPlayers() {
    return Collections.singleton(getPlayer());
  }
}

package network.walrus.games.core.facets.group;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import network.walrus.games.core.facets.group.color.GroupColor;
import network.walrus.utils.bukkit.translation.LocalizedConfigurationProperty;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * A member of a group. This is simply a polymorphic representation of a a player inside of a group,
 * in the sense that groups can have any number of members, but a member can only be in one group at
 * a time.
 *
 * @author Avicus Network
 */
public class GroupMember implements Competitor {

  private final String id;
  private final Group group;
  private final UUID uuid;
  private final LocalizedConfigurationProperty name;

  /**
   * Constructor.
   *
   * @param id of this member
   * @param group which this object is a member of
   * @param player which this membership is for
   */
  public GroupMember(String id, Group group, Player player) {
    this.id = id;
    this.group = group;
    this.uuid = player.getUniqueId();
    this.name = new LocalizedConfigurationProperty(player.getName());
  }

  public Player getPlayer() {
    return Bukkit.getPlayer(this.uuid);
  }

  @Override
  public String id() {
    return this.id;
  }

  @Override
  public LocalizedConfigurationProperty getName() {
    return this.name;
  }

  @Override
  public Group getGroup() {
    return this.group;
  }

  @Override
  public boolean hasPlayer(UUID uuid) {
    Player player = Bukkit.getPlayer(uuid);
    if (player == null) {
      return false;
    } else {
      return getPlayers().contains(player);
    }
  }

  @Override
  public Set<Player> getPlayers() {
    return Collections.singleton(getPlayer());
  }

  @Override
  public GroupColor getColor() {
    return this.group.getColor(getPlayer());
  }
}

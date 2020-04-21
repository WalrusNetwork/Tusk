package network.walrus.games.core.facets.group.spectate;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import network.walrus.games.core.GamesCoreMessages;
import network.walrus.games.core.facets.group.Group;
import network.walrus.games.core.facets.group.GroupMember;
import network.walrus.games.core.facets.group.color.GroupColor;
import network.walrus.utils.bukkit.translation.LocalizedConfigurationProperty;
import org.bukkit.entity.Player;

/**
 * The default spectating group. When players are observing an in-progress round, they should be in
 * this group.
 *
 * <p>Players in this group can have no interactions with the game world, or the players inside of
 * it.
 *
 * @author Avicus Network
 */
public class Spectators implements Group {

  private final Map<UUID, GroupMember> members;
  private LocalizedConfigurationProperty name;

  /** Constructor. */
  public Spectators() {
    this.name = new LocalizedConfigurationProperty(GamesCoreMessages.UI_SPECTATORS);
    this.members = new HashMap<>();
  }

  @Override
  public String id() {
    return "spectators";
  }

  @Override
  public Spectators object() {
    return this;
  }

  @Override
  public LocalizedConfigurationProperty getName() {
    return this.name;
  }

  @Override
  public void setName(LocalizedConfigurationProperty name) {
    this.name = name;
  }

  @Override
  public LocalizedConfigurationProperty getOriginalName() {
    return this.name;
  }

  @Override
  public GroupColor getColor() {
    return GroupColor.AQUA;
  }

  @Override
  public void add(Player player) {
    String random = UUID.randomUUID().toString().substring(0, 8);
    this.members.put(player.getUniqueId(), new GroupMember(random, this, player));
  }

  @Override
  public void remove(Player player) {
    this.members.remove(player.getUniqueId());
  }

  @Override
  public boolean isObserving() {
    return true;
  }

  @Override
  public void setObserving(boolean observing) {
    // Don't do anything.
  }

  @Override
  public boolean isFriendlyFireEnabled() {
    return false;
  }

  @Override
  public boolean isFull(boolean withOverfill) {
    return false;
  }

  @Override
  public Collection<GroupMember> getMembers() {
    return this.members.values();
  }

  @Override
  public boolean isMember(Player player) {
    return this.members.containsKey(player.getUniqueId());
  }

  @Override
  public Set<Player> getPlayers() {
    Set<Player> set = new HashSet<>();
    for (GroupMember groupMember : this.members.values()) {
      Player player = groupMember.getPlayer();
      set.add(player);
    }
    return set;
  }

  @Override
  public int size() {
    return this.members.size();
  }

  @Override
  public int getMinPlayers() {
    return -1;
  }

  @Override
  public int getMaxPlayers() {
    return Integer.MAX_VALUE;
  }

  @Override
  public int getMaxOverfill() {
    return Integer.MAX_VALUE;
  }

  @Override
  public void setMaxPlayers(int max, int overfill) {}
}

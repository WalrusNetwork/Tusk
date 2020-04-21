package network.walrus.games.octc.global.groups.teams;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import network.walrus.games.core.facets.group.Competitor;
import network.walrus.games.core.facets.group.Group;
import network.walrus.games.core.facets.group.GroupMember;
import network.walrus.games.core.facets.group.color.GroupColor;
import network.walrus.utils.bukkit.translation.LocalizedConfigurationProperty;
import org.bukkit.entity.Player;

/**
 * A group of players who compete to achieve a common goal.
 *
 * @author Avicus Network
 */
public class Team implements Group, Competitor {

  private final String id;
  private final LocalizedConfigurationProperty originalName;
  private final GroupColor color;
  private final int min;
  private final Map<UUID, GroupMember> members;
  private LocalizedConfigurationProperty name;
  private int max;
  private int maxOverfill;
  private boolean observing;

  /**
   * Constructor.
   *
   * @param id of the team
   * @param name of the team
   * @param color of the team
   * @param min player count of the team
   * @param max player count of the team
   * @param maxOverfill player count of the team
   */
  public Team(
      String id,
      LocalizedConfigurationProperty name,
      GroupColor color,
      int min,
      int max,
      int maxOverfill) {
    this.id = id;
    this.name = name;
    this.originalName = name;
    this.color = color;
    this.min = min;
    this.max = max;
    this.maxOverfill = maxOverfill;
    this.members = new HashMap<>();
  }

  public void add(Player player) {
    String random = UUID.randomUUID().toString().substring(0, 8);
    this.members.put(player.getUniqueId(), new GroupMember(random, this, player));
  }

  public void remove(Player player) {
    this.members.remove(player.getUniqueId());
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
    return this.originalName;
  }

  @Override
  public Group getGroup() {
    return this;
  }

  @Override
  public GroupColor getColor() {
    return this.color;
  }

  @Override
  public boolean hasPlayer(UUID uuid) {
    return this.members.containsKey(uuid);
  }

  @Override
  public boolean isObserving() {
    return this.observing;
  }

  @Override
  public void setObserving(boolean observing) {
    this.observing = observing;
  }

  @Override
  public boolean isFriendlyFireEnabled() {
    return false;
  }

  @Override
  public boolean isFull(boolean withOverfill) {
    if (withOverfill) {
      return this.members.size() >= this.maxOverfill;
    }
    return this.members.size() >= this.max;
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
    Set<Player> list = new HashSet<>();
    for (GroupMember groupMember : this.members.values()) {
      Player player = groupMember.getPlayer();
      list.add(player);
    }
    return list;
  }

  @Override
  public int size() {
    return this.members.size();
  }

  @Override
  public int getMinPlayers() {
    return this.min;
  }

  @Override
  public int getMaxPlayers() {
    return this.max;
  }

  @Override
  public void setMaxPlayers(int max, int overfill) {
    this.max = max;
    this.maxOverfill = overfill == -1 ? (max + (max / 2)) : overfill;
  }

  @Override
  public String id() {
    return this.id;
  }

  @Override
  public Team object() {
    return this;
  }

  @Override
  public boolean equals(Object compare) {
    return compare instanceof Team && ((Team) compare).id().equals(this.id());
  }

  @Override
  public int getMaxOverfill() {
    return this.maxOverfill;
  }
}

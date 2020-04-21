package network.walrus.games.uhc.facets.groups;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import network.walrus.games.core.facets.group.Competitor;
import network.walrus.games.core.facets.group.Group;
import network.walrus.games.core.facets.group.GroupMember;
import network.walrus.games.core.facets.group.color.GroupColor;
import network.walrus.games.uhc.UHCManager;
import network.walrus.utils.bukkit.translation.LocalizedConfigurationProperty;
import network.walrus.utils.core.translation.Localizable;
import org.bukkit.entity.Player;

/**
 * A team in a game of UHC.
 *
 * @author Austin Mayes
 */
public class UHCTeam implements Group, Competitor {

  private final String id;
  private final GroupColor color;
  private final List<UUID> invites;
  private final Map<UUID, GroupMember> members;
  private Optional<UUID> owner;
  private Optional<LocalizedConfigurationProperty> cachedName;

  /**
   * @param id of the team
   * @param color of the team
   */
  public UHCTeam(String id, GroupColor color) {
    this.id = id;
    this.color = color;
    this.members = new HashMap<>();
    this.invites = Lists.newArrayList();
    this.owner = Optional.empty();
    this.cachedName = Optional.empty();
  }

  @Override
  public LocalizedConfigurationProperty getName() {
    if (cachedName.isPresent()) {
      return cachedName.get();
    } else {
      List<Localizable> list = new ArrayList<>();
      for (GroupMember groupMember : members.values()) {
        Localizable coloredName = groupMember.getColoredName();
        list.add(coloredName);
      }
      return new LocalizedConfigurationProperty(list);
    }
  }

  @Override
  public void setName(LocalizedConfigurationProperty name) {
    // Unused
    throw new UnsupportedOperationException();
  }

  void cacheName() {
    cachedName = Optional.of(getName());
  }

  @Override
  public LocalizedConfigurationProperty getOriginalName() {
    // Unused
    throw new UnsupportedOperationException();
  }

  @Override
  public GroupColor getColor() {
    return this.color;
  }

  @Override
  public void add(Player player) {
    String random = UUID.randomUUID().toString().substring(0, 8);
    this.members.put(player.getUniqueId(), new GroupMember(random, this, player));
    this.invites.remove(player.getUniqueId());
  }

  @Override
  public void remove(Player player) {
    this.members.remove(player.getUniqueId());
  }

  @Override
  public boolean isObserving() {
    return false;
  }

  @Override
  public void setObserving(boolean observing) {
    // Unused
  }

  @Override
  public boolean isFriendlyFireEnabled() {
    return true;
  }

  @Override
  public boolean isFull(boolean withOverfill) {
    return size() >= getMaxPlayers();
  }

  @Override
  public Collection<? extends GroupMember> getMembers() {
    return this.members.values();
  }

  @Override
  public boolean isMember(Player player) {
    return this.members.containsKey(player.getUniqueId());
  }

  @Override
  public int size() {
    return this.members.size();
  }

  @Override
  public int getMinPlayers() {
    return 0;
  }

  @Override
  public int getMaxPlayers() {
    return UHCManager.instance.getConfig().teamSize.get();
  }

  @Override
  public void setMaxPlayers(int max, int overfill) {
    // Unused
  }

  @Override
  public int getMaxOverfill() {
    return getMaxPlayers();
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
  public String id() {
    return this.id;
  }

  @Override
  public Group object() {
    return this;
  }

  @Override
  public Group getGroup() {
    return this;
  }

  @Override
  public boolean hasPlayer(UUID uuid) {
    return this.members.containsKey(uuid);
  }

  /**
   * Check if the specified player is invited to this team.
   *
   * @param player to check
   * @return if the player is invited
   */
  public boolean isInvited(Player player) {
    return this.invites.contains(player.getUniqueId());
  }

  /**
   * Check if the supplied player is the one who created this team.
   *
   * @param player to check
   * @return if the player owns the team
   */
  public boolean isOwner(Player player) {
    return this.owner.isPresent() && this.owner.get().equals(player.getUniqueId());
  }

  public void setOwner(Optional<UUID> owner) {
    this.owner = owner;
  }

  public List<UUID> getInvites() {
    return invites;
  }
}

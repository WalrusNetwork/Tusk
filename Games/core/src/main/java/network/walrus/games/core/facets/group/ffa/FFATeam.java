package network.walrus.games.core.facets.group.ffa;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import network.walrus.games.core.facets.group.Group;
import network.walrus.games.core.facets.group.GroupsManager;
import network.walrus.games.core.facets.group.color.GroupColor;
import network.walrus.utils.bukkit.translation.LocalizedConfigurationProperty;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import org.bukkit.entity.Player;

/**
 * A team which uses random colors and puts each player in their own {@link FFAMember membership}
 * object.
 *
 * @author Avicus Network
 */
public class FFATeam implements Group {

  private final int min;
  private final Map<UUID, FFAMember> members;
  private final boolean colorize;
  private final boolean friendlyFire;
  private final FacetHolder holder;
  private GroupsManager manager;
  private LocalizedConfigurationProperty name;
  private int max;
  private int maxOverfill;
  private boolean observing;

  /**
   * Constructor.
   *
   * @param holder facet holder
   * @param name of the team
   * @param min player count of the team
   * @param max player count of the team
   * @param maxOverfill player count of the team
   * @param colorize if each player should get a unique color
   * @param friendlyFire if players can hit each other
   */
  public FFATeam(
      FacetHolder holder,
      LocalizedConfigurationProperty name,
      int min,
      int max,
      int maxOverfill,
      boolean colorize,
      boolean friendlyFire) {
    this.name = name;
    this.min = min;
    this.max = max;
    this.maxOverfill = maxOverfill;
    this.colorize = colorize;
    this.friendlyFire = friendlyFire;
    this.members = new HashMap<>();
    this.holder = holder;
  }

  public void add(Player player) {
    if (manager == null) {
      manager = holder.getFacetRequired(GroupsManager.class);
    }

    GroupColor color;
    if (this.colorize) {
      color = manager.nextColor();
    } else {
      color = GroupColor.BLUE;
    }

    String random = UUID.randomUUID().toString().substring(0, 8);
    this.members.put(player.getUniqueId(), new FFAMember(random, this, player, color));
  }

  public void remove(Player player) {
    this.members.remove(player.getUniqueId());
  }

  @Override
  public Collection<FFAMember> getMembers() {
    return new ArrayList<>(this.members.values());
  }

  @Override
  public boolean isMember(Player player) {
    return this.members.containsKey(player.getUniqueId());
  }

  @Override
  public Set<Player> getPlayers() {
    Set<Player> list = new HashSet<>();
    for (FFAMember ffaMember : this.members.values()) {
      Player player = ffaMember.getPlayer();
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
  public int getMaxOverfill() {
    return this.maxOverfill;
  }

  @Override
  public String id() {
    return "ffa";
  }

  @Override
  public Group object() {
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
    return GroupColor.GRAY;
  }

  @Override
  public GroupColor getColor(Player player) {
    return members.get(player.getUniqueId()).getColor();
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
    return this.friendlyFire;
  }

  @Override
  public boolean isFull(boolean withOverfill) {
    if (withOverfill) {
      return this.members.size() >= this.maxOverfill;
    }
    return this.members.size() >= this.max;
  }

  @Override
  public boolean equals(Object compare) {
    return compare instanceof FFATeam && ((FFATeam) compare).id().equals(this.id());
  }
}

package network.walrus.games.core.facets.group;

import co.aikar.timings.Timing;
import co.aikar.timings.Timings;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.annotation.Nullable;
import net.md_5.bungee.api.ChatColor;
import network.walrus.games.core.GamesCoreMessages;
import network.walrus.games.core.GamesCorePermissions;
import network.walrus.games.core.GamesPlugin;
import network.walrus.games.core.events.competitor.PlayerChangeCompetitorEvent;
import network.walrus.games.core.events.group.GroupRenameEvent;
import network.walrus.games.core.events.group.PlayerChangeGroupEvent;
import network.walrus.games.core.events.group.PlayerChangedGroupEvent;
import network.walrus.games.core.events.group.PlayerObserverStateChangeEvent;
import network.walrus.games.core.events.player.PlayerSpawnBeginEvent;
import network.walrus.games.core.events.player.PlayerSpawnCompleteEvent;
import network.walrus.games.core.facets.group.color.GroupColor;
import network.walrus.games.core.facets.group.spectate.ObserverTask;
import network.walrus.games.core.facets.group.spectate.Spectators;
import network.walrus.games.core.round.GameRound;
import network.walrus.games.core.util.EventUtil;
import network.walrus.ubiquitous.bukkit.settings.PlayerSettings;
import network.walrus.ubiquitous.bukkit.settings.Setting;
import network.walrus.ubiquitous.bukkit.settings.types.SettingTypes;
import network.walrus.utils.bukkit.sound.ConfiguredSound;
import network.walrus.utils.bukkit.translation.LocalizedConfigurationProperty;
import network.walrus.utils.core.stage.Stage;
import network.walrus.utils.parsing.facet.Facet;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Parent object which manages the entire {@link Group} system for a specific {@link GameRound}.
 *
 * @author Avicus Network
 */
public abstract class GroupsManager extends Facet {

  public static final List<GroupColor> COLORS = new ArrayList<>(Arrays.asList(GroupColor.COLORS));
  public static final Setting<Boolean> OBS_SETTING =
      new Setting<>(
          "games.see-observers",
          SettingTypes.BOOLEAN,
          true,
          GamesCoreMessages.OBSERVER_SETTING_NAME.with(),
          GamesCoreMessages.OBSERVER_SETTING_DESC.with());

  static {
    buildColors();
  }

  private final Timing refreshTimer = Timings.of(GamesPlugin.instance, "Observer refresh");
  private final Timing changeTimer = Timings.of(GamesPlugin.instance, "Group change");
  private final FacetHolder holder;
  private final ObserverTask observerTask;
  private final Map<UUID, Group> playerGroups = Maps.newConcurrentMap();
  private final AtomicBoolean loaded = new AtomicBoolean(false);

  /** Constructor. */
  public GroupsManager(FacetHolder holder) {
    this.holder = holder;
    this.observerTask = new ObserverTask(holder, this);
  }

  private static void buildColors() {
    COLORS.remove(GroupColor.AQUA);
    COLORS.remove(GroupColor.BLACK);

    List<GroupColor> toAdd = Lists.newArrayList();

    // Randomize (otherwise its the same order every match)
    Collections.shuffle(COLORS);

    for (GroupColor groupColor : COLORS) {
      toAdd.add(groupColor.clone().bold(true));
    }

    for (GroupColor groupColor : COLORS) {
      toAdd.add(groupColor.clone().italic(true));
    }

    for (GroupColor groupColor : COLORS) {
      toAdd.add(groupColor.clone().underline(true));
    }

    for (GroupColor groupColor : COLORS) {
      toAdd.add(groupColor.clone().italic(true).bold(true));
    }

    for (GroupColor groupColor : COLORS) {
      toAdd.add(groupColor.clone().italic(true).underline(true));
    }

    for (GroupColor groupColor : COLORS) {
      toAdd.add(groupColor.clone().underline(true).bold(true));
    }

    for (GroupColor groupColor : COLORS) {
      toAdd.add(groupColor.clone().underline(true).bold(true).italic(true));
    }
    COLORS.addAll(toAdd);
  }

  /**
   * Determine the expected group size ratio based on min player counts.
   *
   * @param group The group.
   */
  private double groupMinSizeRatio(Group group) {
    int min = group.getMinPlayers();
    int totalMin = 0;
    for (Group g : getGroups()) {
      if (!g.isSpectator()) {
        int minPlayers = g.getMinPlayers();
        totalMin += minPlayers;
      }
    }
    return totalMin == 0 ? 0 : (double) min / (double) totalMin;
  }

  /**
   * Determine the expected group size ratio based on max player counts.
   *
   * @param group The group.
   */
  private double groupMaxSizeRatio(Group group) {
    int max = group.getMaxPlayers();
    int totalMax = 0;
    for (Group g : getGroups()) {
      if (!g.isSpectator()) {
        int maxPlayers = g.getMaxPlayers();
        totalMax += maxPlayers;
      }
    }
    return totalMax == 0 ? 0 : (double) max / (double) totalMax;
  }

  /**
   * Determine the current group size ratio (A team of 4 versus a team of 3 has a ratio of 1.75)
   *
   * @param group The group to check.
   * @param additionalPlayers Players to add include into the size of the group.
   */
  private double currentGroupSizeRatio(Group group, int additionalPlayers) {
    int size = group.size() + additionalPlayers;
    int sum = 0;
    for (Group g : getGroups()) {
      if (!g.isSpectator()) {
        int i = g.size();
        sum += i;
      }
    }
    int totalSize = sum + additionalPlayers;
    return totalSize == 0 ? 0 : (double) size / (double) totalSize;
  }

  /**
   * Check if a group has enough players and is balanced, based on its size ratio.
   *
   * @param additionalPlayers Players to include into the size of the group.
   */
  public boolean isGroupBalanced(Group group, int additionalPlayers) {
    // Ignore spectators
    if (group.isSpectator()) {
      return true;
    }

    // Ignore if scrimmage
    if (GamesPlugin.getStage() == Stage.TOURNAMENT) {
      return true;
    }

    int newSize = group.size() + additionalPlayers;

    // Counts below min players is acceptable no matter what
    if (newSize < group.getMinPlayers()) {
      return true;
    }

    // Group is always balanced if its the only group
    int count = 0;
    for (Group g : getGroups()) {
      if (!g.isSpectator()) {
        count++;
      }
    }
    if (count == 1) {
      return true;
    }

    // Only check group size ratios if there is a difference in group sizes
    // greater than 1. This is equivalent to: groups are considered balanced
    // if the the group sizes are equal.
    boolean checkRatios = false;
    for (Group test : getGroups()) {
      if (test.isSpectator()) {
        continue;
      }

      int difference = Math.abs(newSize - test.size());

      if (difference > 1) {
        checkRatios = true;
        break;
      }
    }

    if (checkRatios) {
      double minExpected = groupMinSizeRatio(group);
      double maxExpected = groupMaxSizeRatio(group);
      double low = Math.min(minExpected, maxExpected);
      double high = Math.max(minExpected, maxExpected);

      double current = currentGroupSizeRatio(group, additionalPlayers);
      for (double x = low; x <= high; x += getMaxGroupImbalance() / 2.0) {
        double diff = current - x;

        // Needs more players
        if (diff < 0) {
          return true;
        }

        // This team has more players, but it's okay if some more join
        if (diff < getMaxGroupImbalance()) {
          return true;
        }
      }
      return false;
    } else {
      return true;
    }
  }

  /** @return maximum amount two ground are allowed to be imbalanced */
  public abstract double getMaxGroupImbalance();

  /**
   * Refresh visibility for a player.
   *
   * @param target player with changed visibility
   */
  public void refreshObserver(Player target) {
    try (Timing timing = refreshTimer.startClosable()) {
      for (Player player : Bukkit.getOnlinePlayers()) {
        if (player.equals(target)) {
          continue;
        }

        if (shouldSee(player, target)) {
          player.showPlayer(target);
        } else {
          player.hidePlayer(target);
        }

        if (shouldSee(target, player)) {
          target.showPlayer(player);
        } else {
          target.hidePlayer(player);
        }
      }
    }
  }

  /**
   * Determine if a player should see a target.
   *
   * <p>The logic works as follows: - If the target is dead, they cannot be seen - If the player
   * observing, they can see everyone - If the target is not observing, the player can see them
   *
   * @param player who is viewing the target
   * @param target who is being viewed by the player
   * @return if the player should see the target
   */
  private boolean shouldSee(Player player, Player target) {
    try {
      boolean playerObserver = isObserving(player);
      boolean targetObserver = isObserving(target);
      boolean targetDead = isDead(target);

      // No one can see dead players
      if (targetDead) {
        return false;
      }

      // Allow player to see others if they are an observer
      if (playerObserver) {
        return !targetObserver || PlayerSettings.get(player, OBS_SETTING);
      }

      // Otherwise the target must simply be participating
      return !targetObserver;
    } catch (RuntimeException ignored) {
      // This happens during round switch, safe to ignore
      return true;
    }
  }

  /**
   * Check if the given player is currently in an observing state. This does not mean specifically
   * that a player is in the {@link Spectators} group, {@link #isSpectator(Player)} should be used
   * if specifically checking for that. A player is not technically observing if they are dead, even
   * though they can't interact with the world.
   *
   * @param player to check status for
   * @return if the given player is currently in an observing state
   */
  public boolean isObserving(Player player) {
    return getGroup(player).isObserving();
  }

  /** {@link #isObserving(Player)} or {@link #isDead(Player)} or {@link #isSpawning(Player)}. */
  public boolean isObservingOrDead(Player player) {
    return isObserving(player) || isDead(player) || isSpawning(player);
  }

  /**
   * Determine if the player is currently dead.
   *
   * @param player to check status for
   * @return if the player is currently dead
   */
  public abstract boolean isDead(Player player);

  /**
   * Determine if the player is currently in a spawning state. This state is extremely short, and
   * only lasts between the calls of the {@link PlayerSpawnBeginEvent} and {@link
   * PlayerSpawnCompleteEvent}. Players in this state may be at any point of the spawn chain, and
   * thus their state could be quite unstable.
   *
   * @param player to check status for
   * @return if the player is currently in a spawning state
   */
  public abstract boolean isSpawning(Player player);

  @Override
  public void load() {
    for (Group groups : getGroups()) {
      groups.setObserving(true);
    }
    this.observerTask.start();
  }

  @Override
  public void unload() {
    for (Player player : Bukkit.getOnlinePlayers()) {
      player.setDisplayName(player.getName());
    }
    this.observerTask.reset();
  }

  /** @return rule used to determine friendly visibility and damage tracking */
  public abstract CompetitorRule getCompetitorRule();

  /** @return the complete collection of groups in this round */
  public abstract Collection<? extends Group> getGroups();

  /** @return the complete collection of competitors engaged in the round */
  public abstract Collection<? extends Competitor> getCompetitors();

  /**
   * Get all {@link Competitor}s inside of a certain group.
   *
   * @param group to get competitors from
   * @return all competitors inside of the specified group
   */
  public abstract Collection<? extends Competitor> getCompetitors(Group group);

  /**
   * Find the first {@link Competitor} which is linked to a specific player. If the player is not
   * party of any competing groups (such as if they were spectating), {@link Optional#empty()} will
   * be returned instead.
   *
   * @param player to get competitor for
   * @return the competitor linked to the requested player, or empty if none was found
   */
  public Optional<Competitor> getCompetitorOf(Player player) {
    for (Competitor competitor : getCompetitors()) {
      if (competitor.hasPlayer(player)) {
        return Optional.of(competitor);
      }
    }
    return Optional.empty();
  }

  /**
   * Get the {@link Group} which a specfic player is a member of. Since a player can only be in one
   * group at a time, this value will remain constant until the player's group is changed by {@link
   * #changeGroup(Player, Optional, Group, boolean, boolean, boolean)}. This *should* never return a
   * {@code null} value, since a player is always supposed to be a member of a group. If it does, we
   * have bigger problems.
   *
   * @param player to get group for
   * @return group which the player is apart of
   */
  public Group getGroup(Player player) {
    Group group = playerGroups.get(player.getUniqueId());
    if (group == null) {
      // Scary stuff
      throw new RuntimeException(
          "getGroup(player) called on player while not on team. Player: " + player.getName());
    }
    return group;
  }

  /**
   * Attempt to rename a group to a specific name. This will fire a {@link GroupRenameEvent}, and
   * will not succeed if the event is canceled. The original group name can always be determined
   * using {@link Group#getOriginalName()}.
   *
   * @param group to rename
   * @param newName to set as the group's name
   * @return if the group was renamed successfully
   */
  public boolean renameGroup(Group group, LocalizedConfigurationProperty newName) {
    GroupRenameEvent call = EventUtil.call(new GroupRenameEvent(group, newName));
    if (call.isCancelled()) {
      return false;
    }
    Preconditions.checkArgument(call.getName() != null, "Group name cannot be null.");
    group.setName(call.getName());
    return true;
  }

  /**
   * Change a player's group, using their old group as a base and firing a ground change events.
   *
   * @see #changeGroup(Player, Optional, Group, boolean, boolean, boolean)
   */
  public Optional<Group> changeGroup(
      Player player, Group newGroup, boolean triggerSpawn, boolean triggerTeleport) {
    return changeGroup(
        player, Optional.of(getGroup(player)), newGroup, triggerSpawn, triggerTeleport);
  }

  /**
   * Change a player's group and fire group events.
   *
   * @see #changeGroup(Player, Optional, Group, boolean, boolean, boolean)
   */
  public Optional<Group> changeGroup(
      Player player,
      Optional<Group> oldGroup,
      Group newGroup,
      boolean triggerSpawn,
      boolean triggerTeleport) {
    return changeGroup(player, oldGroup, newGroup, triggerSpawn, triggerTeleport, true);
  }

  /**
   * The main method responsible for handling the core group change logic for all game types.
   *
   * <p>The group switch chain operates as follows ([E] sections are only executed if callEvent is
   * {@code true}): - [E] If old group is present, call a {@link PlayerChangeGroupEvent}. If the
   * event is canceled, {@link Optional#empty()} will be returned and the chain will be halted. -
   * Remove the player from the old group, if it exists - Add the player to the new group - Set the
   * player's display name to the competitor's color, and fall back to group color if the group
   * isn't a competitor - Call a {@link PlayerChangeCompetitorEvent} if the competitor was changed.
   * This is always called regardless of the event setting. - [E] Call a {@link
   * PlayerChangedGroupEvent} - Return the group the player is now in
   *
   * @param player to move from one group to another
   * @param oldGroup the player's old group, if they were in one
   * @param newGroup the player's target group
   * @param triggerSpawn f the player should be (re)spawned in this group's spawn location (Also
   *     gives kit)
   * @param triggerTeleport if the player should be teleported to this group's spawn location
   * @param callEvent if group events should be called
   * @return the group the player is now in
   */
  public Optional<Group> changeGroup(
      Player player,
      Optional<Group> oldGroup,
      Group newGroup,
      boolean triggerSpawn,
      boolean triggerTeleport,
      boolean callEvent) {
    try (Timing timing = changeTimer.startClosable()) {
      Optional<Competitor> oldCompetitor = getCompetitorOf(player);

      PlayerChangeGroupEvent call =
          new PlayerChangeGroupEvent(player, oldGroup, newGroup, triggerSpawn, triggerTeleport);

      if (callEvent) {
        oldGroup.ifPresent(
            (group) -> {
              EventUtil.call(call);
            });
      }

      if (call.isCancelled()) {
        return Optional.empty();
      }

      oldGroup.ifPresent(group -> removeMember(group, player));
      call.getGroup().add(player);
      playerGroups.put(player.getUniqueId(), call.getGroup());

      GroupColor color = call.getGroup().getColor();

      Optional<Competitor> competitor = getCompetitorOf(player);
      if (competitor.isPresent()) {
        color = competitor.get().getColor();
      }

      player.setDisplayName(color.getPrefix() + player.getName() + ChatColor.RESET);

      if (!oldCompetitor.equals(competitor)) {
        PlayerChangeCompetitorEvent callCompetitor =
            new PlayerChangeCompetitorEvent(player, oldCompetitor, competitor);
        EventUtil.call(callCompetitor);
      }

      if (!oldGroup.isPresent() || oldGroup.get().isObserving() != newGroup.isObserving()) {
        EventUtil.call(new PlayerObserverStateChangeEvent(player, newGroup.isObserving()));
      }

      if (callEvent) {
        PlayerChangedGroupEvent change =
            new PlayerChangedGroupEvent(player, oldGroup, newGroup, triggerSpawn, triggerTeleport);
        EventUtil.call(change);
      }

      return Optional.of(call.getGroup());
    }
  }

  /**
   * Remove a player from a group.
   *
   * @param group to remove the player from
   * @param player to remove
   */
  public void removeMember(Group group, Player player) {
    group.remove(player);
    playerGroups.remove(player.getUniqueId());
  }

  /** Shuffle all groups. */
  public void shuffle() {
    shuffle(getGroups());
  }

  /**
   * Shuffle players around a collection of groups. Only groups that have more than one member will
   * be considered for shuffle-ability. Groups which are currently not observing will also be
   * ignored. This means that shuffling cannot happen to groups which are currently participating in
   * a game.
   *
   * @param groups to shuffle players around in
   */
  public void shuffle(Collection<? extends Group> groups) {
    // Get groups that are larger than one person and are playing in the round.
    List<Group> shuffleable = new ArrayList<>();
    for (Group group1 : groups) {
      if (!group1.isObserving() && group1.getMembers().size() > 1) {
        shuffleable.add(group1);
      }
    }

    List<Player> playingPlayers = new ArrayList<>();

    for (Group group : shuffleable) {
      playingPlayers.addAll(group.getPlayers());
    }

    Random rand = new Random();

    for (Player player : playingPlayers) {
      changeGroup(player, shuffleable.get(rand.nextInt(shuffleable.size())), true, true);
    }
  }

  /** @return the spectating group */
  public Spectators getSpectators() {
    for (Group group : getGroups()) {
      if (group instanceof Spectators) {
        return (Spectators) group;
      }
    }
    throw new RuntimeException("No spectator team found.");
  }

  /**
   * Run a deep search for all groups which match a {@link CommandSender}'s query string. At the
   * first level, the search will simply try to use group IDs to determine the requested group (only
   * one, since IDs are unique). At the next level, the sender's locale will be used to generate
   * localized versions of group names for the viewer, and any of those that start with the query
   * will be added to a list. The list is then sorted by order of how close the match is, with the
   * first item being the closest match.
   *
   * <p>If a player has the {@link GamesCorePermissions#QUERY_ALL} permission and uses {@code @all},
   * all groups will be returned.
   */
  public List<Group> search(CommandSender viewer, String query) {
    if (viewer.hasPermission(GamesCorePermissions.QUERY_ALL) && query.equals("@all")) {
      return Lists.newArrayList(this.getGroups());
    }

    // Preliminary check by id
    for (Group group : getGroups()) {
      if (group.id().equals(query)) {
        return Collections.singletonList(group);
      }
    }

    List<Group> result = new ArrayList<>();
    Locale locale = viewer.getLocale();

    for (Group group : getGroups()) {
      String translated = group.getName().toText().render(viewer).toPlainText();
      if (translated.toLowerCase().startsWith(query.toLowerCase())) {
        result.add(group);
      }
    }

    result.sort(
        (o1, o2) -> {
          String name1 = o1.getName().render(viewer).toLowerCase();
          String name2 = o2.getName().render(viewer).toLowerCase();

          if (name1.equals(query.toLowerCase())) {
            return 1;
          }

          if (name2.equals(query.toLowerCase())) {
            return -1;
          }

          if (name1.startsWith(query.toLowerCase()) && !name2.startsWith(query.toLowerCase())) {
            return 1;
          }

          if (!name1.startsWith(query.toLowerCase()) && name2.startsWith(query.toLowerCase())) {
            return -1;
          }

          return 0;
        });

    return result;
  }

  /** @return all players who are currently competing */
  public Set<Player> playingPlayers() {
    Set<Player> set = new HashSet<>();
    for (Competitor c : getCompetitors()) {
      set.addAll(c.getPlayers());
    }
    return set;
  }

  /**
   * Determine if a player is in the {@link Spectators} group. This does not directly correlate with
   * player observation status, since players can be a member of a competing group but be observing
   * due to various round mechanics. Use {@link #isObserving(Player)} to check observing status.
   *
   * @param player to check if spectator
   * @return if the player is a member of the spectators group
   */
  public boolean isSpectator(Player player) {
    return getGroup(player).isSpectator();
  }

  /**
   * {@link #playScopedSound(Collection, Location, ConfiguredSound, ConfiguredSound,
   * ConfiguredSound, ConfiguredSound)} which uses the listening player's location as the sound
   * source and only considers one player as "self"
   */
  public void playScopedSound(
      Player player,
      ConfiguredSound self,
      ConfiguredSound team,
      ConfiguredSound enemy,
      ConfiguredSound spectator) {
    playScopedSound(player, null, self, team, enemy, spectator);
  }

  /**
   * {@link #playScopedSound(Collection, Location, ConfiguredSound, ConfiguredSound,
   * ConfiguredSound, ConfiguredSound)} which uses the listening player's location as the sound
   * source
   */
  public void playScopedSound(
      Collection<? extends Player> players,
      ConfiguredSound self,
      ConfiguredSound team,
      ConfiguredSound enemy,
      ConfiguredSound spectator) {
    playScopedSound(players, null, self, team, enemy, spectator);
  }

  /**
   * {@link #playScopedSound(Collection, Location, ConfiguredSound, ConfiguredSound,
   * ConfiguredSound, ConfiguredSound)} which only considers one player as "self"
   */
  public void playScopedSound(
      Player player,
      @Nullable Location location,
      ConfiguredSound self,
      ConfiguredSound team,
      ConfiguredSound enemy,
      ConfiguredSound spectator) {
    playScopedSound(Collections.singleton(player), location, self, team, enemy, spectator);
  }

  /**
   * Broadcast a specific sound to all players in the holder based on their relation to the
   * collection of players which are considered to be the sound source.
   *
   * @param players which are the source of the sound
   * @param location to play the sound at. If this attribute is {@code null}, the listening player's
   *     location will be used as the sound source
   * @param self sound to be sent to the sources
   * @param team sound to be sent to players who are on the same {@link Competitor} as the sources
   * @param enemy sound to be sent to players who are not on the same {@link Competitor} as the
   *     sources
   * @param spectator sound to be sent to spectating players
   */
  public void playScopedSound(
      Collection<? extends Player> players,
      @Nullable Location location,
      ConfiguredSound self,
      ConfiguredSound team,
      ConfiguredSound enemy,
      ConfiguredSound spectator) {
    Set<Group> groups = Sets.newHashSet();
    for (Player player : players) {
      groups.add(getGroup(player));
    }
    if (self != null) {
      self.play(players, location);
    }
    if (team != null) {
      for (Group group : groups) {
        for (Competitor c : getCompetitors(group)) {
          for (Player p : c.getPlayers()) {
            if (players.contains(p)) {
              continue;
            }
            team.play(p, location);
          }
        }
      }
    }
    if (enemy != null) {
      for (Competitor competitor : getCompetitors()) {
        if (groups.contains(competitor.getGroup())) {
          continue;
        }

        for (Player p : competitor.getPlayers()) {
          enemy.play(p, location);
        }
      }
    }
    if (spectator != null) {
      for (Player p : getSpectators().getPlayers()) {
        spectator.play(p, location);
      }
    }
  }

  /**
   * Pick the least used color from a random set of possible {@link GroupColor}s.
   *
   * @return least used color
   */
  public GroupColor nextColor() {
    // Map team color to number of players with that color
    Map<GroupColor, Integer> colorCounts = new HashMap<>();

    for (Group group : getGroups()) {
      for (Player player : group.getPlayers()) {
        GroupColor color = group.getColor(player);
        int current = colorCounts.getOrDefault(color, 0);
        colorCounts.put(color, current + 1);
      }
    }

    // Sort from lowest count to highest
    COLORS.sort(
        (o1, o2) -> {
          Integer count1 = colorCounts.getOrDefault(o1, 0);
          Integer count2 = colorCounts.getOrDefault(o2, 0);

          return count1.compareTo(count2);
        });

    return COLORS.get(0);
  }

  public FacetHolder getHolder() {
    return holder;
  }

  public boolean isLoaded() {
    return loaded.get();
  }

  public void setLoaded(boolean loaded) {
    this.loaded.set(loaded);
  }
}

package network.walrus.games.uhc.facets.groups;

import app.ashcon.intake.CommandException;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import network.walrus.games.core.events.round.RoundStateChangeEvent;
import network.walrus.games.core.facets.group.Competitor;
import network.walrus.games.core.facets.group.CompetitorRule;
import network.walrus.games.core.facets.group.Group;
import network.walrus.games.core.facets.group.GroupMember;
import network.walrus.games.core.facets.group.color.GroupColor;
import network.walrus.games.core.facets.group.spectate.Spectators;
import network.walrus.games.uhc.UHCMessages;
import network.walrus.games.uhc.UHCRound;
import network.walrus.utils.bukkit.item.ItemUtils;
import network.walrus.utils.core.color.NetworkColorConstants.Games.UHC.Teams;
import network.walrus.utils.core.command.exception.TranslatableCommandErrorException;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Manager for groups inside of team-based UHCs
 *
 * @author Austin Mayes
 */
public class TeamsManager extends UHCGroupsManager implements Listener {

  private final List<UHCTeam> teams;
  private final List<UHCTeam> emptyTeams;
  private final UHCTeam lonersTeam;
  private final HashMap<UUID, UHCTeam> ownedTeams;

  /** @param holder which this manager is operating inside of */
  public TeamsManager(FacetHolder holder) {
    super(holder);
    this.teams = Lists.newArrayList();
    this.emptyTeams = Lists.newArrayList();
    this.ownedTeams = Maps.newHashMap();
    this.lonersTeam = new UHCTeam("loners", GroupColor.BLUE);
    this.teams.add(lonersTeam);
    getGroups().addAll(this.teams);
  }

  @Override
  public void addPlayer(Player player) {
    UHCRound round = (UHCRound) getHolder();
    changeGroup(player, lonersTeam, false, false);
    if (round.getState().starting() || round.getState().started()) {
      try {
        claimTeam(player);
      } catch (CommandException e) {
        e.printStackTrace();
      }
    }
  }

  /** Assign players to a group on join. */
  @EventHandler(priority = EventPriority.NORMAL)
  public void onJoin(PlayerJoinEvent event) {
    if (((UHCRound) getHolder()).getState().started()) {
      changeGroup(event.getPlayer(), Optional.empty(), getSpectators(), false, false);
    } else {
      changeGroup(event.getPlayer(), Optional.empty(), lonersTeam, false, false);
    }

    event
        .getPlayer()
        .getInventory()
        .addItem(
            ItemUtils.createBook(
                event.getPlayer(),
                UHCMessages.TEAM_INFO_BOOK_TITLE.with(Teams.BOOK_TITLE),
                UHCMessages.TEAM_INFO_BOOK_CONTENT));
  }

  /** Cache members of a team at start of round */
  @EventHandler
  public void onRoundStart(RoundStateChangeEvent event) {
    if (event.getTo().isPresent() && event.getTo().get().starting()) {
      for (Player player : lonersTeam.getPlayers()) {
        try {
          claimTeam(player);
        } catch (CommandException e) {
          e.printStackTrace();
        }
      }

      for (UHCTeam team : teams) {
        team.cacheName();
      }
    }
  }

  @Override
  public CompetitorRule getCompetitorRule() {
    return CompetitorRule.TEAM;
  }

  @Override
  public Collection<? extends Competitor> getCompetitors() {
    return this.teams;
  }

  @Override
  public Collection<? extends Competitor> getCompetitors(Group group) {
    if (group instanceof Spectators) {
      return Collections.emptyList();
    }

    return Collections.singletonList((UHCTeam) group);
  }

  /**
   * Claim a team on behalf of a player, and set their group to the team.
   *
   * @param player who is attempting to claim a team
   * @throws CommandException if the player cannot claim the team
   */
  public void claimTeam(Player player) throws CommandException {
    Group current = getGroup(player);
    if (current instanceof Spectators || current.id().equals(this.lonersTeam.id())) {
      if (emptyTeams.isEmpty()) {
        createTeam();
      }

      UHCTeam claim = emptyTeams.remove(0);
      ownedTeams.put(player.getUniqueId(), claim);
      claim.setOwner(Optional.of(player.getUniqueId()));
      changeGroup(player, claim, false, false);
    } else {
      throw new TranslatableCommandErrorException(UHCMessages.CREATE_NOT_LONER);
    }
  }

  private UHCTeam createTeam() {
    UHCTeam team = new UHCTeam(UUID.randomUUID().toString().substring(0, 6), nextColor());
    teams.add(team);
    emptyTeams.add(team);
    getGroups().add(team);
    return team;
  }

  /**
   * Determine if a specific player can manage invites for their current group.
   *
   * @param player to check
   * @return if the player can manage invites
   */
  public boolean canManageInvites(Player player) {
    Group group = getGroup(player);
    return group instanceof UHCTeam && ((UHCTeam) group).isOwner(player);
  }

  /**
   * Determine if a player may be invited to a team.
   *
   * @param player to check
   * @return if the player can be invited
   */
  public boolean canBeInvited(Player player) {
    Group group = getGroup(player);
    return group instanceof Spectators || group.id().equals(this.lonersTeam.id());
  }

  public List<UHCTeam> getTeams() {
    List<UHCTeam> teams = new ArrayList<>(this.ownedTeams.values());
    teams.add(0, this.lonersTeam);
    return teams;
  }

  /** @return loners team */
  public UHCTeam getLonersTeam() {
    return lonersTeam;
  }

  /**
   * Change a player's group to the loner team.
   *
   * @param player to change
   * @throws CommandException if the player is already a loner
   */
  public void becomeLoner(Player player) throws CommandException {
    if (lonersTeam.isMember(player)) {
      throw new TranslatableCommandErrorException(UHCMessages.ALREADY_LONER);
    }

    UHCTeam team = (UHCTeam) getCompetitorOf(player).get();
    if (team.isOwner(player) && team.size() > 1) {
      // Get first member from stream and set owner
      Optional<GroupMember> found = Optional.empty();
      for (GroupMember m : team.getMembers()) {
        if (!m.hasPlayer(player.getUniqueId())) {
          found = Optional.of(m);
          break;
        }
      }
      UUID newOwner = found.get().getPlayer().getUniqueId();
      team.setOwner(Optional.of(newOwner));

      ownedTeams.remove(player.getUniqueId());
      ownedTeams.put(newOwner, team);
    } else if (team.size() == 1) {
      ownedTeams.remove(player.getUniqueId());
      emptyTeams.add(team);
    }

    changeGroup(player, lonersTeam, false, false);
    player.sendMessage(UHCMessages.JOIN_LONER.with(Teams.BECAME_LONER));
  }
}

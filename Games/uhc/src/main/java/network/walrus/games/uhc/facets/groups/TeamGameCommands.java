package network.walrus.games.uhc.facets.groups;

import app.ashcon.intake.Command;
import app.ashcon.intake.CommandException;
import app.ashcon.intake.bukkit.parametric.annotation.Sender;
import app.ashcon.intake.parametric.annotation.Default;
import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.Optional;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.HoverEvent;
import network.walrus.games.core.facets.group.Group;
import network.walrus.games.uhc.UHCManager;
import network.walrus.games.uhc.UHCMessages;
import network.walrus.games.uhc.UHCPermissions;
import network.walrus.games.uhc.UHCRound;
import network.walrus.games.uhc.scenarios.ScenarioManager;
import network.walrus.games.uhc.scenarios.type.BackpackScenario;
import network.walrus.utils.bukkit.sound.NetworkSoundConstants.Games.UHC;
import network.walrus.utils.bukkit.sound.NetworkSoundConstants.Games.UHC.Teams.Join;
import network.walrus.utils.bukkit.translation.text.player.PersonalizedBukkitPlayer;
import network.walrus.utils.core.color.NetworkColorConstants.Games.UHC.Teams;
import network.walrus.utils.core.color.NetworkColorConstants.Games.UHC.Teams.List;
import network.walrus.utils.core.command.exception.InvalidPaginationPageException;
import network.walrus.utils.core.command.exception.TranslatableCommandErrorException;
import network.walrus.utils.core.command.exception.TranslatableCommandWarningException;
import network.walrus.utils.core.text.LocalizedNumber;
import network.walrus.utils.core.text.UnlocalizedFormat;
import network.walrus.utils.core.text.UnlocalizedText;
import network.walrus.utils.core.translation.Localizable;
import network.walrus.utils.core.translation.TextStyle;
import network.walrus.utils.core.util.Paginator;
import network.walrus.utils.core.util.StringUtils;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import network.walrus.utils.parsing.facet.parse.configurator.command.FacetCommandContainer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Commands for managing teams during team-based UHCs.
 *
 * @author Austin Mayes
 */
public class TeamGameCommands extends FacetCommandContainer<UHCGroupsManager> {

  /**
   * @param holder which this object is inside of
   * @param facet which this object is bound to
   */
  public TeamGameCommands(FacetHolder holder, UHCGroupsManager facet) {
    super(holder, facet);
  }

  private TeamsManager getManager() throws CommandException {
    return getManager(true);
  }

  private TeamsManager getManager(boolean before) throws CommandException {
    if (before && ((UHCRound) getHolder()).getState().started()) {
      throw new TranslatableCommandErrorException(UHCMessages.CANNOT_MANAGE_STARTED);
    }

    if (getFacet() instanceof TeamsManager) {
      return (TeamsManager) getFacet();
    } else {
      throw new TranslatableCommandWarningException(UHCMessages.TEAMS_DISABLED);
    }
  }

  /**
   * Create a team
   *
   * @throws CommandException if the action is not currently possible
   */
  @Command(
      aliases = {"create", "c", "make", "new"},
      desc = "Create a team")
  public void create(@Sender Player player) throws CommandException {
    TeamsManager manager = getManager();
    manager.claimTeam(player);
    UHC.Teams.CREATED.play(player);
    player.sendMessage(UHCMessages.TEAM_CLAIMED.with(Teams.CREATED));
  }

  /**
   * Invite a player to the sender's current team
   *
   * @throws CommandException if the action is not currently possible
   */
  @Command(
      aliases = {"invite", "i", "inviteplayer", "sendinvite", "si"},
      desc = "Invite a player to a team")
  public void invite(@Sender Player player, Player toInvite) throws CommandException {
    TeamsManager manager = getManager();
    if (manager.canBeInvited(player)) {
      throw new TranslatableCommandWarningException(UHCMessages.NO_TEAM_CREATED);
    }
    if (!manager.canManageInvites(player)) {
      throw new TranslatableCommandWarningException(UHCMessages.CANNOT_MANAGE_INVITES);
    }
    UHCTeam team = (UHCTeam) manager.getGroup(player);
    if (!manager.canBeInvited(toInvite)) {
      throw new TranslatableCommandWarningException(UHCMessages.CANNOT_INVITE);
    }
    team.getInvites().add(toInvite.getUniqueId());
    UHC.Teams.INVITE_SENT.play(player);
    player.sendMessage(
        UHCMessages.TEAM_INVITED.with(Teams.INVITE_SENT, new PersonalizedBukkitPlayer(toInvite)));
    UHC.Teams.INVITE_RECEIVED.play(toInvite);

    String playerName = player.getName(toInvite);
    TextStyle acceptStyle = Teams.ACCEPT_INVITE.duplicate();
    acceptStyle.click(new ClickEvent(Action.RUN_COMMAND, "/team accept " + playerName));
    BaseComponent hoverText =
        UHCMessages.INVITE_ACCEPT_HOVER
            .with(Teams.INVITE_HOVER, new PersonalizedBukkitPlayer(player))
            .render(toInvite);
    acceptStyle.hover(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[] {hoverText}));

    Localizable acceptButton = UHCMessages.INVITE_ACCEPT.with(acceptStyle);
    toInvite.sendMessage(
        UHCMessages.prefix(
            UHCMessages.INVITED.with(
                Teams.INVITE_RECEIVED, new PersonalizedBukkitPlayer(player), acceptButton)));
  }

  /**
   * Join a team a player has been invited to.
   *
   * @throws CommandException if the action is not currently possible
   */
  @Command(
      aliases = {"accept", "jointeam", "acceptinvite", "ai", "jt"},
      desc = "Join a player's team")
  public void join(@Sender Player player, Player toJoin) throws CommandException {
    TeamsManager manager = getManager();
    if (!manager.canBeInvited(player)) {
      throw new TranslatableCommandErrorException(UHCMessages.ALREADY_ON_TEAM);
    }

    Group group = manager.getGroup(toJoin);

    if (!(group instanceof UHCTeam) || !((UHCTeam) group).isInvited(player)) {
      throw new TranslatableCommandErrorException(UHCMessages.NOT_INVITED);
    }

    UHCTeam team = (UHCTeam) group;

    int maxTeamSize = UHCManager.instance.getConfig().teamSize.get();
    if (team.size() >= maxTeamSize) {
      throw new TranslatableCommandErrorException(
          UHCMessages.TEAM_FULL, new LocalizedNumber(maxTeamSize));
    }

    if (!manager.changeGroup(player, team, false, false).isPresent()) {
      return;
    }

    player.sendMessage(
        UHCMessages.TEAM_JOINED.with(Teams.JOINED, new PersonalizedBukkitPlayer(toJoin)));
    toJoin.sendMessage(
        UHCMessages.INVITE_ACCEPTED.with(
            Teams.INVITE_ACCEPTED, new PersonalizedBukkitPlayer(player)));
    Join.SELF.play(player);
    for (Player teamMember : team.getPlayers()) {
      Join.TEAMMATE.play(teamMember);
    }
  }

  /**
   * Become a loner.
   *
   * @throws CommandException if the action is not currently possible
   */
  @Command(
      aliases = {"leave", "bl", "becomeloner", "lone"},
      desc = "Leave your current team")
  public void leave(@Sender Player player) throws CommandException {
    TeamsManager manager = getManager();
    manager.becomeLoner(player);
    UHC.Teams.BECOME_LONER.play(player);
  }

  /**
   * View a team inventory.
   *
   * @throws CommandException if the action is not currently possible
   */
  @Command(
      aliases = {"inventory", "i", "inv"},
      desc = "View a team inventory")
  public void inventory(@Sender Player player, Optional<String> teamName) throws CommandException {
    TeamsManager manager = getManager(false);

    ScenarioManager scenarioManager = UHCManager.instance.getScenarioManager();
    if (!scenarioManager.isActive(BackpackScenario.class)) {
      throw new TranslatableCommandWarningException(UHCMessages.BACKPACK_NOT_ENABLED);
    }

    if (!((UHCRound) getHolder()).getState().playing()) {
      throw new TranslatableCommandErrorException(UHCMessages.BACKPACK_NOT_ENABLED);
    }

    Group group = manager.getGroup(player);
    if (teamName.isPresent() && player.hasPermission(UHCPermissions.BACKPACK_VIEW)) {
      java.util.List<Group> search = getManager().search(player, teamName.get());
      if (search.size() != 1) {
        throw new TranslatableCommandErrorException(UHCMessages.BACKPACK_NOT_FOUND);
      }

      group = search.get(0);
    }

    scenarioManager.getActive(BackpackScenario.class).open(player, group);
  }

  /**
   * List all teams
   *
   * @throws CommandException if the supplied page is out of bounds
   */
  @Command(
      aliases = {"list", "a", "all", "l"},
      desc = "List all current teams")
  public void list(@Sender CommandSender sender, @Default("1") int page) throws CommandException {
    page = page - 1;
    TeamsManager manager = getManager();
    Paginator<UHCTeam> paginator = new Paginator<>(manager.getTeams(), 5);
    Collection<UHCTeam> list;
    try {
      list = paginator.getPage(page);
    } catch (IllegalArgumentException e) {
      throw new InvalidPaginationPageException(paginator);
    }

    Localizable page1 = new UnlocalizedText((page + 1) + "", List.CURRENT_PAGE);
    Localizable page2 = new UnlocalizedText(paginator.getPageCount() + "", List.TOTAL_PAGES);

    Localizable header = UHCMessages.UI_TEAMS.with(List.HEADER, page1, page2);
    sender.sendMessage(header);
    UnlocalizedFormat format = new UnlocalizedFormat("{0}. {1}");
    for (UHCTeam team : list) {
      if (team.id().equals("loners")) continue;

      int index = paginator.getIndex(team);
      String members =
          StringUtils.join(
              Lists.newLinkedList(team.getPlayers()), ",", (p) -> p.getDisplayName(sender));
      sender.sendMessage(
          format.with(
              List.DELIMITER,
              new LocalizedNumber(index, List.INDEX),
              new UnlocalizedText(members)));
    }
  }

  @Override
  public String[] rootAlias() {
    return new String[] {"team", "teams"};
  }
}

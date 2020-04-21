package network.walrus.games.octc.global.groups;

import app.ashcon.intake.Command;
import app.ashcon.intake.bukkit.parametric.annotation.Sender;
import app.ashcon.intake.parametric.annotation.Text;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import network.walrus.games.core.events.group.GroupMaxPlayerCountChangeEvent;
import network.walrus.games.core.events.group.GroupRenameEvent;
import network.walrus.games.core.facets.group.Group;
import network.walrus.games.core.util.EventUtil;
import network.walrus.games.octc.OCNMessages;
import network.walrus.games.octc.OCNPermissions;
import network.walrus.utils.bukkit.translation.LocalizedConfigurationProperty;
import network.walrus.utils.core.color.NetworkColorConstants.Games.OCN.Groups.Errors;
import network.walrus.utils.core.text.UnlocalizedText;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import network.walrus.utils.parsing.facet.parse.configurator.command.FacetCommandContainer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Commands used to interact with groups.
 *
 * @author Austin Mayes
 */
public class GroupCommands extends FacetCommandContainer<OCNGroupsManager> {

  /**
   * @param holder which this object is inside of
   * @param facet which this object is bound to
   */
  public GroupCommands(FacetHolder holder, OCNGroupsManager facet) {
    super(holder, facet);
  }

  /**
   * Join a team.
   *
   * @param player who is joining
   * @param team id of the team being requested
   */
  @Command(
      aliases = {"join", "play", "j", "joingame", "kpom", "jg", "entergame", "eg"},
      desc = "Join a team")
  public void join(@Sender Player player, @Text @Nullable String team) {
    Group from = getFacet().getGroup(player);
    Group to;

    if (team == null) {
      getFacet().join(player, from);
      return;
    }

    List<Group> search = getFacet().search(player, team.trim());
    if (search.size() > 0) {
      to = search.get(0);
    } else {
      to = null;
    }

    if (to != null && !to.isSpectator() && !player.hasPermission(OCNPermissions.JOIN_PICK)) {
      player.sendMessage(OCNMessages.ERROR_CANNOT_PICK_TEAM.with(Errors.CANNOT_PICK));
      return;
    }

    getFacet().join(player, from, to);
  }

  /**
   * Leave the match.
   *
   * @param player who is leaving
   */
  @Command(
      aliases = {"leave", "quit", "exit", "l", "bye", "obs", "spectate", "observers", "spec"},
      desc = "Join a the spectators team.")
  public void leave(@Sender Player player) {
    join(player, "spectators");
  }

  /**
   * Forces a player onto another team
   *
   * @param sender the sender of the command
   * @param player the target player for the command
   * @param teamName the target team, to force the target player onto
   */
  @Command(
      aliases = {"teamforce"},
      desc = "Force a player onto another team",
      perms = OCNPermissions.TEAM_FORCE)
  public void force(@Sender CommandSender sender, Player player, @Text String teamName) {
    Optional<Group> optGroup = getGroup(sender, teamName);
    Group from = getFacet().getGroup(player);

    if (!optGroup.isPresent()) {
      sender.sendMessage(OCNMessages.ERROR_TEAM_NOT_FOUND.with(Errors.NOT_FOUND));
      return;
    }

    getFacet().joinGroup(player, optGroup.get(), Optional.of(from));
  }

  /**
   * Changes the size of a team
   *
   * @param sender the sender of the command
   * @param teamSize the new max size of the team
   * @param teamName the name of the team to set the max size of
   */
  @Command(
      aliases = {"teamsize"},
      desc = "Changes the max size of a team",
      perms = OCNPermissions.TEAM_SIZE)
  public void size(@Sender CommandSender sender, String teamName, int teamSize) {
    if (teamSize < 0) {
      // use the same text style as not found error
      sender.sendMessage(OCNMessages.ERROR_NEGATIVE_TEAMSIZE.with(Errors.NEGATIVE_TEAM_SIZE));
      return;
    }

    // see if it's the wildcard, set on all teams then
    if (teamName.equals("*")) {
      for (Group x : getFacet().getGroups()) {
        setMaxPlayers(x, teamSize);
      }
      return;
    }

    Optional<Group> group = getGroup(sender, teamName);
    if (!group.isPresent()) {
      sender.sendMessage(OCNMessages.ERROR_TEAM_NOT_FOUND.with(Errors.NOT_FOUND));
      return;
    }

    group.ifPresent(x -> setMaxPlayers(x, teamSize));
  }

  private void setMaxPlayers(Group group, int size) {
    group.setMaxPlayers(size, 0);
    EventUtil.call(new GroupMaxPlayerCountChangeEvent(group));
  }

  /**
   * Sets an alias for a team
   *
   * @param sender the sender of the command
   * @param name the current name of the group
   * @param alias the new name of the group
   */
  @Command(
      aliases = {"teamalias"},
      desc = "Sets the alias of a team",
      perms = OCNPermissions.TEAM_ALIAS)
  public void alias(@Sender CommandSender sender, String name, @Text String alias) {
    Optional<Group> group = getGroup(sender, name);

    if (!group.isPresent()) {
      sender.sendMessage(OCNMessages.ERROR_TEAM_NOT_FOUND.with(Errors.NOT_FOUND));
      return;
    }

    group.ifPresent(
        x -> {
          LocalizedConfigurationProperty newName =
              new LocalizedConfigurationProperty(
                  Collections.singletonList(new UnlocalizedText(alias)));

          x.setName(newName);
          EventUtil.call(new GroupRenameEvent(x, newName));
        });
  }

  private Optional<Group> getGroup(CommandSender sender, String team) {
    List<Group> found = getFacet().search(sender, team.trim());
    if (!found.isEmpty()) {
      return Optional.of(found.get(0));
    }
    return Optional.empty();
  }
}

package network.walrus.games.core.facets.visual;

import app.ashcon.intake.Command;
import app.ashcon.intake.CommandException;
import app.ashcon.intake.bukkit.parametric.annotation.Sender;
import network.walrus.games.core.GamesCoreMessages;
import network.walrus.ubiquitous.bukkit.UbiquitousMessages;
import network.walrus.utils.core.color.NetworkColorConstants;
import network.walrus.utils.core.color.NetworkColorConstants.Boolean;
import network.walrus.utils.core.color.NetworkColorConstants.Games;
import network.walrus.utils.core.command.exception.TranslatableCommandErrorException;
import network.walrus.utils.core.text.UnlocalizedText;
import network.walrus.utils.parsing.facet.holder.FacetHolder;
import network.walrus.utils.parsing.facet.parse.configurator.command.FacetCommandContainer;
import org.bukkit.entity.Player;

/**
 * Commands for configuring the player-specific scoreboard
 *
 * @author Austin Mayes
 */
public class ScoreboardCommands extends FacetCommandContainer<SidebarFacet> {

  /**
   * @param holder which this object is inside of
   * @param facet which this object is bound to
   */
  public ScoreboardCommands(FacetHolder holder, SidebarFacet facet) {
    super(holder, facet);
  }

  @Override
  public String[] rootAlias() {
    return new String[] {"sidebar", "scoreboard", "sb"};
  }

  /**
   * oggle between the different scoreboards
   *
   * @throws CommandException if the player has no pane
   */
  @Command(
      aliases = {"toggle", "next", "t", "n"},
      desc = "Toggle between the different scoreboards")
  public void toggle(@Sender Player player) throws CommandException {
    PaneGroup current = getCurrent(player);
    String id = current.next(player, getFacet().displayManager);
    player.sendMessage(
        GamesCoreMessages.SCOREBOARD_TOGGLE.with(
            NetworkColorConstants.Games.Scoreboard.TOGGLED,
            new UnlocalizedText(id, NetworkColorConstants.Games.Scoreboard.NAME)));
  }

  /**
   * Get a list of selectable scoreboards
   *
   * @throws CommandException if the player has no pane
   */
  @Command(
      aliases = {"list", "l", "all"},
      desc = "Get a list of selectable scoreboards")
  public void list(@Sender Player player) throws CommandException {
    PaneGroup current = getCurrent(player);
    player.sendMessage(
        GamesCoreMessages.SCOREBOARD_LIST.with(
            Games.Scoreboard.LIST, new UnlocalizedText(current.list())));
  }

  /**
   * Turn on/off scoreboard automatic alternating
   *
   * @throws CommandException if the player has no pane
   */
  @Command(
      aliases = {"alternate", "alt", "a"},
      desc = "Turn on/off scoreboard automatic alternating")
  public void alternate(@Sender Player player, boolean alternate) throws CommandException {
    PaneGroup current = getCurrent(player);
    if (alternate) {
      current.setPreference(player, DisplayMode.ALTERNATE, getFacet().displayManager);
    } else {
      current.setPreference(player, DisplayMode.STATIC, getFacet().displayManager);
    }

    player.sendMessage(
        GamesCoreMessages.SCOREBOARD_ALTERNATING_TOGGLED.with(
            alternate
                ? UbiquitousMessages.TRUE.with(Boolean.TRUE)
                : UbiquitousMessages.FALSE.with(Boolean.FALSE)));
  }

  private PaneGroup getCurrent(Player player) throws CommandException {
    PaneGroup current = getFacet().getCurrent(player);
    if (current == null) {
      throw new TranslatableCommandErrorException(UbiquitousMessages.UH_OH);
    }
    return current;
  }
}
